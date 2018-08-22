package org.exp.jmemadmin.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author ZhangQingliang
 *
 */
public class Constants {
    /**
     * Encoding.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final Charset DEFAULT_ENCODING_CHARSET = StandardCharsets.UTF_8;

    /**
     * Delimiters.
     */
    // public static final String SLASH_DELIMITER = "/";

    private Constants() {
        // Do nothing
    }

    /*
     * TODO Delete codes below.
     */
    public static final String IP = "10.142.90.152";
    public static final String SLAVE_USERNAME = "dfs";
    public static final String SLAVE_PASSWORD = "dfs123";

    public static final String PATH_DELIMITER = "/";
    public static final String HOST_PORT_DELIMITER = ":";
    public static final String COMMAND_DELIMITER = " ";
    public static String DEFAULT_CHART = "UTF-8";

}
