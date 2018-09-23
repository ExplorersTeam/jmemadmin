package org.exp.jmemadmin.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MCMonitor extends AbstractMonitor {
    private static final Log LOG = LogFactory.getLog(MCMonitor.class);

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
    protected boolean check() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void report() {
        // TODO Auto-generated method stub

    }

    public static void main(String[] args) {
        Runnable monitor = new MCMonitor();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(monitor, 0, 0, null);// TODO Complete it.
    }

}
