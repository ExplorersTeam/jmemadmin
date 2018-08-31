package org.exp.jmemadmin.rest.services;

import java.net.InetAddress;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.instance.MemCachedManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.resource.Singleton;

/**
 * Agent operations service.
 * 
 * @author ZhangQingliang
 * @see org.exp.jmemadmin.agent.services.MCAgentService
 *
 */
@Deprecated
@Singleton
@Path(Constants.REST_AGENT_PATH)
public class MemAgentService {
    private static final Logger LOG = LoggerFactory.getLogger(MemAgentService.class);

    @GET
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean startMemInstance(@QueryParam(Constants.PORT) int port, @DefaultValue("256") @QueryParam(Constants.MEMORY_SIZE) int memorySize,
            @QueryParam(Constants.IS_MASTER) boolean isMaster) throws Exception {
        String host = InetAddress.getLocalHost().getHostName();
        boolean result = MemCachedManager.start(host, port, memorySize, isMaster);
        return result;
    }

    @GET
    @Path("/stop")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean stopMemInstance(@QueryParam(Constants.PORT) int port) throws Exception {
        String host = InetAddress.getLocalHost().getHostName();
        boolean result = MemCachedManager.stop(host, port);
        return result;
    }
}
