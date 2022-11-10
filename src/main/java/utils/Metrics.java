package utils;

import org.apache.log4j.Logger;

public class Metrics {
    private final static Logger LOGGER = Logger.getLogger(Metrics.class.getName());

    private static final Metrics instance = new Metrics();

    private int rounds;
    private int timeToDetection;
    private int falsePositives;

    private Metrics() {
        rounds = 0;
        timeToDetection = 0;
        falsePositives = 0;
    }

    public static Metrics getInstance() {
        return instance;
    }

    public int getTimeToDetection() {
        return timeToDetection;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public int getRounds() {
        return rounds;
    }

    public void incFalsePositives() {
        this.falsePositives += 1;
    }

    public void incRounds() {
        this.rounds += 1;
    }

    public void getSummary() {
        LOGGER.info("Results: ");
        LOGGER.info("  rounds: " + getRounds());
        LOGGER.info("  time to detection: " + getTimeToDetection());
        LOGGER.info("  false positives: " + getFalsePositives());
        LOGGER.info("  false positive rate: " + ((float) getFalsePositives() / (float) getRounds()) * 100.0);
    }
}
