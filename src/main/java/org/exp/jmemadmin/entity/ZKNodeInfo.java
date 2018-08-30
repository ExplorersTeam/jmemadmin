package org.exp.jmemadmin.entity;

/**
 * Bean for ZooKeeper ZNode information.
 * 
 * @author ZhangQingliang
 *
 */
public class ZKNodeInfo {

    private String instanceCmd;
    private int instancePid;
    private boolean isMaster;

    public ZKNodeInfo(String instanceCmd, int instancePid, boolean isMaster) {
        super();
        this.instanceCmd = instanceCmd;
        this.instancePid = instancePid;
        this.isMaster = isMaster;
    }

    public String getInstanceCmd() {
        return instanceCmd;
    }

    public void setInstanceCmd(String instanceCmd) {
        this.instanceCmd = instanceCmd;
    }

    public int getInstancePid() {
        return instancePid;
    }

    public void setInstancePid(int instancePid) {
        this.instancePid = instancePid;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

}
