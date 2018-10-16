package org.exp.jmemadmin.mcserver;

import org.exp.jmemadmin.mcserver.common.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MCManager {
    private static final Logger LOG = LoggerFactory.getLogger(MCManager.class);

    private static MemCachedClient activeClient = null;

    private MCManager() {
        // DO nothing
    }

    public static MemCachedClient getActiveClient() {
        return activeClient;
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
        LOG.info("****************初始化连接池成功*******************");
        MemCachedClient activeClient = new MemCachedClient(poolName);
        return activeClient;
    }

    public static void shutdownPool(String poolName) {
        SockIOPool.getInstance(poolName).shutDown();
    }
}
