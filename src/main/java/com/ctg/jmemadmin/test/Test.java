package com.ctg.jmemadmin.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctg.jmemadmin.common.Constants;
import com.ctg.jmemadmin.utils.FileMonitor;
import com.ctg.jmemadmin.utils.MemCachedAdmin;
import com.ctg.jmemadmin.utils.PortsCheck;
import com.ctg.jmemadmin.utils.HostCmdAdmin;
import com.ctg.jmemadmin.zookeeper.NodeMonitor;
import com.ctg.jmemadmin.zookeeper.NodeRegister;

public class Test {
	private static final Logger LOG = LoggerFactory.getLogger(Test.class);
	
	public static void main(String[] args) throws Exception {
		MemCachedAdmin memCachedAdmin = new MemCachedAdmin();

//		Object value =  "dfafafafaf";
//		memCachedAdmin.add("name13", value);
//		memCachedAdmin.set("name3", value);
//		boolean existence = memCachedAdmin.keyExists("name3");
//		LOG.info("existence= " + existence);
//		String result = (String)memCachedAdmin.get("name1");
//		LOG.info("get运行结果为：name3 = " + result);
//		memCachedAdmin.flushAll();
		
//		memCachedAdmin.delete("mcp:00000062");
//		memCachedAdmin.listKeys();
//		memCachedAdmin.stats(Constants.SERVERS);
//		
//		memCachedAdmin.startMemcached(256, Constants.SINGLE_IP, Constants.PORT[1]);

		
//		boolean portExistence = false;
//		portExistence = PortsCheck.checkPortBySocket(Constants.SINGLE_IP, 12301);
//		System.out.println("10.142.90.152的12301端口占用情况：" + portExistence);
//		portExistence = false;
//		portExistence = PortsCheck.checkPortBySocket(Constants.SLAVE_DOMAIN_NAME, 12302);
//		System.out.println("10.142.90.154的12302端口占用情况：" + portExistence);
		

		
//		boolean ipFlag = PortsCheck.ipCheck("10.142.90.1555");
//		LOG.info("10.142.90.1555 ipFlag ==== " + ipFlag);
//		ipFlag = PortsCheck.ipCheck("10.142.90.152");
//		LOG.info("10.142.90.152 ipFlag ==== " + ipFlag);
		PortsCheck portsCheck = new PortsCheck();
		List<String> pidInfos = portsCheck.getPidInfos("12302");
		Set<Integer> pids = portsCheck.getPids(pidInfos);


//		HostCmdAdmin hostCmdAdmin = new HostCmdAdmin(Constants.SLAVE_IP, Constants.SLAVE_USERNAME, Constants.SLAVE_PASSWORD);
//		hostCmdAdmin.executeLocalCmd("ls", null);
//		hostCmdAdmin.executeLocalCmd("java -version");
//		hostCmdAdmin.executeLocalCmd("ps -ax|grep memcached|grep 12301|grep -v grep", null);
//		hostCmdAdmin.executeLocalCmd("ps -ef|grep memcached|grep 12301|grep -v grep");
//		hostCmdAdmin.executeRemoteCmd("ps -ef|grep memcached|grep 12301|grep -v grep");
		

//		NodeMonitor nodeMonitor = new NodeMonitor();
//		nodeMonitor.connectZookeeper();
//		Thread.sleep(Long.MAX_VALUE);
	}
}
