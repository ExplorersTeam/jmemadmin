package org.exp.jmemadmin.mcserver.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.HTTPUtils;
import org.exp.jmemadmin.mcserver.common.ServerConfig;
import org.exp.jmemadmin.mcserver.manager.Observer;
import org.exp.jmemadmin.mcserver.services.MCServerService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemServer {
    private static final Logger LOG = LoggerFactory.getLogger(MemServer.class);

    private String hostAddress = null;
    private int port = ServerConfig.getRESTfulServerPort();
    private HttpServer server = null;

    public MemServer() {
        init();
    }

    public MemServer(int port) {
        this.port = port;
        init();
    }

    private void init() {
        try {
            this.hostAddress = InetAddress.getLocalHost().getHostAddress();
            ResourceConfig config = new ResourceConfig();
            config.packages(false, MCServerService.class.getPackage().getName());
            config.register(MoxyJsonFeature.class);
            config.register(LoggingFeature.class).property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "INFO");

            URI httpServerURI = HTTPUtils.buildURI(getHostAddress(), getPort());
            this.server = GrizzlyHttpServerFactory.createHttpServer(httpServerURI, config, false, null, false);

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
        if (null == server) {
            throw new NullPointerException("No such server.");
        }
        if (server.isStarted()) {
            throw new IllegalStateException("Server is still running.");
        }
        server.start();
    }

    public static void main(String[] args) {
        MemServer memServer = (0 == args.length) ? new MemServer() : new MemServer(Integer.parseInt(args[0]));
        try {
            memServer.start();
            Runnable observer = new Observer();
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(observer, ServerConfig.getObserverThreadInitaldelay(), ServerConfig.getObserverThreadPeriod(), TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
