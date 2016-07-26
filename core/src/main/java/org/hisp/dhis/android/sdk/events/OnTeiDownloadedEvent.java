package org.hisp.dhis.android.sdk.events;

import android.support.design.widget.Snackbar;

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

    public String getUserFriendlyMessage() {
        switch (eventType) {
            case START:
                return String.format("Downloading 1/%s", totalNumberOfTeis);
            case UPDATE:
                return String.format("Downloading %s/%s", eventNumber, totalNumberOfTeis);
            case ERROR:
                return String.format("Error downloading element %s", eventNumber);
            case END:
                if (errorHasOccured) {
                    return "Download completed with errors";
                }
                return "Download complete";
        }
        return "Download error - please try again";
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


