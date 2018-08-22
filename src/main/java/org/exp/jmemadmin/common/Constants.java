package org.exp.jmemadmin.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author ZhangQingliang
 *
 */
public class Constants {
    public static final String HTTP_SHCEME = "http";
    /**
     * Encoding.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final Charset DEFAULT_ENCODING_CHARSET = StandardCharsets.UTF_8;

    private Constants() {
        // Do nothing
    }

    /**
     * Delimiters.
     */
    public static final String SLASH_DELIMITER = "/";
    public static final String COLON_DELIMITER = ":";
    public static final String COMMAND_DELIMITER = " ";
    public static String DEFAULT_CHART = "UTF-8";

    /**
     * Rest
     */
    public static final String REST_AGENT_PATH = "/memcached/agent";
    public static final String REST_SERVER_PATH = "/memcached";
    public static final String PORT = "port";
    public static final String MEMORY_SIZE = "memorySize";
    public static final String IS_MASTER = "isMaster";
    /*
     * TODO Delete codes below.
     */
    public static final String IP = "10.142.90.152";
    public static final String SLAVE_USERNAME = "dfs";
    public static final String SLAVE_PASSWORD = "dfs123";
}
