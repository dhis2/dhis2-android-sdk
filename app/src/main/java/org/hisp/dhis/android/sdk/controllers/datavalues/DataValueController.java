/*
 *  Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.controllers.datavalues;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem$Table;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;
import org.hisp.dhis.android.sdk.utils.APIException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 * Handles management of data values
 */
public class DataValueController {

    private static final String CLASS_TAG = "DataValueController";

    private DataValueLoader dataValueLoader;
    private DataValueSender dataValueSender;


    public DataValueController() {
        Dhis2Application.bus.register(this);
        dataValueLoader = new DataValueLoader();
        dataValueSender = new DataValueSender();
    }

    public static List<Enrollment> getEnrollments(String program, String organisationUnit) {
        return new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.PROGRAM).
                is(program)).and(Condition.column(Enrollment$Table.ORGUNIT).is(organisationUnit)).
                orderBy(false, Enrollment$Table.DATEOFENROLLMENT).
                queryList();
    }

    public static List<Enrollment> getEnrollments(TrackedEntityInstance trackedEntityInstance) {
        return new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.LOCALTRACKEDENTITYINSTANCEID).
                is(trackedEntityInstance.localId)).queryList();
    }

    /**
     * Returns a list of enrollments for a given program and tracked entity instance
     * @param program
     * @param trackedEntityInstance
     * @return
     */
    public static List<Enrollment> getEnrollments(String program, TrackedEntityInstance trackedEntityInstance) {
        List<Enrollment> enrollments = new Select().from(Enrollment.class).
                where(Condition.column(Enrollment$Table.PROGRAM).is(program)).
                and(Condition.column(Enrollment$Table.LOCALTRACKEDENTITYINSTANCEID).
                        is(trackedEntityInstance.localId)).queryList();
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
     * @param enrollment
     * @return
     */
    public static List<Event> getEventsByEnrollment(String enrollment) {
        return new Select().from(Event.class).where(Condition.column(Event$Table.ENROLLMENT).is(enrollment)).queryList();
    }

    /**
     * returns a list of events for a given localEnrollmentId
     * @param localEnrollmentId
     * @return
     */
    public static List<Event> getEventsByEnrollment(long localEnrollmentId) {
        return new Select().from(Event.class).where(Condition.column(Event$Table.LOCALENROLLMENTID).is(localEnrollmentId)).queryList();
    }

    /**
     * Returns a list of events for a given org unit and program
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
     * @param localId
     * @return
     */
    public static Event getEvent(long localId) {
        return new Select().from(Event.class).where(Condition.column(Event$Table.LOCALID).is(localId)).querySingle();
    }

    /**
     * Returns an Event for a given enrollment and program stage
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
    public static List<TrackedEntityAttributeValue> getProgramTrackedEntityAttributeValues(Program program, TrackedEntityInstance trackedEntityInstance)
    {
        List<TrackedEntityAttributeValue> programTrackedEntityAttributeValues = new ArrayList<>();
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = MetaDataController.getProgramTrackedEntityAttributes(program.id);

        for(ProgramTrackedEntityAttribute ptea : programTrackedEntityAttributes)
        {
            TrackedEntityAttributeValue v = DataValueController.getTrackedEntityAttributeValue
                    (ptea.trackedEntityAttribute, trackedEntityInstance.localId);
            if (v != null && v.getValue() != null && !v.getValue().isEmpty()) {
                programTrackedEntityAttributeValues.add(v);
            }
        }
        return programTrackedEntityAttributeValues;
    }


    /**
     * Returns a tracked entity attribute value for a given trackedentityattribute and trackedEntityInstance
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
     * @param trackedEntityInstance
     * @return
     */
    public static List<TrackedEntityAttributeValue> getTrackedEntityAttributeValues
    (long trackedEntityInstance) {
        return new Select().from(TrackedEntityAttributeValue.class).where(Condition.column
                (TrackedEntityAttributeValue$Table.LOCALTRACKEDENTITYINSTANCEID).is(trackedEntityInstance)).queryList();
    }

    /**
     * Returns a list of failed items from the database, or null if there are none.
     * Failed items are items that have failed to upload and sync with the server for some reason
     * @return
     */
    public static List<FailedItem> getFailedItems() {
        List<FailedItem> failedItems = new Select().from(FailedItem.class).queryList();
        if(failedItems == null || failedItems.size() <= 0) return null;
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
     * @param context
     */
    public void clearDataValueLoadedFlags(Context context) {
        dataValueLoader.clearDataValueLoadedFlags(context);
    }

    /**
     * Loads user generated data from the server. Which data is loaded is determined by enabling
     * or disabling flags in DHIS 2. Avoid calling this method directly, use Dhis2.sendLocalValues to
     * be thread safe.
     */
    public void synchronizeDataValues(final Context context, final ApiRequestCallback parentCallback) {
        ApiRequestCallback callback = new ApiRequestCallback() {
            private ApiRequestCallback callback;
            {
                this.callback = parentCallback;
            }
            @Override
            public void onSuccess(Response response) {
                loadDataValues(context, true, callback);
            }

            @Override
            public void onFailure(APIException exception) {
                callback.onFailure(exception);
            }
        };
        sendLocalData(context, callback);
    }

    /**
     * Loads Tracker Related data including Tracked Entity Instances, Enrollments and Events
     * for the current user's assigned programs and organisation units. Set update to true if you only want to load new values.
     * False if you want it all.
     * @param context
     * @param update
     */
    public void loadDataValues(Context context, boolean update, ApiRequestCallback callback) {
        dataValueLoader.loadDataValues(context, update, callback);
    }

    /**
     * Tries to send locally stored data to the server
     */
    public void sendLocalData(Context context, ApiRequestCallback callback) {
        Log.d(CLASS_TAG, "sending local data");
        dataValueSender.sendLocalData(context, callback);
    }

    public void dataValueIntegrityCheck()
    {
        dataValueLoader.dataValueIntegrityCheck();
    }

    public boolean isSending() {
        return dataValueSender.sending;
    }
    public boolean isLoading() { return dataValueLoader.loading; }
}