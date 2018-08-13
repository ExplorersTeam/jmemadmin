package org.exp.jmemadmin.common;

import org.apache.hadoop.conf.Configuration;

/**
 * 
 * Class for configurations management.
 * 
 * @author ChenJintong
 *
 */
public class Configs {
    // Configuration file name.
    private static final String CONF_FILENAME = "jmemadmin-site.xml";

    /*
     * ZooKeeper configurations.
     */
    private static final String ZK_QUORUM_KEY = "jmemadmin.zookeeper.quorum";
    private static final String DEFAULT_INET_ADDR_VALUE = "127.0.0.1";

    private static final String ZNODE_ROOT_KEY = "jmemadmin.zookeeper.znode.root";
    private static final String DEFAULT_ZNODE_ROOT_VALUE = "/memcached";

    private static final String ZNODE_INS_PREFIX_KEY = "jmemadmin.zookeeper.znode.instance.prefix";
    private static final String DEFAULT_ZNODE_INS_PREFIX_VALUE = "mc";

    private static final String ZK_CONN_TIMEOUT_MS_KEY = "jmemadmin.zookeeper.connection.timeout.ms";
    private static final int DEFAULT_ZK_CONN_TIMEOUT_MS_VALUE = 30000;

    private static final String ZK_SESSION_TIMEOUT_MS_KEY = "jmemadmin.zookeeper.session.timeout.ms";
    private static final int DEFAULT_ZK_SESSION_TIMEOUT_MS_VALUE = 60000;

    private static final String ZK_RETRIES_MAX_KEY = "jmemadmin.zookeeper.retries.max";
    private static final int DEFAULT_ZK_RETRIES_MAX_VALUE = 3;

    private static final String ZK_RETRY_EBOP_BASE_SLEEP_TIME_MS_KEY = "jmemadmin.zookeeper.retry.exponential-back-off.sleep-time.base.ms";
    private static final int DEFAULT_ZK_RETRY_EBOP_BASE_SLEEP_TIME_MS_VALUE = 1000;

    private static final String ZK_RETRY_EBOP_MAX_SLEEP_TIME_MS_KEY = "jmemadmin.zookeeper.retry.exponential-back-off.sleep-time.max.ms";
    private static final int DEFAULT_ZK_RETRY_EBOP_MAX_SLEEP_TIME_MS_VALUE = 1000;

    /*
     * RESTful service configurations.
     */
    private static final String REST_SERVER_PORT_KEY = "jmemadmin.rest.server.port";
    private static final int DEFAULT_REST_SERVER_PORT_VALUE = 8725;

    /*
     * Monitor service configurations.
     */
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

    public static String getZNodeRoot() {
        return getConf().getTrimmed(ZNODE_ROOT_KEY, DEFAULT_ZNODE_ROOT_VALUE);
    }

    public static String getZNodeInsPrefix() {
        return getConf().getTrimmed(ZNODE_INS_PREFIX_KEY, DEFAULT_ZNODE_INS_PREFIX_VALUE);
    }

    public static int getZKConnectionTimeoutMS() {
        return getConf().getInt(ZK_CONN_TIMEOUT_MS_KEY, DEFAULT_ZK_CONN_TIMEOUT_MS_VALUE);
    }

    public static int getZKSessionTimeoutMS() {
        return getConf().getInt(ZK_SESSION_TIMEOUT_MS_KEY, DEFAULT_ZK_SESSION_TIMEOUT_MS_VALUE);
    }

    public static int getZKMaxRetries() {
        return getConf().getInt(ZK_RETRIES_MAX_KEY, DEFAULT_ZK_RETRIES_MAX_VALUE);
    }

    public static int getZKExponentialBackoffRetryBaseSleepTimeMS() {
        return getConf().getInt(ZK_RETRY_EBOP_BASE_SLEEP_TIME_MS_KEY, DEFAULT_ZK_RETRY_EBOP_BASE_SLEEP_TIME_MS_VALUE);
    }

    public static int getZKExponentialBackoffRetryMaxSleepTimeMS() {
        return getConf().getInt(ZK_RETRY_EBOP_MAX_SLEEP_TIME_MS_KEY, DEFAULT_ZK_RETRY_EBOP_MAX_SLEEP_TIME_MS_VALUE);
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
