package org.exp.jmemadmin.monitor;

public abstract class Monitor implements Runnable {
    protected abstract void work();

    @Override
    public void run() {
        work();
    }

}
