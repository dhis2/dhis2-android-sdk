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

package org.hisp.dhis.android.sdk.controllers.tracker;

import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.ApiResponse;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.Relationship$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;
import org.hisp.dhis.android.sdk.utils.StringConverter;
import org.hisp.dhis.android.sdk.utils.Utils;
import org.hisp.dhis.android.sdk.utils.NetworkUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.ConversionException;

/**
 * @author Simen Skogly Russnes on 24.08.15.
 */
final class TrackerDataSender {

    public static final String CLASS_TAG = TrackerDataSender.class.getSimpleName();

    private TrackerDataSender() {
    }

    static Map<Long,ImportSummary> sendEventChanges(DhisApi dhisApi) throws APIException {
        List<Event> events = new Select().from(Event.class).where
                (Condition.column(Event$Table.FROMSERVER).is(false)).queryList();
        return sendEventChanges(dhisApi, events);
    }

    static Map<Long,ImportSummary> sendEventChanges(DhisApi dhisApi, List<Event> events) throws APIException {
        Map<Long,ImportSummary> importSummaryResults=new HashMap<>();
        if (events == null || events.isEmpty()) {
            return importSummaryResults;
        }

        for (int i = 0; i < events.size(); i++) {/* removing events with local enrollment reference. In this case, the enrollment needs to be synced first*/
            Event event = events.get(i);
            if (Utils.isLocal(event.getEnrollment()) && event.getEnrollment() != null/*if enrollments==null, then it is probably a single event without reg*/) {
                events.remove(i);
                i--;
                continue;
            }
        }
        Log.d(CLASS_TAG, "got this many events to send:" + events.size());

        for (Event event : events) {
            importSummaryResults.put(event.getLocalId(), sendEventChanges(dhisApi, event));
        }
        return importSummaryResults;
    }

    static ImportSummary sendEventChanges(DhisApi dhisApi, Event event) throws APIException {
        if (event == null) {
            return null;
        }

        if (Utils.isLocal(event.getEnrollment()) && event.getEnrollment() != null/*if enrollments==null, then it is probably a single event without reg*/) {
            return null;
        }

        if(event.getCreated() == null) {
            return postEvent(event, dhisApi);
        } else {
            return putEvent(event, dhisApi);
        }
    }

