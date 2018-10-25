package org.exp.jmemadmin.agent.services;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exp.jmemadmin.agent.AgentService;
import org.exp.jmemadmin.common.CommonConfigs;
import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.HostCmdUtils;
import org.exp.jmemadmin.common.utils.MCToolUtils;
import org.exp.jmemadmin.common.utils.ZKUtils;
import org.exp.jmemadmin.entity.MemInstance;
import org.exp.jmemadmin.entity.Response;
import org.exp.jmemadmin.entity.Response.ResultStatus;
import org.exp.jmemadmin.entity.ZKNodeInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Agent operations service.
 */
@Singleton
@Path(Constants.REST_AGENT_ROOT_PATH)
public class MCAgentService implements AgentService {
    private static final Log LOG = LogFactory.getLog(AgentService.class);

    private static String getHost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    @POST
    @Path(Constants.REST_AGENT_START_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response start(MemInstance instance) {
        Response response = new Response();
        response.setCode(ResultStatus.FAILED.value());

        int memSize = instance.getMemSize();
        if (0 >= memSize) {
            response.setContent("When need to start an instance, memory size is necessary.");
            return response;
        }

        int port = instance.getPort();

        try {
            if (HostCmdUtils.isPortUsing(getHost(), port)) {
                response.setContent("Port [" + port + "] conflict.");
                return response;
            }
        } catch (UnknownHostException e) {
            LOG.error(e.getMessage(), e);
            response.setContent(e.getMessage());
            return response;
        }

        try {
            String startupCmd = MCToolUtils.composeStartupCmd(getHost(), port, memSize);
            LOG.info("StartupCmd is [" + startupCmd + "].");
            HostCmdUtils.executeLocalCmd(startupCmd, null);

            String readPidCmd = MCToolUtils.composeReadPidFileCmd(port);
            LOG.info("ReadPidCmd is [" + readPidCmd + "].");
            int pid = Integer.parseInt(HostCmdUtils.executeLocalCmd(readPidCmd, null));

            String removePidCmd = MCToolUtils.composeRemovePidFileCmd(port);
            LOG.info("RemovePidCmd is [" + removePidCmd + "].");
            HostCmdUtils.executeLocalCmd(removePidCmd, null);

            if (!HostCmdUtils.isPortUsing(getHost(), port)) {// 再次通过检测端口来判断是否启动成功
                LOG.error("Instance process start failed.");
                response.setContent("Instance process start failed.");
                return response;
            }

            String nodePath = MCToolUtils.composeNodePath(getHost(), port);
            LOG.info("Instance ZNode [" + nodePath + "] will be created.");
            ZKNodeInfo zkNodeInfo = new ZKNodeInfo(startupCmd, pid, instance.isMaster());
            byte[] data = JSON.toJSONString(zkNodeInfo).getBytes();
            ZKUtils.create(nodePath, data);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            response.setContent(e.getMessage());
            return response;
        }

        response.setCode(ResultStatus.SUCCESS.value());
        return response;
    }

    @POST
    @Path(Constants.REST_AGENT_STOP_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response stop(MemInstance instance) {
        Response response = new Response();
        response.setCode(ResultStatus.FAILED.value());

        StringBuilder memNodePath = new StringBuilder();
        String znodeRoot = CommonConfigs.getZNodeRoot();
        if (!znodeRoot.startsWith(Constants.SLASH_DELIMITER)) {
            memNodePath.append(Constants.SLASH_DELIMITER);
        }
        memNodePath.append(znodeRoot);
        if (!znodeRoot.endsWith(Constants.SLASH_DELIMITER)) {
            memNodePath.append(Constants.SLASH_DELIMITER);
        }
        try {
            memNodePath.append(getHost()).append(Constants.SLASH_DELIMITER).append(instance.getPort());
        } catch (UnknownHostException e) {
            LOG.error(e.getMessage(), e);
            response.setContent(e.getMessage());
            return response;
        }
        String znodePath = memNodePath.toString();

        try {
            byte[] data = ZKUtils.get(znodePath);
            ZKNodeInfo zkNodeInfo = JSONObject.parseObject(data, ZKNodeInfo.class);

            int pid = zkNodeInfo.getInstancePid();
            LOG.info("Got instance process ID, value is [" + pid + "], now kill it.");
            ZKUtils.delete(znodePath);// 删除节点

            String killPidCmd = "kill " + pid;
            String grepPidCmd = "ps -ef|grep -v grep|grep " + pid;
            boolean flag = false;
            while (!flag) {
                HostCmdUtils.executeLocalCmd(killPidCmd);
                flag = HostCmdUtils.executeLocalCmd(grepPidCmd).isEmpty();
            }
            response.setContent("stop instance success.");
            response.setCode(ResultStatus.SUCCESS.value());
            return response;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            response.setContent(e.getMessage());
            return response;
        }

    }

}
