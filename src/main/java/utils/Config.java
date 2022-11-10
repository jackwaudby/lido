package utils;

public class Config {
    private static final Config instance = new Config();

    private static int initialNbrOfMembers;
    private static int protocolPeriod;

    private static int timeout;
    private static int sizeOfFailureDetectionSubgroup;

    private static double messageRate;

    private static long seedValue = 0;
    private static boolean fixSeed = true;

    private Config() {

    }

    public static Config getInstance() {
        return instance;
    }

    public void setInitialNbrOfMembers(int clusterSize) {
        Config.initialNbrOfMembers = clusterSize;
    }

    public int getInitialNbrOfMembers() {
        return initialNbrOfMembers;
    }

    public void setProtocolPeriod(int protocolPeriod) {
        Config.protocolPeriod = protocolPeriod;
    }

    public double getProtocolPeriod() {
        return protocolPeriod;
    }

    public void setTimeout(int timeout) {
        Config.timeout = timeout;
    }

    public double getTimeout() {
        return timeout;
    }

    public void setSizeOfFailureDetectionSubgroup( int k )
    {
        Config.sizeOfFailureDetectionSubgroup = k;
    }

    public int getSizeOfFailureDetectionSubgroup() {
        return sizeOfFailureDetectionSubgroup;
    }

    public void setSeedValue(long seedValue) {
        Config.seedValue = seedValue;
    }

    public long getSeedValue() {
        return seedValue;
    }

    public boolean isSeedSet() {
        return fixSeed;
    }

    public void setFixSeed(boolean fixSeed) {
        Config.fixSeed = fixSeed;
    }

    public double getMessageRateInSecs()
    {
        return messageRate;
    }

    public double getAverageMessageDelayInSecs()
    {
        return 1 / messageRate;
    }

    public void setMessageRate( double messageRate )
    {
        Config.messageRate = messageRate;
    }


    @Override
    public String toString() {
        return "\n" +
                "    initial cluster size: " + initialNbrOfMembers + "\n" +
                "    timeout (ms): " + timeout + "\n" +
                "    T' (ms): " + protocolPeriod + "\n" +
                "    k: " + sizeOfFailureDetectionSubgroup + "\n" +
                "    av. message delay (ms): " + getAverageMessageDelayInSecs() * 1000.0 + "\n" +
                "    set seed: " + fixSeed + "\n" +
                "    seed value: " + seedValue;
    }
}
