package utils;

import org.apache.log4j.Logger;

public class Metrics {
    private final static Logger LOGGER = Logger.getLogger(Metrics.class.getName());

    private static final Metrics instance = new Metrics();

    private int rounds;
    private double timeToDetection;

    private double failureTime;


    private Metrics() {
        rounds = 0;
        timeToDetection = 0.0;
        failureTime = 0.0;
    }

    public static Metrics getInstance() {
        return instance;
    }

    public double getTimeToDetection() {
        return timeToDetection;
    }


    public int getRounds() {
        return rounds;
    }


    public void incRounds() {
        this.rounds += 1;
    }

    public void setFailureTime(double failureTime) {
        this.failureTime = failureTime;
    }

    public void setTimeToDetection(double currentTime) {
        this.timeToDetection = currentTime - failureTime;
    }

    public double getFailureTime() {
        return failureTime;
    }

    public void getSummary() {
        LOGGER.info("Results: ");
        LOGGER.info("  cumulative rounds: " + getRounds());
        LOGGER.info(String.format("  failure time (ms): %.2f", getFailureTime() * 1000.0));
        LOGGER.info(String.format("  time to detection (ms): %.2f", getTimeToDetection() * 1000.0));
    }
}
