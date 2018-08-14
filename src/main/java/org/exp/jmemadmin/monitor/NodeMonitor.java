package org.exp.jmemadmin.monitor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.exp.jmemadmin.common.Configs;
import org.exp.jmemadmin.common.Constants;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 节点管理服务监听zookeeper临时目录节点创建/删除事件
 * 客户端监听server节点变化
 * @author 111
 *
 */
public class NodeMonitor {
	private static final Logger LOG = LoggerFactory.getLogger(NodeMonitor.class);
	
	private ZooKeeper zk;
	private volatile List<String> serverList;
	
	public NodeMonitor() {
		//Do nothing
	}
	
	public void connectZookeeper() throws IOException, KeeperException, InterruptedException {
		zk = new ZooKeeper(Configs.getZKQuorum(), 5000, new Watcher() //注册全局默认watcher
		{
			@Override
			public void process(WatchedEvent event) {
				LOG.info("**********event.getPath() is : " + event.getPath());
				if((event.getType() == EventType.NodeChildrenChanged) && (Configs.getZNodeRoot().equals(event.getPath()))) {
					LOG.info("Configs.getZNodeRoot().equals(event.getPath())为真。。。。");
					try {
						updateServerList();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					boolean flag = event.getType() == EventType.NodeChildrenChanged;
					System.out.println("****flag = " + flag + "****");
					LOG.info("Configs.getZNodeRoot().equals(event.getPath())为假。。。。");
				}
				
			}
		});
		updateServerList();
	}
	
	private void updateServerList() throws KeeperException, InterruptedException, UnsupportedEncodingException {
		List<String> newServerList = new ArrayList<String>();
		List<String> subList = zk.getChildren(Configs.getZNodeRoot(), true);//watcher注册后，只能监听事件一次，参数true表示继续使用默认watcher监听事件
		for(String subNode : subList) {
			byte[] nodeData = zk.getData(Configs.getZNodeRoot(), false, null);//获取节点数据
			newServerList.add(new String(nodeData,"utf-8"));
		}
		serverList = newServerList;
		LOG.info("server list updated: " + serverList);
	}

}
