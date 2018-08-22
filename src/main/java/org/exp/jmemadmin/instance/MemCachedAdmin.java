package org.exp.jmemadmin.instance;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.exp.jmemadmin.common.Configs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.entity.KeysBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic API operations for memcached.
 *
 * @author ZhangQingliang
 *
 */
public class MemCachedAdmin {
    private static final Logger LOG = LoggerFactory.getLogger(MemCachedAdmin.class);

    // TODO:测试直接用获取静态实例后的一个静态变量，当pool启停时会不会还是用的原来pool中的实例。
    // private static MemCachedClient memCachedClient =
    // MemCachedManager.getActiveClient();

    private MemCachedAdmin() {
        // DO nothing
    }

    public static boolean add(String key, Object value) {
        boolean flag = false;
        flag = MemCachedManager.getActiveClient().add(key, value);
        if (flag == false) {
            LOG.info(key + "is already existed!");
        } else {
            LOG.info("add [" + key + "]success!");
        }
        return flag;
    }

    public static boolean keyExists(String key) {
        return MemCachedManager.getActiveClient().keyExists(key);
    }

    public static boolean set(String key, Object value) {
        boolean flag = false;
        flag = MemCachedManager.getActiveClient().set(key, value);
        if (flag == false) {
            LOG.info("*******set [" + key + "] fail!*******");
        } else {
            LOG.info("*******set [" + key + "] success!*******");
        }
        return flag;
    }

    // //TODO:方法待测试
    // public static boolean add(String key, Object value, Date exptime) {
    // return MemCachedManager.getActiveClient().add(key, value, exptime);
    // }
    //
    // //TODO:方法待测试
    // public static boolean set(String key, Object value, Date expiry) {
    // boolean flag = false;
    // flag = MemCachedManager.getActiveClient().set(key, value, expiry);
    // if(flag == false) {
    // LOG.info("*******set [" + key + "] fail!*******");
    // }else {
    // LOG.info("*******set [" + key + "] success!*******");
    // }
    // return flag;
    // }

    public static Object get(String key) {
        return MemCachedManager.getActiveClient().get(key);
    }

    public static boolean delete(String key) {
        return MemCachedManager.getActiveClient().delete(key);
    }

    public static boolean flushAll() {
        return MemCachedManager.getActiveClient().flushAll();
    }

    public static boolean flushAll(String[] servers) {
        return MemCachedManager.getActiveClient().flushAll(servers);
    }

    public static Map<String, Map<String, String>> stats() {
        Map<String, Map<String, String>> serversStatus = MemCachedManager.getActiveClient().stats();
        LOG.info("***********************ServersStatus************************");
        LOG.info(serversStatus.values().toString());
        return serversStatus;
    }

    public static Map<String, Map<String, String>> stats(String[] servers) {
        Map<String, Map<String, String>> serversStatus = MemCachedManager.getActiveClient().stats(servers);
        LOG.info("***********************ServersStatus************************");
        LOG.info(serversStatus.values().toString());
        return serversStatus;
    }

