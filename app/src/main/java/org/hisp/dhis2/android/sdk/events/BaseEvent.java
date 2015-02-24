package org.hisp.dhis2.android.sdk.events;

import org.hisp.dhis2.android.sdk.controllers.ResponseHolder;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
public class BaseEvent {

    public static enum EventType {
        onLogin, loadAssignedPrograms, loadSmallOptionSet, onLoadingMetaDataFinished, showRegisterEventFragment, loadDataElements, loadProgramStages, showSelectProgramFragment, sendEvent, loadProgram
    }

    public EventType eventType;

    public BaseEvent(EventType eventType) {
        this.eventType = eventType;
    }
    public BaseEvent() {}
}
