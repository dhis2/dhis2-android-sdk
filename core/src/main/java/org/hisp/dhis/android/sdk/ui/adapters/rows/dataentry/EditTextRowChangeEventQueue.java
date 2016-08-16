package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.os.Handler;
import android.util.Log;

import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

/**
 * Created by thomaslindsjorn on 28/07/16.
 */
public class EditTextRowChangeEventQueue {

    private final int DELAY_MILLIS = 1000; // program rules are ran after this many milliseconds
    private Handler handler;
    private Runnable runnable;
    private RowValueChangedEvent rowValueChangedEvent;

    public EditTextRowChangeEventQueue() {
        handler = new Handler();
    }

    public void enqueue(RowValueChangedEvent rowValueChangedEvent) {
        this.rowValueChangedEvent = rowValueChangedEvent;

        if (runnable == null) {
            initRunnable();
        } else {
            handler.removeCallbacks(runnable);
        }
        handler.postDelayed(runnable, DELAY_MILLIS);

    }

    public void runNow() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler.post(runnable);
        }
    }

    private void initRunnable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (rowValueChangedEvent != null) {
                    Dhis2Application.getEventBus()
                            .post(rowValueChangedEvent);
                } else {
                    Log.d("ChangeEventQueue", "rowValueChangedEvent is null");
                }
                EditTextRowChangeEventQueue.this.runnable = null;
            }
        };

    }
}
