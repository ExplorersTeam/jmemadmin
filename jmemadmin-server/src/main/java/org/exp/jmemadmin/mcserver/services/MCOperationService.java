package org.exp.jmemadmin.mcserver.services;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.common.utils.MCManager;
import org.exp.jmemadmin.common.utils.MCOperationUtils;
import org.exp.jmemadmin.entity.KeysBean;
import org.exp.jmemadmin.entity.MemInstance;
import org.exp.jmemadmin.entity.Response;
import org.exp.jmemadmin.entity.Response.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.sun.jersey.spi.resource.Singleton;
import com.whalin.MemCached.MemCachedClient;

@Singleton
@Path(Constants.REST_SERVER_PATH)
public class MCOperationService {
    private static final Logger LOG = LoggerFactory.getLogger(MCOperationService.class);

    @POST
    @Path(Constants.REST_SERVER_GET_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMCRecordByKey(MemInstance instance, @QueryParam("key") String key) {
        String poolName = MCManager.getPoolName(instance.getHost(), instance.getPort());
        MemCachedClient client = MCManager.getClient(poolName);
        MCOperationUtils.setMemCachedClient(client);
        Response response = new Response();
        try {
            Object result = MCOperationUtils.get(key);
            response.setContent(JSON.toJSONString(result));
            response.setCode(ResultStatus.SUCCESS.value());
        } catch (Exception e) {
            response.setContent(e.getMessage());
            response.setCode(ResultStatus.FAILED.value());
        }
        return response;
    }

    @POST
    @Path(Constants.REST_SERVER_SET_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setMCRecord(MemInstance instance, @QueryParam("key") String key, @QueryParam("value") String value) {
        String poolName = MCManager.getPoolName(instance.getHost(), instance.getPort());
        MemCachedClient client = MCManager.getClient(poolName);
        MCOperationUtils.setMemCachedClient(client);
        boolean result = MCOperationUtils.set(key, value);
        Response response = new Response();
        if (result == true) {
            response.setContent("Set memcached record success.");
            response.setCode(ResultStatus.SUCCESS.value());
        } else {
            response.setContent("Set memcached record fail.");
            response.setCode(ResultStatus.FAILED.value());
        }
        return response;
    }

    @POST
    @Path(Constants.REST_SERVER_DELETE_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteMCRecord(MemInstance instance, @QueryParam("key") String key) {
        String poolName = MCManager.getPoolName(instance.getHost(), instance.getPort());
        MemCachedClient client = MCManager.getClient(poolName);
        MCOperationUtils.setMemCachedClient(client);
        boolean result = MCOperationUtils.delete(key);
        Response response = new Response();
        if (result == true) {
            response.setContent("Deleted memcached record success.");
            response.setCode(ResultStatus.SUCCESS.value());
        } else {
            response.setContent("Deleted memcached record fail.");
            response.setCode(ResultStatus.FAILED.value());
        }
        return response;
    }

    @POST
    @Path(Constants.REST_SERVER_LIST_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listMCRecordKeys(MemInstance instance) {
        String poolName = MCManager.getPoolName(instance.getHost(), instance.getPort());
        MemCachedClient client = MCManager.getClient(poolName);
        MCOperationUtils.setMemCachedClient(client);
        Response response = new Response();
        Map<String, KeysBean> result = new HashMap<String, KeysBean>();
        try {
            result = MCOperationUtils.listKeys();
            response.setContent(JSON.toJSONString(result));
            response.setCode(ResultStatus.SUCCESS.value());
        } catch (NumberFormatException | UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
            response.setContent(e.getMessage());
            response.setCode(ResultStatus.FAILED.value());
        }
        return response;
    }

    @POST
    @Path(Constants.REST_SERVER_STAT_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response statMCServersStatus(MemInstance instance) {
        String poolName = MCManager.getPoolName(instance.getHost(), instance.getPort());
        MemCachedClient client = MCManager.getClient(poolName);
        MCOperationUtils.setMemCachedClient(client);
        Response response = new Response();
        try {
            Map<String, Map<String, String>> serversStatus = MCOperationUtils.stats();
            response.setContent(JSON.toJSONString(serversStatus));
            response.setCode(ResultStatus.SUCCESS.value());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            response.setContent(e.getMessage());
            response.setCode(ResultStatus.FAILED.value());
        }
        return response;
    }
}
