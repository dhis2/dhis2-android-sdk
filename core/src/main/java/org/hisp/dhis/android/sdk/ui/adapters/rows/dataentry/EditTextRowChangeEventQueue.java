package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.os.Handler;
import android.util.Log;

import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

/**
 * Created by thomaslindsjorn on 28/07/16.
 */
public class EditTextRowChangeEventQueue {

    private final int delayMillis = 1000;
    private Handler handler;
    private Runnable runnable;
    private RowValueChangedEvent rowValueChangedEvent;

    public EditTextRowChangeEventQueue() {
        init();
    }

    public void enque(RowValueChangedEvent rowValueChangedEvent) {
        this.rowValueChangedEvent = rowValueChangedEvent;
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delayMillis);
    }

    private void init() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (rowValueChangedEvent != null) {
                    Dhis2Application.getEventBus()
                            .post(rowValueChangedEvent);
                } else {
                    Log.d("ChangeEventQueue", "rowValueChangedEvent is null");
                }
            }
        };

    }
}
