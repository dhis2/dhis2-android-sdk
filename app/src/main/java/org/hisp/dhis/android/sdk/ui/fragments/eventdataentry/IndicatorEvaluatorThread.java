package org.hisp.dhis.android.sdk.ui.fragments.eventdataentry;

import android.util.Log;

import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.AsyncHelperThread;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

class IndicatorEvaluatorThread extends AsyncHelperThread {
    private EventDataEntryFragment eventDataEntryFragment;
    private ConcurrentLinkedQueue<ProgramIndicator> queuedProgramIndicators = new ConcurrentLinkedQueue<>();

    void init(EventDataEntryFragment eventDataEntryFragment) {
        setEventDataEntryFragment(eventDataEntryFragment);
    }

    private void setEventDataEntryFragment(EventDataEntryFragment eventDataEntryFragment) {
        this.eventDataEntryFragment = eventDataEntryFragment;
    }

    protected void work() {
        if(eventDataEntryFragment!=null) {
            while (!queuedProgramIndicators.isEmpty()) {
                ProgramIndicator programIndicator = queuedProgramIndicators.poll();
                eventDataEntryFragment.evaluateAndApplyProgramIndicator(programIndicator);
            }
            eventDataEntryFragment.refreshListView();
        }
    }

    void schedule(List<ProgramIndicator> programIndicators) {
        for(ProgramIndicator programIndicator : programIndicators) {
            if(!queuedProgramIndicators.contains(programIndicator)) {
                queuedProgramIndicators.add(programIndicator);
            }
        }
        super.schedule();
    }

    public void kill() {
        super.kill();
        setEventDataEntryFragment(null);
        if(queuedProgramIndicators!=null) {
            queuedProgramIndicators.clear();
        }
        queuedProgramIndicators = null;
    }
}
