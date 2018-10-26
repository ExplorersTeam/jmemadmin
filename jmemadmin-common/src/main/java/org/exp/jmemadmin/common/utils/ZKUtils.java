package org.exp.jmemadmin.common.utils;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.exp.jmemadmin.common.CommonConfigs;
import org.exp.jmemadmin.common.Constants;

/**
 *
 * Operations to ZooKeeper.
 *
 */
public class ZKUtils {
    private static final Log LOG = LogFactory.getLog(ZKUtils.class);

    private static CuratorFramework curator = null;

    static {
        Builder builder = CuratorFrameworkFactory.builder();
        builder.connectString(CommonConfigs.getZKQuorum());
        builder.connectionTimeoutMs(CommonConfigs.getZKConnectionTimeoutMS());
        builder.sessionTimeoutMs(CommonConfigs.getZKSessionTimeoutMS());// session超时时间
        // ms
        builder.retryPolicy(new ExponentialBackoffRetry(CommonConfigs.getZKExponentialBackoffRetryBaseSleepTimeMS(), CommonConfigs.getZKMaxRetries(),
                CommonConfigs.getZKExponentialBackoffRetryMaxSleepTimeMS()));// 重试策略
        curator = builder.build();// 通过工厂创建连接
        curator.start();
        LOG.info("Curator initialized, state is [" + curator.getState().name() + "].");

        try {
            boolean flag = checkExists(CommonConfigs.getZNodeRoot());
            if (!flag) {
                create(CommonConfigs.getZNodeRoot());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ZKUtils() {
        // Do nothing.
    }

    public static boolean checkExists(String path) throws Exception {
        Stat stat = curator.checkExists().forPath(path);// 判断指定路径是否存在
        boolean existence = false;
        if (null == stat) {
            LOG.info("ZK path [" + path + "] isn't existed.");
        } else {
            existence = true;
            LOG.info("ZK path [" + path + "] is existed.");
        }
        return existence;
    }

    /**
     * Create ZNode with path.
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String create(String path) throws Exception {
        LOG.info("Create ZNode, path is [" + path + "].");
        return curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
    }

    /**
     * Create ZNode with path and data.
     * 创建节点，creatingParentsIfNeeded()方法的意思是如果父节点不存在，则在创建节点的同时创建父节点；
     * withMode()方法指定创建的节点类型，跟原生的Zookeeper API一样，不设置默认为PERSISTENT类型。
     *
     * @param path
     * @param data
     *            内容信息
     * @return
     * @throws Exception
     */
    public static String create(String path, byte[] data) throws Exception {
        LOG.info("Create ZNode, path is [" + path + "], and data is [" + new String(data) + "].");
        return curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, data);
    }

    /**
     * Get ZNode data.
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static byte[] get(String path) throws Exception {
        LOG.info("Get ZNode data, path is [" + path + "].");
        return curator.getData().forPath(path);
    }

    /**
     * Set ZNode data with default value.
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static Stat set(String path) throws Exception {
        LOG.info("Set ZNode data, path is [" + path + "].");
        return curator.setData().forPath(path);
    }

    /**
     * Set ZNode data with specific value.
     *
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public static Stat set(String path, byte[] data) throws Exception {
        LOG.info("Set ZNode data, path is [" + path + "], and value is [" + new String(data) + "].");
        return curator.setData().forPath(path, data);
    }

    /**
     * List ZNode children.
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static List<String> list(String path) throws Exception {
        LOG.info("List ZNode children, path is [" + path + "].");
        LOG.info("List ZNode children : [" + curator.getChildren().forPath(path).toString() + "].");
        return curator.getChildren().forPath(path);
    }

    /**
     * Delete ZNode.
     *
     * @param path
     * @throws Exception
     */
    public static void delete(String path) throws Exception {
        LOG.info("Delete ZNode, path is [" + path + "].");
        curator.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
    }

    /**
     * Mark deleted ZNode 注意：同一端口只保留对应的最新删除的实例节点
     *
     * @param path
     * @throws Exception
     */
    public static void markDeletedNode(String path) throws Exception {
        LOG.info("Marking node to be deleted,path is [" + path + "].");
        path = path.endsWith(Constants.SLASH_DELIMITER) ? path.substring(0, path.length() - 1) : path;
        String markedPath = path + CommonConfigs.getZKDeletedNodeMark();
        if (checkExists(markedPath)) {
            delete(markedPath);
        }
        create(markedPath, get(path));
        delete(path);
    }

}
