package org.hisp.dhis.android.sdk.ui.fragments.eventdataentry;

import org.hisp.dhis.android.sdk.ui.fragments.dataentry.AsyncHelperThread;

import java.util.concurrent.ConcurrentLinkedQueue;

class IndicatorEvaluatorThread extends AsyncHelperThread {
    private EventDataEntryFragment eventDataEntryFragment;
    private ConcurrentLinkedQueue<String> queuedDataElements = new ConcurrentLinkedQueue<>();

    void init(EventDataEntryFragment eventDataEntryFragment) {
        setEventDataEntryFragment(eventDataEntryFragment);
    }

    private void setEventDataEntryFragment(EventDataEntryFragment eventDataEntryFragment) {
        this.eventDataEntryFragment = eventDataEntryFragment;
    }

    protected void work() {
        if(eventDataEntryFragment!=null) {
            while (!queuedDataElements.isEmpty()) {
                String dataElement = queuedDataElements.poll();
                eventDataEntryFragment.evaluateAndApplyProgramIndicators(dataElement);
            }
            eventDataEntryFragment.refreshListView();
        }
    }

    void schedule(String dataElement) {
        if(!queuedDataElements.contains(dataElement)) {
            queuedDataElements.add(dataElement);
        }
        super.schedule();
    }

    public void kill() {
        super.kill();
        setEventDataEntryFragment(null);
        if(queuedDataElements!=null) {
            queuedDataElements.clear();
        }
        queuedDataElements = null;
    }
}
