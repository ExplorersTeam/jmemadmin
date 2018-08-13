package org.exp.jmemadmin.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.exp.jmemadmin.common.Constants;
import org.apache.zookeeper.ZooKeeper;

/**
 * 服务器启动后在zookeeper创建临时目录
 * 服务节点启动后注册到zookeeper
 * @author zhangqingliang
 *
 */
public class NodeRegister extends Thread{
	private static final Logger LOG = LoggerFactory.getLogger(NodeRegister.class);
	
	private String serverName;
	private int sleepTime;
	
	public NodeRegister() {
		//Do nothing
	}
	
	public NodeRegister(String serverName, int sleepTime) {
		this.serverName = serverName;
		this.sleepTime = sleepTime;
	}
	
	public void run() {
		try {
			connectZookeeper(serverName);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void connectZookeeper(String address) throws IOException, KeeperException, InterruptedException {
		ZooKeeper zk = new ZooKeeper(Constants.ZOOKEEPER_LIST, Constants.SLEEP_TIME, new Watcher() {			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
			}
		});
		
		//关键方法，创建包含自增长id名称的目录，这个方法支持了分布式锁的实现
		//四个参数：1、目录名称 2、目录文本信息 3、文件夹权限，Ids.OPEN_ACL_UNSAFE表示所有权限 
		//4、目录类型，CreateMode.EPHEMERAL_SEQUENTIAL表示创建临时目录，session断开连接则目录自动删除
		String createPath = zk.create(Constants.ZK_NODE_ROOT + "/instance", address.getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		LOG.info("*********create: " + createPath);
		Thread.sleep(sleepTime);
	}
	
}
