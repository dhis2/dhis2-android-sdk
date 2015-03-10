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
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadEnrollmentsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadEventsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadTrackedEntityInstancesTask;
import org.hisp.dhis2.android.sdk.controllers.wrappers.TrackedEntityInstancesWrapper;
import org.hisp.dhis2.android.sdk.events.BaseEvent;
import org.hisp.dhis2.android.sdk.events.DataValueResponseEvent;
import org.hisp.dhis2.android.sdk.events.LoadingEvent;
import org.hisp.dhis2.android.sdk.events.ResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis2.android.sdk.persistence.models.Program;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis2.android.sdk.utils.APIException;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 04.03.15.
 */
public class DataValueLoader {

    private static final String CLASS_TAG = "DataValueLoader";


    boolean loading = false;
    boolean synchronizing = false;

    private List<OrganisationUnit> assignedOrganisationUnits;
    private List<Program> programsForOrgUnit;
    /* used while loading Tracked Entity Instances */
    private int organisationUnitCounter = -1;
    /* used while loading Tracked Entity Instances */
    private int programCounter = -1;
    private Context context;

    /**
     * Loads tracker data from server. Set update to true if you only want to load new values.
     * False if you want it all.
     * @param context
     * @param update
     */
    public void loadTrackerData(Context context, boolean update) {
        this.context = context;
        loading = true;
        synchronizing = update;
        if(Dhis2.isLoadTrackerDataEnabled(context))
            loadTrackedEntityInstances();
        else if(Dhis2.isLoadEventCaptureEnabled(context))
            loadEvents();
    }

    /**
     * called when loading of data values has finished.
     */
    public void onFinishLoading(boolean success) {
        Log.e(CLASS_TAG, "onfinishloading");
        if(success) {
            if( success ) {
                SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                LocalDate localDate = new LocalDate();
                editor.putString(Dhis2.LAST_UPDATED_DATAVALUES, localDate.toString());
                editor.commit();
            }
        }

        if(!synchronizing) {
            Dhis2.setHasLoadedInitialDataPart(context, success, Dhis2.INITIAL_DATA_LOADED_PART_DATAVALUES);
            LoadingEvent loadingEvent = new LoadingEvent(BaseEvent.EventType.onLoadDataValuesFinished);//called in Dhis2 Subscribing method
            loadingEvent.success = success;
            DataValueController.onFinishLoading(loadingEvent);
        } else {
            LoadingEvent event = new LoadingEvent(BaseEvent.EventType.onUpdateDataValuesFinished);
            DataValueController.onFinishLoading(event); //todo: not yet used but will be used to notify updates in fragments etc.
        }

        synchronizing = false;
        loading = false;
    }

    /**
     * Loads Tracked Entity Instances for assigned programs and organisation units.
     */
    void loadTrackedEntityInstances() {
        assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        organisationUnitCounter = assignedOrganisationUnits.size();
        loadTrackedEntityInstancesForAssignedOrganisationUnits();
    }

