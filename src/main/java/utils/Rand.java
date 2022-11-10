package utils;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import java.util.HashSet;
import java.util.Random;

import static java.lang.Integer.MAX_VALUE;

public class Rand {
    private static final Rand instance = new Rand();

    private final ExponentialDistribution messageDelayDistribution;
    private final Random misc;

    public static Rand getInstance() {
        return instance;
    }

    private Rand() {
        var config = Config.getInstance();

        // lam = 200 => 1/200 = 0.005 (5ms)
        double messageRate = 1 / config.getMessageRateInSecs();
        this.messageDelayDistribution = new ExponentialDistribution(messageRate);
        this.misc = new Random();

        // seed setting
        long seedValue;
        if (config.isSeedSet()) {
            seedValue = config.getSeedValue();
        } else {
            // choose random seed
            seedValue = misc.nextLong(0, MAX_VALUE);
            config.setSeedValue(seedValue);
        }

        messageDelayDistribution.reseedRandomGenerator(seedValue);
        misc.setSeed(seedValue);
    }

    public double generateMessageDelay() {
        return messageDelayDistribution.sample();
    }

    public int getRandom(int excBound) {
        return misc.nextInt(0, excBound);
    }

}
