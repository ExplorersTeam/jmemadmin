package org.exp.jmemadmin.server.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.exp.jmemadmin.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path(Constants.REST_SERVER_PATH)
public class MCServerService {
    private static final Logger LOG = LoggerFactory.getLogger(MCServerService.class);

    @GET
    @Path("/key")
    @Produces(MediaType.APPLICATION_JSON)
    public String get() {

        return "";
    }

}
