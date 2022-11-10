import action.*;
import event.*;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import state.Cluster;
import utils.*;

import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

public class Main implements Callable<Integer> {
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Option(names = {"-m", "--members"}, description = "Cluster size")
    private int initialMembers = 3;

    @Option(names = {"-lam", "--messageRate"}, description = "Message rate (messages/s)")
    private int messageRate = 200;

    @Option(names = {"-k", "--subset"}, description = "Number of members to contact if timeout")
    private int k = 1;

    @Option(names = {"-t", "--timeout"}, description = "Timeout duration (ms)")
    private int timeout = 10;

    @Option(names = {"-p", "--period"}, description = "Protocol period (ms)")
    private int protocolPeriod = 50;

    @Option(names = {"-s", "--seed"}, description = "Fix seed")
    private String fixSeed = "false";

    @Option(names = {"-sv", "--seedValue"}, description = "Seed value")
    private int seedValue = 0;

    @Option(names = {"-d", "--duration"}, description = "Simulation duration (secs)")
    private double timeLimit = 10;

    @Override
    public Integer call() {
        // set config from command line args
        var config = Config.getInstance();
        var metrics = Metrics.getInstance();
        config.setInitialNbrOfMembers(initialMembers);
        config.setMessageRate(messageRate);
        config.setSizeOfFailureDetectionSubgroup(k);
        config.setTimeout(timeout);
        config.setProtocolPeriod(protocolPeriod);
        config.setFixSeed(Boolean.parseBoolean(fixSeed));
        config.setSeedValue(seedValue);


        // global variables
        var rand = Rand.getInstance();
        var eventList = EventList.getInstance();

        // init
        var initEventTime = 0;
        for (int memberId = 0; memberId < config.getInitialNbrOfMembers(); memberId++) {
            var epochEvent = new StartProtocolPeriodEvent(initEventTime, EventType.START_PROTOCOL_PERIOD, memberId);
            eventList.addEvent(epochEvent);
        }

//        var failureEvent = new FailureEvent(0.006, EventType.FAILURE, 2);
//        eventList.addEvent(failureEvent);

        // run simulation
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        LOGGER.info("--------------------");
        LOGGER.info(String.format("Starting simulation at %s", dtf.format(now)));
        LOGGER.info("Simulation time (secs): " + String.format("%.5f", timeLimit));
        LOGGER.info("Configuration: " + Config.getInstance());
        var start = System.currentTimeMillis();
        LOGGER.info("Simulating....");
        runSimulation(timeLimit, config, rand, eventList);
        var end = System.currentTimeMillis();
        metrics.getSummary();
        var realTime = (end - start) / 1000.0;
        LOGGER.info("Real time (secs): " + String.format("%.5f", realTime));
        LOGGER.info("Simulation completed!");
        LOGGER.info("--------------------");
        LOGGER.info("");


        WriteOutResults.writeOutResults( config, metrics );

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    static void runSimulation(double timeLimit, Config config, Rand rand, EventList eventList) {
        var cluster = Cluster.getInstance();
        var clock = Clock.getInstance();
        var metrics = Metrics.getInstance();

        while (clock.getClock() < timeLimit) {

            AbstractEvent nextEvent = eventList.getNextEvent();
            clock.setClock(nextEvent.getEventTime());

            LOGGER.debug("Event: " + nextEvent.getEventType() + " at " + String.format("%.2f (ms)", nextEvent.getEventTime() * 1000.0));
            LOGGER.debug("Action(s): ");

            var eventType = nextEvent.getEventType();
            switch (eventType) {
                case START_PROTOCOL_PERIOD ->
                        StartProtocolAction.start((StartProtocolPeriodEvent) nextEvent, cluster, config, eventList, rand);
                case END_PROTOCOL_PERIOD -> {
                    EndProtocolAction.end((EndProtocolPeriodEvent) nextEvent, cluster, eventList, metrics);
//                    return;
                }
                case TIMEOUT -> TimeoutAction.timeout((TimeoutEvent) nextEvent, cluster, config, eventList, rand);
                case DIRECT_PING -> DirectPingAction.reply((DirectPingEvent) nextEvent, cluster, eventList, rand);
                case DIRECT_PING_ACK -> DirectPingAckAction.consume((DirectPingAckEvent) nextEvent, cluster);
                case PING_REQUEST -> PingRequestAction.forward((PingRequestEvent) nextEvent, cluster, eventList, rand);
                case INDIRECT_PING -> IndirectPingAction.reply((IndirectPingEvent) nextEvent, cluster, eventList, rand);
                case PING_REQUEST_ACK -> PingRequestAckAction.consume((PingRequestAckEvent) nextEvent, cluster);
                case INDIRECT_PING_ACK ->
                        IndirectPingAckAction.forward((IndirectPingAckEvent) nextEvent, eventList, rand);
                case FAILURE -> FailureAction.failed((FailureEvent) nextEvent, cluster);
            }
            LOGGER.debug("");
        }
    }
}
