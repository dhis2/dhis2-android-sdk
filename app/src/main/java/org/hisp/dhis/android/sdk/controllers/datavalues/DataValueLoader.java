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
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadEnrollmentsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadEventsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadSystemInfoTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadTrackedEntityInstancesTask;
import org.hisp.dhis.android.sdk.controllers.wrappers.TrackedEntityInstancesWrapper;
import org.hisp.dhis.android.sdk.events.BaseEvent;
import org.hisp.dhis.android.sdk.events.DataValueResponseEvent;
import org.hisp.dhis.android.sdk.events.InvalidateEvent;
import org.hisp.dhis.android.sdk.events.ResponseEvent;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem$Table;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary$Table;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnitProgramRelationship$Table;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.Program$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageSection;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageSection$Table;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute$Table;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;
import org.hisp.dhis.android.sdk.utils.APIException;
import org.hisp.dhis.android.sdk.utils.Utils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Simen Skogly Russnes on 04.03.15.
 */
public class DataValueLoader {

    private static final String CLASS_TAG = "DataValueLoader";

    public static final String EVENTS = "events";
    public static final String TRACKED_ENTITY_INSTANCES = "trackedentityinstances";
    public static final String ENROLLMENTS = "enrollments";
    private String randomUUID = Dhis2.QUEUED + UUID.randomUUID().toString(); // for integrity check

    boolean loading = false;
    static boolean synchronizing = false;
    private int loadedEventCounter = 0;
    private Context context;

    /* Used to keep a reference to which orgunit/program datavalues is loaded for*/
    private String currentOrganisationUnit;
    private String currentProgram;

    private SystemInfo systemInfo;

    private ApiRequestCallback callback;

    /**
     * Loads data values from server. Set update to true if you only want to load new values.
     * False if you want it all.
     * @param context
     * @param update
     */
    public void loadDataValues(Context context, boolean update, ApiRequestCallback callback) {
        if(loading) return;
        this.callback = callback;
        this.context = context;
        loading = true;
        synchronizing = update;
        loadSystemInfo();
    }

