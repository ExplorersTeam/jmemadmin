package org.exp.jmemadmin.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean for memcached instance.
 * 
 * @author ZhangQingliang
 *
 */
@XmlRootElement
public class MemInstance {
    @XmlElement(required = false)
    private String host;

    private int port = -1;

    @XmlElement(name = "mem", required = false)
    private int memSize = -1;

    @XmlElement(name = "master", required = false)
    private boolean isMaster;

    public MemInstance() {
        // Do nothing.
    }

    public MemInstance(int port) {
        this.port = port;
    }

    public MemInstance(int port, int memSize) {
        this(port, memSize, false);
    }

    public MemInstance(int port, int memSize, boolean isMaster) {
        this.port = port;
        this.memSize = memSize;
        this.isMaster = isMaster;
    }

    public MemInstance(String host, int port, int memSize, boolean isMaster) {
        this.host = host;
        this.port = port;
        this.memSize = memSize;
        this.isMaster = isMaster;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMemSize() {
        return memSize;
    }

    public void setMemSize(int memSize) {
        this.memSize = memSize;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

}
