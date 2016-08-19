package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.os.Handler;
import android.util.Log;

import org.hisp.dhis.android.sdk.persistence.Dhis2Application;

/**
 * Created by thomaslindsjorn on 28/07/16.
 */
public class RunProgramRulesDelayedDispatcher {

    private final int DELAY_MILLIS = 1000; // program rules are ran after this many milliseconds
    private Handler handler;
    private Runnable runnable;
    private RunProgramRulesEvent runProgramRulesEvent;

    public RunProgramRulesDelayedDispatcher() {
        handler = new Handler();
    }

    public void dispatchDelayed(RunProgramRulesEvent runProgramRulesEvent) {
        this.runProgramRulesEvent = runProgramRulesEvent;

        if (runnable == null) {
            initRunnable();
        } else {
            handler.removeCallbacks(runnable);
        }
        handler.postDelayed(runnable, DELAY_MILLIS);

    }

    public void dispatchNow() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler.post(runnable);
        }
    }

    private void initRunnable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (runProgramRulesEvent != null) {
                    Dhis2Application.getEventBus()
                            .post(runProgramRulesEvent);
                } else {
                    Log.d("RunProgramRules", "runProgramRulesEvent is null");
                }
                RunProgramRulesDelayedDispatcher.this.runnable = null;
            }
        };

    }
}
