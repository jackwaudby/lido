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
    private String fixSeed = "true";

    @Option(names = {"-sv", "--seedValue"}, description = "Seed value")
    private int seedValue = 0;

    @Option(names = {"-r", "--runs"}, description = "Simulation runs")
    private double runs = 10;

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
        var clock = Clock.getInstance();
        var cluster = Cluster.getInstance();

        // run simulation
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        LOGGER.info("--------------------");
        LOGGER.info(String.format("Starting simulation at %s", dtf.format(now)));
        LOGGER.info("Simulation runs: " + String.format("%.5f", runs));
        LOGGER.info("Configuration: " + Config.getInstance());
        var start = System.currentTimeMillis();
        LOGGER.info("Simulating....");

        for (int i = 0; i < runs; i++) {
            initialize(config, eventList);
            injectFailure(config, eventList);
            runSimulation(config, rand, eventList);
            metrics.getSummary();
            // clear state
            clock.reset();
            cluster.reset();
            metrics.reset();
            eventList.clear();
        }

        var end = System.currentTimeMillis();
        var realTime = (end - start) / 1000.0;
        LOGGER.info("Real time (secs): " + String.format("%.5f", realTime));
        LOGGER.info("Simulation completed!");
        LOGGER.info("--------------------");
        LOGGER.info("");

//        WriteOutResults.writeOutResults(config, metrics);

        return 0;
    }

    private void injectFailure(Config config, EventList eventList) {
        // fail some time during first round
        var randomFailureTime = Rand.getInstance().getRandom(protocolPeriod);
        var time = (double) randomFailureTime / 1000.0;
        var randomMember = Rand.getInstance().getRandom(config.getInitialNbrOfMembers());
        var failureEvent = new FailureEvent(time, EventType.FAILURE, randomMember);
        eventList.addEvent(failureEvent);
    }

    private static void initialize(Config config, EventList eventList) {
        // init
        var initEventTime = 0;
        for (int memberId = 0; memberId < config.getInitialNbrOfMembers(); memberId++) {
            var epochEvent = new StartProtocolPeriodEvent(initEventTime, EventType.START_PROTOCOL_PERIOD, memberId);
            eventList.addEvent(epochEvent);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    static void runSimulation(Config config, Rand rand, EventList eventList) {
        var cluster = Cluster.getInstance();
        var clock = Clock.getInstance();
        var metrics = Metrics.getInstance();

        while (true) {

            AbstractEvent nextEvent = eventList.getNextEvent();
            clock.setClock(nextEvent.getEventTime());

            LOGGER.debug("Event: " + nextEvent.getEventType() + " at " + String.format("%.2f (ms)", nextEvent.getEventTime() * 1000.0));
            LOGGER.debug("Action(s): ");

            var eventType = nextEvent.getEventType();
            switch (eventType) {
                case START_PROTOCOL_PERIOD ->
                        StartProtocolAction.start((StartProtocolPeriodEvent) nextEvent, cluster, config, eventList, rand);
                case END_PROTOCOL_PERIOD -> {
                    if (EndProtocolAction.end((EndProtocolPeriodEvent) nextEvent, cluster, eventList, metrics) == 0) {
                        return;
                    }
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
