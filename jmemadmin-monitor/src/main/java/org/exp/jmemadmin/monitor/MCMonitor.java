package org.exp.jmemadmin.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MCMonitor implements Monitor {
    private static final Log LOG = LogFactory.getLog(MCMonitor.class);

    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private String host;

    public MCMonitor() {
        init();
    }

    private void init() {
        try {
            this.host = InetAddress.getLocalHost().getHostAddress();
            LOG.info("Monitor service initialized, address is [" + getHost() + "].");
        } catch (UnknownHostException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public String getHost() {
        return host;
    }

    @Override
    public void start() {
        service.scheduleAtFixedRate(() -> {
            // TODO Auto-generated method stub
        }, 0, 0, null);
    }

}
