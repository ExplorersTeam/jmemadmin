package org.exp.jmemadmin.mcserver.manager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.exp.jmemadmin.common.CommonConfigs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.DateUtils;
import org.exp.jmemadmin.common.utils.HTTPUtils;
import org.exp.jmemadmin.entity.MemInstance;
import org.exp.jmemadmin.entity.RequestBody;
import org.exp.jmemadmin.entity.Response;
import org.exp.jmemadmin.mcserver.common.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MCManager {
    private static final Logger LOG = LoggerFactory.getLogger(MCManager.class);

    private static Map<String, MemCachedClient> historyClients = new ConcurrentHashMap<>();// 暂时未起作用
    private static Map<String, SockIOPool> historyPools = new ConcurrentHashMap<>();// 暂时未起作用
    private static Map<String, String> historyPoolNames = new ConcurrentHashMap<>();
    private static List<String> serversList = new ArrayList<>();
    private static MemCachedClient activeClient = null;
    private static String tenant = "memcached";
    private static String activePoolName = null;

    private MCManager() {
        // DO nothing
    }

    public static MemCachedClient getClient() {
        return activeClient;
    }

    public static Map<String, SockIOPool> getHistoryPools() {
        return historyPools;
    }

    public static Map<String, String> getHistoryPoolNames() {
        return historyPoolNames;
    }

    public static void removeHistoryPoolNamesByKey(String poolNameKey) {
        historyPoolNames.remove(poolNameKey);
    }

    public static void removeHistoryClientsByKey(String poolNameKey) {
        historyClients.remove(poolNameKey);
    }

    public static void removeHistoryPoolsByKey(String poolNameKey) {
        historyPools.remove(historyPoolNames.get(poolNameKey));
    }

    public static void shutdownPool(String poolName) {
        // historyPools.get(poolName).shutDown();
        SockIOPool.getInstance(poolName).shutDown();
    }

    public static MemCachedClient createMCClient(String poolName, String[] servers) {
        SockIOPool pool = SockIOPool.getInstance(poolName);
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
        LOG.info("Pool initialize success.");
        historyPools.put(poolName, pool);
        MemCachedClient activeClient = new MemCachedClient(poolName);
        return activeClient;
    }

    public static Response executeRequest(MemInstance instance, String requestPath)
            throws URISyntaxException, ParseException, ClientProtocolException, IOException {
        String host = instance.getHost();
        int instancePort = instance.getPort();
        int agentServicePort = CommonConfigs.getRESTfulAGENTPort();
        RequestBody requestBody = new RequestBody();
        requestBody.setHost(host);
        requestBody.setPort(instancePort);
        requestBody.setMem(instance.getMemSize());
        requestBody.setMaster(instance.isMaster());

        LOG.info("host:[" + host + "];port:[" + instancePort + "]");
        String body = JSONObject.toJSONString(requestBody);
        LOG.info("Request body is [" + body + "].");

        URI uri = HTTPUtils.buildURI(host, agentServicePort, requestPath);
        Response response = HTTPUtils.sendPOSTRequest(uri, body);
        LOG.info("Response is [" + response.toString() + "].");
        return response;
    }

    public static Response startMemInstance(MemInstance instance) {
        StringBuffer startInstancePath = new StringBuffer();
        startInstancePath.append(Constants.REST_AGENT_ROOT_PATH).append(Constants.REST_AGENT_START_SUBPATH);
        Response response = null;
        try {
            response = executeRequest(instance, startInstancePath.toString());
            LOG.info("Response of normal startMemInstance is [" + response + "].");
        } catch (Exception e) {
            LOG.info("Response of Exception startMemInstance is [" + response + "].");
            LOG.error(e.getMessage(), e);
        }
        if (response.getCode() == Response.ResultStatus.SUCCESS.value()) {
            String server = instance.getHost() + Constants.COLON_DELIMITER + String.valueOf(instance.getPort());
            serversList.add(server);
            String poolName = tenant + Constants.BAR_DELIMITER + DateUtils.getNowTimeHM();
            LOG.info("server is [" + server + "]; poolname is [" + poolName + "]");
            if (null == activeClient) {
                activeClient = createMCClient(poolName, serversList.toArray(new String[serversList.size()]));
                activePoolName = poolName;
            } else {
                String poolNameKey = activePoolName + Constants.BAR_DELIMITER + DateUtils.getNowTimeHM();
                historyClients.put(poolNameKey, activeClient);
                historyPoolNames.put(poolNameKey, activePoolName);
                activeClient = createMCClient(poolName, serversList.toArray(new String[serversList.size()]));
                activePoolName = poolName;
            }
            LOG.info("historyPoolNames is [" + historyPoolNames.toString() + "].");
        }
        return response;
    }

    public static Response stopMemInstance(MemInstance instance) {
        StringBuffer stopInstancePath = new StringBuffer();
        stopInstancePath.append(Constants.REST_AGENT_ROOT_PATH).append(Constants.REST_AGENT_STOP_SUBPATH);
        Response response = null;
        // TODO:Solve bug about stop instance when serversList is null.
        try {
            response = executeRequest(instance, stopInstancePath.toString());
            LOG.info("Response of normal stopMemInstance is [" + response + "].");
            String server = instance.getHost() + Constants.COLON_DELIMITER + String.valueOf(instance.getPort());
            serversList.remove(server);
            String poolName = tenant + Constants.BAR_DELIMITER + DateUtils.getNowTimeHM();
            LOG.info("server is [" + server + "]; poolname is [" + poolName + "]");
            if (null == activeClient) {
                activeClient = createMCClient(poolName, serversList.toArray(new String[serversList.size()]));
            } else {
                String poolNameKey = poolName + Constants.BAR_DELIMITER + DateUtils.getNowTimeHM();
                historyClients.put(poolNameKey, activeClient);
                historyPoolNames.put(poolNameKey, poolName);
                activeClient = createMCClient(poolName, serversList.toArray(new String[serversList.size()]));
            }
            LOG.info("historyPoolNames is [" + historyPoolNames.toString() + "].");
        } catch (Exception e) {
            LOG.info("Response of Exception stopMemInstance is [" + response + "].");
            LOG.error(e.getMessage(), e);
        }
        return response;
    }

}
