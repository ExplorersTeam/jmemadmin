package com.ctg.jmemadmin.utils;

import java.util.List;

import javax.validation.constraints.Null;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctg.jmemadmin.common.Constants;

public class ZookeeperAdmin {
	private static final Logger LOG = LoggerFactory.getLogger(ZookeeperAdmin.class);
	
	private static CuratorFramework curator;
	public static void init() {
		int SESSION_TIMEOUT = 15000; //session超时时间 ms
	    RetryPolicy policy = new ExponentialBackoffRetry(1000, 10);//重试策略，初试时间1秒，重试10次
	    curator = CuratorFrameworkFactory.builder().connectString(Constants.ZOOKEEPER_LIST)
	    		.sessionTimeoutMs(SESSION_TIMEOUT).retryPolicy(policy).build(); //通过工厂创建连接
	    curator.start();//开启连接
	    LOG.info(curator.getState().toString());
	}
	/**
	 * 创建节点，creatingParentsIfNeeded()方法的意思是如果父节点不存在，则在创建节点的同时创建父节点；
     * withMode()方法指定创建的节点类型，跟原生的Zookeeper API一样，不设置默认为PERSISTENT类型。
	 * @param nodeName 节点名称
	 * @param infos  节点内容信息
	 * @throws Exception
	 */
	public static void createZkNode(String nodeName, Object infos) throws Exception {
		boolean existence = checkExists(nodeName);
		if(existence) {
			LOG.info("********The node of [" + nodeName + "] has already existed********");
		}else {
			curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(Constants.ZK_NODE_ROOT + Constants.PATH_DELIMITER + nodeName, ObjectAndByte.toByteArray(infos));
		}
	}
	
	public static Object getZkNodeInfos(String nodeName) throws Exception {
		return curator.getData().forPath(Constants.ZK_NODE_ROOT  + Constants.PATH_DELIMITER + nodeName);//获取节点数据信息
	}
	
	public static boolean checkExists(String nodeName) throws Exception {
		Stat stat = curator.checkExists().forPath(Constants.ZK_NODE_ROOT + Constants.PATH_DELIMITER + nodeName);//判断指定节点是否存在
		boolean existence = false;
		if(null == stat) {
			LOG.info("********The node of [" + nodeName + "] isn't existed********");
		}else {
			existence = true;
			LOG.info("********The node of [" + nodeName + "] is existed********");
		}
		return existence;
	}
	
	public static void updateZkNodeInfos(String nodeName, Object newInfos) throws Exception {
		curator.setData().forPath(Constants.ZK_NODE_ROOT + Constants.PATH_DELIMITER + nodeName, ObjectAndByte.toByteArray(newInfos));
	}
	
	public static List<String> getAllZkNodes() throws Exception{
		List<String> memcachedNodes = curator.getChildren().forPath(Constants.ZK_NODE_ROOT);
		return memcachedNodes;
	}
	
	public static void deleteZkNode(String nodeName) throws Exception {
		boolean existence = checkExists(nodeName);
		if (existence) {
			curator.delete().guaranteed().deletingChildrenIfNeeded().forPath(Constants.ZK_NODE_ROOT + Constants.PATH_DELIMITER + nodeName);
		} else {
			LOG.info("********No node of [" + nodeName + "] to delete!********");
		}
	}
	//TODO:未初始化时此函数不起作用。待回收。
	public static boolean isStarted() {
		CuratorFrameworkState state = curator.getState();
		if(CuratorFrameworkState.STOPPED == state) {
			return false;
		}else {
			return true;
		}
	}
	public static void close() {
		if(isStarted()){
			curator.close();
		}else {
			LOG.info("*************No zookeeper is connected*************");
		}
	}
}
