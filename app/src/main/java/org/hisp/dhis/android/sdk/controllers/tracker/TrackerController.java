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

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.LoadingController;
import org.hisp.dhis.android.sdk.controllers.ResourceController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem$Table;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.Relationship$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.utils.UiUtils;
import org.hisp.dhis.android.sdk.utils.api.ProgramType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 *         Handles management of data values
 */
public final class TrackerController extends ResourceController {

    private static final String CLASS_TAG = "DataValueController";

    private TrackerController() {}

    /**
     * Changes the max number of events to retreive from the server
     * @param max
     */
    public static void setMaxEvents(int max){
        TrackerDataLoader.setMaxEvents(max);
    }

    /**
     * Returns false if some data value flags that have been enabled have not been downloaded.
     *
     * @param context
     * @return
     */
    public static boolean isDataLoaded(Context context) {
        Log.d(CLASS_TAG, "isdatavaluesloaded..");
        if( context==null ) {
            return false;
        }
        if (LoadingController.isLoadFlagEnabled(context, ResourceType.EVENTS)) {
            if( DateTimeManager.getInstance().getLastUpdated(ResourceType.EVENTS) == null) {
                return false;
            }
        }
        Log.d(CLASS_TAG, "data values are loaded.");
        return true;
    }

    public static List<Relationship> getRelationships(String trackedEntityInstance) {
        return new Select().from(Relationship.class).where(Condition.column
                (Relationship$Table.TRACKEDENTITYINSTANCEA).is(trackedEntityInstance)).
                or(Condition.column(Relationship$Table.TRACKEDENTITYINSTANCEB).is
                        (trackedEntityInstance)).queryList();
    }

