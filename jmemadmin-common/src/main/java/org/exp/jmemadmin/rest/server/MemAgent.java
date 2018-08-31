package org.exp.jmemadmin.rest.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.exp.jmemadmin.common.Configs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.HTTPUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemAgent {
    private static final Logger LOG = LoggerFactory.getLogger(MemAgent.class);

    private String hostAddress = null;
    private int port = Configs.getRESTfulAGENTPort();
    private HttpServer server = null;

    public MemAgent() {
        init();
    }

    public MemAgent(int port) {
        this.port = port;
        init();
    }

    private void init() {
        try {
            this.hostAddress = InetAddress.getLocalHost().getHostAddress();
            ResourceConfig config = new ResourceConfig();
            config.packages(false, "org.exp.jmemadmin.rest.services");//false stand for no recursion.
            config.register(MoxyJsonFeature.class);
            config.register(LoggingFeature.class).property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "INFO");
            this.server = GrizzlyHttpServerFactory.createHttpServer(HTTPUtils.buildURI(getHostAddress(), getPort()), config, false, null, false);
            LOG.info("REST server initialized, address is [" + getHostAddress() + Constants.COLON_DELIMITER + getPort() + "].");
        } catch (UnknownHostException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public int getPort() {
        return port;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    /**
     * Start server.
     *
     * @throws IOException
     */
    public void start() throws IOException {
        if (null == server) {
            throw new NullPointerException("No such server.");
        }
        if (server.isStarted()) {
            throw new IllegalStateException("Server is still running.");
        }
        server.start();
    }

    public static void main(String[] args) {
        MemAgent memAgent = 0 == args.length ? new MemAgent() : new MemAgent(Integer.parseInt(args[0]));
        try {
            memAgent.start();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
