package org.exp.jmemadmin.common;

import org.apache.hadoop.conf.Configuration;

public class Configs {
    private static final String CONF_FILENAME = "jmemadmin-site.xml";

    private static final String ZK_QUORUM_KEY = "jmemadmin.zookeeper.quorum";
    private static final String DEFAULT_INET_ADDR_VALUE = "127.0.0.1";

    private static final String REST_SERVER_PORT_KEY = "jmemadmin.rest.server.port";
    private static final int DEFAULT_REST_SERVER_PORT_VALUE = 8725;

    private static final String MC_HISTORY_LIFETIME_KEY = "jmemadmin.mc.history.lifetime";
    private static final int DEFAULT_MC_HISTORY_LIFETIME_VALUE = 5;

    private static final String MONITOR_MC_STAT_INTERVAL_MS_KEY = "jmemadmin.monitor.mc.status.interval.ms";
    private static final int DEFAULT_MONITOR_MC_STAT_INTERVAL_MS_VALUE = 10000;

    private static final Configuration CONF = new Configuration();

    static {
        CONF.addResource(CONF_FILENAME);
    }

    private Configs() {
        // Do nothing.
    }

    public static Configuration getConf() {
        return CONF;
    }

    public static String getZKQuorum() {
        return getConf().getTrimmed(ZK_QUORUM_KEY, DEFAULT_INET_ADDR_VALUE);
    }

    public static int getRESTfulServerPort() {
        return getConf().getInt(REST_SERVER_PORT_KEY, DEFAULT_REST_SERVER_PORT_VALUE);
    }

    public static int getMCHistoryLifetime() {
        return getConf().getInt(MC_HISTORY_LIFETIME_KEY, DEFAULT_MC_HISTORY_LIFETIME_VALUE);
    }

    public static int getMCStatMonitorIntervalMS() {
        return getConf().getInt(MONITOR_MC_STAT_INTERVAL_MS_KEY, DEFAULT_MONITOR_MC_STAT_INTERVAL_MS_VALUE);
    }

}
