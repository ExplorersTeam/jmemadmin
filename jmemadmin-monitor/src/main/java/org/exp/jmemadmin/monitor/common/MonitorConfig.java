package org.exp.jmemadmin.monitor.common;

import org.apache.hadoop.conf.Configuration;

public class MonitorConfig {
    /**
     * Configuration file name.
     */
    private static final String CONF_FILENAME = "jmemadmin-monitor.xml";

    private static final Configuration CONF = new Configuration();

    static {
        CONF.addResource(CONF_FILENAME);
    }

    private MonitorConfig() {
        // DO nothing
    }

    public static Configuration getConf() {
        return CONF;
    }

    /*
     * Monitor service configurations.
     */
    private static final String THREAD_INITIAL_DELAY = "jmemadmin.monitor.thread.initaldelay.ms";
    private static final long DEFAULT_THREAD_INITIAL_DELAY = 0;

    private static final String THREAD_PERIOD = "jmemadmin.monitor.thread.period.ms";
    private static final long DEFAULT_THREAD_PERIOD = 5000;

    public static long getMonitorThreadInitaldelay() {
        return getConf().getLong(THREAD_INITIAL_DELAY, DEFAULT_THREAD_INITIAL_DELAY);
    }

    public static long getMonitorThreadPeriod() {
        return getConf().getLong(THREAD_PERIOD, DEFAULT_THREAD_PERIOD);
    }
}