    private static ImportSummary postEvent(Event event, DhisApi dhisApi) throws APIException {
        try {
            ImportSummary importSummary=null;
            Response response = dhisApi.postEvent(event);

            if(response.getStatus() == 200) {
                importSummary = getImportSummary(response);
                handleImportSummary(importSummary, FailedItem.EVENT, event.getLocalId());
                if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.OK.equals(importSummary.getStatus())) {
                    // also, we will need to find UUID of newly created event,
                    // which is contained inside of HTTP Location header
                    Header header = NetworkUtils.findLocationHeader(response.getHeaders());
                    // change state and save event
                    event.setFromServer(true);
                    event.save();
                    clearFailedItem(FailedItem.EVENT, event.getLocalId());
                    UpdateEventTimestamp(event, dhisApi);
                }
            }
            return importSummary;
        } catch (APIException apiException) {
            NetworkUtils.handleEventSendException(apiException, event);
            return null;
        }
    }

    private static ImportSummary putEvent(Event event, DhisApi dhisApi) throws APIException {
        try {
            ImportSummary importSummary=null;
            Response response = dhisApi.putEvent(event.getEvent(), event);

            if(response.getStatus() == 200) {
                importSummary = getImportSummary(response);
                handleImportSummary(importSummary, FailedItem.EVENT, event.getLocalId());
                if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.OK.equals(importSummary.getStatus())) {

                    event.setFromServer(true);
                    event.save();
                    clearFailedItem(FailedItem.EVENT, event.getLocalId());
                    UpdateEventTimestamp(event, dhisApi);
                }
            }
            return importSummary;
        } catch (APIException apiException) {
            NetworkUtils.handleEventSendException(apiException, event);
            return null;
        }
    }

    private static void updateEventReferences(long localId, String newReference) {
        new Update(DataValue.class).set(Condition.column
                (DataValue$Table.EVENT).is
                (newReference)).where(Condition.column(DataValue$Table.LOCALEVENTID).is(localId)).async().execute();

        new Update(Event.class).set(Condition.column
                (Event$Table.EVENT).is
                (newReference), Condition.column(Event$Table.FROMSERVER).
                is(true)).where(Condition.column(Event$Table.LOCALID).is(localId)).async().execute();
        Event event = new Event();
        event.save();
        event.delete();//for triggering modelchangelistener
    }

    private static void UpdateEventTimestamp(Event event, DhisApi dhisApi) throws APIException {
        try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "created,lastUpdated");
            Event updatedEvent = dhisApi
                    .getEvent(event.getEvent(), QUERY_PARAMS);

            // merging updated timestamp to local event model
            event.setCreated(updatedEvent.getCreated());
            event.setLastUpdated(updatedEvent.getLastUpdated());
            event.save();
        } catch (APIException apiException) {
            NetworkUtils.handleApiException(apiException);
        }
    }

    static void sendEnrollmentChanges(DhisApi dhisApi, boolean sendEvents) throws APIException {
        List<Enrollment> enrollments = new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.FROMSERVER).is(false)).queryList();
        sendEnrollmentChanges(dhisApi, enrollments, sendEvents);
    }

    static void sendEnrollmentChanges(DhisApi dhisApi, List<Enrollment> enrollments, boolean sendEvents) throws APIException {
        if (enrollments == null || enrollments.isEmpty()) {
            return;
        }

        for (int i = 0; i < enrollments.size(); i++) {/* workaround for not attempting to upload enrollments with local tei reference*/
            Enrollment enrollment = enrollments.get(i);
            if (Utils.isLocal(enrollment.getTrackedEntityInstance())) {
                enrollments.remove(i);
                i--;
            }
        }
        Log.d(CLASS_TAG, "got this many enrollments to send:" + enrollments.size());
        for (Enrollment enrollment : enrollments) {
            sendEnrollmentChanges(dhisApi, enrollment, sendEvents);
        }
    }

    static void sendEnrollmentChanges(DhisApi dhisApi, Enrollment enrollment, boolean sendEvents) throws APIException {
        if (enrollment == null) {
            return;
        }
        if (Utils.isLocal(enrollment.getTrackedEntityInstance())) {//don't send enrollment with locally made uid
            return;
        }
        boolean success;

        if(enrollment.getCreated() == null) {
            success = postEnrollment(enrollment, dhisApi);
            if( success && sendEvents ) {
                List<Event> events = TrackerController.getEventsByEnrollment(enrollment.getLocalId());
                sendEventChanges(dhisApi, events);
            }
        } else {
            success = putEnrollment(enrollment, dhisApi);
            if( success && sendEvents ) {
                List<Event> events = TrackerController.getEventsByEnrollment(enrollment.getLocalId());
                sendEventChanges(dhisApi, events);
            }
        }
    }

    private static boolean postEnrollment(Enrollment enrollment, DhisApi dhisApi) throws APIException {
        try {
            Response response = dhisApi.postEnrollment(enrollment);
            if (response.getStatus() == 200) {
                ImportSummary importSummary = getImportSummary(response);
                handleImportSummary(importSummary, FailedItem.ENROLLMENT, enrollment.getLocalId());

                if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.OK.equals(importSummary.getStatus())) {
                    // change state and save enrollment

                    //enrollment.setState(State.SYNCED);
                    enrollment.setFromServer(true);
                    enrollment.save();
                    clearFailedItem(FailedItem.ENROLLMENT, enrollment.getLocalId());
                    UpdateEnrollmentTimestamp(enrollment, dhisApi);
                }
            }
        } catch (APIException apiException) {
            NetworkUtils.handleEnrollmentSendException(apiException, enrollment);
            return false;
        }
        return true;
    }

    private static boolean putEnrollment(Enrollment enrollment, DhisApi dhisApi) throws APIException {
        try {
            Response response = dhisApi.putEnrollment(enrollment.getEnrollment(), enrollment);
            if (response.getStatus() == 200) {
                ImportSummary importSummary = getImportSummary(response);
                handleImportSummary(importSummary, FailedItem.ENROLLMENT, enrollment.getLocalId());

                if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.OK.equals(importSummary.getStatus())) {

                    //enrollment.setState(State.SYNCED);
                    enrollment.setFromServer(true);
                    enrollment.save();
                    clearFailedItem(FailedItem.ENROLLMENT, enrollment.getLocalId());
                    UpdateEnrollmentTimestamp(enrollment, dhisApi);
                }
            }
        } catch (APIException apiException) {
            NetworkUtils.handleEnrollmentSendException(apiException, enrollment);
            return false;
        }
        return true;
    }

    private static void updateEnrollmentReferences(long localId, String newReference) {
        //updating any local events that had reference to local enrollment to new
        //reference from server.
        Log.d(CLASS_TAG, "updating enrollment references");
        new Update(Event.class).set(Condition.column
                (Event$Table.ENROLLMENT).is
                (newReference)).where(Condition.column(Event$Table.LOCALENROLLMENTID).is(localId)).async().execute();

        new Update(Enrollment.class).set(Condition.column
                (Enrollment$Table.ENROLLMENT).is
                (newReference), Condition.column(Enrollment$Table.FROMSERVER)
                .is(true)).where(Condition.column(Enrollment$Table.LOCALID).is
                (localId)).async().execute();
    }

    private static void UpdateEnrollmentTimestamp(Enrollment enrollment, DhisApi dhisApi) throws APIException {
        try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "created,lastUpdated");
            Enrollment updatedEnrollment = dhisApi
                    .getEnrollment(enrollment.getEnrollment(), QUERY_PARAMS);

            // merging updated timestamp to local enrollment model
            enrollment.setCreated(updatedEnrollment.getCreated());
            enrollment.setLastUpdated(updatedEnrollment.getLastUpdated());
            enrollment.save();
        } catch (APIException apiException) {
            NetworkUtils.handleApiException(apiException);
        }
    }

    static void sendTrackedEntityInstanceChanges(DhisApi dhisApi, boolean sendEnrollments) throws APIException {
        List<TrackedEntityInstance> trackedEntityInstances = new Select().from(TrackedEntityInstance.class).where(Condition.column(TrackedEntityInstance$Table.FROMSERVER).is(false)).queryList();
        sendTrackedEntityInstanceChanges(dhisApi, trackedEntityInstances, sendEnrollments);
    }

    static void sendTrackedEntityInstanceChanges(DhisApi dhisApi, List<TrackedEntityInstance> trackedEntityInstances, boolean sendEnrollments) throws APIException {
        if (trackedEntityInstances == null || trackedEntityInstances.isEmpty()) {
            return;
        }
        Log.d(CLASS_TAG, "got this many teis to send:" + trackedEntityInstances.size());

        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            sendTrackedEntityInstanceChanges(dhisApi, trackedEntityInstance, sendEnrollments);
        }
    }

    static void sendTrackedEntityInstanceChanges(DhisApi dhisApi, TrackedEntityInstance trackedEntityInstance, boolean sendEnrollments) throws APIException {
        if (trackedEntityInstance == null) {
            return;
        }
        boolean success;
        if(trackedEntityInstance.getCreated() == null) {
            success = postTrackedEntityInstance(trackedEntityInstance, dhisApi);
        } else {
            success = putTrackedEntityInstance(trackedEntityInstance, dhisApi);
        }
        if( success && sendEnrollments ) {
            List<Enrollment> enrollments = TrackerController.getEnrollments(trackedEntityInstance);
            sendEnrollmentChanges(dhisApi, enrollments, sendEnrollments);
        }
    }

    private static boolean postTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance, DhisApi dhisApi) throws APIException {
        try {
            Response response = dhisApi.postTrackedEntityInstance(trackedEntityInstance);
            if (response.getStatus() == 200) {
                ImportSummary importSummary = getImportSummary(response);
                handleImportSummary(importSummary, FailedItem.TRACKEDENTITYINSTANCE, trackedEntityInstance.getLocalId());
                if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.OK.equals(importSummary.getStatus())) {

                    // change state and save trackedentityinstance

                    //trackedEntityInstance.setState(State.SYNCED);
                    trackedEntityInstance.setFromServer(true);
                    trackedEntityInstance.save();

                    clearFailedItem(FailedItem.TRACKEDENTITYINSTANCE, trackedEntityInstance.getLocalId());
                    UpdateTrackedEntityInstanceTimestamp(trackedEntityInstance, dhisApi);
                }
            }
        } catch (APIException apiException) {
            NetworkUtils.handleTrackedEntityInstanceSendException(apiException, trackedEntityInstance);
            return false;
        }
        return true;
    }

    private static boolean putTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance, DhisApi dhisApi) throws APIException {
        try {
            Response response = dhisApi.putTrackedEntityInstance(trackedEntityInstance.getTrackedEntityInstance(), trackedEntityInstance);
            if (response.getStatus() == 200) {
                ImportSummary importSummary = getImportSummary(response);
                handleImportSummary(importSummary, FailedItem.TRACKEDENTITYINSTANCE, trackedEntityInstance.getLocalId());
                if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.OK.equals(importSummary.getStatus())) {
                    //trackedentityinstance.setState(State.SYNCED);
                    trackedEntityInstance.setFromServer(true);
                    trackedEntityInstance.save();
                    clearFailedItem(FailedItem.TRACKEDENTITYINSTANCE, trackedEntityInstance.getLocalId());
                    UpdateTrackedEntityInstanceTimestamp(trackedEntityInstance, dhisApi);
                }
            }
        } catch (APIException apiException) {
            NetworkUtils.handleTrackedEntityInstanceSendException(apiException, trackedEntityInstance);
            return false;
        }
        return true;
    }

    private static void updateTrackedEntityInstanceReferences(long localId, String newTrackedEntityInstanceReference, String oldTempTrackedEntityInstanceReference) {
        //update references with uid received from server
        new Update(TrackedEntityAttributeValue.class).set(Condition.column
                (TrackedEntityAttributeValue$Table.TRACKEDENTITYINSTANCEID).is
                (newTrackedEntityInstanceReference)).where(Condition.column(TrackedEntityAttributeValue$Table.LOCALTRACKEDENTITYINSTANCEID).is(localId)).async().execute();

        new Update(Event.class).set(Condition.column(Event$Table.
                TRACKEDENTITYINSTANCE).is(newTrackedEntityInstanceReference)).where(Condition.
                column(Event$Table.TRACKEDENTITYINSTANCE).is(oldTempTrackedEntityInstanceReference)).async().execute();

        new Update(Enrollment.class).set(Condition.column
                (Enrollment$Table.TRACKEDENTITYINSTANCE).is(newTrackedEntityInstanceReference)).
                where(Condition.column(Enrollment$Table.TRACKEDENTITYINSTANCE).is
                        (oldTempTrackedEntityInstanceReference)).async().execute();

        long updated = new Update(Relationship.class).set(Condition.column(Relationship$Table.TRACKEDENTITYINSTANCEA
        ).is(newTrackedEntityInstanceReference)).where(Condition.
                column(Relationship$Table.TRACKEDENTITYINSTANCEA).is(oldTempTrackedEntityInstanceReference)).count();

        updated += new Update(Relationship.class).set(Condition.column(Relationship$Table.TRACKEDENTITYINSTANCEB
        ).is(newTrackedEntityInstanceReference)).where(Condition.
                column(Relationship$Table.TRACKEDENTITYINSTANCEB).is(oldTempTrackedEntityInstanceReference)).count();

        Log.d(CLASS_TAG, "updated relationships: " + updated);

                    /* mechanism for triggering updating of relationships
                    * a relationship can only be uploaded if both involved teis are sent to server
                    * and have a valid UID.
                    * So, we check if this tei was just updated with a valid reference, and if there now
                    * exist >0 relationships that are valid. If >0 relationship is valid, it
                    * should get uploaded, as it is the first time it has been valid. */
        boolean hasValidRelationship = false;
        if (Utils.isLocal(oldTempTrackedEntityInstanceReference)) {
            List<Relationship> teiIsB = new Select().from(Relationship.class).where(Condition.column(Relationship$Table.TRACKEDENTITYINSTANCEB).is(newTrackedEntityInstanceReference)).queryList();
            List<Relationship> teiIsA = new Select().from(Relationship.class).where(Condition.column(Relationship$Table.TRACKEDENTITYINSTANCEA).is(newTrackedEntityInstanceReference)).queryList();
            if (teiIsB != null) {
                for (Relationship relationship : teiIsB) {
                    if (!Utils.isLocal(relationship.getTrackedEntityInstanceA())) {
                        hasValidRelationship = true;
                    }
                }
            }
            if (teiIsA != null) {
                for (Relationship relationship : teiIsA) {
                    if (!Utils.isLocal(relationship.getTrackedEntityInstanceB())) {
                        hasValidRelationship = true;
                    }
                }
            }
        }
        boolean fullySynced = !(hasValidRelationship && updated > 0);

        new Update(TrackedEntityInstance.class).set(Condition.column
                (TrackedEntityInstance$Table.TRACKEDENTITYINSTANCE).is
                (newTrackedEntityInstanceReference), Condition.column(TrackedEntityInstance$Table.FROMSERVER).is(fullySynced)).
                where(Condition.column(TrackedEntityInstance$Table.LOCALID).is(localId)).async().execute();
    }

    private static void UpdateTrackedEntityInstanceTimestamp(TrackedEntityInstance trackedEntityInstance, DhisApi dhisApi) throws APIException {
        try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "created,lastUpdated");
            TrackedEntityInstance updatedTrackedEntityInstance = dhisApi
                    .getTrackedEntityInstance(trackedEntityInstance.getTrackedEntityInstance(), QUERY_PARAMS);

            // merging updated timestamp to local trackedentityinstance model
            trackedEntityInstance.setCreated(updatedTrackedEntityInstance.getCreated());
            trackedEntityInstance.setLastUpdated(updatedTrackedEntityInstance.getLastUpdated());
            trackedEntityInstance.save();
        } catch (APIException apiException) {
            NetworkUtils.handleApiException(apiException);
        }
    }


    static void clearFailedItem(String type, long id) {
        FailedItem item = TrackerController.getFailedItem(type, id);
        if (item != null) {
            item.async().delete();
        }
    }

    private static void handleImportSummary(ImportSummary importSummary, String type, long id) {
        if (ImportSummary.ERROR.equals(importSummary.getStatus())) {
            Log.d(CLASS_TAG, "failed.. ");
            NetworkUtils.handleImportSummaryError(importSummary, type, 200, id);
        }
    }

    private static ImportSummary getImportSummary(Response response) {
        //because the web api almost randomly gives the responses in different forms, this
        //method checks which one it is that is being returned, and parses accordingly.
        try {
            JsonNode node = DhisController.getInstance().getObjectMapper().
                    readTree(new StringConverter().fromBody(response.getBody(), String.class));
            if (node == null) {
                return null;
            }
            if (node.has("response")) {
                return getPutImportSummary(response);
            } else {
                return getPostImportSummary(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConversionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ImportSummary getPostImportSummary(Response response) {
        ImportSummary importSummary = null;
        try {
            String body = new StringConverter().fromBody(response.getBody(), String.class);
            Log.d(CLASS_TAG, body);
            importSummary = DhisController.getInstance().getObjectMapper().
                    readValue(body, ImportSummary.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConversionException e) {
            e.printStackTrace();
        }
        return importSummary;
    }

    private static ImportSummary getPutImportSummary(Response response) {
        ApiResponse apiResponse = null;
        try {
            String body = new StringConverter().fromBody(response.getBody(), String.class);
            Log.d(CLASS_TAG, body);
            apiResponse = DhisController.getInstance().getObjectMapper().
                    readValue(body, ApiResponse.class);
            if (apiResponse != null && apiResponse.getImportSummaries() != null && !apiResponse.getImportSummaries().isEmpty()) {
                return (apiResponse.getImportSummaries().get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConversionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
