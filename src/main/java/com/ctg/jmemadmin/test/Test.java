package com.ctg.jmemadmin.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctg.jmemadmin.common.Constants;
import com.ctg.jmemadmin.utils.FileMonitor;
import com.ctg.jmemadmin.utils.MemCachedAdmin;
import com.ctg.jmemadmin.utils.RemoteExecuteCmd;
import com.ctg.jmemadmin.zookeeper.NodeMonitor;
import com.ctg.jmemadmin.zookeeper.NodeRegister;

public class Test {
	private static final Logger LOG = LoggerFactory.getLogger(Test.class);
	
	public static void main(String[] args) throws NumberFormatException, IOException, KeeperException, InterruptedException {
		MemCachedAdmin memCachedAdmin = new MemCachedAdmin();

		Object value =  "dfafafafaf";
		memCachedAdmin.add("name13", value);
		memCachedAdmin.set("name3", value);
		boolean existence = memCachedAdmin.keyExists("name3");
		LOG.info("existence= " + existence);
		String result = (String)memCachedAdmin.get("name1");
		LOG.info("get运行结果为：name3 = " + result);
		memCachedAdmin.flushAll();
		
//		memCachedAdmin.delete("mcp:00000062");
//		memCachedAdmin.listKeys();
//		memCachedAdmin.stats(Constants.SERVERS);
//		
//		memCachedAdmin.startMemcached(256, Constants.SINGLE_IP, Constants.PORT[1]);


		


//		RemoteExecuteCmd remoteExecuteCmd = new RemoteExecuteCmd(Constants.SLAVE_IP, Constants.SLAVE_USERNAME, Constants.SLAVE_PASSWORD);
//		remoteExecuteCmd.executeCmd(Constants.CREATE_SLAVE_MC_INSTANCE_CMD);
//		new NodeRegister("memcached2", 5000).start();
		
//		NodeMonitor nodeMonitor = new NodeMonitor();
//		nodeMonitor.connectZookeeper();
//		Thread.sleep(Long.MAX_VALUE);
	}
}
