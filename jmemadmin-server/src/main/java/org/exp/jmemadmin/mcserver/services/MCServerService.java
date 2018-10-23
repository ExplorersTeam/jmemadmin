package org.exp.jmemadmin.mcserver.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.exp.jmemadmin.common.Constants;
import org.exp.jmemadmin.entity.MemInstance;
import org.exp.jmemadmin.entity.Response;
import org.exp.jmemadmin.entity.Response.ResultStatus;
import org.exp.jmemadmin.mcserver.MCManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path(Constants.REST_SERVER_PATH)
public class MCServerService {
    private static final Logger LOG = LoggerFactory.getLogger(MCServerService.class);

    @POST
    @Path(Constants.REST_SERVER_START_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response startMemInstance(MemInstance instance) {
        Response response = new Response();
        response.setCode(ResultStatus.FAILED.value());
        try {
            String result = MCManager.startMemInstance(instance);
            // TODO:adjust result content.
            response.setContent(result);
            response.setCode(ResultStatus.SUCCESS.value());
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
        return response;
    }

    @POST
    @Path(Constants.REST_SERVER_STOP_SUBPATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response stopMemInstance(MemInstance instance) {
        Response response = new Response();
        response.setCode(ResultStatus.FAILED.value());
        try {
            String result = MCManager.stopMemInstance(instance);
            // TODO:adjust result content.
            response.setContent(result);
            response.setCode(ResultStatus.SUCCESS.value());
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
        return response;
    }

}
