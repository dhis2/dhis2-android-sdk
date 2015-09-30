package org.hisp.dhis.android.sdk.ui.fragments.eventdataentry;

import android.util.Log;

import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.AsyncHelperThread;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.DataEntryFragment;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class enables thread safe scheduling and re-scheduling of saving of data in a data entry
 * fragment. The class has been implemented as a mechanism to simply handle problematic cases
 * where the user wants to save data while data is already being saved on the same element.
 */
public class EventSaveThread extends AsyncHelperThread {
    private EventDataEntryFragment dataEntryFragment;
    private Event event;
    private boolean saveEvent = false;
    private ConcurrentLinkedQueue<String> queuedDataValues = new ConcurrentLinkedQueue<>();
    private HashMap<String, DataValue> dataValues = new HashMap<>();

    public void init(EventDataEntryFragment dataEntryFragment) {
        setDataEntryFragment(dataEntryFragment);
    }

    public void setDataEntryFragment(EventDataEntryFragment dataEntryFragment) {
        this.dataEntryFragment = dataEntryFragment;
    }

    public void setEvent(Event event) {
        this.event = event;
        if(event.getDataValues() != null) {
            for(DataValue dataValue : event.getDataValues()) {
                dataValues.put(dataValue.getDataElement(), dataValue);
            }
        }
    }

    protected void work() {
        if(this.dataEntryFragment!=null && this.event != null) {
            while(saveEvent) {
                saveEvent();
            }
            boolean invalidateEvent = false;
            while(!queuedDataValues.isEmpty()) {
                saveDataValue();

                //after saving data values have to schedule saving the event to flag it to "not from server"
                invalidateEvent = true;
            }
            if(invalidateEvent) {
                saveEvent();
            }

            this.dataEntryFragment.save();
        }
    }

    private void saveEvent() {
        saveEvent = false;
        event.setFromServer(false);
        Event tempEvent = new Event();
        tempEvent.setLocalId(event.getLocalId());
        tempEvent.setEvent(event.getEvent());
        tempEvent.setStatus(event.getStatus());
        tempEvent.setLatitude(event.getLatitude());
        tempEvent.setLongitude(event.getLongitude());
        tempEvent.setTrackedEntityInstance(event.getTrackedEntityInstance());
        tempEvent.setLocalEnrollmentId(event.getLocalEnrollmentId());
        tempEvent.setEnrollment(event.getEnrollment());
        tempEvent.setProgramId(event.getProgramId());
        tempEvent.setProgramStageId(event.getProgramStageId());
        tempEvent.setOrganisationUnitId(event.getOrganisationUnitId());
        tempEvent.setEventDate(event.getEventDate());
        tempEvent.setDueDate(event.getDueDate());
        tempEvent.setFromServer(event.isFromServer());
        tempEvent.setUid(event.getUid());
        tempEvent.setName(event.getName());
        tempEvent.setDisplayName(event.getDisplayName());
        tempEvent.setCreated(event.getCreated());
        tempEvent.setLastUpdated(event.getLastUpdated());
        tempEvent.setAccess(event.getAccess());
        tempEvent.save();
    }

    private void saveDataValue() {
        String dataElementDataValue = queuedDataValues.poll();
        DataValue dataValue = dataValues.get(dataElementDataValue);
        if(dataValue != null) {
            dataValue.save();
        }
    }

    public void scheduleSaveEvent() {
        saveEvent = true;
        super.schedule();
    }

    public void scheduleSaveDataValue(String dataValueDataElement) {
        if(!queuedDataValues.contains(dataValueDataElement)) {
            queuedDataValues.add(dataValueDataElement);
        }
        super.schedule();
    }

    @Override
    public void kill() {
        super.kill();
        dataEntryFragment = null;
        event = null;
        if(dataValues != null) {
            dataValues.clear();
            dataValues = null;
        }
    }
}
