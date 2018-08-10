package org.exp.jmemadmin.services;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO:只构思个框架，待细化填充
@Singleton
@Path("/memcached")
public class MemcachedService {
	private static final Logger LOG = LoggerFactory.getLogger(MemcachedService.class);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Object startMemcachedInstance() {
		
		return null;
	}
	
	@POST
	public Object stopMemcachedInstance() {
		
		return null;
	}
	
	@POST
	public Object set() {
		
		return null;
	}
	
	@GET
	public Object get() {
		
		return null;
	}
	
	public Object delete() {
		
		return null;
	}
	
	public Object flushAll() {
		
		return null;
	}
	
	@GET
	public Object listKeys() {
		
		return null;
	}
}
