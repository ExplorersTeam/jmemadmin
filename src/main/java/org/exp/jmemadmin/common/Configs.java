package org.exp.jmemadmin.common;

import org.apache.hadoop.conf.Configuration;

public class Configs {
    private static final String CONF_FILENAME = "jmemadmin-site.xml";

    private static final String REST_SERVER_PORT_KEY = "jmemadmin.rest.server.port";
    private static final int DEFAULT_REST_SERVER_PORT_VALUE = 8725;

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

    public static int getRESTfulServerPort() {
        return CONF.getInt(REST_SERVER_PORT_KEY, DEFAULT_REST_SERVER_PORT_VALUE);
    }

}
