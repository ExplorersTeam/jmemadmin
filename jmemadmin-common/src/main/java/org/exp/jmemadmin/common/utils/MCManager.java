package org.exp.jmemadmin.common.utils;

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
import org.exp.jmemadmin.entity.MemInstance;
import org.exp.jmemadmin.entity.RequestBody;
import org.exp.jmemadmin.entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MCManager {
    private static final Logger LOG = LoggerFactory.getLogger(MCManager.class);

    private static Map<String, String> historyPoolNames = new ConcurrentHashMap<>();
    private static Map<String, MemCachedClient> historyClients = new ConcurrentHashMap<>();
    private static Map<String, SockIOPool> historyPools = new ConcurrentHashMap<>();

    private MCManager() {
        // DO nothing
    }

    public static String getPoolName(String host, int port) {
        String key = host + Constants.COLON_DELIMITER + String.valueOf(port);
        String poolName = historyPoolNames.get(key);
        return poolName;
    }

    public static MemCachedClient getClient(String poolName) {
        // MemCachedClient client = new MemCachedClient(poolName);
        MemCachedClient client = historyClients.get(poolName);
        return client;
    }

    public static Map<String, SockIOPool> getHistoryPools() {
        return historyPools;
    }

    public static MemCachedClient createMCClient(String poolName, String[] servers) {
        SockIOPool pool = SockIOPool.getInstance(poolName);
        pool.setServers(servers); // 设置memcached服务器地址
        // pool.setWeights(weights); //设置每个memcached服务器权重
        pool.setFailover(CommonConfigs.getPoolFailover()); // 当一个memcached服务器失效的时候是否去连接另一个memcached服务器.
        pool.setInitConn(CommonConfigs.getPoolInitConns()); // 初始化时对每个服务器建立的连接数目
        pool.setMinConn(CommonConfigs.getPoolMinConns()); // 每个服务器建立最小的连接数
        pool.setMaxConn(CommonConfigs.getPoolMaxConns()); // 每个服务器建立最大的连接数
        pool.setMaintSleep(CommonConfigs.getPoolMaintSleep()); // 自查线程周期进行工作，其每次休眠时间
        pool.setNagle(CommonConfigs.getPoolNagle()); // Socket的参数，如果是true，在写数据时不缓冲，立即发送出去。Tcp的规则是在发送一个包之前，包的发送方会等待远程接收方确认已收到上一次发送过来的包；这个方法就可以关闭套接字的缓存——包准备立即发出。
        pool.setSocketTO(CommonConfigs.getPoolSocketTimeout()); // Socket阻塞读取数据的超时时间
        pool.setAliveCheck(CommonConfigs.getPoolAliveCheck()); // 设置是否检查memcached服务器是否失效
        pool.setMaxIdle(CommonConfigs.getPoolMaxIdle()); // 设置最大处理时间
        pool.setSocketConnectTO(CommonConfigs.getPoolConnectTimeout()); // 连接建立时对超时的控制
        pool.setMaintSleep(CommonConfigs.getPoolMaintSleep()); // 设置主线程睡眠时间，每30秒苏醒一次，维持连接池大小
        pool.initialize(); // 初始化连接池
        LOG.info("Pool initialize success.");
        historyPools.put(poolName, pool);
        MemCachedClient activeClient = new MemCachedClient(poolName);
        return activeClient;
    }

    public static void shutdownPool(String poolName) {
        SockIOPool.getInstance(poolName).shutDown();
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
            String serverKey = instance.getHost() + Constants.COLON_DELIMITER + String.valueOf(instance.getPort());
            List<String> serversList = new ArrayList<String>();
            serversList.add(serverKey);
            // TODO:adjust pool name
            String poolName = CommonConfigs.getPoolMemnamePrefix() + serverKey;
            historyPoolNames.put(serverKey, poolName);
            LOG.info("serverKey is [" + serverKey + "]; poolname is [" + poolName + "]; historyPoolNames is [" + historyPoolNames + "].");
            MemCachedClient client = createMCClient(poolName, serversList.toArray(new String[serversList.size()]));
            historyClients.put(poolName, client);
            LOG.info("historyClients is [" + historyClients.toString() + "].");
        } catch (ParseException | URISyntaxException | IOException e) {
            LOG.info("Response of Exception startMemInstance is [" + response + "].");
            LOG.error(e.getMessage(), e);
        }
        return response;
    }

    public static Response stopMemInstance(MemInstance instance) {
        StringBuffer stopInstancePath = new StringBuffer();
        stopInstancePath.append(Constants.REST_AGENT_ROOT_PATH).append(Constants.REST_AGENT_STOP_SUBPATH);
        Response response = null;
        try {
            response = executeRequest(instance, stopInstancePath.toString());
            LOG.info("Response of normal stopMemInstance is [" + response + "].");
            String serverKey = instance.getHost() + Constants.COLON_DELIMITER + String.valueOf(instance.getPort());
            LOG.info("serverKey is [" + serverKey + "]; historyPoolNames is [" + historyPoolNames.toString() + "].");
            if (historyPoolNames.containsKey(serverKey)) {
                String poolName = historyPoolNames.get(serverKey);
                shutdownPool(poolName);
                LOG.info("PoolName [" + poolName + "] shutdown success.");
                historyPools.remove(poolName);
                historyPoolNames.remove(serverKey);
                historyClients.remove(poolName);
            }
        } catch (ParseException | URISyntaxException | IOException e) {
            LOG.info("Response of Exception stopMemInstance is [" + response + "].");
            LOG.error(e.getMessage(), e);
        }
        return response;
    }

}
