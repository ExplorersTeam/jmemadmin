package org.exp.jmemadmin.mcserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.exp.jmemadmin.common.CommonConfigs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.HostCmdUtils;
import org.exp.jmemadmin.common.utils.MCToolUtils;
import org.exp.jmemadmin.common.utils.ZKUtils;
import org.exp.jmemadmin.entity.ZKNodeInfo;
import org.exp.jmemadmin.mcserver.common.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

//TODO:to be deleted
public class MemCachedManager {
    private static final Logger LOG = LoggerFactory.getLogger(MemCachedManager.class);

    private static final AtomicInteger ID = new AtomicInteger(-1);
    private static MemCachedClient activeClient = null;
    private static Map<String, MemCachedClient> historyClients = new ConcurrentHashMap<>();
    private static SockIOPool activePool = null;
    private static Map<String, SockIOPool> historyPools = new ConcurrentHashMap<>();
    private static List<String> serversList = new ArrayList<>();

    public static MemCachedClient getActiveClient() {
        return activeClient;
    }

    private static MemCachedClient initSocketIOPool(SockIOPool pool, String poolName, String[] servers) {
        pool.setServers(servers); // 设置memcached服务器地址
        // pool.setWeights(weights); //设置每个memcached服务器权重
        pool.setFailover(ServerConfig.getPoolFailover()); // 当一个memcached服务器失效的时候是否去连接另一个memcached服务器.
        pool.setInitConn(ServerConfig.getPoolInitConns()); // 初始化时对每个服务器建立的连接数目
        pool.setMinConn(ServerConfig.getPoolMinConns()); // 每个服务器建立最小的连接数
        pool.setMaxConn(ServerConfig.getPoolMaxConns()); // 每个服务器建立最大的连接数
        pool.setMaintSleep(ServerConfig.getPoolMaintSleep()); // 自查线程周期进行工作，其每次休眠时间
        pool.setNagle(ServerConfig.getPoolNagle()); // Socket的参数，如果是true，在写数据时不缓冲，立即发送出去。Tcp的规则是在发送一个包之前，包的发送方会等待远程接收方确认已收到上一次发送过来的包；这个方法就可以关闭套接字的缓存——包准备立即发出。
        pool.setSocketTO(ServerConfig.getPoolSocketTimeout()); // Socket阻塞读取数据的超时时间
        pool.setAliveCheck(ServerConfig.getPoolAliveCheck()); // 设置是否检查memcached服务器是否失效
        pool.setMaxIdle(ServerConfig.getPoolMaxIdle()); // 设置最大处理时间
        pool.setSocketConnectTO(ServerConfig.getPoolConnectTimeout()); // 连接建立时对超时的控制
        pool.setMaintSleep(ServerConfig.getPoolMaintSleep()); // 设置主线程睡眠时间，每30秒苏醒一次，维持连接池大小
        pool.initialize(); // 初始化连接池
        LOG.info("****************初始化连接池成功*******************");
        MemCachedClient activeName = new MemCachedClient(poolName);
        return activeName;
    }

    private static void initActiveMemcached(String[] servers) {
        String activePoolName = CommonConfigs.getPoolMemnamePrefix() + ID.incrementAndGet();
        activePool = SockIOPool.getInstance(activePoolName);
        activeClient = initSocketIOPool(activePool, activePoolName, servers);
    }

    public static boolean start(String host, int port, int memorySize, boolean isMaster) throws Exception {
        boolean flag = false;
        try {
            flag = HostCmdUtils.isPortUsing(host, port);
            if (!flag) {// 端口未被占时
                serversList.add(host + Constants.COLON_DELIMITER + String.valueOf(port));
                String startupCmd = MCToolUtils.composeStartupCmd(host, port, memorySize);
                HostCmdUtils.executeLocalCmd(startupCmd, null);
                String readPidCmd = MCToolUtils.composeReadPidFileCmd(port);
                String pid = HostCmdUtils.executeLocalCmd(readPidCmd, null);
                String removePidCmd = MCToolUtils.composeRemovePidFileCmd(port);
                HostCmdUtils.executeLocalCmd(removePidCmd, null);
                if (null == activePool) {
                    initActiveMemcached((String[]) serversList.toArray());
                } else {
                    String key = CommonConfigs.getPoolMemnamePrefix() + ID.get();
                    historyPools.put(key, activePool);
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {// 创建一个新的计时器任务
                        @Override
                        public void run() {
                            historyPools.remove(key);
                            System.out.println(historyPools);
                        }
                    };
                    timer.schedule(task, 100000);// 在指定延迟后执行指定的任务。task是所要安排的任务。10000是执行任务前的延迟时间，单位是毫秒。
                    initActiveMemcached((String[]) serversList.toArray());
                }
                String nodePath = MCToolUtils.composeNodePath(host, port);
                ZKNodeInfo zkNodeInfo = new ZKNodeInfo(startupCmd, Integer.valueOf(pid), isMaster);
                byte[] data = JSON.toJSONString(zkNodeInfo).getBytes();
                ZKUtils.create(nodePath, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !HostCmdUtils.isPortUsing(host, port);// 再次通过检测端口来判断是否启动成功
    }

    public static boolean stop(String host, int port) {
        boolean flag = false;
        serversList.remove(host + ":" + String.valueOf(port));
        StringBuffer memNodePath = new StringBuffer();
        memNodePath.append(CommonConfigs.getZNodeRoot()).append(Constants.SLASH_DELIMITER).append(host).append(Constants.SLASH_DELIMITER).append(port);
        byte[] data = null;
        try {
            data = ZKUtils.get(memNodePath.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        ZKNodeInfo zkNodeInfo = JSONObject.parseObject(data, ZKNodeInfo.class);
        int pid = zkNodeInfo.getInstancePid();
        try {
            ZKUtils.delete(memNodePath.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } // 删除节点
        String killPidCmd = "kill " + pid;
        String grepPidCmd = "ps -ef|grep -v grep|grep " + pid;
        do {
            HostCmdUtils.executeLocalCmd(killPidCmd);
        } while (!(flag = HostCmdUtils.executeLocalCmd(grepPidCmd).isEmpty()));
        return flag;
    }

}
