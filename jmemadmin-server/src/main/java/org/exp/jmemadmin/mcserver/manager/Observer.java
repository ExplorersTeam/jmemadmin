package org.exp.jmemadmin.mcserver.manager;

import java.util.Map;

import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.DateUtils;
import org.exp.jmemadmin.mcserver.common.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Observer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Observer.class);

    @Override
    public void run() {
        Map<String, String> historyPoolNames = MCManager.getHistoryPoolNames();
        LOG.info("HistoryPoolNames are [" + historyPoolNames + "].");
        for (String poolNameKey : historyPoolNames.keySet()) {
            String afterTime = DateUtils.getNowTimeHM();
            String beforeTime = poolNameKey.substring(poolNameKey.lastIndexOf(Constants.BAR_DELIMITER) + 1);
            long diff = DateUtils.getSecondTimeDifference(beforeTime, afterTime);
            LOG.info("Time difference = [" + diff + "] ms.");
            if (diff > ServerConfig.getHistoryClientKeepTime()) {
                String poolName = historyPoolNames.get(poolNameKey);
                LOG.info("PoolName is [" + poolName + "].");
                MCManager.shutdownPool(poolName);
                MCManager.removeHistoryPoolNamesByKey(poolNameKey);
                // MCManager.removeHistoryClientsByKey(poolNameKey);
                // MCManager.removeHistoryPoolsByKey(poolNameKey);
                LOG.info("Shutdown poolName success.");
            }
        }
    }
}
