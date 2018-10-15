package org.exp.jmemadmin.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exp.jmemadmin.common.Configs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.entity.ZKNodeInfo;

import com.alibaba.fastjson.JSONObject;

public class MemToolUtils {
    private static final Log LOG = LogFactory.getLog(MemToolUtils.class);

    public static String unifyStartEndSlash(String path) {
        StringBuffer unifiedPath = new StringBuffer();
        if (!path.startsWith(Constants.SLASH_DELIMITER)) {
            unifiedPath.append(Constants.SLASH_DELIMITER);
        }
        unifiedPath.append(path);
        if (!path.endsWith(Constants.SLASH_DELIMITER)) {
            unifiedPath.append(Constants.SLASH_DELIMITER);
        }
        return unifiedPath.toString();
    }

    public static String composeNodePath(String host, int port) {
        StringBuffer nodePath = new StringBuffer();
        String znodeRoot = Configs.getZNodeRoot();
        nodePath.append(unifyStartEndSlash(znodeRoot)).append(host).append(Constants.SLASH_DELIMITER).append(port);
        return nodePath.toString();
    }

    public static String composeStartupCmd(String host, int port, int memorySize) {
        // xx/memcached -d -u root -l 10.142.90.152 -p 12301 -m 200 -c 1000 -P
        // /tmp/memcached12301.pid
        StringBuffer cmd = new StringBuffer();
        cmd.append(Configs.getMCStartupShellPath()).append(" -l ").append(host).append(" -p ").append(port).append(" -m ").append(memorySize)
                .append(Constants.COMMAND_DELIMITER).append(" -P ").append(Configs.getInsPIDDir()).append(Constants.SLASH_DELIMITER)
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

    public static List<String> listZKNodePorts(String host) {
        List<String> listPorts = new ArrayList<>();
        StringBuffer nodePortsPath = new StringBuffer();
        String znodeRoot = Configs.getZNodeRoot();
        nodePortsPath.append(unifyStartEndSlash(znodeRoot)).append(host);
        try {
            listPorts = ZKUtils.list(nodePortsPath.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return listPorts;
    }

    // TODO:In order to prepare for the future
    public static List<Integer> listZKNodePids(String host) {
        List<String> listPorts = new ArrayList<>();
        List<Integer> listPids = new ArrayList<>();
        listPorts = listZKNodePorts(host);
        for (int i = 0; i < listPorts.size(); i++) {
            StringBuffer nodePidsPath = new StringBuffer();
            nodePidsPath.append(unifyStartEndSlash(Configs.getZNodeRoot())).append(host).append(Constants.SLASH_DELIMITER).append(listPorts.get(i));
            byte[] data = null;
            try {
                data = ZKUtils.get(nodePidsPath.toString());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
            ZKNodeInfo zkNodeInfo = JSONObject.parseObject(data, ZKNodeInfo.class);
            listPids.add(zkNodeInfo.getInstancePid());
        }
        return listPids;
    }
}
