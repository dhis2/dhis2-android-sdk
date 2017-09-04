/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.ui.fragments.eventdataentry;

import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.AsyncHelperThread;

import java.util.HashMap;
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
        if (event != null) {
            this.event = event;
            if (event.getDataValues() != null) {
                for (DataValue dataValue : event.getDataValues()) {
                    dataValues.put(dataValue.getDataElement(), dataValue);
                }
            }
        }
    }

    protected void work() {
        if (this.dataEntryFragment != null && this.event != null) {
            if (event.getLocalId() < 0) {
                saveEvent = true;
            }

            while (saveEvent) {
                saveEvent();
            }

            boolean invalidateEvent = false;
            while (!queuedDataValues.isEmpty()) {
                saveDataValue();

                //after saving data values have to schedule saving the event to flag it to "not from server"
                invalidateEvent = true;
            }
            if (invalidateEvent) {
                saveEvent();
            }

            this.dataEntryFragment.save();
        }
    }

    private void saveEvent() {
        if (event == null) {
            return;
        }
        saveEvent = false;
        event.setFromServer(false);
        Enrollment enrollment = TrackerController.getEnrollment(event.getEnrollment());
        enrollment.setFromServer(false);
        enrollment.save();
        TrackedEntityInstance trackedEntityInstance = TrackerController.getTrackedEntityInstance(event.getTrackedEntityInstance());
        trackedEntityInstance.setFromServer(false);
        trackedEntityInstance.save();
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
        event.setLocalId(tempEvent.getLocalId());
    }

    private void saveDataValue() {
        if (event != null) {
            String dataElementDataValue = queuedDataValues.poll();
            DataValue dataValue = dataValues.get(dataElementDataValue);
            if (dataValue != null) {
                dataValue.setLocalEventId(event.getLocalId());
                dataValue.save();
            }
        }
    }

    public void scheduleSaveEvent() {
        saveEvent = true;
        super.schedule();
    }

    public void scheduleSaveDataValue(String dataValueDataElement) {
        if (!queuedDataValues.contains(dataValueDataElement)) {
            queuedDataValues.add(dataValueDataElement);
        }
        super.schedule();
    }

    @Override
    public void kill() {
        super.kill();
        dataEntryFragment = null;
        event = null;
        if (dataValues != null) {
            dataValues.clear();
            dataValues = null;
        }
    }
}
