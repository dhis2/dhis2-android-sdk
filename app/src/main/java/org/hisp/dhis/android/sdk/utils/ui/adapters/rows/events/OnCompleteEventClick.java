package org.hisp.dhis.android.sdk.utils.ui.adapters.rows.events;

import android.widget.Button;

import org.hisp.dhis.android.sdk.persistence.models.Event;

/**
 * Created by erling on 6/3/15.
 */
public class OnCompleteEventClick
{
    private String action, label;
    private Event event;
    private Button complete;

    public OnCompleteEventClick(String label, String action, Event event, Button complete)
    {
        this.label = label;
        this.action = action;
        this.event = event;
        this.complete = complete;
    }

    public String getAction() {
        return action;
    }

    public Button getComplete() {
        return complete;
    }

    public Event getEvent() {
        return event;
    }

    public String getLabel() {
        return label;
    }
}
