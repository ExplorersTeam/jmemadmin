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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Object getMCRecordByKey(MemInstance instance, @QueryParam("key") String key) {
        String poolName = MCManager.getPoolName(instance.getHost(), instance.getPort());
        MemCachedClient client = MCManager.getClient(poolName);
        MCOperationUtils.setMemCachedClient(client);
        Object result = MCOperationUtils.get(key);
        return result;
    }

    @POST
    @Path(Constants.REST_SERVER_SET_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean setMCRecord(MemInstance instance, @QueryParam("key") String key, @QueryParam("value") String value) {
        String poolName = MCManager.getPoolName(instance.getHost(), instance.getPort());
        MemCachedClient client = MCManager.getClient(poolName);
        MCOperationUtils.setMemCachedClient(client);
        boolean result = MCOperationUtils.set(key, value);
        return result;
    }

    @POST
    @Path(Constants.REST_SERVER_LIST_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, KeysBean> listMCRecordKeys(MemInstance instance) {
        String poolName = MCManager.getPoolName(instance.getHost(), instance.getPort());
        MemCachedClient client = MCManager.getClient(poolName);
        MCOperationUtils.setMemCachedClient(client);
        Map<String, KeysBean> result = new HashMap<String, KeysBean>();
        try {
            result = MCOperationUtils.listKeys();
        } catch (NumberFormatException e) {
            LOG.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    @POST
    @Path(Constants.REST_SERVER_STAT_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, String>> statMCServersStatus(MemInstance instance) {
        String poolName = MCManager.getPoolName(instance.getHost(), instance.getPort());
        MemCachedClient client = MCManager.getClient(poolName);
        MCOperationUtils.setMemCachedClient(client);
        Map<String, Map<String, String>> serversStatus = MCOperationUtils.stats();
        return serversStatus;
    }
}
