package org.exp.jmemadmin.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.memcached.MemCachedAdmin;
import org.exp.jmemadmin.utils.FileMonitor;
import org.exp.jmemadmin.utils.HostCmdAdmin;
import org.exp.jmemadmin.utils.ObjectAndByte;
import org.exp.jmemadmin.utils.PortsCheck;
import org.exp.jmemadmin.utils.ZookeeperAdmin;
import org.exp.jmemadmin.zookeeper.NodeMonitor;
import org.exp.jmemadmin.zookeeper.NodeRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
//		PortsCheck portsCheck = new PortsCheck();
//		List<String> pidInfos = portsCheck.getPidInfos("12302");
//		Set<Integer> pids = portsCheck.getPids(pidInfos);


//		HostCmdAdmin hostCmdAdmin = new HostCmdAdmin(Constants.SLAVE_IP, Constants.SLAVE_USERNAME, Constants.SLAVE_PASSWORD);
//		hostCmdAdmin.executeLocalCmd("ls", null);
//		hostCmdAdmin.executeLocalCmd("java -version");
//		hostCmdAdmin.executeLocalCmd("ps -ax|grep memcached|grep 12301|grep -v grep", null);
//		hostCmdAdmin.executeLocalCmd("ps -ef|grep memcached|grep 12301|grep -v grep");
//		hostCmdAdmin.executeRemoteCmd("ps -ef|grep memcached|grep 12301|grep -v grep");
		

//		NodeMonitor nodeMonitor = new NodeMonitor();
//		nodeMonitor.connectZookeeper();
//		Thread.sleep(Long.MAX_VALUE);
		ZookeeperAdmin.init();
		List<String> nodes = ZookeeperAdmin.getAllZkNodes();
		LOG.info("all nodes are :" + nodes.toString());
		ZookeeperAdmin.checkExists("instance0000000013");
		ZookeeperAdmin.deleteZkNode("instance0000000013");
		ZookeeperAdmin.checkExists("instance0000000013");
		Object value = "nodeId = 12301";
		ZookeeperAdmin.createZkNode("10.142.90.152:12301", value);
		Object object = ZookeeperAdmin.getZkNodeInfos("10.142.90.152:12301");
		LOG.info("node info is " + ObjectAndByte.toByteArray(object).toString());
		ZookeeperAdmin.updateZkNodeInfos("10.142.90.152:12301", "nodeId = 12302");
		object = ZookeeperAdmin.getZkNodeInfos("10.142.90.152:12301");
		LOG.info("changed node info is " + ObjectAndByte.toByteArray(object).toString());
		ZookeeperAdmin.close();
	}
}
