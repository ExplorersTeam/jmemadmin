package org.exp.jmemadmin.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exp.jmemadmin.common.Configs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.HostCmdUtils;
import org.exp.jmemadmin.common.utils.MemToolUtils;
import org.exp.jmemadmin.common.utils.ZKUtils;

public class MCMonitor extends AbstractMonitor {
    private static final Log LOG = LogFactory.getLog(MCMonitor.class);

    private String host;

    private Queue<String> deletedNodePorts;

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
        List<String> listPorts = new ArrayList<>();
        listPorts = MemToolUtils.listZKNodePorts(host);
        boolean portStateFlag;
        for (int i = 0; i < listPorts.size(); i++) {
            portStateFlag = false;
            try {
                portStateFlag = HostCmdUtils.checkPortBySocket(host, Integer.valueOf(listPorts.get(i)));
            } catch (NumberFormatException | UnknownHostException e) {
                LOG.error(e.getMessage(), e);
            }
            if (false == portStateFlag) {
                deletedNodePorts.offer(listPorts.get(i));
            }
        }
        return deletedNodePorts.isEmpty();
    }

    @Override
    protected void report() {
        // TODO:added kafka plugin
        if (deletedNodePorts.isEmpty()) {
            LOG.info("Memcached instances are all normal.");
        } else {
            StringBuffer nodePartialPath = new StringBuffer();
            nodePartialPath.append(MemToolUtils.unifyStartEndSlash(Configs.getZNodeRoot())).append(host).append(Constants.SLASH_DELIMITER);
            String partialPath = nodePartialPath.toString();
            for (int i = 0; i < deletedNodePorts.size(); i++) {
                String nodePath = partialPath + deletedNodePorts.poll();
                LOG.warn("Memcached instance exception, node path is [" + nodePath + "]");
                try {
                    ZKUtils.markDeletedNode(nodePath);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String[] args) {
        Runnable monitor = new MCMonitor();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(monitor, Configs.getMonitorThreadInitaldelay(), Configs.getMonitorThreadPeriod(), null);
    }
}
