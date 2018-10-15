package org.exp.jmemadmin.trash;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO:只构思个框架，待细化填充

@Singleton
@Path("/status")
public class SystemStatusService {
	private static final Logger LOG = LoggerFactory.getLogger(SystemStatusService.class);
	
	@GET
	public Object stats() {
		
		return null;
	}
}
