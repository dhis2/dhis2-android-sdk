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

package org.hisp.dhis2.android.sdk.controllers.datavalues;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.squareup.otto.Subscribe;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.events.DataValueResponseEvent;
import org.hisp.dhis2.android.sdk.events.LoadingEvent;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue$Table;
import org.hisp.dhis2.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis2.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis2.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttributeValue$Table;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityInstance$Table;

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

    /**
     * Returns a list of enrollments for a given program and tracked entity instance
     * @param program
     * @param trackedEntityInstance
     * @return
     */
    public static List<Enrollment> getEnrollments(String program, String trackedEntityInstance) {
        List<Enrollment> enrollments = new Select().from(Enrollment.class).
                where(Condition.column(Enrollment$Table.PROGRAM).is(program)).
                and(Condition.column(Enrollment$Table.TRACKEDENTITYINSTANCE).
                        is(trackedEntityInstance)).queryList();
        return enrollments;
    }

    public static Enrollment getEnrollment(String enrollment) {
        return new Select().from(Enrollment.class).where(Condition.column
                (Enrollment$Table.ENROLLMENT).is(enrollment)).querySingle();
    }

    /**
     * Returns a list of events for the given server-assigned UID. Note that if possible,
     * getEventsByEnrollment(long) should always be used if possible, as the UID may change if the
     * enrollment is created locally on the device, and then synced with the server.
     * @param enrollment
     * @return
     */
    public static List<Event> getEventsByEnrollment(String enrollment) {
        return Select.all(Event.class, Condition.column(Event$Table.ENROLLMENT).is(enrollment));
    }

    /**
     * returns a list of events for a given localEnrollmentId
     * @param localEnrollmentId
     * @return
     */
    public static List<Event> getEventsByEnrollment(long localEnrollmentId) {
        return Select.all(Event.class, Condition.column(Event$Table.LOCALENROLLMENTID).is(localEnrollmentId));
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
        List<Event> result = Select.all(Event.class, Condition.column(Event$Table.LOCALID).is(localId));
        if( result != null && !result.isEmpty() ) {
            return result.get(0);
        }
        else return null;
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
        List<Event> result = Select.all(Event.class, Condition.column(Event$Table.EVENT).is(event));
        if( result != null && !result.isEmpty() ) {
            return result.get(0);
        }
        else return null;
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
     * Returns a list of failed items from the database, or null if there are none.
     * Failed items are items that have failed to upload and sync with the server for some reason
     * @return
     */
    public static List<FailedItem> getFailedItems() {
        List<FailedItem> failedItems = Select.all(FailedItem.class);
        if(failedItems == null || failedItems.size() <= 0) return null;
        else return failedItems;
    }

    public void clearDataValueLoadedFlags(Context context) {
        dataValueLoader.clearDataValueLoadedFlags(context);
    }

    /**
     * Loads user generated data from the server. Which data is loaded is determined by enabling
     * or disabling flags in DHIS 2. Avoid calling this method directly, use Dhis2.sendLocalValues to
     * be thread safe.
     */
    public void synchronizeDataValues(Context context) {
        sendLocalData(context);
    }

    /**
     * Loads Tracker Related data including Tracked Entity Instances, Enrollments and Events
     * for the current user's assigned programs and organisation units. Set update to true if you only want to load new values.
     * False if you want it all.
     * @param context
     * @param update
     */
    public void loadDataValues(Context context, boolean update) {
        //if(dataValueSender.sending || dataValueLoader.loading || Dhis2.getInstance().getMetaDataController().isLoading() ||
        //        Dhis2.getInstance().getMetaDataController().isSynchronizing()) return; //todo: implement a global checker to see if loading is occurring.
        dataValueLoader.loadDataValues(context, update);
    }

    /**
     * Tries to send locally stored data to the server
     */
    public void sendLocalData(Context context) {
        Log.d(CLASS_TAG, "sending local data");
        if( dataValueSender.sending || dataValueLoader.loading || Dhis2.getInstance().getMetaDataController().isLoading() ||
                Dhis2.getInstance().getMetaDataController().isSynchronizing()) return;
        dataValueSender.sendLocalData(context);
    }


    /* called from dataValueLoader */
    static void onFinishLoading(LoadingEvent event) {
        Dhis2Application.bus.post(event); //subscribed to in Dhis2
    }

    public static void onFinishSending() {

    }

    @Subscribe
    public void onResponse(DataValueResponseEvent responseEvent) {
        Log.e(CLASS_TAG, "onResponse");
    }

    public boolean isSending() {
        return dataValueSender.sending;
    }
    public boolean isLoading() { return dataValueLoader.loading; }
}