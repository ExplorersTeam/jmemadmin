package org.exp.jmemadmin.instance;

import java.io.Console;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import org.apache.hadoop.metrics2.util.Servers;
import org.exp.jmemadmin.common.Configs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.entity.ZKNodeInfo;
import org.exp.jmemadmin.utils.HostCmdAdmin;
import org.exp.jmemadmin.utils.PortsCheck;
import org.exp.jmemadmin.utils.ZKUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.tools.internal.jxc.ap.Const;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

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

	private static MemCachedClient initSocketIOPool(SockIOPool pool, String name, String[] servers) {		
		pool.setServers(servers);	//设置memcached服务器地址
		//pool.setWeights(weights);	//设置每个memcached服务器权重
		pool.setFailover(Configs.getPoolFailover());		// 当一个memcached服务器失效的时候是否去连接另一个memcached服务器.
		pool.setInitConn(Configs.getPoolInitConns());		// 初始化时对每个服务器建立的连接数目 
		pool.setMinConn(Configs.getPoolMinConns());			// 每个服务器建立最小的连接数  
		pool.setMaxConn(Configs.getPoolMaxConns());			// 每个服务器建立最大的连接数  
		pool.setMaintSleep(Configs.getPoolMaintSleep());	// 自查线程周期进行工作，其每次休眠时间  
		pool.setNagle(Configs.getPoolNagle());				// Socket的参数，如果是true，在写数据时不缓冲，立即发送出去。Tcp的规则是在发送一个包之前，包的发送方会等待远程接收方确认已收到上一次发送过来的包；这个方法就可以关闭套接字的缓存——包准备立即发出。  
		pool.setSocketTO(Configs.getPoolSocketTimeout());	// Socket阻塞读取数据的超时时间  
		pool.setAliveCheck(Configs.getPoolAliveCheck());	// 设置是否检查memcached服务器是否失效 		
		pool.setMaxIdle(Configs.getPoolMaxIdle());			// 设置最大处理时间  
		pool.setSocketConnectTO(Configs.getPoolConnectTimeout());	// 连接建立时对超时的控制  
		pool.setMaintSleep(Configs.getPoolMaintSleep());			// 设置主线程睡眠时间，每30秒苏醒一次，维持连接池大小  	        
		pool.initialize();			//初始化连接池
		LOG.info("****************初始化连接池成功*******************");
		MemCachedClient activeName = new MemCachedClient(name);
		return activeName;
	}
    
    
    public static void start(String host, int port, int memorySize, boolean isMaster) throws Exception {   	   	
    	boolean flag = false;
		try {
			flag = PortsCheck.checkPortBySocket(host, port);
			if(flag) {//端口未被占
				serversList.add(host + ":" + String.valueOf(port));
				
				String startupCmd = MemCachedAdmin.composeStartupCmd(host, port, memorySize);
				HostCmdAdmin.executeLocalCmd(startupCmd, null);
				String readPidCmd = MemCachedAdmin.composeReadPidFileCmd(port);
				String pid = HostCmdAdmin.executeLocalCmd(readPidCmd, null);
				String removePidCmd = MemCachedAdmin.composeRemovePidFileCmd(port);
				HostCmdAdmin.executeLocalCmd(removePidCmd, null);
				
				// start.sh
		        if (null == activePool) {
		        	initActiveMemcached((String[])serversList.toArray());
		        	
		        } else {
		        	String key = Configs.getPoolMemnamePrefix() + ID.get();
		            historyPools.put(key, activePool);
		            Timer timer = new Timer();
		            TimerTask task = new TimerTask() {//创建一个新的计时器任务
						@Override
						public void run() {
							historyPools.remove(key);
							System.out.println(historyPools);
						}
					};
					timer.schedule(task, 100000);//在指定延迟后执行指定的任务。task : 所要安排的任务。10000 : 执行任务前的延迟时间，单位是毫秒。
					
		            initActiveMemcached((String[])serversList.toArray());
		        }
		        
		        String nodePath = MemCachedAdmin.composeNodePath(host, port);
		        ZKNodeInfo zkNodeInfo = new ZKNodeInfo(startupCmd, Integer.valueOf(pid), isMaster);
		        byte[] data = JSON.toJSONString(zkNodeInfo).getBytes();
		        ZKUtils.create(nodePath, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public static void stop(String host, int port) throws Exception {
    	serversList.remove(host + ":" + String.valueOf(port));
    	StringBuffer memNodePath = new StringBuffer();
    	memNodePath.append(Configs.getZNodeRoot()).append(Constants.PATH_DELIMITER).append(host)
    	.append(Constants.PATH_DELIMITER).append(port);
    	byte[] data = ZKUtils.get(memNodePath.toString());
    	ZKNodeInfo zkNodeInfo = JSONObject.parseObject(data, ZKNodeInfo.class, null);
    	int pid = zkNodeInfo.getInstancePid();
    	ZKUtils.delete(memNodePath.toString());//删除节点
    	String killPidCmd = "kill " + pid;
    	String grepPidCmd = "ps -ef|grep -v grep|grep " + pid;
    	do {
    		HostCmdAdmin.executeLocalCmd(killPidCmd);
    	}while(!(HostCmdAdmin.executeLocalCmd(grepPidCmd).isEmpty()));
    }
    
    private static void initActiveMemcached(String[] servers) {
        String activeMemName = Configs.getPoolMemnamePrefix() + ID.incrementAndGet();
        activePool = SockIOPool.getInstance(activeMemName);
        activeClient = initSocketIOPool(activePool, activeMemName, servers);
    }


    //XXX:
    static void historyMonitor() {
        historyClients.forEach(new BiConsumer<String, MemCachedClient>() {
            @Override
            public void accept(String t, MemCachedClient u) {
                // check
                // SockIOPool.getInstance().shutDown();
            }
        });
    }

}