    public static List<Enrollment> getEnrollments(String program, String organisationUnit) {
        return new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.PROGRAM).
                is(program)).and(Condition.column(Enrollment$Table.ORGUNIT).is(organisationUnit)).
                orderBy(false, Enrollment$Table.ENROLLMENTDATE).
                queryList();
    }

    public static List<Enrollment> getEnrollments(TrackedEntityInstance trackedEntityInstance) {
        return new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.LOCALTRACKEDENTITYINSTANCEID).
                is(trackedEntityInstance.getLocalId())).queryList();
    }

    /**
     * Returns a list of enrollments for a given program and tracked entity instance
     *
     * @param program
     * @param trackedEntityInstance
     * @return
     */
    public static List<Enrollment> getEnrollments(String program, TrackedEntityInstance trackedEntityInstance) {
        List<Enrollment> enrollments = new Select().from(Enrollment.class).
                where(Condition.column(Enrollment$Table.PROGRAM).is(program)).
                and(Condition.column(Enrollment$Table.LOCALTRACKEDENTITYINSTANCEID).
                        is(trackedEntityInstance.getLocalId())).queryList();
        return enrollments;
    }

    public static Enrollment getEnrollment(String enrollment) {
        return new Select().from(Enrollment.class).where(Condition.column
                (Enrollment$Table.ENROLLMENT).is(enrollment)).querySingle();
    }

    public static Enrollment getEnrollment(long localEnrollmentId) {
        return new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.LOCALID).
                is(localEnrollmentId)).querySingle();
    }

    /**
     * Returns a list of Events that have dueDate between the given dates, and corresponds to
     * program and orgunit
     *
     * @param programId
     * @param orgUnitId
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Event> getScheduledEvents(String programId, String orgUnitId,
                                                 String startDate, String endDate) {
        return new Select().from(Event.class).where(Condition.column(Event$Table.PROGRAMID).is
                (programId)).and(Condition.column(Event$Table.ORGANISATIONUNITID).is
                (orgUnitId)).and(Condition.column(Event$Table.DUEDATE).between(startDate).and
                (endDate)).orderBy(Event$Table.DUEDATE).queryList();
    }

    /**
     * Returns a list of events for the given server-assigned UID. Note that if possible,
     * getEventsByEnrollment(long) should always be used if possible, as the UID may change if the
     * enrollment is created locally on the device, and then synced with the server.
     *
     * @param enrollment
     * @return
     */
    public static List<Event> getEventsByEnrollment(String enrollment) {
        return new Select().from(Event.class).where(Condition.column(Event$Table.ENROLLMENT).is(enrollment)).queryList();
    }

    /**
     * returns a list of events for a given localEnrollmentId
     *
     * @param localEnrollmentId
     * @return
     */
    public static List<Event> getEventsByEnrollment(long localEnrollmentId) {
        return new Select().from(Event.class).where(Condition.column(Event$Table.LOCALENROLLMENTID).is(localEnrollmentId)).queryList();
    }

    /**
     * Returns a list of events for a given org unit and program
     *
     * @param organisationUnitId
     * @param programId
     * @return
     */
    public static List<Event> getEvents(String organisationUnitId, String programId) {
        List<Event> events = new Select().from(Event.class).where(Condition.column
                (Event$Table.ORGANISATIONUNITID).is(organisationUnitId)).
                and(Condition.column(Event$Table.PROGRAMID).is(programId)).orderBy(false, Event$Table.LASTUPDATED).queryList();
        return events;
    }

    /**
     * Returns an Event based on the given localId
     *
     * @param localId
     * @return
     */
    public static Event getEvent(long localId) {
        return new Select().from(Event.class).where(Condition.column(Event$Table.LOCALID).is(localId)).querySingle();
    }

    /**
     * Returns an Event for a given enrollment and program stage
     *
     * @param localEnrollment
     * @param programStage
     * @return
     */
    public static Event getEvent(long localEnrollment, String programStage) {
        return new Select().from(Event.class).where(Condition.column
                        (Event$Table.LOCALENROLLMENTID).is(localEnrollment),
                Condition.column(Event$Table.PROGRAMSTAGEID).is(programStage)).querySingle();
    }

    /**
     * Returns an event based on UID generated on server. Note that this reference may change if
     * an event is created on the device, and then synced with the server. If possible, always use
     * getEvent(localId) which is safer.
     *
     * @param event
     * @return
     */
    public static Event getEventByUid(String event) {
        return new Select().from(Event.class).where(Condition.column(Event$Table.EVENT).is(event)).querySingle();
    }

    public static DataValue getDataValue(long eventId, String dataElement) {
        return new Select().from(DataValue.class).where(Condition.column(DataValue$Table.
                LOCALEVENTID).is(eventId), Condition.column(DataValue$Table.DATAELEMENT).is(dataElement)).querySingle();
    }

    /**
     * Returns a tracked entity instance based on the given id
     *
     * @param trackedEntityInstance
     * @return
     */
    public static TrackedEntityInstance getTrackedEntityInstance(String trackedEntityInstance) {
        return new Select().from(TrackedEntityInstance.class).where(Condition.column(TrackedEntityInstance$Table.TRACKEDENTITYINSTANCE).is(trackedEntityInstance)).querySingle();
    }

    public static TrackedEntityInstance getTrackedEntityInstance(long localId) {
        return new Select().from(TrackedEntityInstance.class).where
                (Condition.column(TrackedEntityInstance$Table.LOCALID).is(localId)).querySingle();
    }

    /*
   * Returns a list of tracked entity attribute values for an instance in a selected program
   * @param trackedEntityInstance
   * @param program
   * @return
   */
    public static List<TrackedEntityAttributeValue> getProgramTrackedEntityAttributeValues(Program program, TrackedEntityInstance trackedEntityInstance) {
        List<TrackedEntityAttributeValue> programTrackedEntityAttributeValues = new ArrayList<>();
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = MetaDataController.getProgramTrackedEntityAttributes(program.getUid());

        for (ProgramTrackedEntityAttribute ptea : programTrackedEntityAttributes) {
            TrackedEntityAttributeValue v = TrackerController.getTrackedEntityAttributeValue
                    (ptea.getTrackedEntityAttributeId(), trackedEntityInstance.getLocalId());
            if (v != null && v.getValue() != null && !v.getValue().isEmpty()) {
                programTrackedEntityAttributeValues.add(v);
            }
        }
        return programTrackedEntityAttributeValues;
    }


    /**
     * Returns a tracked entity attribute value for a given trackedentityattribute and trackedEntityInstance
     *
     * @param trackedEntityAttribute
     * @param trackedEntityInstance
     * @return
     */
    public static TrackedEntityAttributeValue getTrackedEntityAttributeValue(String trackedEntityAttribute, String trackedEntityInstance) {
        return new Select().from(TrackedEntityAttributeValue.class).where(
                Condition.column(TrackedEntityAttributeValue$Table.
                        TRACKEDENTITYATTRIBUTEID).is(trackedEntityAttribute),
                Condition.column(TrackedEntityAttributeValue$Table.
                        TRACKEDENTITYINSTANCEID).is(trackedEntityInstance)).querySingle();
    }

    /**
     * Returns a list of all trackedEntityAttributeValues for a given TEI
     *
     * @param trackedEntityInstance
     * @return
     */
    public static List<TrackedEntityAttributeValue> getTrackedEntityAttributeValues
    (String trackedEntityInstance) {
        return new Select().from(TrackedEntityAttributeValue.class).where(Condition.column
                (TrackedEntityAttributeValue$Table.TRACKEDENTITYINSTANCEID).is(trackedEntityInstance)).queryList();
    }

    /**
     * Returns a tracked entity attribute value for a given trackedentityattribute and trackedEntityInstance
     *
     * @param trackedEntityAttribute
     * @param trackedEntityInstance
     * @return
     */
    public static TrackedEntityAttributeValue getTrackedEntityAttributeValue(String trackedEntityAttribute, long trackedEntityInstance) {
        return new Select().from(TrackedEntityAttributeValue.class).where(
                Condition.column(TrackedEntityAttributeValue$Table.
                        TRACKEDENTITYATTRIBUTEID).is(trackedEntityAttribute),
                Condition.column(TrackedEntityAttributeValue$Table.
                        LOCALTRACKEDENTITYINSTANCEID).is(trackedEntityInstance)).querySingle();
    }

    /**
     * Returns a list of all trackedEntityAttributeValues for a given TEI
     *
     * @param trackedEntityInstance
     * @return
     */
    public static List<TrackedEntityAttributeValue> getTrackedEntityAttributeValues
    (long trackedEntityInstance) {
        return new Select().from(TrackedEntityAttributeValue.class).where(Condition.column
                (TrackedEntityAttributeValue$Table.LOCALTRACKEDENTITYINSTANCEID).is(trackedEntityInstance)).orderBy(TrackedEntityAttributeValue$Table.TRACKEDENTITYATTRIBUTEID).queryList();
    }

    /**
     * Returns a list of failed items from the database, or null if there are none.
     * Failed items are items that have failed to upload and sync with the server for some reason
     *
     * @return
     */
    public static List<FailedItem> getFailedItems() {
        List<FailedItem> failedItems = new Select().from(FailedItem.class).queryList();
        if (failedItems == null || failedItems.size() <= 0) return null;
        else return failedItems;
    }

    public static List<FailedItem> getFailedItems(String type) {
        return new Select().from(FailedItem.class).where(Condition.column(FailedItem$Table.ITEMTYPE).is(type)).queryList();
    }

    public static FailedItem getFailedItem(String type, long id) {
        return new Select().from(FailedItem.class).where(Condition.column(FailedItem$Table.ITEMTYPE).is(type), Condition.column(FailedItem$Table.ITEMID).is(id)).querySingle();
    }

    /**
     * Clear flags for loaded data values, deleting the status info for when data values were
     * last updated
     * Sets all flags for all loaded data values to false, and all updated dates to null
     */
    public static void clearDataValueLoadedFlags() {
        List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
            if (organisationUnit.getId() == null)
                break;
            List<Program> programsForOrgUnit = new ArrayList<>();
            List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                    (organisationUnit.getId(),
                            ProgramType.WITHOUT_REGISTRATION);
            if (programsForOrgUnitSEWoR != null)
                programsForOrgUnit.addAll(programsForOrgUnitSEWoR);

            for (Program program : programsForOrgUnit) {
                if (program.getUid() == null)
                    break;
                DateTimeManager.getInstance().deleteLastUpdated(ResourceType.EVENTS, organisationUnit.getId() + program.getUid());
            }
        }
    }

    /**
     * Loads user generated data from the server. Which data is loaded is determined by enabling
     * or disabling flags in DHIS 2.
     */
    public static void synchronizeDataValues(Context context, DhisApi dhisApi) throws APIException {
        sendLocalData(dhisApi);
        loadDataValues(context, dhisApi);
    }

    /**
     * Tries to send locally stored data to the server
     */
    public static void sendLocalData(DhisApi dhisApi) throws APIException {
        Log.d(CLASS_TAG, "sending local data");
        TrackerDataSender.sendTrackedEntityInstanceChanges(dhisApi, false);
        TrackerDataSender.sendEnrollmentChanges(dhisApi, true);
        TrackerDataSender.sendEventChanges(dhisApi);
    }

    /**
     * Tries to send locally stored events to the server
     * @param dhisApi
     * @return Map that contains the ImportSummary per each pushed event (key: event.localId)
     * @throws APIException
     */
    public static Map<Long,ImportSummary> sendEventChanges(DhisApi dhisApi)throws APIException{
        Log.d(CLASS_TAG, "sending local events");
        return TrackerDataSender.sendEventChanges(dhisApi);
    }

    /**
     * Loads datavalues from the server and stores it in local persistence.
     */
    public static void loadDataValues(Context context, DhisApi dhisApi) throws APIException {
        UiUtils.postProgressMessage(context.getString(R.string.loading_metadata));
        TrackerDataLoader.updateDataValueDataItems(context, dhisApi);
    }

    public static List<TrackedEntityInstance> queryTrackedEntityInstancesDataFromServer(DhisApi dhisApi,
                                                                                 String organisationUnitUid,
                                                                                 String programUid,
                                                                                 String queryString,
                                                                                 TrackedEntityAttributeValue... params) throws APIException {
        return TrackerDataLoader.queryTrackedEntityInstancesDataFromServer(dhisApi, organisationUnitUid, programUid, queryString, params);
    }

    public static void getTrackedEntityInstancesDataFromServer(DhisApi dhisApi, List<TrackedEntityInstance> trackedEntityInstances, boolean getEnrollments) throws APIException {
        TrackerDataLoader.getTrackedEntityInstancesDataFromServer(dhisApi, trackedEntityInstances, getEnrollments);
    }

    public static void getEnrollmentDataFromServer(DhisApi dhisApi, String uid, boolean getEvents) throws APIException {
        TrackerDataLoader.getEnrollmentDataFromServer(dhisApi, uid, getEvents);
    }

    public static void getEventDataFromServer(DhisApi dhisApi, String uid) throws APIException {
        TrackerDataLoader.getEventDataFromServer(dhisApi, uid);
    }

    public static void sendEventChanges(DhisApi dhisApi, Event event) throws APIException {
        TrackerDataSender.sendEventChanges(dhisApi, event);
    }

    public static void sendEnrollmentChanges(DhisApi dhisApi, Enrollment enrollment, boolean sendEvents) throws APIException {
        TrackerDataSender.sendEnrollmentChanges(dhisApi, enrollment, sendEvents);
    }

    public static void sendTrackedEntityInstanceChanges(DhisApi dhisApi, TrackedEntityInstance trackedEntityInstance, boolean sendEnrollments) throws APIException {
        TrackerDataSender.sendTrackedEntityInstanceChanges(dhisApi, trackedEntityInstance, sendEnrollments);
    }
}