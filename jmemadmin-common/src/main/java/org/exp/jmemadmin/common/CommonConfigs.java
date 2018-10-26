package org.exp.jmemadmin.common;

import org.apache.hadoop.conf.Configuration;

/**
 *
 * Class for configurations management.
 *
 * @author ChenJintong
 *
 */
public class CommonConfigs {
    // Configuration file name.
    private static final String CONF_FILENAME = "jmemadmin-site.xml";

    /*
     * SockIOPool Configuration Items
     */
    private static final String SOCKIOPOOL_FAILOVER_KEY = "jmemadmin.sockiopool.failover";
    private static final boolean DEFAULT_SOCKIOPOOL_FAILOVER_VALUE = true;

    private static final String SOCKIOPOOL_INIT_CONNECTIONS_KEY = "jmemadmin.sockiopool.init.connections";
    private static final int DEFAULT_SOCKIOPOOL_INIT_CONNECTIONS_VALUE = 10;

    private static final String SOCKIOPOOL_MIN_CONNECTIONS_KEY = "jmemadmin.sockiopool.min.connections";
    private static final int DEFAULT_SOCKIOPOOL_MIN_CONNECTIONS_VALUE = 5;

    private static final String SOCKIOPOOL_MAX_CONNECTIONS_KEY = "jmemadmin.sockiopool.max.connections";
    private static final int DEFAULT_SOCKIOPOOL_MAX_CONNECTIONS_VALUE = 250;

    private static final String SOCKIOPOOL_MAINT_SLEEP_KEY = "jmemadmin.sockiopool.maint.sleep";
    private static final int DEFAULT_SOCKIOPOOL_MAINT_SLEEP_VALUE = 30;

    private static final String SOCKIOPOOL_NAGLE_KEY = "jmemadmin.sockiopool.nagle";
    private static final boolean DEFAULT_SOCKIOPOOL_NAGLE_VALUE = false;

    private static final String SOCKIOPOOL_SOCKET_TIMEOUT_KEY = "jmemadmin.sockiopool.socket.timeout";
    private static final int DEFAULT_SOCKIOPOOL_SOCKET_TIMEOUT_VALUE = 3000;

    private static final String SOCKIOPOOL_ALIVE_CHECK_KEY = "jmemadmin.sockiopool.alive.check";
    private static final boolean DEFAULT_SOCKIOPOOL_ALIVE_CHECK_VALUE = true;

    private static final String SOCKIOPOOL_MAX_IDLE_KEY = "jmemadmin.sockiopool.max.idle";
    private static final int DEFAULT_SOCKIOPOOL_MAX_IDLE_VALUE = 1000 * 30 * 30;

    private static final String SOCKIOPOOL_CONNECT_TIMEOUT_KEY = "jmemadmin.sockiopool.connect.timeout";
    private static final int DEFAULT_SOCKIOPOOL_CONNECT_TIMEOUT_VALUE = 0;

    private static final String SOCKIOPOOL_GARBAGE_COLLECTION_INTERVAL_MS_KEY = "jmemadmin.sockiopool.garbage.collection.interval.ms";
    private static final int DEFAULT_SOCKIOPOOL_GARBAGE_COLLECTION_INTERVAL_MS_VALUE = 1000000;

    /*
     * ZooKeeper configurations.
     */
    private static final String ZK_QUORUM_KEY = "jmemadmin.zookeeper.quorum";
    private static final String DEFAULT_INET_ADDR_VALUE = "127.0.0.1";

    private static final String ZNODE_ROOT_KEY = "jmemadmin.zookeeper.znode.root";
    private static final String DEFAULT_ZNODE_ROOT_VALUE = "/memcached";

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

    private static final String ZK_NODE_DELETED_MARK = "jmemadmin.zookeeper.deletednode.mark";
    private static final String DEFAULT_ZK_NODE_DELETED_MARK = "-deleted";

    /*
     * Monitor service configurations.
     */
    private static final String MC_INS_PREFIX_KEY = "jmemadmin.mc.instance.prefix";
    private static final String DEFAULT_MC_INS_PREFIX_VALUE = "mc";

    private static final String MC_INS_SUFFIX_KEY = "jmemadmin.mc.instance.suffix";
    private static final String DEFAULT_MC_INS_SUFFIX_VALUE = ".pid";

