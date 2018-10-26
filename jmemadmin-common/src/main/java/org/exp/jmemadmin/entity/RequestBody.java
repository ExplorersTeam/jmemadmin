package org.exp.jmemadmin.entity;

public class RequestBody {
    private String host;
    private int port = -1;
    private int mem = -1;
    private boolean master;

    public RequestBody() {
        // Do nothing
    }

    public RequestBody(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    public RequestBody(String host, int port, int mem) {
        super();
        this.host = host;
        this.port = port;
        this.mem = mem;
    }

    public RequestBody(String host, int port, int mem, boolean master) {
        super();
        this.host = host;
        this.port = port;
        this.mem = mem;
        this.master = master;
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

    public int getMem() {
        return mem;
    }

    public void setMem(int mem) {
        this.mem = mem;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }
}
