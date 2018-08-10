package org.exp.jmemadmin.entity;

public class SystemStatus {

	private String server;
	
	private String attribution;
	
	private String value;
	
	public SystemStatus(String server, String attribution, String value) {
		this.server = server;
		this.attribution = attribution;
		this.value = value;
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getAttribution() {
		return attribution;
	}
	
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
