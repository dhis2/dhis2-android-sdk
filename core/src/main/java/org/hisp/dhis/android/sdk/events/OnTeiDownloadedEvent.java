package org.hisp.dhis.android.sdk.events;

import android.content.Context;
import android.support.design.widget.Snackbar;

import org.hisp.dhis.android.sdk.R;

/**
 * Created by thomaslindsjorn on 25/07/16.
 */
public class OnTeiDownloadedEvent {

    private int eventNumber;
    private int totalNumberOfTeis;
    private EventType eventType;
    private boolean errorHasOccured;

    public OnTeiDownloadedEvent(EventType eventType) {
        this(eventType, -1);
    }

    public OnTeiDownloadedEvent(EventType eventType, int totalNumberOfTeis) {
        this(eventType, totalNumberOfTeis, 0);
    }

    public OnTeiDownloadedEvent(EventType eventType, int totalNumberOfTeis, int eventNumber) {
        this.eventType = eventType;
        this.totalNumberOfTeis = totalNumberOfTeis;
        this.eventNumber = eventNumber;
    }

    public String getUserFriendlyMessage(Context context) {
        switch (eventType) {
            case START:
                return String.format(context.getString(R.string.downloading) + " 1/%s", totalNumberOfTeis);
            case UPDATE:
                return String.format(context.getString(R.string.downloading) + " %s/%s", eventNumber, totalNumberOfTeis);
            case ERROR:
                return String.format(context.getString(R.string.error_downloading) + " %s", eventNumber);
            case END:
                if (errorHasOccured) {
                    return context.getString(R.string.downloaded_with_errors);
                }
                return context.getString(R.string.download_complete);
        }
        return context.getString(R.string.download_error_try_again);
    }

    public int getMessageDuration() {
        switch (eventType) {
            case END:
                return Snackbar.LENGTH_SHORT;
            default:
                return Snackbar.LENGTH_INDEFINITE;
        }
    }

    public void setErrorHasOccured(boolean errorHasOccured) {
        this.errorHasOccured = errorHasOccured;
    }

    public enum EventType {
        START, UPDATE, ERROR, END
    }

    public EventType getEventType() {
        return eventType;
    }
}


