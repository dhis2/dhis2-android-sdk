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
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadEventsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadSystemInfoTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadTrackedEntityInstancesTask;
import org.hisp.dhis.android.sdk.controllers.tasks.QueryTrackedEntityInstancesTask;
import org.hisp.dhis.android.sdk.events.BaseEvent;
import org.hisp.dhis.android.sdk.events.DataValueResponseEvent;
import org.hisp.dhis.android.sdk.events.InvalidateEvent;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.utils.APIException;
import org.hisp.dhis.android.sdk.utils.Utils;
import org.hisp.dhis.android.sdk.utils.support.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 04.03.15.
 */
public final class DataValueLoader {

    private static final String CLASS_TAG = "DataValueLoader";

    public static final String EVENTS = "events";
    public static final String TRACKED_ENTITY_INSTANCES = "trackedentityinstances";
    public static final String ENROLLMENTS = "enrollments";

    private final static DataValueLoader dataValueLoader;

    static {
        dataValueLoader = new DataValueLoader();
    }

    private DataValueLoader() {

    }

    public static DataValueLoader getInstance() {
        return dataValueLoader;
    }

    boolean loading = false;

    private SystemInfo systemInfo;

    /**
     * Loads data values from server. Set update to true if you only want to load new values.
     * False if you want it all.
     *
     * @param context
     * @param update
     */
    public static void loadDataValues(final Context context, final boolean update, ApiRequestCallback callback) {
        if (Dhis2.isLoading()) {
           callback.onSuccess(null);
           return;
        }
        getInstance().systemInfo = null;
        getInstance().loading = true;
        final OnFinishLoadingCallback onFinishLoadingCallback = new OnFinishLoadingCallback(callback, context);
        LoadSystemInfoCallback loadSystemInfoCallback = new LoadSystemInfoCallback(context, onFinishLoadingCallback, update);
        loadSystemInfo(loadSystemInfoCallback);
    }

    private static final class OnFinishLoadingCallback implements ApiRequestCallback {
        private final ApiRequestCallback callback;
        private final Context context;

        private OnFinishLoadingCallback(ApiRequestCallback callback, Context context) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            Log.e(CLASS_TAG, "onfinishloadingsuccess");
            SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Dhis2.LAST_UPDATED_DATAVALUES, getInstance().systemInfo.getServerDate());
            editor.commit();

            InvalidateEvent event = new InvalidateEvent(InvalidateEvent.EventType.dataValuesLoaded);
            Dhis2Application.getEventBus().post(event);

            getInstance().loading = false;
            callback.onSuccess(responseHolder);
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            Log.e(CLASS_TAG, "onfinishloadingfailure");
            InvalidateEvent event = new InvalidateEvent(InvalidateEvent.EventType.dataValuesLoaded);
            Dhis2Application.getEventBus().post(event);

