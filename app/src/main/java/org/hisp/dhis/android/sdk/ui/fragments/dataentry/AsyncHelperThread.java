package org.hisp.dhis.android.sdk.ui.fragments.dataentry;

/**
 * @author Simen Skogly Russnes on 03.09.15.
 */
public abstract class AsyncHelperThread extends Thread {
    private boolean killed = false;
    private boolean working = false;
    private boolean doWork = false;

    @Override
    public void run() {
        while(!killed) {
            doWork();
            idle();
        }
    }

    private void doWork() {
        working = true;
        doWork = false;
        work();
        working = false;
    }

    private void idle() {
        while(!doWork) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void kill() {
        killed = true;
        doWork = false;
        while(working) {
            Thread.yield();
        }
    }

    public void schedule() {
        if(killed) {
            return;
        }
        doWork = true;
    }

    public boolean isKilled() {
        return killed;
    }

    protected abstract void work();
}
