package org.exp.jmemadmin.monitor;

public abstract class Monitor implements Runnable {
    protected abstract boolean check();

    protected abstract void report();

    @Override
    public void run() {
        if (!check()) {
            report();
        }
    }

}
