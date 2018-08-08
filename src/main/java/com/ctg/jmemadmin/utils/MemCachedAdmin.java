package com.ctg.jmemadmin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctg.jmemadmin.common.Constants;
import com.ctg.jmemadmin.model.KeysBean;
import com.ctg.jmemadmin.zookeeper.NodeRegister;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;


public class MemCachedAdmin {
	private static final Logger LOG = LoggerFactory.getLogger(MemCachedAdmin.class);
	
	private static MemCachedClient memCachedClient = new MemCachedClient();
	static {
		SockIOPool pool = SockIOPool.getInstance();//创建一个socked连接池实例
		pool.setServers(Constants.SERVERS);	//设置memcached服务器地址
		pool.setWeights(Constants.WEIGHTS);	//设置每个memcached服务器权重
		pool.setFailover(true);		// 当一个memcached服务器失效的时候是否去连接另一个memcached服务器.
		pool.setInitConn(10);		// 初始化时对每个服务器建立的连接数目 
		pool.setMinConn(5);			// 每个服务器建立最小的连接数  
		pool.setMaxConn(250);		// 每个服务器建立最大的连接数  
		pool.setMaintSleep(30);		// 自查线程周期进行工作，其每次休眠时间  
		pool.setNagle(false);		// Socket的参数，如果是true，在写数据时不缓冲，立即发送出去。Tcp的规则是在发送一个包之前，包的发送方会等待远程接收方确认已收到上一次发送过来的包；这个方法就可以关闭套接字的缓存——包准备立即发出。  
		pool.setSocketTO(3000);		// Socket阻塞读取数据的超时时间  
		pool.setAliveCheck(true);	// 设置是否检查memcached服务器是否失效 
		
		pool.setMaxIdle(1000*30*30);// 设置最大处理时间  
		pool.setSocketConnectTO(0);	// 连接建立时对超时的控制  
		pool.setMaintSleep(30);		// 设置主线程睡眠时间，每30秒苏醒一次，维持连接池大小  
	        
		pool.initialize();			//初始化连接池
		LOG.info("****************初始化连接池成功*******************");
		if(memCachedClient == null) {
			memCachedClient = new MemCachedClient();
		}
	}
	
	private MemCachedAdmin() {
		//Do nothing
	}

	private static MemCachedAdmin memCachedAdmin = new MemCachedAdmin();
	
	//获取唯一实例
	public static MemCachedAdmin getInstance() {
		return memCachedAdmin;
	}
	
	
	
	public static boolean add(String key, Object value, Date exptime) {
		return memCachedClient.add(key, value, exptime);
	}

	public static Object get(String key) {
		return memCachedClient.get(key);
	}
	
	/**
	 * 在java memcached client documentation中没有提共遍历memcache所有key的方法。但是提供了两个方法statsItems和statsCacheDump，
	 * 通过statsitems可以获取memcache中有多少个item，每个item上有多少个key，
	 * 而statsCacheDump可以获取每个item上各个key的信息（key的名称，大小，以及有效期）。
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws NumberFormatException 
	 */
	public static Map<String, KeysBean> getKeysForMap() throws NumberFormatException, UnsupportedEncodingException{
		Map<String, KeysBean> keylist = new HashMap<String, KeysBean>();
		//遍历statsItems  STAT items:32:number 2446 
		Map<String, Map<String, String>> statsItems = memCachedClient.statsItems();
		Map<String, String> statsItems_sub = null;
		
		String statsItems_sub_key=null;  
        int items_number=0;  
        String keyName=null;  
        //根据items:32:number 2446，调用statsCacheDump，获取每个item中的key  ITEM mcp:0000129f [102400 b; 0 s] 
        Map<String, Map<String, String>> statsCacheDump = null;
        Map<String, String> statsCacheDump_sub = null;
        String statsCacheDump_sub_key = null;
        String statsCacheDump_sub_value = null;
        Iterator iterator = statsItems.keySet().iterator();
        while(iterator.hasNext()) {
        	keyName = (String)iterator.next();
        	statsItems_sub = statsItems.get(keyName);
        	LOG.info(keyName + "===" + statsItems_sub);
        	for(Iterator iterator_item = statsItems_sub.keySet().iterator(); iterator_item.hasNext();) {
        		statsItems_sub_key = (String)iterator_item.next();
        		if(statsItems_sub_key.toUpperCase().startsWith("items:".toUpperCase()) && statsItems_sub_key.toUpperCase().endsWith(":number".toUpperCase())){
					items_number=Integer.parseInt(statsItems_sub.get(statsItems_sub_key).trim());
					LOG.info(statsItems_sub_key+":=:"+items_number);
					statsCacheDump=memCachedClient.statsCacheDump(new String[]{keyName},Integer.parseInt(statsItems_sub_key.split(":")[1].trim()), items_number);				
					for(Iterator statsCacheDump_iterator=statsCacheDump.keySet().iterator(); statsCacheDump_iterator.hasNext(); ) {
						statsCacheDump_sub=statsCacheDump.get(statsCacheDump_iterator.next());
						LOG.info(statsCacheDump_sub.toString());
						for (Iterator iterator_keys=statsCacheDump_sub.keySet().iterator(); iterator_keys.hasNext(); ) {
							statsCacheDump_sub_key=(String) iterator_keys.next();
							statsCacheDump_sub_value=statsCacheDump_sub.get(statsCacheDump_sub_key);							
//							LOG.info("**********statsCacheDump_sub_key**********");
//							LOG.info(statsCacheDump_sub_key);//key是中文被编码了,是客户端在set之前编码的，服务端中文key存的是密文
//							LOG.info("**********statsCacheDump_sub_value**********");
//							LOG.info(statsCacheDump_sub_value);
							keylist.put(URLDecoder.decode(statsCacheDump_sub_key, "UTF-8"),
									new KeysBean(keyName,Long.parseLong(statsCacheDump_sub_value.substring(1, statsCacheDump_sub_value.indexOf("b;")-1).trim()),
												Long.parseLong(statsCacheDump_sub_value.substring(statsCacheDump_sub_value.indexOf("b;")+2,
																statsCacheDump_sub_value.indexOf("s]")-1).trim())));     
						}
					}
        		}
        	}
        }
        System.out.println("**********************************");
        System.out.println(keylist.toString());
        return keylist;
	}
	
	public static Map<String, Map<String, String>> stats(String[] servers){
		Map<String, Map<String, String>> statsStatus = memCachedClient.stats();
		LOG.info("***********************statsStatus************************");
		LOG.info(statsStatus.values().toString());
		return statsStatus;
	}
	
	public static String executeLinuxCmd(String cmd) {
		LOG.info("got cmd job: " + cmd);
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(cmd);
			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer result = new StringBuffer();
			String tmp = null;
			while((tmp = bufferedReader.readLine()) != null) {
				result.append(tmp);
			}
			LOG.info("job result [" + result.toString() + "]");
			inputStream.close();
			//process.waitFor();
			process.destroy();
			return result.toString();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

 