    private void loadSystemInfo() {
        final ResponseHolder<SystemInfo> holder = new ResponseHolder<>();
        final DataValueResponseEvent<SystemInfo> event = new
                DataValueResponseEvent<>(BaseEvent.EventType.loadSystemInfo);
        event.setResponseHolder(holder);
        LoadSystemInfoTask task = new LoadSystemInfoTask(NetworkManager.getInstance(),
                new ApiRequestCallback<SystemInfo>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);

                        try {
                            SystemInfo systemInfo = Dhis2.getInstance().getObjectMapper().
                                    readValue(response.getBody(), SystemInfo.class);
                            holder.setItem(systemInfo);
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
                });
        task.execute();
    }

    private void loadItem() {
        if(synchronizing) {
            updateItem();
            return;
        }

        /**
         * Loading Tracked Entity Instances
         */
        if(Dhis2.isLoadFlagEnabled(context, TRACKED_ENTITY_INSTANCES)) {
            List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
            for(OrganisationUnit organisationUnit: assignedOrganisationUnits) {
                if(organisationUnit.getId() == null || organisationUnit.getId().length() == randomUUID.length())
                    break;

                currentOrganisationUnit = organisationUnit.getId();
                List<Program> programsForOrgUnit = new ArrayList<>();
                if(Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                    if (programsForOrgUnitMEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitMEWR);
                }
                if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.SINGLE_EVENT_WITH_REGISTRATION);

                    if (programsForOrgUnitSEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitSEWR);
                }

                for( Program program: programsForOrgUnit) {
                    if(program.getId() == null || program.getId().length() == randomUUID.length())
                        break;

                    if (!isDataValueItemLoaded(context, TRACKED_ENTITY_INSTANCES+organisationUnit.getId() + program.id)) {
                        Dhis2.postProgressMessage(context.getString(R.string.loading_tracked_entity_instances) + ": "
                                + organisationUnit.getLabel()+ ": " + program.getName());
                        currentOrganisationUnit = organisationUnit.getId();
                        currentProgram = program.id;
                        loadTrackedEntityInstances(currentOrganisationUnit, currentProgram);
                        return;
                    }
                }
            }
        }

        /**
         * Loading Enrollments
         */
        if(Dhis2.isLoadFlagEnabled(context, ENROLLMENTS)) {
            List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
            for(OrganisationUnit organisationUnit: assignedOrganisationUnits) {

                if(organisationUnit.getId() == null || organisationUnit.getId().length() == randomUUID.length())
                    break;

                currentOrganisationUnit = organisationUnit.getId();
                List<Program> programsForOrgUnit = new ArrayList<>();
                if(Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                    if (programsForOrgUnitMEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitMEWR);
                }
                if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.SINGLE_EVENT_WITH_REGISTRATION);

                    if (programsForOrgUnitSEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitSEWR);
                }

                for( Program program: programsForOrgUnit) {

                    if(program.getId() == null || program.getId().length() == randomUUID.length())
                        break;

                    if (!isDataValueItemLoaded(context, ENROLLMENTS+organisationUnit.getId()+ program.id)) {
                        Dhis2.postProgressMessage(context.getString(R.string.loading_enrollments) + ": "
                                + organisationUnit.getLabel()+ ": " + program.getName());
                        currentOrganisationUnit = organisationUnit.getId();
                        currentProgram = program.id;
                        loadEnrollments(currentOrganisationUnit, currentProgram);
                        return;
                    }
                }
            }
        }

        /**
         * Loading Events
         */
        if(Dhis2.isLoadFlagEnabled(context, EVENTS)) {
            List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
            for(OrganisationUnit organisationUnit: assignedOrganisationUnits) {
                if(organisationUnit.getId() == null || organisationUnit.getId().length() == randomUUID.length())
                    break;

                List<Program> programsForOrgUnit = new ArrayList<>();
                if(Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                    if (programsForOrgUnitMEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitMEWR);
                }
                if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.SINGLE_EVENT_WITH_REGISTRATION);

                    if (programsForOrgUnitSEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitSEWR);
                }
                if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITHOUT_REGISTRATION)) {
                    List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                    if(programsForOrgUnitSEWoR!=null) programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
                }

                for( Program program: programsForOrgUnit) {
                    if(program.getId() == null || program.getId().length() == randomUUID.length())
                        break;

                    if (!isDataValueItemLoaded(context, EVENTS+organisationUnit.getId()+ program.id)) {
                        Dhis2.postProgressMessage(context.getString(R.string.loading_events) + ": "
                                + organisationUnit.getLabel()+ ": " + program.getName());
                        currentOrganisationUnit = organisationUnit.getId();
                        currentProgram = program.id;
                        loadEvents(currentOrganisationUnit, currentProgram);
                        return;
                    }
                }
            }
        }
        onFinishLoading(true);
    }

    private void updateItem() {
        String currentLoadingDate = systemInfo.getServerDate();
        if(currentLoadingDate == null) {
            return;
        }
        DateTime currentDateTime = DateTimeFormat.forPattern(Utils.DATE_FORMAT).parseDateTime(currentLoadingDate);

        /**
         * Updating Tracked Entity Instances
         */
        if(Dhis2.isLoadFlagEnabled(context, TRACKED_ENTITY_INSTANCES)) {
            List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
            for(OrganisationUnit organisationUnit: assignedOrganisationUnits) {
                if(organisationUnit.getId() == null || organisationUnit.getId().length() == randomUUID.length())
                    break;
                currentOrganisationUnit = organisationUnit.getId();
                List<Program> programsForOrgUnit = new ArrayList<>();
                if(Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                    if (programsForOrgUnitMEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitMEWR);
                }
                if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.SINGLE_EVENT_WITH_REGISTRATION);

                    if (programsForOrgUnitSEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitSEWR);
                }

                for( Program program: programsForOrgUnit) {
                    if(program.getId() == null || program.getId().length() == randomUUID.length())
                        break;

                    currentProgram = program.id;
                    String lastUpdatedString = getLastUpdatedDateForDataValueItem(context,
                            TRACKED_ENTITY_INSTANCES+currentOrganisationUnit + currentProgram);
                    if(lastUpdatedString == null) {
                        loadTrackedEntityInstances(currentOrganisationUnit, currentProgram);
                        return;
                    }
                    DateTime updatedDateTime = DateTimeFormat.forPattern(Utils.DATE_FORMAT).parseDateTime(lastUpdatedString);
                    if(updatedDateTime.isBefore(currentDateTime)) {
                        loadTrackedEntityInstances(currentOrganisationUnit, currentProgram);
                        return;
                    }
                }
            }
        }

        /**
         * Updating Enrollments
         */
        if(Dhis2.isLoadFlagEnabled(context, ENROLLMENTS)) {
            List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
            for(OrganisationUnit organisationUnit: assignedOrganisationUnits) {
                if(organisationUnit.getId() == null || organisationUnit.getId().length() == randomUUID.length())
                    break;

                currentOrganisationUnit = organisationUnit.getId();
                List<Program> programsForOrgUnit = new ArrayList<>();
                if(Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                    if (programsForOrgUnitMEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitMEWR);
                }
                if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.SINGLE_EVENT_WITH_REGISTRATION);

                    if (programsForOrgUnitSEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitSEWR);
                }

                for( Program program: programsForOrgUnit) {
                    if(program.getId() == null || program.getId().length() == randomUUID.length())
                        break;
                    currentProgram = program.id;
                    String lastUpdatedString = getLastUpdatedDateForDataValueItem(context,
                            ENROLLMENTS+currentOrganisationUnit + currentProgram);
                    if(lastUpdatedString == null) {
                        loadEnrollments(currentOrganisationUnit, currentProgram);
                        return;
                    }
                    DateTime updatedDateTime = DateTimeFormat.forPattern(Utils.DATE_FORMAT).parseDateTime(lastUpdatedString);
                    if(updatedDateTime.isBefore(currentDateTime)) {
                        loadEnrollments(currentOrganisationUnit, currentProgram);
                        return;
                    }
                }
            }
        }

        /**
         * Updating Events
         */
        if(Dhis2.isLoadFlagEnabled(context, EVENTS)) {
            List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
            for(OrganisationUnit organisationUnit: assignedOrganisationUnits) {
                if(organisationUnit.getId() == null || organisationUnit.getId().length() == randomUUID.length())
                    break;
                currentOrganisationUnit = organisationUnit.getId();
                List<Program> programsForOrgUnit = new ArrayList<>();
                if(Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                    if (programsForOrgUnitMEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitMEWR);
                }
                if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                    List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.SINGLE_EVENT_WITH_REGISTRATION);

                    if (programsForOrgUnitSEWR != null)
                        programsForOrgUnit.addAll(programsForOrgUnitSEWR);
                }
                if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITHOUT_REGISTRATION)) {
                    List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                            (organisationUnit.getId(),
                                    Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                    if(programsForOrgUnitSEWoR!=null) programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
                }

                for( Program program: programsForOrgUnit) {
                    if(program.getId() == null || program.getId().length() == randomUUID.length())
                        break;

                    currentProgram = program.id;
                    String lastUpdatedString = getLastUpdatedDateForDataValueItem(context,
                            EVENTS+currentOrganisationUnit + currentProgram);
                    if(lastUpdatedString == null) {
                        loadEvents(currentOrganisationUnit, currentProgram);
                        return;
                    }
                    DateTime updatedDateTime = DateTimeFormat.forPattern(Utils.DATE_FORMAT).parseDateTime(lastUpdatedString);
                    if(updatedDateTime.isBefore(currentDateTime)) {
                        loadEvents(currentOrganisationUnit, currentProgram);
                        return;
                    }
                }
            }
        }
        onFinishLoading(true);
    }

    /**
     * called when loading of data values has finished.
     */
    private void onFinishLoading(boolean success) {
        Log.e(CLASS_TAG, "onfinishloading");
        if(success) {
            if( success ) {
                SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                LocalDate localDate = new LocalDate();
                editor.putString(Dhis2.LAST_UPDATED_DATAVALUES, systemInfo.getServerDate());
                editor.commit();
            }
        }

        InvalidateEvent event = new InvalidateEvent(InvalidateEvent.EventType.dataValuesLoaded);
        Dhis2Application.getEventBus().post(event);

        synchronizing = false;
        loading = false;
        if(success) {
            callback.onSuccess(null);
        } else {
            callback.onFailure(null);
        }
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
     * Loads events for a given org unit and program
     * @param organisationUnitId
     * @param programId
     */
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

    private void onResponse(ResponseEvent responseEvent) {
        if(responseEvent.getResponseHolder().getApiException() == null) {
            if (responseEvent.eventType == BaseEvent.EventType.loadSystemInfo) {
                systemInfo = (SystemInfo) responseEvent.getResponseHolder().getItem();
                Log.d(CLASS_TAG, "got system info " + systemInfo.getServerDate());
                loadItem();
            } else if (responseEvent.eventType == BaseEvent.EventType.loadTrackedEntityInstances) {
                Object[] items = (Object[]) responseEvent.getResponseHolder().getItem();
                List<TrackedEntityInstance> trackedEntityInstances = (List<TrackedEntityInstance>) items[0];
                List<TrackedEntityAttributeValue> values = (List<TrackedEntityAttributeValue>) items[1];
                if(trackedEntityInstances != null) {
                    if(synchronizing) {
                        //todo: implement different handling if synchronizing than if doing 1st load
                    }

                    for(TrackedEntityInstance tei: trackedEntityInstances) {
                        tei.save();
                    }

                    for(TrackedEntityAttributeValue value: values) {
                        TrackedEntityInstance tei = DataValueController.getTrackedEntityInstance(value.getTrackedEntityInstanceId());
                        if(tei!=null) {
                            value.setLocalTrackedEntityInstanceId(tei.localId);
                        }
                        value.async().save();
                    }
                }
                flagDataValueItemUpdated(context, TRACKED_ENTITY_INSTANCES+currentOrganisationUnit+currentProgram, systemInfo.getServerDate());
                flagDataValueItemLoaded(TRACKED_ENTITY_INSTANCES+currentOrganisationUnit+currentProgram, true);
                loadItem();
            } else if (responseEvent.eventType == BaseEvent.EventType.loadEnrollments) {
                List<Enrollment> enrollments = (List<Enrollment>) responseEvent.getResponseHolder().getItem();
                for(Enrollment enrollment: enrollments) {
                    if(synchronizing) {
                        //todo: implement different handling if synchronizing than if doing 1st load
                    }
                    enrollment.setOrgUnit(currentOrganisationUnit);
                    TrackedEntityInstance tei = DataValueController.getTrackedEntityInstance(enrollment.trackedEntityInstance);
                    if(tei!=null) enrollment.setLocalTrackedEntityInstanceId(tei.localId);
                    enrollment.save();
                }
                flagDataValueItemUpdated(context, ENROLLMENTS+currentOrganisationUnit+currentProgram, systemInfo.getServerDate());
                flagDataValueItemLoaded(ENROLLMENTS+currentOrganisationUnit+currentProgram, true);
                loadItem();
            } else if (responseEvent.eventType == BaseEvent.EventType.loadEvents) {
                List<Event> events = (List<Event>) responseEvent.getResponseHolder().getItem();
                for(Event event: events) {
                    if(synchronizing) {
                        //todo: implement different handling if synchronizing than if doing 1st load
                    }
                    loadedEventCounter++;
                    //check if there have been changes made locally since last update.
                    //if there are local updates, don't overwrite with data from server.
                    Event localEvent = DataValueController.getEventByUid(event.getEvent());
                    if(localEvent != null) {
                        event.setLocalId(localEvent.getLocalId());
                        event.setLocalEnrollmentId(localEvent.getLocalEnrollmentId());
                        if( localEvent.getFromServer() == true ) {
                            event.update();
                        }
                    } else {
                        //check if there is an enrollment for this event stored on the device
                        //and store the localId of the enrollment
                        //(there will not be enrollment if its a single event without registration)
                        Enrollment enrollment = DataValueController.getEnrollment(event.getEnrollment());

                        if(enrollment!=null) {
                            event.setLocalEnrollmentId(enrollment.localId);
                        } else {//could be single event without registration
                        }
                        event.save();
                    }
                }

                flagDataValueItemUpdated(context, EVENTS+currentOrganisationUnit+currentProgram, systemInfo.getServerDate());
                flagDataValueItemLoaded(EVENTS+currentOrganisationUnit+currentProgram, true);
                loadItem();
            }
        } else {
            //TODO: handle exceptions..
            if(responseEvent.getResponseHolder() != null && responseEvent.getResponseHolder().getApiException() != null)
                responseEvent.getResponseHolder().getApiException().printStackTrace();
            onFinishLoading(false);
        }
    }

    /**
     * Flags a DataValue item like Events or Enrollments to indicate whether or not it has been loaded.
     * Can also be set for a UID for example for an individual Program.
     * @param item
     * @param loaded
     */
    private void flagDataValueItemLoaded(String item, boolean loaded) {
        if(this.context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Dhis2.LOADED+item, loaded);
        editor.commit();
    }

    /**
     * Returns a boolean indicating whether or not a DataValue item has been loaded successfully.
     * @param item
     * @return
     */
    private static boolean isDataValueItemLoaded(Context context, String item) {
        if(context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(Dhis2.LOADED+item, false);
    }

    private void flagDataValueItemUpdated(Context context, String item, String dateTime) {
        if(context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Dhis2.UPDATED+item, dateTime);
        editor.commit();
    }

    /**
     * returns the date in a string for the last time an item was updated
     * @param context
     * @param item
     * @return
     */
    private static String getLastUpdatedDateForDataValueItem(Context context, String item) {
        if(context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Dhis2.UPDATED + item, null);
    }

    /**
     * Goes through all assigned programs and checks if they are loaded if loading is enabled.
     * Returns false if some programs have not been loaded, but have been flagged to load.
     * @param context
     * @return
     */
    public static boolean isEventsLoaded(Context context) {
        List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
            if(organisationUnit.getId() == null)
                break;

            List<Program> programsForOrgUnit = new ArrayList<>();
            if (Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                if (programsForOrgUnitMEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitMEWR);
            }
            if (Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITH_REGISTRATION);

                if (programsForOrgUnitSEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWR);
            }
            if (Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITHOUT_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                if (programsForOrgUnitSEWoR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
            }

            for (Program program : programsForOrgUnit) {
                if(program.getId() == null)
                    break;

                if (!isDataValueItemLoaded(context, EVENTS+organisationUnit.getId()+ program.id)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isEnrollmentsLoaded(Context context) {
        List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
            if(organisationUnit.getId() == null)
                break;

            List<Program> programsForOrgUnit = new ArrayList<>();
            if (Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                if (programsForOrgUnitMEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitMEWR);
            }
            if (Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITH_REGISTRATION);

                if (programsForOrgUnitSEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWR);
            }
            if (Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITHOUT_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                if (programsForOrgUnitSEWoR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
            }

            for (Program program : programsForOrgUnit) {
                if(program.getId() == null)
                    break;

                if (!isDataValueItemLoaded(context, ENROLLMENTS+organisationUnit.getId()+ program.id)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isTrackedEntityInstancesLoaded(Context context) {
        List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        for (OrganisationUnit organisationUnit : assignedOrganisationUnits) {
            if(organisationUnit.getId() == null)
                break;

            List<Program> programsForOrgUnit = new ArrayList<>();
            if (Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                if (programsForOrgUnitMEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitMEWR);
            }
            if (Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITH_REGISTRATION);

                if (programsForOrgUnitSEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWR);
            }
            if (Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITHOUT_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                if (programsForOrgUnitSEWoR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
            }

            for (Program program : programsForOrgUnit) {
                if(program.getId() == null)
                    break;

                if (!isDataValueItemLoaded(context, TRACKED_ENTITY_INSTANCES+organisationUnit.getId()+ program.id)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void dataValueIntegrityCheck()
    {
        Log.d(CLASS_TAG, "Running data value integrity check");


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
        }
    }

    /**
     * Sets all flags for all loaded data values to false, and all updated dates to null
     * @param context
     */
    void clearDataValueLoadedFlags(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        List<OrganisationUnit> assignedOrganisationUnits = MetaDataController.getAssignedOrganisationUnits();
        for(OrganisationUnit organisationUnit: assignedOrganisationUnits) {
            if(organisationUnit.getId() == null)
                break;

            String assignedOrganisationUnit = organisationUnit.getId();
            List<Program> programsForOrgUnit = new ArrayList<>();
            if(Dhis2.isLoadFlagEnabled(context, Program.MULTIPLE_EVENTS_WITH_REGISTRATION)) {
                List<Program> programsForOrgUnitMEWR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.MULTIPLE_EVENTS_WITH_REGISTRATION);
                if (programsForOrgUnitMEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitMEWR);
            }
            if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITH_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITH_REGISTRATION);

                if (programsForOrgUnitSEWR != null)
                    programsForOrgUnit.addAll(programsForOrgUnitSEWR);
            }
            if(Dhis2.isLoadFlagEnabled(context, Program.SINGLE_EVENT_WITHOUT_REGISTRATION)) {
                List<Program> programsForOrgUnitSEWoR = MetaDataController.getProgramsForOrganisationUnit
                        (organisationUnit.getId(),
                                Program.SINGLE_EVENT_WITHOUT_REGISTRATION);
                if(programsForOrgUnitSEWoR!=null) programsForOrgUnit.addAll(programsForOrgUnitSEWoR);
            }

            for( Program program: programsForOrgUnit) {
                if(program.getId() == null)
                    break;

                String programId = program.id;
                flagDataValueItemLoaded(EVENTS+assignedOrganisationUnit+programId, false);
                flagDataValueItemLoaded(TRACKED_ENTITY_INSTANCES+assignedOrganisationUnit+programId, false);
                flagDataValueItemLoaded(ENROLLMENTS+assignedOrganisationUnit+programId, false);
                flagDataValueItemUpdated(context, EVENTS+assignedOrganisationUnit+programId, null);
                flagDataValueItemUpdated(context, TRACKED_ENTITY_INSTANCES+assignedOrganisationUnit+programId, null);
                flagDataValueItemUpdated(context, ENROLLMENTS+assignedOrganisationUnit+programId, null);
            }
        }
    }
    public static boolean isSynchronizing() {
        return synchronizing;
    }
}
