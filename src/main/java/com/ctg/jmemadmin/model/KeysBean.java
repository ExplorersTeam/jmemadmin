package com.ctg.jmemadmin.model;

public class KeysBean {
	private String name;
	private long bytes;
	private long exptime;
	
	public KeysBean(String name, long bytes, long exptime) {
		this.name = name;
		this.bytes = bytes;
		this.exptime = exptime;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getBytes() {
		return bytes;
	}
	public void setBytes(long bytes) {
		this.bytes = bytes;
	}
	public long getExptime() {
		return exptime;
	}
	public void setExptime(long exptime) {
		this.exptime = exptime;
	}
	
}
