package org.hisp.dhis.android.sdk.ui.fragments.eventdataentry;

import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.AsyncHelperThread;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.HideLoadingDialogEvent;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RefreshListViewEvent;

/**
 * Thread that handles asynchronous updating of results based on ProgramRules and Indicators.
 * This thread enables thread safe scheduling of ProgramRule and Indicator evaluation and updating,
 * typically triggered by data changed in data entry rows.
 */
class RulesEvaluatorThread extends AsyncHelperThread {
    private EventDataEntryFragment eventDataEntryFragment;

    void init(EventDataEntryFragment eventDataEntryFragment) {
        setEventDataEntryFragment(eventDataEntryFragment);
    }

    public void setEventDataEntryFragment(EventDataEntryFragment eventDataEntryFragment) {
        this.eventDataEntryFragment = eventDataEntryFragment;
    }

    protected void work() {
        if(eventDataEntryFragment!=null) {
            eventDataEntryFragment.resetHiding(eventDataEntryFragment.getListViewAdapter(), eventDataEntryFragment.getSpinnerAdapter());
            eventDataEntryFragment.evaluateAndApplyProgramRules();
            Dhis2Application.getEventBus().post(new RefreshListViewEvent());
            Dhis2Application.getEventBus().post(new HideLoadingDialogEvent());
        }
    }

    public void kill() {
        super.kill();
        setEventDataEntryFragment(null);
    }
}
