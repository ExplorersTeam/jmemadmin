package org.exp.jmemadmin.agent.common;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AgentConfig.class);

    /**
     * Configuration file name.
     */
    private static final String CONF_FILENAME = "jmemadmin-agent.xml";

    private static final Configuration CONF = new Configuration();

    static {
        CONF.addResource(CONF_FILENAME);
    }

    private AgentConfig() {
        // Do nothing
    }

    private static Configuration getConf() {
        return CONF;
    }

    /*
     * RESTful service configurations.
     */
    private static final String REST_AGENT_PORT_KEY = "jmemadmin.rest.agent.port";
    private static final int DEFAULT_REST_AGENT_PORT_VALUE = 8726;

    public static int getRESTfulAGENTPort() {
        return getConf().getInt(REST_AGENT_PORT_KEY, DEFAULT_REST_AGENT_PORT_VALUE);
    }

}
