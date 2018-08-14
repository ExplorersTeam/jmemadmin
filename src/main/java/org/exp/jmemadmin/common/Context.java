package org.exp.jmemadmin.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;


public class Context {
	private static final Logger LOG = LoggerFactory.getLogger(Context.class);
	
	private static MemCachedClient memCachedClient = new MemCachedClient();
	static {
		SockIOPool pool = SockIOPool.getInstance();//创建一个socked连接池实例
		pool.setServers(Constants.SERVERS);	//设置memcached服务器地址
		pool.setWeights(Constants.WEIGHTS);	//设置每个memcached服务器权重
		pool.setFailover(true);		// 当一个memcached服务器失效的时候是否去连接另一个memcached服务器.
		pool.setInitConn(10);		// 初始化时对每个服务器建立的连接数目 
		pool.setMinConn(5);			// 每个服务器建立最小的连接数  
		pool.setMaxConn(250);		// 每个服务器建立最大的连接数  
		pool.setMaintSleep(30);		// 自查线程周期进行工作，其每次休眠时间   设置主线程睡眠时间，每30秒苏醒一次，维持连接池大小  
		pool.setNagle(false);		// Socket的参数，如果是true，在写数据时不缓冲，立即发送出去。Tcp的规则是在发送一个包之前，包的发送方会等待远程接收方确认已收到上一次发送过来的包；这个方法就可以关闭套接字的缓存——包准备立即发出。  
		pool.setSocketTO(3000);		// Socket阻塞读取数据的超时时间  
		pool.setAliveCheck(true);	// 设置是否检查memcached服务器是否失效 
		
		pool.setMaxIdle(1000*30*30);// 设置最大处理时间  
		pool.setSocketConnectTO(0);	// 连接建立时对超时的控制  
	        
		pool.initialize();			//初始化连接池
		LOG.info("****************初始化连接池成功*******************");
		if(memCachedClient == null) {
			memCachedClient = new MemCachedClient();
		}
	}
	
	
	
	private Context() {
        // Do nothing.
    }
	
	public static MemCachedClient getMemcachedClient() {
		return memCachedClient;
	}
}