    /**
     * 在java memcached client
     * documentation中没有提共遍历memcache所有key的方法。但是提供了两个方法statsItems和statsCacheDump，
     * 通过statsitems可以获取memcache中有多少个item，每个item上有多少个key，
     * 而statsCacheDump可以获取每个item上各个key的信息（key的名称，大小，以及有效期）。
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws NumberFormatException
     */
    public static Map<String, KeysBean> listKeys() throws NumberFormatException, UnsupportedEncodingException {
        Map<String, KeysBean> keylist = new HashMap<String, KeysBean>();
        // 遍历statsItems STAT items:32:number 2446
        Map<String, Map<String, String>> statsItems = MemCachedManager.getActiveClient().statsItems();
        Map<String, String> statsItems_sub = null;

        String statsItems_sub_key = null;
        int items_number = 0;
        String keyName = null;
        // 根据items:32:number 2446，调用statsCacheDump，获取每个item中的key ITEM
        // mcp:0000129f [102400 b; 0 s]
        Map<String, Map<String, String>> statsCacheDump = null;
        Map<String, String> statsCacheDump_sub = null;
        String statsCacheDump_sub_key = null;
        String statsCacheDump_sub_value = null;
        Iterator<String> iterator = statsItems.keySet().iterator();
        while (iterator.hasNext()) {
            keyName = iterator.next();
            statsItems_sub = statsItems.get(keyName);
            LOG.info(keyName + "===" + statsItems_sub);
            for (Iterator<String> iterator_item = statsItems_sub.keySet().iterator(); iterator_item.hasNext();) {
                statsItems_sub_key = iterator_item.next();
                if (statsItems_sub_key.toUpperCase().startsWith("items:".toUpperCase()) && statsItems_sub_key.toUpperCase().endsWith(":number".toUpperCase())) {
                    items_number = Integer.parseInt(statsItems_sub.get(statsItems_sub_key).trim());
                    // LOG.info(statsItems_sub_key+":=:"+items_number);
                    statsCacheDump = MemCachedManager.getActiveClient().statsCacheDump(new String[] { keyName },
                            Integer.parseInt(statsItems_sub_key.split(":")[1].trim()), items_number);
                    for (Iterator<String> statsCacheDump_iterator = statsCacheDump.keySet().iterator(); statsCacheDump_iterator.hasNext();) {
                        statsCacheDump_sub = statsCacheDump.get(statsCacheDump_iterator.next());
                        LOG.info(statsCacheDump_sub.toString());
                        for (Iterator<String> iterator_keys = statsCacheDump_sub.keySet().iterator(); iterator_keys.hasNext();) {
                            statsCacheDump_sub_key = iterator_keys.next();
                            statsCacheDump_sub_value = statsCacheDump_sub.get(statsCacheDump_sub_key);
                            // LOG.info("**********statsCacheDump_sub_key**********");
                            // LOG.info(statsCacheDump_sub_key);//key是中文被编码了,是客户端在set之前编码的，服务端中文key存的是密文
                            // LOG.info("**********statsCacheDump_sub_value**********");
                            // LOG.info(statsCacheDump_sub_value);
                            keylist.put(URLDecoder.decode(statsCacheDump_sub_key, "UTF-8"),
                                    new KeysBean(keyName,
                                            Long.parseLong(statsCacheDump_sub_value.substring(1, statsCacheDump_sub_value.indexOf("b;") - 1).trim()),
                                            Long.parseLong(statsCacheDump_sub_value
                                                    .substring(statsCacheDump_sub_value.indexOf("b;") + 2, statsCacheDump_sub_value.indexOf("s]") - 1)
                                                    .trim())));
                        }
                    }
                }
            }
        }
        LOG.info("****************listKeys******************");
        LOG.info(keylist.values().toString());
        return keylist;
    }

    public static String composeNodePath(String host, int port) {
        StringBuffer nodePath = new StringBuffer();
        nodePath.append(Configs.getZNodeRoot()).append(Constants.PATH_DELIMITER).append(host).append(Constants.PATH_DELIMITER).append(port);
        return nodePath.toString();
    }

    public static String composeStartupCmd(String host, int port, int memorySize) {
        // xx/memcached -d -u root -l 10.142.90.152 -p 12301 -m 200 -c 1000 -P
        // /tmp/memcached12301.pid
        StringBuffer cmd = new StringBuffer();
        cmd.append(Configs.getMCStartupShellPath()).append(" -l ").append(host).append(" -p ").append(port).append(" -m ").append(memorySize)
                .append(Constants.COMMAND_DELIMITER).append(" -P ").append(Configs.getInsPIDDir()).append(Constants.PATH_DELIMITER)
                .append(Configs.getInsPrefix()).append(port).append(Configs.getInsSuffix()).append(Configs.getMCStartupConfigurableParams());
        return cmd.toString();
    }

    public static String composeReadPidFileCmd(int port) {
        StringBuffer cmd = new StringBuffer();
        cmd.append("cat ").append(Configs.getInsPIDDir()).append(Configs.getInsPrefix()).append(port).append(Configs.getInsSuffix());
        return cmd.toString();
    }

    public static String composeRemovePidFileCmd(int port) {
        StringBuffer cmd = new StringBuffer();
        cmd.append("rm ").append(Configs.getInsPIDDir()).append(Configs.getInsPrefix()).append(port).append(Configs.getInsSuffix());
        return cmd.toString();
    }

    public static void main() {
        // TODO:若要在此运行，要先初始化SocketIOPool哦
        MemCachedAdmin.flushAll();
        Object value = "dfafafafaf";
        MemCachedAdmin.add("name13", value);
        MemCachedAdmin.set("name3", value);
        boolean existence = MemCachedAdmin.keyExists("name3");
        LOG.info("existence= " + existence);
        String result = (String) MemCachedAdmin.get("name1");
        LOG.info("get运行结果为：name3 = " + result);
    }
}