    private void loadTrackedEntityInstancesForAssignedOrganisationUnits() {
        if( programsForOrgUnit == null || programCounter <= 0) {
            programsForOrgUnit = new ArrayList<>();
            List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                    (assignedOrganisationUnits.get(organisationUnitCounter-1).getId(),
                            Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
            List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                    (assignedOrganisationUnits.get(organisationUnitCounter-1).getId(),
                            Program.SINGLE_EVENT_WITH_REGISTRATION);
            if(programsForOrgUnitMEWR!=null) programsForOrgUnit.addAll(programsForOrgUnitMEWR);
            if(programsForOrgUnitSEWR!=null) programsForOrgUnit.addAll(programsForOrgUnitSEWR);
            programCounter = programsForOrgUnit.size();
        }

        if(context != null) {
            int current = programsForOrgUnit.size() - programCounter;
            current ++;
            Dhis2.postProgressMessage(context.getString(R.string.loading_tracked_entity_instances)
                    + " " + assignedOrganisationUnits.get(organisationUnitCounter -1 ).getLabel()
                    + ": " + current + "/" + programsForOrgUnit.size());
        }

        loadTrackedEntityInstances(assignedOrganisationUnits.get(organisationUnitCounter - 1).getId(),
                programsForOrgUnit.get(programCounter - 1).getId());
    }

    /**
     * Loads Tracked Entity Instances for a given program and org unit
     */
    private void loadTrackedEntityInstances(String organisationUnit, String program) {
        final ResponseHolder<Object[]> holder = new ResponseHolder<>();
        final DataValueResponseEvent<Object[]> event = new
                DataValueResponseEvent<>(BaseEvent.EventType.loadTrackedEntityInstances);
        event.setResponseHolder(holder);
        LoadTrackedEntityInstancesTask task = new LoadTrackedEntityInstancesTask(NetworkManager.getInstance(),
                new ApiRequestCallback<Object[]>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        Log.e(CLASS_TAG, "onsuccess loadTEI");

                        try {
                            Object[] items = TrackedEntityInstancesWrapper.parseTrackedEntityInstances(response.getBody());
                            holder.setItem(items);
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        onResponse(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(event);
                    }
                }, organisationUnit, program);
        task.execute();
    }

    /**
     * Initiates loading of all enrollments for assigned org units
     */
    private void loadEnrollments() {
        assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        organisationUnitCounter = assignedOrganisationUnits.size();
        loadEnrollmentsForAssignedOrganisationUnits();
    }

    private void loadEnrollmentsForAssignedOrganisationUnits() {
        if( programsForOrgUnit == null || programCounter <= 0) {
            programsForOrgUnit = new ArrayList<>();
            List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                    (assignedOrganisationUnits.get(organisationUnitCounter-1).getId(),
                            Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
            List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                    (assignedOrganisationUnits.get(organisationUnitCounter-1).getId(),
                            Program.SINGLE_EVENT_WITH_REGISTRATION);
            if(programsForOrgUnitMEWR!=null) programsForOrgUnit.addAll(programsForOrgUnitMEWR);
            if(programsForOrgUnitSEWR!=null) programsForOrgUnit.addAll(programsForOrgUnitSEWR);
            programCounter = programsForOrgUnit.size();
        }

        loadEnrollments(assignedOrganisationUnits.get(organisationUnitCounter - 1).getId(),
                programsForOrgUnit.get(programCounter - 1).getId());
    }

    /**
     * Loads enrollments for a given org unit and program
     * @param organisationUnitId
     * @param programId
     */
    private void loadEnrollments(String organisationUnitId, String programId) {
        final ResponseHolder<List<Enrollment>> holder = new ResponseHolder<>();
        final DataValueResponseEvent<List<Enrollment>> event = new
                DataValueResponseEvent<>(BaseEvent.EventType.loadEnrollments);
        event.setResponseHolder(holder);
        LoadEnrollmentsTask task = new LoadEnrollmentsTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<Enrollment>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        Log.e(CLASS_TAG, "onsuccess loadEnrollments");

                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("enrollments");
                            if( node == null ) { /* in case there are no enrollments */
                                holder.setItem(new ArrayList<Enrollment>());
                            } else {
                                TypeReference<List<Enrollment>> typeRef =
                                        new TypeReference<List<Enrollment>>(){};
                                List<Enrollment> enrollments = Dhis2.getInstance().getObjectMapper().
                                        readValue( node.traverse(), typeRef);
                                holder.setItem(enrollments);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        onResponse(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(event);
                    }
                }, organisationUnitId, programId);
        task.execute();
    }

    /**
     * Initiates loading of Events
     */
    private void loadEvents() {
        assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        organisationUnitCounter = assignedOrganisationUnits.size();
        loadEventsForAssignedOrganisationUnits();
    }

    private void loadEventsForAssignedOrganisationUnits() {
        if( programsForOrgUnit == null || programCounter <= 0) {
            programsForOrgUnit = new ArrayList<>();
            if(Dhis2.isLoadTrackerDataEnabled(context)) {
                List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                        (assignedOrganisationUnits.get(organisationUnitCounter - 1).getId(),
                                Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                        (assignedOrganisationUnits.get(organisationUnitCounter - 1).getId(),
                                Program.SINGLE_EVENT_WITH_REGISTRATION);
                if (programsForOrgUnitMEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitMEWR);
                if (programsForOrgUnitSEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWR);
            }
            if(Dhis2.isLoadEventCaptureEnabled(context)) {
                List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                        (assignedOrganisationUnits.get(organisationUnitCounter - 1).getId(),
                                Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                if(programsForOrgUnitSEWoR!=null) programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
            }
            programCounter = programsForOrgUnit.size();
        }

        if(programCounter > 0) {

            if(context != null) {
                int current = programsForOrgUnit.size() - programCounter;
                current ++;
                Dhis2.postProgressMessage(context.getString(R.string.loading_events)
                        + " " + assignedOrganisationUnits.get(organisationUnitCounter -1 ).getLabel()
                        + ": " + current + "/" + programsForOrgUnit.size());
            }

            loadEvents(assignedOrganisationUnits.get(organisationUnitCounter - 1).getId(),
                    programsForOrgUnit.get(programCounter - 1).getId());
        } else {
            organisationUnitCounter--;
            if(organisationUnitCounter > 0) loadEventsForAssignedOrganisationUnits();
            else onFinishLoading(true);
        }
    }

    private void loadEvents(String organisationUnitId, String programId) {
        final ResponseHolder<List<Event>> holder = new ResponseHolder<>();
        final DataValueResponseEvent<List<Event>> event = new
                DataValueResponseEvent<>(BaseEvent.EventType.loadEvents);
        event.setResponseHolder(holder);
        LoadEventsTask task = new LoadEventsTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<Event>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        Log.e(CLASS_TAG, "onsuccess loadEvents");

                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("events");
                            if( node == null ) { /* in case there are no enrollments */
                                holder.setItem(new ArrayList<Event>());
                            } else {
                                TypeReference<List<Event>> typeRef =
                                        new TypeReference<List<Event>>(){};
                                List<Event> events = Dhis2.getInstance().getObjectMapper().
                                        readValue( node.traverse(), typeRef);
                                holder.setItem(events);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        onResponse(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(event);
                    }
                }, organisationUnitId, programId, synchronizing);
        task.execute();
    }

    public void onResponse(ResponseEvent responseEvent) {
        if(responseEvent.getResponseHolder().getApiException() == null) {
            if (responseEvent.eventType == BaseEvent.EventType.loadTrackedEntityInstances) {
                Object[] items = (Object[]) responseEvent.getResponseHolder().getItem();
                List<TrackedEntityInstance> trackedEntityInstances = (List<TrackedEntityInstance>) items[0];
                List<TrackedEntityAttributeValue> values = (List<TrackedEntityAttributeValue>) items[1];
                if(trackedEntityInstances != null) {
                    for(TrackedEntityInstance tei: trackedEntityInstances) tei.save(false);
                    for(TrackedEntityAttributeValue value: values) value.save(false);
                }
                programCounter--;
                if( programCounter <= 0) {
                    organisationUnitCounter--;
                    if( organisationUnitCounter <= 0 ) loadEnrollments();
                    else loadTrackedEntityInstancesForAssignedOrganisationUnits();
                } else loadTrackedEntityInstancesForAssignedOrganisationUnits();
            } else if (responseEvent.eventType == BaseEvent.EventType.loadEnrollments) {
                List<Enrollment> enrollments = (List<Enrollment>) responseEvent.getResponseHolder().getItem();
                for(Enrollment enrollment: enrollments) enrollment.save(false);
                programCounter--;
                if( programCounter <= 0) {
                    organisationUnitCounter--;
                    if( organisationUnitCounter <= 0 ) loadEvents();
                    else loadEnrollmentsForAssignedOrganisationUnits();
                } else loadEnrollmentsForAssignedOrganisationUnits();
            } else if (responseEvent.eventType == BaseEvent.EventType.loadEvents) {
                List<Event> events = (List<Event>) responseEvent.getResponseHolder().getItem();
                for(Event event: events) {
                    event.save(false);
                    if(event.dataValues != null) {
                        for(DataValue dataValue: event.dataValues) {
                            dataValue.event = event.event;
                            dataValue.save(false);
                        }
                    }
                }
                programCounter--;
                if( programCounter <= 0) {
                    organisationUnitCounter--;
                    if( organisationUnitCounter <= 0 ) onFinishLoading(true);
                    else loadEventsForAssignedOrganisationUnits();
                } else loadEventsForAssignedOrganisationUnits();
            }
        } else {
            //TODO: handle exceptions..
            if(responseEvent.getResponseHolder() != null && responseEvent.getResponseHolder().getApiException() != null)
                responseEvent.getResponseHolder().getApiException().printStackTrace();
            onFinishLoading(false);
        }
    }
}