    private static final String MC_INS_PID_DIR_KEY = "jmemadmin.mc.instance.pid.dir";
    private static final String DEFAULT_MC_INS_PID_DIR_VALUE = "/tmp";

    private static final String MC_HISTORY_LIFETIME_KEY = "jmemadmin.mc.history.lifetime";
    private static final int DEFAULT_MC_HISTORY_LIFETIME_VALUE = 5;

    private static final String MONITOR_MC_STAT_INTERVAL_MS_KEY = "jmemadmin.monitor.mc.status.interval.ms";
    private static final int DEFAULT_MONITOR_MC_STAT_INTERVAL_MS_VALUE = 10000;

    private static final String SOCKIOPOOL_MEMNAME_PREFIX_KEY = "jmemadmin.sockiopool.memname.prefix";
    private static final String DEFAULT_SOCKIOPOOL_MEMNAME_PREFIX_VALUE = "mem";

    /*
     * Memcached startup command configurations.
     * /opt/memcached/1.5.9/memcached-1.5.9/memcached -d -m 200 -u root -l
     * 10.142.90.152 -p 12301 -c 1000 -P /tmp/memcached12301.pid
     */
    private static final String MC_STARTUP_SHELL_PATH_KEY = "jmemadmin.mc.startup.shell.path";
    private static final String DEFAULT_MC_STARTUP_SHELL_PATH_VALUE = "/opt/memcached/1.5.9/memcached-1.5.9/memcached";

    // XXX:是否所有参数用一个变量表示更好一些？！
    private static final String MC_STARTUP_USER_KEY = "jmemadmin.mc.startup.user";
    private static final String DEFAULT_MC_STARTUP_USER_VALUE = "root";
    // XXX:是否所有参数用一个变量表示更好一些？！
    private static final String MC_STARTUP_MAX_SIMULTANEOUS_CONNECTIONS_KEY = "jmemadmin.mc.startup.max.simultaneous.connections";
    private static final int DEFAULT_MC_STARTUP_MAX_SIMULTANEOUS_CONNECTIONS_VALUE = 1024;

    private static final String MC_STARTUP_CONFIGURABLE_PARAMETERS_KEY = "jmemadmin.mc.startup.configurable.parameters";
    private static final String DEFAULT_MC_STARTUP_CONFIGURABLE_PARAMETERS_VALUE = " -d -u root -c 1000 ";

    /*
     * RESTful service configurations.
     */
    private static final String REST_AGENT_PORT_KEY = "jmemadmin.rest.agent.port";
    private static final int DEFAULT_REST_AGENT_PORT_VALUE = 8726;

    private static final Configuration CONF = new Configuration();

    static {
        CONF.addResource(CONF_FILENAME);
    }

    private CommonConfigs() {
        // Do nothing.
    }

    public static Configuration getConf() {
        return CONF;
    }

    public static boolean getPoolFailover() {
        return getConf().getBoolean(SOCKIOPOOL_FAILOVER_KEY, DEFAULT_SOCKIOPOOL_FAILOVER_VALUE);
    }

    public static int getPoolInitConns() {
        return getConf().getInt(SOCKIOPOOL_INIT_CONNECTIONS_KEY, DEFAULT_SOCKIOPOOL_INIT_CONNECTIONS_VALUE);
    }

    public static int getPoolMinConns() {
        return getConf().getInt(SOCKIOPOOL_MIN_CONNECTIONS_KEY, DEFAULT_SOCKIOPOOL_MIN_CONNECTIONS_VALUE);
    }

    public static int getPoolMaxConns() {
        return getConf().getInt(SOCKIOPOOL_MAX_CONNECTIONS_KEY, DEFAULT_SOCKIOPOOL_MAX_CONNECTIONS_VALUE);
    }

    public static int getPoolMaintSleep() {
        return getConf().getInt(SOCKIOPOOL_MAINT_SLEEP_KEY, DEFAULT_SOCKIOPOOL_MAINT_SLEEP_VALUE);
    }

    public static boolean getPoolNagle() {
        return getConf().getBoolean(SOCKIOPOOL_NAGLE_KEY, DEFAULT_SOCKIOPOOL_NAGLE_VALUE);
    }

