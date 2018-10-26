package org.exp.jmemadmin.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exp.jmemadmin.common.CommonConfigs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.HostCmdUtils;
import org.exp.jmemadmin.common.utils.MCManager;
import org.exp.jmemadmin.common.utils.MCToolUtils;
import org.exp.jmemadmin.common.utils.ZKUtils;
import org.exp.jmemadmin.monitor.common.MonitorConfig;

import com.whalin.MemCached.SockIOPool;

public class MCMonitor extends AbstractMonitor {
    private static final Log LOG = LogFactory.getLog(MCMonitor.class);

    private String host;

    private List<String> deletedNodePorts = new ArrayList<>();

    private Map<String, SockIOPool> historyPools = new ConcurrentHashMap<>();

    public MCMonitor() {
        init();
    }

    private void init() {
        try {
            this.host = InetAddress.getLocalHost().getHostAddress();
            this.historyPools = MCManager.getHistoryPools();
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
        listPorts = MCToolUtils.listZKNodePorts(host);
        boolean isPortUsingflag;
        for (int i = 0; i < listPorts.size(); i++) {
            if (!listPorts.get(i).matches("[0-9]*")) {
                continue;
            }
            isPortUsingflag = true;
            isPortUsingflag = HostCmdUtils.isPortUsing(host, Integer.parseInt(listPorts.get(i)));
            LOG.info("Monitor port : [" + listPorts.get(i) + "]; isPortUsingflag : [" + isPortUsingflag + "].");
            if (false == isPortUsingflag) {
                deletedNodePorts.add(listPorts.get(i));
            }
        }
        return deletedNodePorts.isEmpty();
    }

    @Override
    protected void report() {
        String poolName = null;
        String port = null;
        // TODO:added kafka plugin
        if (deletedNodePorts.isEmpty()) {
            LOG.info("Memcached instances are all normal.");
        } else {
            LOG.info("Abnormal memcached instance ports : [" + deletedNodePorts.toString() + "].");
            StringBuffer nodePartialPath = new StringBuffer();
            nodePartialPath.append(MCToolUtils.unifyStartEndSlash(CommonConfigs.getZNodeRoot())).append(host).append(Constants.SLASH_DELIMITER);
            String partialPath = nodePartialPath.toString();
            for (int i = 0; i < deletedNodePorts.size(); i++) {
                port = deletedNodePorts.get(i);
                LOG.info("Wait to delete port [" + port + "].");
                // TODO:adjust pool name
                poolName = CommonConfigs.getPoolMemnamePrefix() + host + Constants.COLON_DELIMITER + port;
                LOG.info("PoolName is [" + poolName + "].");
                // TODO:wait to debug 2 methods.
                // SockIOPool.getInstance(poolName).shutDown();
                if (historyPools.containsKey(poolName)) {
                    historyPools.get(poolName).shutDown();
                }
                String nodePath = partialPath + port;
                try {
                    ZKUtils.markDeletedNode(nodePath);
                    deletedNodePorts.remove(i);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String[] args) {
        Runnable monitor = new MCMonitor();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(monitor, MonitorConfig.getMonitorThreadInitaldelay(), MonitorConfig.getMonitorThreadPeriod(), TimeUnit.MILLISECONDS);
    }
}
