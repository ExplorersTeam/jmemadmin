package org.exp.jmemadmin.entity;

public class KeysBean {
	private String name;	//名称
	private long bytes;		//大小
	private long exptime;	//有效期
	
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

	@Override
	public String toString() {
		return "KeysBean [name=" + name + ", bytes=" + bytes + ", exptime=" + exptime + "]";
	}
	
}
