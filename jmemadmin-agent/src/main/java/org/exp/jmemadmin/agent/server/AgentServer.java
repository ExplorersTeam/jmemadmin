package org.exp.jmemadmin.agent.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exp.jmemadmin.agent.services.MCAgentService;
import org.exp.jmemadmin.common.CommonConfigs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.HTTPUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author ZhangQingliang
 *
 */

public class AgentServer {
    private static final Log LOG = LogFactory.getLog(AgentServer.class);

    private String hostAddress = null;

    private int port = CommonConfigs.getRESTfulAGENTPort();

    private HttpServer httpServer = null;

    public AgentServer() {
        init();
    }

    public AgentServer(int port) {
        this.port = port;
        init();
    }

    private void init() {
        try {
            this.hostAddress = InetAddress.getLocalHost().getHostAddress();

            /*
             * Resource configurations.
             *
             * false stand for no recursion.
             */
            ResourceConfig config = new ResourceConfig();
            config.packages(false, MCAgentService.class.getPackage().getName());
            config.register(MoxyJsonFeature.class);
            config.register(LoggingFeature.class).property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "INFO");

            URI httpServerURI = HTTPUtils.buildURI(getHostAddress(), getPort());
            this.httpServer = GrizzlyHttpServerFactory.createHttpServer(httpServerURI, config, false, null, false);

            /*
             * Log.
             */
            String serverAddrLogStr = getHostAddress() + Constants.COLON_DELIMITER + getPort();
            LOG.info("REST server initialized, address is [" + serverAddrLogStr + "].");

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
        if (null == httpServer) {
            throw new NullPointerException("No such server.");
        }
        if (httpServer.isStarted()) {
            throw new IllegalStateException("Server is still running.");
        }
        httpServer.start();
    }

    public static void main(String[] args) {
        AgentServer server = null;
        if (0 == args.length) {
            server = new AgentServer();
        } else {
            server = new AgentServer(Integer.parseInt(args[0]));
        }

        try {
            server.start();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