            getInstance().loading = false;
            callback.onFailure(responseHolder);
        }
    }

    private static void loadSystemInfo(final ApiRequestCallback<SystemInfo> parentCallback) {
        LoadSystemInfoTask task = new LoadSystemInfoTask(NetworkManager.getInstance(),
                new ApiRequestCallback<SystemInfo>() {
                    @Override
                    public void onSuccess(ResponseHolder<SystemInfo> holder) {

                        try {
                            SystemInfo systemInfo = Dhis2.getInstance().getObjectMapper().
                                    readValue(holder.getResponse().getBody(), SystemInfo.class);
                            holder.setItem(systemInfo);
                            parentCallback.onSuccess(holder);
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(holder.getResponse().getUrl(), holder.getResponse(), e));
                            parentCallback.onFailure(holder);
                        }
                    }

                    @Override
                    public void onFailure(ResponseHolder<SystemInfo> holder) {
                        parentCallback.onFailure(holder);
                    }
                });
        task.execute();
    }

    private static class LoadSystemInfoCallback implements ApiRequestCallback<SystemInfo> {

        private final Context context;
        private final ApiRequestCallback callback;
        private final boolean update;

        private LoadSystemInfoCallback(Context context, ApiRequestCallback callback, boolean update) {
            this.context = context;
            this.callback = callback;
            this.update = update;
        }

        @Override
        public void onSuccess(ResponseHolder<SystemInfo> responseHolder) {
            getInstance().systemInfo = responseHolder.getItem();
            Log.d(CLASS_TAG, "got system info " + getInstance().systemInfo.getServerDate());
            loadItem(context, update, callback);
        }

        @Override
        public void onFailure(ResponseHolder<SystemInfo> responseHolder) {
            if (responseHolder != null && responseHolder.getApiException() != null)
                responseHolder.getApiException().printStackTrace();
            callback.onFailure(responseHolder);
        }
    }

    /**
     * Queries the server for a list of TrackedEntityInstances, which is returned in the given callbacks Response.getItem as a List<TrackedEntityInstances>
     *
     * @param callback
     * @param orgUnit
     * @param program    can be null
     * @param parameters can be null
     */
    public static void queryTrackedEntityInstances(ApiRequestCallback callback, String orgUnit, String program, String queryString, TrackedEntityAttributeValue... parameters) {
        QueryTrackedEntityInstancesTask queryTask = new QueryTrackedEntityInstancesTask(NetworkManager.getInstance(), callback, orgUnit, program, queryString, parameters);
        queryTask.execute();
    }

    public static void loadTrackedEntityInstances(ApiRequestCallback<TrackedEntityInstancesResultHolder> callback, List<TrackedEntityInstance> trackedEntityInstances) {
        LoadTrackedEntityInstancesTask task = new LoadTrackedEntityInstancesTask(NetworkManager.getInstance(), callback, trackedEntityInstances);
        task.execute();
    }

    private static void loadItem(final Context context, final boolean synchronizing, final ApiRequestCallback callback) {

        List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        Hashtable<String, List<Program>> programsForOrganisationUnits = new Hashtable<>();

        String currentLoadingDate = getInstance().systemInfo.getServerDate();
        DateTime currentDateTime = DateTimeFormat.forPattern(DateUtils.LONG_DATE_FORMAT.toPattern()).parseDateTime(currentLoadingDate);

        /**
         * Loading Single Events without registration
         */
        if (Dhis2.isLoadFlagEnabled(context, EVENTS)) {

            for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
                if (organisationUnit.getId() == null || organisationUnit.getId().length() == Utils.randomUUID.length())
                    continue;

                List<Program> programsForOrgUnit = new ArrayList<>();
                if (Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITHOUT_REGISTRATION)) {
                    List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                    if (programsForOrgUnitSEWoR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
                }
                programsForOrganisationUnits.put(organisationUnit.getId(), programsForOrgUnit);
            }

            for (final OrganisationUnit organisationUnit : assignedOrganisationUnits) {
                if (organisationUnit.getId() == null || organisationUnit.getId().length() == Utils.randomUUID.length())
                    continue;

                for (final Program program : programsForOrganisationUnits.get(organisationUnit.getId())) {
                    if (program.getId() == null || program.getId().length() == Utils.randomUUID.length())
                        continue;
                        
                    String lastUpdatedString = getLastUpdatedDateForDataValueItem(context,
                        EVENTS + organisationUnit.getId() + program.getId());
                    DateTime updatedDateTime = null;
                    if( lastUpdatedString != null ) {
                        updatedDateTime = DateTimeFormat.forPattern(DateUtils.LONG_DATE_FORMAT.toPattern()).parseDateTime(lastUpdatedString);
                    }

                    if (!isDataValueItemLoaded(context, EVENTS + organisationUnit.getId() + program.getId())
                            || ( synchronizing &&
                            ( lastUpdatedString == null || updatedDateTime.isBefore(currentDateTime) ) ) ) {
                        Dhis2.postProgressMessage(context.getString(R.string.loading_events) + ": "
                                + organisationUnit.getLabel() + ": " + program.getName());
                                
                        ApiRequestCallback loadEventsCallback = new ApiRequestCallback<List<Event>>() {
                            @Override
                            public void onSuccess(ResponseHolder<List<Event>> responseHolder) {
                                List<Event> events = responseHolder.getItem();
                                saveEvents(events, synchronizing);

                                flagDataValueItemUpdated(context, EVENTS + organisationUnit.getId() + program.getId(), getInstance().systemInfo.getServerDate());
                                flagDataValueItemLoaded(context, EVENTS + organisationUnit.getId() + program.getId(), true);
                                loadItem(context, synchronizing, callback);
                            }

                            @Override
                            public void onFailure(ResponseHolder<List<Event>> responseHolder) {
                                callback.onFailure(responseHolder);
                                //todo this may completely stop loading unnecessarily if it fails for one orgunit+program, implement a way to "continue"
                                //todo loading probably using an iterator for the orgunits
                            }
                        };
                        loadEvents(loadEventsCallback, organisationUnit.getId(), program.getId(), synchronizing);
                        return;
                    }
                }
            }
        }
        callback.onSuccess(null);
    }

    /**
     * Loads events for a given org unit and program
     *
     * @param organisationUnitId
     * @param programId
     */
    private static void loadEvents(final ApiRequestCallback callback, String organisationUnitId, String programId, boolean synchronizing) {
        final ResponseHolder<List<Event>> holder = new ResponseHolder<>();
        final DataValueResponseEvent<List<Event>> event = new
                DataValueResponseEvent<>(BaseEvent.EventType.loadEvents);
        event.setResponseHolder(holder);
        LoadEventsTask task = new LoadEventsTask(NetworkManager.getInstance(),
                new LoadEventsCallback(callback), organisationUnitId, programId, synchronizing);
        task.execute();
    }

    private static class LoadEventsCallback implements ApiRequestCallback<List<Event>> {

        private final ApiRequestCallback<List<Event>> parentCallback;

        public LoadEventsCallback(ApiRequestCallback<List<Event>> parentCallback) {
            this.parentCallback = parentCallback;
        }

        @Override
        public void onSuccess(ResponseHolder<List<Event>> holder) {
            Log.d(CLASS_TAG, "onsuccess loadEvents");
            List<Event> events;
            try {
                JsonNode node = Dhis2.getInstance().getObjectMapper().
                        readTree(holder.getResponse().getBody());
                node = node.get("events");
                if (node == null) { /* in case there are no enrollments */
                    events = new ArrayList<>();
                } else {
                    TypeReference<List<Event>> typeRef =
                            new TypeReference<List<Event>>() {
                            };
                    events = Dhis2.getInstance().getObjectMapper().
                            readValue(node.traverse(), typeRef);
                    holder.setItem(events);
                }

            } catch (IOException e) {
                e.printStackTrace();
                if (holder.getApiException() == null) {
                    holder.setApiException(APIException.conversionError(holder.getResponse().getUrl(), holder.getResponse(), e));
                }
                events = new ArrayList<>();
            }
            holder.setItem(events);
            parentCallback.onSuccess(holder);
        }

        @Override
        public void onFailure(ResponseHolder<List<Event>> holder) {
            parentCallback.onSuccess(holder);
        }
    }

    /**
     * Saves a given list of Events to the on-device database
     *
     * @param events
     */
    public static void saveEvents(List<Event> events, boolean synchronizing) {
        for (Event event : events) {
            if (synchronizing) {
                //todo: implement different handling if synchronizing than if doing 1st load
            }

            //check if there have been changes made locally since last update.
            //if there are local updates, don't overwrite with data from server.
            Event localEvent = DataValueController.getEventByUid(event.getEvent());
            if (localEvent != null) {
                event.setLocalId(localEvent.getLocalId());
                event.setLocalEnrollmentId(localEvent.getLocalEnrollmentId());
                if (localEvent.isFromServer() == true) {
                    event.update();
                }
            } else {
                //check if there is an enrollment for this event stored on the device
                //and store the localId of the enrollment
                //(there will not be enrollment if its a single event without registration)
                Enrollment enrollment = DataValueController.getEnrollment(event.getEnrollment());

                if (enrollment != null) {
                    event.setLocalEnrollmentId(enrollment.getLocalId());
                } else {//could be single event without registration
                }
                event.save();
            }
        }
    }

    /**
     * Saves a list of TrackedEntityInstances, along with Enrollments and Events to the on-device database
     *
     * @param trackedEntityInstances
     * @param enrollments
     * @param events
     * @param synchronizing
     * @param currentOrganisationUnit
     */
    public static void saveTrackedEntityInstances(List<TrackedEntityInstance> trackedEntityInstances, List<Enrollment> enrollments, List<Event> events, boolean synchronizing, String currentOrganisationUnit) {
        Log.d(CLASS_TAG, "saving teis: " + trackedEntityInstances.size());
        Log.d(CLASS_TAG, "saving enrollments: " + enrollments.size());
        Log.d(CLASS_TAG, "saving events: " + events.size());
        if (trackedEntityInstances != null) {
            if (synchronizing) {
                //todo: implement different handling if synchronizing than if doing 1st load
            }

            for (TrackedEntityInstance tei : trackedEntityInstances) {
                tei.save();
                if (tei.getAttributes() != null) {
                    for (TrackedEntityAttributeValue value : tei.getAttributes()) {
                        if (value != null) {
                            value.setTrackedEntityInstanceId(tei.getTrackedEntityInstance());
                            value.setLocalTrackedEntityInstanceId(tei.getLocalId());
                        }
                        value.async().save();
                    }
                }
                if (tei.getRelationships() != null) {
                    for (Relationship relationship : tei.getRelationships()) {
                        if (relationship != null) {
                            relationship.async().save();
                        }
                    }
                }
            }
        }
        for (Enrollment enrollment : enrollments) {
            if (synchronizing) {
                //todo: implement different handling if synchronizing than if doing 1st load
            }
            enrollment.setOrgUnit(currentOrganisationUnit);
            TrackedEntityInstance tei = DataValueController
                    .getTrackedEntityInstance(enrollment.getTrackedEntityInstance());
            if (tei != null) {
                enrollment.setLocalTrackedEntityInstanceId(tei.getLocalId());
            }
            enrollment.save();
        }

        saveEvents(events, synchronizing);
    }

    /**
     * Flags a DataValue item like Events or Enrollments to indicate whether or not it has been loaded.
     * Can also be set for a UID for example for an individual Program.
     *
     * @param item
     * @param loaded
     */
    private static void flagDataValueItemLoaded(Context context, String item, boolean loaded) {
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Dhis2.LOADED + item, loaded);
        editor.commit();
    }

    /**
     * Returns a boolean indicating whether or not a DataValue item has been loaded successfully.
     *
     * @param item
     * @return
     */
    private static boolean isDataValueItemLoaded(Context context, String item) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(Dhis2.LOADED + item, false);
    }

    private static void flagDataValueItemUpdated(Context context, String item, String dateTime) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Dhis2.UPDATED + item, dateTime);
        editor.commit();
    }

    /**
     * returns the date in a string for the last time an item was updated
     *
     * @param context
     * @param item
     * @return
     */
    private static String getLastUpdatedDateForDataValueItem(Context context, String item) {
        if (context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        Date date = DateUtils.parseDate(prefs.getString(Dhis2.UPDATED + item, null));
        return DateUtils.getLongDateString(date);
    }

    /**
     * Goes through all assigned programs and checks if they are loaded if loading is enabled.
     * Returns false if some programs have not been loaded, but have been flagged to load.
     *
     * @param context
     * @return
     */
    public static boolean isEventsLoaded(Context context) {
        List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
            if (organisationUnit.getId() == null)
                break;

            List<Program> programsForOrgUnit = new ArrayList<>();
            if (Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITHOUT_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                if (programsForOrgUnitSEWoR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
            }

            for (Program program : programsForOrgUnit) {
                if (program.getId() == null)
                    break;

                if (!isDataValueItemLoaded(context, EVENTS + organisationUnit.getId() + program.getId())) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void dataValueIntegrityCheck() {
        /*Log.d(CLASS_TAG, "Running data value integrity check");


        List<DataValue> dataValues = new Select().from(DataValue.class).where(
                Condition.column(DataValue$Table.DATAELEMENT).isNull()).
                and(Condition.column(DataValue$Table.EVENT).isNull()).
                and(Condition.column(DataValue$Table.LOCALEVENTID).isNull()).queryList();
        if(dataValues == null || dataValues.size() < 1 )
        {
            Log.d(CLASS_TAG, "Data values are valid");
        }
        else
        {
            Log.d(CLASS_TAG, "Data values are NOT valid");
            for(DataValue dataValue : dataValues)
                dataValue.delete();
        }

        List<Enrollment> enrollments = new Select().from(Enrollment.class).where(
                Condition.column(Enrollment$Table.ORGUNIT).isNull()).
                or(Condition.column(Enrollment$Table.PROGRAM).isNull()).
                and(Condition.column(Enrollment$Table.TRACKEDENTITYINSTANCE).isNull()).
                and(Condition.column(Enrollment$Table.LOCALTRACKEDENTITYINSTANCEID).isNull()).queryList();

        if(enrollments == null || enrollments.size() < 1 )
        {
            Log.d(CLASS_TAG, "Enrollments are valid");
        }
        else
        {
            Log.d(CLASS_TAG, "Enrollments NOT valid");

            for(Enrollment enrollment : enrollments)
                enrollment.delete();
        }

        List<Event> events = new Select().from(Event.class).where(
                Condition.column(Event$Table.ENROLLMENT).isNull()).
                or(Condition.column(Event$Table.PROGRAMID).isNull()).
                or(Condition.column(Event$Table.PROGRAMSTAGEID).isNull()).
                or(Condition.column(Event$Table.ORGANISATIONUNITID).isNull()).
                or(Condition.column(Event$Table.TRACKEDENTITYINSTANCE).isNull()).queryList();

        if(events == null || events.size() < 1 )
        {
            Log.d(CLASS_TAG, "Events are valid ");
        }
        else
        {
            Log.d(CLASS_TAG, "Events are NOT valid");
            for(Event event : events)
            {
                event.delete();
                Log.d(CLASS_TAG, "Event: Enrollment: " + event.getEnrollment() + " ProgramID: " + event.getProgramId() +
                " ProgramStageId: " + event.getProgramStageId() + " OrgUnitId: " + event.getOrganisationUnitId() +
                        " TrackedEntityInstance: " + event.getTrackedEntityInstance());
            }
        }


        List<TrackedEntityInstance> trackedEntityInstances = new Select().from(TrackedEntityInstance.class).where(
                Condition.column(TrackedEntityInstance$Table.TRACKEDENTITY).isNull()).
                or(Condition.column(TrackedEntityInstance$Table.ORGUNIT).isNull()).queryList();
        if(trackedEntityInstances == null || trackedEntityInstances.size() < 1 )
        {
            Log.d(CLASS_TAG, "Trackedentity instances are valid");
        }
        else
        {
            Log.d(CLASS_TAG, "Tracked entity instances are NOT valid");
            for(TrackedEntityInstance trackedEntityInstance : trackedEntityInstances)
                trackedEntityInstance.delete();
        }

        List<TrackedEntityAttributeValue> trackedEntityAttributeValues = new Select().from(TrackedEntityAttributeValue.class).where(
                Condition.column(TrackedEntityAttributeValue$Table.TRACKEDENTITYATTRIBUTEID).isNull()).
                or(Condition.column(TrackedEntityAttributeValue$Table.TRACKEDENTITYINSTANCEID).isNull()).queryList();

        if(trackedEntityAttributeValues == null || trackedEntityAttributeValues.size() < 1 )
        {
            Log.d(CLASS_TAG, "Trackedentity attribute values are valid");
        }
        else
        {
            Log.d(CLASS_TAG, "Trackedentity attribute values are NOT valid");
            for(TrackedEntityAttributeValue trackedEntityAttributeValue : trackedEntityAttributeValues)
                trackedEntityAttributeValue.delete();
        }*/
    }

    /**
     * Sets all flags for all loaded data values to false, and all updated dates to null
     *
     * @param context
     */
    static void clearDataValueLoadedFlags(Context context) {
        List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
            if (organisationUnit.getId() == null)
                break;

            String assignedOrganisationUnit = organisationUnit.getId();
            List<Program> programsForOrgUnit = new ArrayList<>();
            if (Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITHOUT_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                if (programsForOrgUnitSEWoR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
            }

            for (Program program : programsForOrgUnit) {
                if (program.getId() == null)
                    break;

                String programId = program.getId();
                flagDataValueItemLoaded(context, EVENTS + assignedOrganisationUnit + programId, false);
                //flagDataValueItemLoaded(TRACKED_ENTITY_INSTANCES+assignedOrganisationUnit/*+programId*/, false);
                //flagDataValueItemLoaded(ENROLLMENTS+assignedOrganisationUnit+programId, false);
                flagDataValueItemUpdated(context, EVENTS + assignedOrganisationUnit + programId, null);
                //flagDataValueItemUpdated(context, TRACKED_ENTITY_INSTANCES+assignedOrganisationUnit/*+programId*/, null);
                //flagDataValueItemUpdated(context, ENROLLMENTS+assignedOrganisationUnit+programId, null);
            }
        }
    }
}
