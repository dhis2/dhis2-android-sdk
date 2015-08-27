package org.hisp.dhis.android.sdk.events;

/**
 * Created by arazabishov on 7/27/15.
 */
public final class UiEvent {
    public enum UiEventType {
        SYNC_DASHBOARDS, USER_LOG_OUT, SYNC_INTERPRETATIONS, SYNCING_START, SYNCING_END
    }

    private final UiEventType mType;

    public UiEvent(UiEventType type) {
        mType = type;
    }

    public UiEventType getEventType() {
        return mType;
    }
}
