package org.exp.jmemadmin.mcserver.common;

import org.apache.hadoop.conf.Configuration;

public class ServerConfig {
    /**
     * Configuration file name.
     */
    private static final String CONF_FILENAME = "jmemadmin-server.xml";
    private static final Configuration CONF = new Configuration();

    static {
        CONF.addResource(CONF_FILENAME);
    }

    private ServerConfig() {
        // Do nothing
    }

    public static Configuration getConf() {
        return CONF;
    }

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

    private static final String SOCKIOPOOL_MEMNAME_PREFIX_KEY = "jmemadmin.sockiopool.memname.prefix";
    private static final String DEFAULT_SOCKIOPOOL_MEMNAME_PREFIX_VALUE = "mem";

    private static final String SOCKIOPOOL_GARBAGE_COLLECTION_INTERVAL_MS_KEY = "jmemadmin.sockiopool.garbage.collection.interval.ms";
    private static final int DEFAULT_SOCKIOPOOL_GARBAGE_COLLECTION_INTERVAL_MS_VALUE = 1000000;

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

    public static String getPoolMemnamePrefix() {
        return getConf().getTrimmed(SOCKIOPOOL_MEMNAME_PREFIX_KEY, DEFAULT_SOCKIOPOOL_MEMNAME_PREFIX_VALUE);
    }

    public static int getPoolGCIntervalMs() {
        return getConf().getInt(SOCKIOPOOL_GARBAGE_COLLECTION_INTERVAL_MS_KEY, DEFAULT_SOCKIOPOOL_GARBAGE_COLLECTION_INTERVAL_MS_VALUE);
    }
}