    public static int getPoolSocketTimeout() {
        return getConf().getInt(SOCKIOPOOL_SOCKET_TIMEOUT_KEY, DEFAULT_SOCKIOPOOL_SOCKET_TIMEOUT_VALUE);
    }

    public static boolean getPoolAliveCheck() {
        return getConf().getBoolean(SOCKIOPOOL_ALIVE_CHECK_KEY, DEFAULT_SOCKIOPOOL_ALIVE_CHECK_VALUE);
    }

    public static int getPoolMaxIdle() {
        return getConf().getInt(SOCKIOPOOL_MAX_IDLE_KEY, DEFAULT_SOCKIOPOOL_MAX_IDLE_VALUE);
    }

    public static int getPoolConnectTimeout() {
        return getConf().getInt(SOCKIOPOOL_CONNECT_TIMEOUT_KEY, DEFAULT_SOCKIOPOOL_CONNECT_TIMEOUT_VALUE);
    }

    public static int getPoolGCIntervalMs() {
        return getConf().getInt(SOCKIOPOOL_GARBAGE_COLLECTION_INTERVAL_MS_KEY, DEFAULT_SOCKIOPOOL_GARBAGE_COLLECTION_INTERVAL_MS_VALUE);
    }

    public static String getZKQuorum() {
        return getConf().getTrimmed(ZK_QUORUM_KEY, DEFAULT_INET_ADDR_VALUE);
    }

    public static String getZNodeRoot() {
        return getConf().getTrimmed(ZNODE_ROOT_KEY, DEFAULT_ZNODE_ROOT_VALUE);
    }

    public static String getInsPrefix() {
        return getConf().getTrimmed(MC_INS_PREFIX_KEY, DEFAULT_MC_INS_PREFIX_VALUE);
    }

    public static String getInsSuffix() {
        return getConf().getTrimmed(MC_INS_SUFFIX_KEY, DEFAULT_MC_INS_SUFFIX_VALUE);
    }

    public static String getInsPIDDir() {
        return getConf().getTrimmed(MC_INS_PID_DIR_KEY, DEFAULT_MC_INS_PID_DIR_VALUE);
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

    public static String getZKDeletedNodeMark() {
        return getConf().getTrimmed(ZK_NODE_DELETED_MARK, DEFAULT_ZK_NODE_DELETED_MARK);
    }

    public static int getMCHistoryLifetime() {
        return getConf().getInt(MC_HISTORY_LIFETIME_KEY, DEFAULT_MC_HISTORY_LIFETIME_VALUE);
    }

    public static int getMCStatMonitorIntervalMS() {
        return getConf().getInt(MONITOR_MC_STAT_INTERVAL_MS_KEY, DEFAULT_MONITOR_MC_STAT_INTERVAL_MS_VALUE);
    }

    public static String getPoolMemnamePrefix() {
        return getConf().getTrimmed(SOCKIOPOOL_MEMNAME_PREFIX_KEY, DEFAULT_SOCKIOPOOL_MEMNAME_PREFIX_VALUE);
    }

    public static String getMCStartupShellPath() {
        return getConf().getTrimmed(MC_STARTUP_SHELL_PATH_KEY, DEFAULT_MC_STARTUP_SHELL_PATH_VALUE);
    }

    public static String getMCStartupUser() {
        return getConf().getTrimmed(MC_STARTUP_USER_KEY, DEFAULT_MC_STARTUP_USER_VALUE);
    }

    public static int getMCStartupMaxSimultaneousConns() {
        return getConf().getInt(MC_STARTUP_MAX_SIMULTANEOUS_CONNECTIONS_KEY, DEFAULT_MC_STARTUP_MAX_SIMULTANEOUS_CONNECTIONS_VALUE);
    }

    public static String getMCStartupConfigurableParams() {
        return getConf().getTrimmed(MC_STARTUP_CONFIGURABLE_PARAMETERS_KEY, DEFAULT_MC_STARTUP_CONFIGURABLE_PARAMETERS_VALUE);
    }

    public static int getRESTfulAGENTPort() {
        return getConf().getInt(REST_AGENT_PORT_KEY, DEFAULT_REST_AGENT_PORT_VALUE);
    }

}
