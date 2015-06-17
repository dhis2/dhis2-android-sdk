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

package org.hisp.dhis.android.sdk.controllers.metadata;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadAssignedProgramsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadConstantsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadOptionSetsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadProgramRuleActionsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadProgramRuleVariablesTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadProgramRulesTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadProgramTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadSystemInfoTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadTrackedEntitiesTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadTrackedEntityAttributesTask;
import org.hisp.dhis.android.sdk.controllers.tasks.UpdateOptionSetsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.UpdateTrackedEntityAttributesTask;
import org.hisp.dhis.android.sdk.events.BaseEvent;
import org.hisp.dhis.android.sdk.events.InvalidateEvent;
import org.hisp.dhis.android.sdk.events.MetaDataResponseEvent;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataElement$Table;
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
import org.hisp.dhis.android.sdk.persistence.models.Option;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet$Table;
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
import org.hisp.dhis.android.sdk.persistence.models.ResponseBody;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntity;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;
import org.hisp.dhis.android.sdk.persistence.models.User;
import org.hisp.dhis.android.sdk.persistence.models.User$Table;
import org.hisp.dhis.android.sdk.utils.APIException;
import org.hisp.dhis.android.sdk.utils.Utils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Handles loading of MetaData, including synchronization efforts
 * @author Simen Skogly Russnes on 04.03.15.
 */
public class MetaDataLoader {

    private static final String CLASS_TAG = "MetaDataLoader";

    public static final String SYSTEM_INFO = "system_info";
    public static final String ASSIGNED_PROGRAMS = "assigned_programs";
    public static final String PROGRAMS = "programs";
    public static final String OPTION_SETS = "option_sets";
    public static final String TRACKED_ENTITY_ATTRIBUTES = "tracked_entity_attributes";
    public static final String CONSTANTS = "constants";
    public static final String PROGRAMRULES = "programrules";
    public static final String PROGRAMRULEVARIABLES = "programrulevariables";
    public static final String PROGRAMRULEACTIONS = "programruleactions";

    private Context context;
    boolean loading = false;
    boolean synchronizing = false;
    private int retries = 0;
    private static final int maxRetries = 9;
    private ApiRequestCallback callback;

    private SystemInfo systemInfo;

    /**
     * Connects to the server and checks for updates in Meta Data. If Meta Data has been updated,
     * changes are downloaded and reflected in the client.
     */
    void synchronizeMetaData(Context context, ApiRequestCallback callback) {
        Log.d(CLASS_TAG, "loading: " + loading);
        if( Dhis2.isLoading() ) return;
        synchronizing = true;
        loadMetaData(context, callback);
    }

    /**
     * Loads metaData from the server and stores it in local persistence.
     * By default this method loads metaData required for data entry in Event Capture
     */
    void loadMetaData(Context context, ApiRequestCallback callback) {
        if( loading ) {
            callback.onFailure(null);
            return;
        }
        this.callback = callback;
        loading = true;
        Dhis2.postProgressMessage(context.getString(R.string.loading_metadata));
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        String lastUpdated = prefs.getString(Dhis2.LAST_UPDATED_METADATA, null);
        systemInfo = null;
        //if(lastUpdated == null || synchronizing)
        loadSystemInfo();
    }

    /**
     * Resumes loading meta data depending on what has already been loaded.
     */
    private void resumeLoading(Context context) {
        loadItem();
    }

    /**
     * Loads a metadata item that is scheduled to be loaded but has not yet been.
     */
    private void loadItem() {
        if(synchronizing) {
            updateItem();
            return;
        }
        //some items depend on each other. Programs depend on AssignedPrograms because we need
        //the ids of programs to load.
        if(Dhis2.isLoadFlagEnabled(context, ASSIGNED_PROGRAMS)) {
            if (!isMetaDataItemLoaded(ASSIGNED_PROGRAMS)) {
                loadAssignedPrograms();
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, PROGRAMS)) {
            List<String> assignedPrograms = MetaDataController.getAssignedPrograms();
            if (assignedPrograms != null) {
                for (String program : assignedPrograms) {
                    if (!isMetaDataItemLoaded(program)) {
                        loadProgram(program);
                        return;
                    }
                }
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, OPTION_SETS)) {
            if (!isMetaDataItemLoaded(OPTION_SETS)) {
                loadOptionSets();
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, TRACKED_ENTITY_ATTRIBUTES)) {
            if (!isMetaDataItemLoaded(TRACKED_ENTITY_ATTRIBUTES)) {
                loadTrackedEntityAttributes();
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, CONSTANTS)) {
            if (!isMetaDataItemLoaded(CONSTANTS)) {
                loadConstants();
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, PROGRAMRULES)) {
            if (!isMetaDataItemLoaded(PROGRAMRULES)) {
                loadProgramRules(synchronizing);
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, PROGRAMRULEVARIABLES)) {
            if (!isMetaDataItemLoaded(PROGRAMRULEVARIABLES)) {
                loadProgramRuleVariables(synchronizing);
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, PROGRAMRULEACTIONS)) {
            if (!isMetaDataItemLoaded(PROGRAMRULEACTIONS)) {
                loadProgramRuleActions(synchronizing);
                return;
            }
        }
        onFinishLoading(true); //called when everything is loaded.
    }

    private void updateItem() {
        String currentLoadingDate = systemInfo.getServerDate();
        if(currentLoadingDate == null) {
            return;
        }
        String pattern = Utils.DATE_FORMAT;
        DateTime currentDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(currentLoadingDate);//combinedFormatter.parseDateTime(currentLoadingDate).withZone(DateTimeZone.getDefault());
        if(Dhis2.isLoadFlagEnabled(context, ASSIGNED_PROGRAMS)) {
            String lastUpdatedString = getLastUpdatedDateForMetaDataItem(ASSIGNED_PROGRAMS);
            if(lastUpdatedString == null) {
                loadAssignedPrograms();
                return;
            }
            DateTime updatedDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(lastUpdatedString);
            if(updatedDateTime.isBefore(currentDateTime)) {
                loadAssignedPrograms();
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, PROGRAMS)) {
            List<String> assignedPrograms = MetaDataController.getAssignedPrograms();
            if (assignedPrograms != null) {
                for (String program : assignedPrograms) {
                    String lastUpdatedString = getLastUpdatedDateForMetaDataItem(program);
                    if(lastUpdatedString == null) {
                        updateProgram(program);
                        return;
                    }
                    DateTime updatedDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(lastUpdatedString);
                    if(updatedDateTime.isBefore(currentDateTime)) {
                        updateProgram(program);
                        return;
                    }
                }
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, OPTION_SETS)) {
            String lastUpdatedString = getLastUpdatedDateForMetaDataItem(OPTION_SETS);
            if(lastUpdatedString == null) {
                updateOptionSets();
                return;
            }
            DateTime updatedDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(lastUpdatedString);
            if(updatedDateTime.isBefore(currentDateTime)) {
                updateOptionSets();
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, TRACKED_ENTITY_ATTRIBUTES)) {
            String lastUpdatedString = getLastUpdatedDateForMetaDataItem(TRACKED_ENTITY_ATTRIBUTES);
            if(lastUpdatedString == null) {
                updateTrackedEntityAttributes();
                return;
            }
            DateTime updatedDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(lastUpdatedString);
            if(updatedDateTime.isBefore(currentDateTime)) {
                updateTrackedEntityAttributes();
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, CONSTANTS)) {
            String lastUpdatedString = getLastUpdatedDateForMetaDataItem(CONSTANTS);
            if(lastUpdatedString == null) {
                updateConstants();
                return;
            }
            DateTime updatedDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(lastUpdatedString);
            if(updatedDateTime.isBefore(currentDateTime)) {
                updateConstants();
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, PROGRAMRULES)) {
            String lastUpdatedString = getLastUpdatedDateForMetaDataItem(PROGRAMRULES);
            if(lastUpdatedString == null) {
                loadProgramRules(synchronizing);
                return;
            }
            DateTime updatedDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(lastUpdatedString);
            if(updatedDateTime.isBefore(currentDateTime)) {
                loadProgramRules(synchronizing);
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, PROGRAMRULEVARIABLES)) {
            String lastUpdatedString = getLastUpdatedDateForMetaDataItem(PROGRAMRULEVARIABLES);
            if(lastUpdatedString == null) {
                loadProgramRuleVariables(synchronizing);
                return;
            }
            DateTime updatedDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(lastUpdatedString);
            if(updatedDateTime.isBefore(currentDateTime)) {
                loadProgramRuleVariables(synchronizing);
                return;
            }
        }
        if(Dhis2.isLoadFlagEnabled(context, PROGRAMRULEACTIONS)) {
            String lastUpdatedString = getLastUpdatedDateForMetaDataItem(PROGRAMRULEACTIONS);
            if(lastUpdatedString == null) {
                loadProgramRuleActions(synchronizing);
                return;
            }
            DateTime updatedDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(lastUpdatedString);
            if(updatedDateTime.isBefore(currentDateTime)) {
                loadProgramRuleActions(synchronizing);
                return;
            }
        }

        onFinishLoading(true);
    }

    /**
     * resets the saved date for last updated meta data
     * @param context
     */
    void resetLastUpdated(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Dhis2.LAST_UPDATED_METADATA, null);
        editor.commit();
    }

    private void loadSystemInfo() {
        Dhis2.postProgressMessage(context.getString(R.string.loading_server_info));
        final ResponseHolder<SystemInfo> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<SystemInfo> event = new
                MetaDataResponseEvent<>(BaseEvent.EventType.loadSystemInfo);
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

    /**
     * Loads a list of assigned organisation units with their corresponding assigned programs.
     */
    private void loadAssignedPrograms() {
        Dhis2.postProgressMessage(context.getString(R.string.loading_assigned_programs));
        final ResponseHolder<List<OrganisationUnit>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<OrganisationUnit>> event = new
                MetaDataResponseEvent<>(BaseEvent.EventType.loadAssignedPrograms);
        event.setResponseHolder(holder);
        LoadAssignedProgramsTask task = new LoadAssignedProgramsTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<OrganisationUnit>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);

                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().readTree( response.getBody() );
                            node = node.get("organisationUnits");
                            if(node!=null) {
                                Iterator<JsonNode> elements = node.elements();
                                List<OrganisationUnit> organisationUnits = new ArrayList<>();
                                while(elements.hasNext()) {
                                    JsonNode orgUnit = elements.next();
                                    OrganisationUnit ou = Dhis2.getInstance().getObjectMapper().
                                            readValue(orgUnit.toString(), OrganisationUnit.class);
                                    organisationUnits.add(ou);
                                }
                                holder.setItem(organisationUnits);
                            } else {
                                holder.setItem(new ArrayList<OrganisationUnit>());
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
                });
        task.execute();
    }

    /**
     * Loads a program from the server based on given id
     * @param id id of program
     */
    private void loadProgram(String id) {
        if(id == null)
            return;

        Dhis2.postProgressMessage(context.getString(R.string.loading_program) + ": " + id);
        final ResponseHolder<Program> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<Program> event = new
                MetaDataResponseEvent<>(BaseEvent.EventType.loadProgram);
        event.setResponseHolder(holder);
        LoadProgramTask task = new LoadProgramTask(NetworkManager.getInstance(),
                new ApiRequestCallback<Program>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            Program program = Dhis2.getInstance().getObjectMapper().readValue(response.getBody(), Program.class);
                            holder.setItem(program);
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
                }, id, false);
        task.execute();
    }

    /**
     * Queries the server and updates a program if it is necessary
     */
    private void updateProgram(String id) {
        if(id == null)
            return;

        final ResponseHolder<Program> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<Program> event = new
                MetaDataResponseEvent<>(BaseEvent.EventType.updateProgram);
        Program program = new Program();
        program.id = id;
        holder.setItem(program); //passing a reference in case there is no reference from server
        event.setResponseHolder(holder);
        LoadProgramTask task = new LoadProgramTask(NetworkManager.getInstance(),
                new ApiRequestCallback<Program>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            Program program = Dhis2.getInstance().getObjectMapper().readValue(response.getBody(), Program.class);
                            if(program.id != null)
                                holder.setItem(program);
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
                }, id, true);
        task.execute();
    }

    /**
     * Updates Option sets from the server if they have been changed since last time
     * they were uploaded.
     */
    private void updateOptionSets() {
        final ResponseHolder<Object> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<Object> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.onUpdateOptionSets);
        event.setResponseHolder(holder);
        UpdateOptionSetsTask task = new UpdateOptionSetsTask(NetworkManager.getInstance(),
                new ApiRequestCallback<Object>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        holder.setItem(new Object());
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

    /**
     * Loads all option sets from the server
     */
    private void loadOptionSets() {
        Dhis2.postProgressMessage(context.getString(R.string.loading_optionsets));
        final ResponseHolder<List<OptionSet>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<OptionSet>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadOptionSets);
        event.setResponseHolder(holder);
        LoadOptionSetsTask task = new LoadOptionSetsTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<OptionSet>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("optionSets");

                            if(node == null)
                            {
                                holder.setItem(new ArrayList<OptionSet>());
                            }
                            else
                            {
                                TypeReference<List<OptionSet>> typeRef =
                                        new TypeReference<List<OptionSet>>(){};
                                List<OptionSet> optionSets = Dhis2.getInstance().getObjectMapper().
                                        readValue( node.traverse(), typeRef);
                                holder.setItem(optionSets);
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
                });
        task.execute();
    }

    private void loadTrackedEntities() {
        final ResponseHolder<List<TrackedEntity>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<TrackedEntity>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadTrackedEntities);
        event.setResponseHolder(holder);
        LoadTrackedEntitiesTask task = new LoadTrackedEntitiesTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<TrackedEntity>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("trackedEntities");

                            if(node == null)
                            {
                                holder.setItem(new ArrayList<TrackedEntity>());
                            }
                            else
                            {
                                TypeReference<List<TrackedEntity>> typeRef =
                                        new TypeReference<List<TrackedEntity>>(){};
                                List<TrackedEntity> trackedEntities = Dhis2.getInstance().getObjectMapper().
                                        readValue( node.traverse(), typeRef);
                                holder.setItem(trackedEntities);
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        event.setResponseHolder(holder);
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

    /**
     * Updates Option sets from the server if they have been changed since last time
     * they were uploaded.
     */
    private void updateTrackedEntityAttributes() {
        final ResponseHolder<Object> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<Object> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.onUpdateTrackedEntityAttributes);
        event.setResponseHolder(holder);
        UpdateTrackedEntityAttributesTask task = new UpdateTrackedEntityAttributesTask(NetworkManager.getInstance(),
                new ApiRequestCallback<Object>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        holder.setItem(new Object());
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

    /**
     * deprecated. TrackedEntityAttributes are loaded together with Programs in the same query
     */
    private void loadTrackedEntityAttributes() {
        Dhis2.postProgressMessage(context.getString(R.string.loading_trackedentityattributes));
        final ResponseHolder<List<TrackedEntityAttribute>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<TrackedEntityAttribute>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadTrackedEntityAttributes);
        event.setResponseHolder(holder);
        LoadTrackedEntityAttributesTask task = new LoadTrackedEntityAttributesTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<TrackedEntityAttribute>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("trackedEntityAttributes");

                            if(node == null){
                                holder.setItem(new ArrayList<TrackedEntityAttribute>());
                            }
                            else
                            {
                                TypeReference<List<TrackedEntityAttribute>> typeRef =
                                        new TypeReference<List<TrackedEntityAttribute>>(){};
                                List<TrackedEntityAttribute> trackedEntityAttributes = Dhis2.getInstance().getObjectMapper().
                                        readValue( node.traverse(), typeRef);
                                holder.setItem(trackedEntityAttributes);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        event.setResponseHolder(holder);
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

    private void updateConstants() {
        Dhis2.postProgressMessage(context.getString(R.string.updating_constants));
        final ResponseHolder<List<Constant>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<Constant>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.updateConstants);
        event.setResponseHolder(holder);
        LoadConstantsTask task = new LoadConstantsTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<Constant>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("constants");
                            if( node == null ) { /* in case there are no enrollments */
                                holder.setItem(new ArrayList<Constant>());
                            } else {
                                TypeReference<List<Constant>> typeRef =
                                        new TypeReference<List<Constant>>() {
                                        };
                                List<Constant> constants = Dhis2.getInstance().getObjectMapper().
                                        readValue(node.traverse(), typeRef);
                                holder.setItem(constants);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        event.setResponseHolder(holder);
                        onResponse(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(event);
                    }
                }, true);
        task.execute();
    }

    private void loadConstants() {
        Dhis2.postProgressMessage(context.getString(R.string.loading_constants));
        final ResponseHolder<List<Constant>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<Constant>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadConstants);
        event.setResponseHolder(holder);
        LoadConstantsTask task = new LoadConstantsTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<Constant>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("constants");
                            if(node == null)
                            {
                                holder.setItem(new ArrayList<Constant>());
                            }
                            else
                            {
                                TypeReference<List<Constant>> typeRef =
                                        new TypeReference<List<Constant>>(){};
                                List<Constant> constants = Dhis2.getInstance().getObjectMapper().
                                        readValue( node.traverse(), typeRef);
                                holder.setItem(constants);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        event.setResponseHolder(holder);
                        onResponse(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(event);
                    }
                }, false);
        task.execute();
    }

    private void loadProgramRules(boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_programrules));
        final ResponseHolder<List<ProgramRule>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<ProgramRule>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadProgramRules);
        event.setResponseHolder(holder);
        LoadProgramRulesTask task = new LoadProgramRulesTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<ProgramRule>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("programRules");
                            if( node == null ) { /* in case there are no items */
                                holder.setItem(new ArrayList<ProgramRule>());
                            } else {
                                TypeReference<List<ProgramRule>> typeRef =
                                        new TypeReference<List<ProgramRule>>() {
                                        };
                                List<ProgramRule> programRules = Dhis2.getInstance().getObjectMapper().
                                        readValue(node.traverse(), typeRef);
                                holder.setItem(programRules);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        event.setResponseHolder(holder);
                        onResponse(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(event);
                    }
                }, update);
        task.execute();
    }

    private void loadProgramRuleVariables(boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_programrulevariables));
        final ResponseHolder<List<ProgramRuleVariable>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<ProgramRuleVariable>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadProgramRuleVariables);
        event.setResponseHolder(holder);
        LoadProgramRuleVariablesTask task = new LoadProgramRuleVariablesTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<ProgramRuleVariable>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("programRuleVariables");
                            if( node == null ) { /* in case there are no items */
                                holder.setItem(new ArrayList<ProgramRuleVariable>());
                            } else {
                                TypeReference<List<ProgramRuleVariable>> typeRef =
                                        new TypeReference<List<ProgramRuleVariable>>() {
                                        };
                                List<ProgramRuleVariable> programRuleVariables = Dhis2.getInstance().getObjectMapper().
                                        readValue(node.traverse(), typeRef);
                                holder.setItem(programRuleVariables);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        event.setResponseHolder(holder);
                        onResponse(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(event);
                    }
                }, update);
        task.execute();
    }

    private void loadProgramRuleActions(boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_programruleactions));
        final ResponseHolder<List<ProgramRuleAction>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<ProgramRuleAction>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadProgramRuleActions);
        event.setResponseHolder(holder);
        LoadProgramRuleActionsTask task = new LoadProgramRuleActionsTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<ProgramRuleAction>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("programRuleActions");
                            if( node == null ) { /* in case there are no items */
                                holder.setItem(new ArrayList<ProgramRuleAction>());
                            } else {
                                TypeReference<List<ProgramRuleAction>> typeRef =
                                        new TypeReference<List<ProgramRuleAction>>() {
                                        };
                                List<ProgramRuleAction> programRuleActions = Dhis2.getInstance().getObjectMapper().
                                        readValue(node.traverse(), typeRef);
                                holder.setItem(programRuleActions);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            holder.setApiException(APIException.conversionError(response.getUrl(), response, e));
                        }
                        event.setResponseHolder(holder);
                        onResponse(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        onResponse(event);
                    }
                }, update);
        task.execute();
    }

    private void onFinishLoading(boolean success) {
        Log.d(CLASS_TAG, "onFinishLoading" + success);
        if( success ) {
            SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            LocalDate localDate = new LocalDate();
            editor.putString(Dhis2.LAST_UPDATED_METADATA, localDate.toString());
            editor.commit();
            if(systemInfo != null) {
                List<SystemInfo> result = new Select().from(SystemInfo.class).queryList();
                if( result != null && !result.isEmpty() ) {
                    systemInfo.async().update();
                }
                else {
                    systemInfo.async().save();
                }
            }
        }
        loading = false;
        synchronizing = false;

        InvalidateEvent event = new InvalidateEvent(InvalidateEvent.EventType.metaDataLoaded);
        Dhis2Application.getEventBus().post(event);

        if(callback!=null) {
            if(success) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(null);
            }
        }
    }

    /**
     * This method checks if all models that all UID references to other models are valid
     */
    public void metaDataIntegrityCheck()
    {
        Log.d(CLASS_TAG, "Running meta data integrity check");

        List<OrganisationUnitProgramRelationship> relationships = new Select().from(OrganisationUnitProgramRelationship.class).
                where(Condition.column(OrganisationUnitProgramRelationship$Table.ORGANISATIONUNITID).isNull()).
                or(Condition.column(OrganisationUnitProgramRelationship$Table.PROGRAMID).isNull()).queryList();

        if(relationships == null || relationships.size() < 1 )
        {
            Log.d(CLASS_TAG, "OrgUnitProgramRelationships are valid");
        }
        else
        {
            Log.d(CLASS_TAG, "orgUnitProgramRelationships are NOT valid");
            for(OrganisationUnitProgramRelationship relationship : relationships)
                relationship.delete();
        }
//          THIS IS COMMENTED AT THE MOMENT DUE TO FIX ON PROGRAM.SETTRACKEDENTITY. ENABLE WHEN TRACKED ENTITY IS LOADED SEPARATE
//        List<Program> programs = new Select().from(Program.class).where(
//                Condition.column(Program$Table.TRACKEDENTITY_TRACKEDENTITY).isNull()
//        ).queryList();

//        if(programs == null || programs.size() < 1)
//        {
//            Log.d(CLASS_TAG, "Programs are valid ");
//        }
//        else
//        {
//            Log.d(CLASS_TAG, "Programs are NOT valid");
//            for(Program program : programs)
//            {
////                Log.d(CLASS_TAG, "Program: Tracked Entity: " + program.getTrackedEntity());
////                program.delete();
//            }
//        }

        List<ProgramIndicator> programIndicators = new Select().from(ProgramIndicator.class).where(
                Condition.column(ProgramIndicator$Table.PROGRAM).isNull()).
                or(Condition.column(ProgramIndicator$Table.PROGRAMSTAGE).isNull()).queryList();

        if(programIndicators == null || programIndicators.size() < 1)
        {
            Log.d(CLASS_TAG, "Valid program indicators");
        }
        else
        {
            Log.d(CLASS_TAG, "Program indicators are NOT valid");
            for(ProgramIndicator programIndicator : programIndicators)
                programIndicator.delete();
        }

        List<ProgramRule> programRules = new Select().from(ProgramRule.class).where(
                Condition.column(ProgramRule$Table.PROGRAM).isNull()).queryList();
        if(programRules == null || programRules.size() < 1 )
        {
            Log.d(CLASS_TAG, "Valid program rules");
        }
        else
        {
            Log.d(CLASS_TAG, "Program rules are NOT valid");
            for(ProgramRule programRule : programRules)
            {
                programRule.delete();
                Log.d(CLASS_TAG, "Program rules: Program: " + programRule.getProgram());
            }
        }

        List<ProgramRuleAction> programRuleActions = new Select().from(ProgramRuleAction.class).where(
                Condition.column(ProgramRuleAction$Table.PROGRAMRULE).isNull()).
                or(Condition.column(ProgramRuleAction$Table.DATAELEMENT).isNull()).queryList();

        if(programRuleActions == null || programRuleActions.size() < 1 )
        {
            Log.d(CLASS_TAG, "Valid program rule actions");
        }
        else
        {
            Log.d(CLASS_TAG, "Program rule actions are NOT valid");
            for(ProgramRuleAction programRuleAction : programRuleActions)
            {
                programRuleAction.delete();
                Log.d(CLASS_TAG, "Program rule action: Program Rule:" + programRuleAction.getProgramRule() + " Data element: " +
                        programRuleAction.getDataElement());
            }
        }

        List<ProgramRuleVariable> programRuleVariables = new Select().from(ProgramRuleVariable.class).where(
                Condition.column(ProgramRuleVariable$Table.PROGRAM).isNull()).
                or(Condition.column(ProgramRuleVariable$Table.DATAELEMENT).isNull()).queryList();

        if(programRuleVariables == null || programRuleVariables.size() < 1)
        {
            Log.d(CLASS_TAG, "Valid program rule variables");
        }
        else
        {
            Log.d(CLASS_TAG, "Program rule variables are NOT valid");
            for(ProgramRuleVariable programRuleVariable : programRuleVariables)
                programRuleVariable.delete();
        }

        List<ProgramStage> programStages = new Select().from(ProgramStage.class).where(
                Condition.column(ProgramStage$Table.PROGRAM).isNull()).queryList();
        if(programStages == null || programStages.size() < 1 )
        {
            Log.d(CLASS_TAG, "Valid program stages");
        }
        else
        {
            Log.d(CLASS_TAG, "Program stages are NOT valid");
            for(ProgramStage programStage : programStages)
                programStage.delete();
        }

        List<ProgramStageDataElement> programStageDataElements = new Select().from(ProgramStageDataElement.class).where(
                Condition.column(ProgramStageDataElement$Table.PROGRAMSTAGE).isNull()).
                or(Condition.column(ProgramStageDataElement$Table.DATAELEMENT).isNull()).queryList();

        if(programStageDataElements == null || programStageDataElements.size() < 1 )
        {
            Log.d(CLASS_TAG, "Valid program stage data elements");
        }
        else
        {
            Log.d(CLASS_TAG, "Program stage data elements are NOT valid");
            for(ProgramStageDataElement programStageDataElement : programStageDataElements)
            {
                programStageDataElement.delete();
                Log.d(CLASS_TAG, "ProgramStageDataElement: ProgramStage: " + programStageDataElement.getProgramStage() +
                         " DataElement: " + programStageDataElement.getDataelement());
            }
        }

        List<ProgramStageSection> programStageSections = new Select().from(ProgramStageSection.class).where(
                Condition.column(ProgramStageSection$Table.PROGRAMSTAGE).isNull()).queryList();
        if(programStageSections == null || programStageSections.size() < 1)
        {
            Log.d(CLASS_TAG, "Valid program stage sections");
        }
        else
        {
            Log.d(CLASS_TAG, "Program stage sections are NOT valid");
            for(ProgramStageSection programStageSection : programStageSections)
                programStageSection.delete();
        }

        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = new Select().from(ProgramTrackedEntityAttribute.class).where(
                Condition.column(ProgramTrackedEntityAttribute$Table.PROGRAM).isNull()).
                or(Condition.column(ProgramTrackedEntityAttribute$Table.TRACKEDENTITYATTRIBUTE).isNull()).queryList();

        if(programTrackedEntityAttributes == null || programTrackedEntityAttributes.size() < 1 )
        {
            Log.d(CLASS_TAG, "Program tracked entity attributes are valid");
        }
        else
        {
            Log.d(CLASS_TAG, "Program tracked entity attributes are NOT valid");
            for(ProgramTrackedEntityAttribute programTrackedEntityAttribute : programTrackedEntityAttributes)
                programTrackedEntityAttribute.delete();
        }
    }

    private void onResponse(MetaDataResponseEvent event) {
        if (event.getResponseHolder().getItem() != null) {
            retries = 0;
            if (event.eventType == BaseEvent.EventType.loadSystemInfo) {
                systemInfo = (SystemInfo) event.getResponseHolder().getItem();

                Log.d(CLASS_TAG, "got system info " + systemInfo.getServerDate());
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.loadAssignedPrograms) {
                List<OrganisationUnit> organisationUnits = ( List<OrganisationUnit> )
                        event.getResponseHolder().getItem();

                ArrayList<String> assignedPrograms = new ArrayList<String>();
                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));


                /**
                 * If we are synchronizing we simply delete all the previously stored
                 * OrganisationUnitProgramRelationships and save the new ones since there
                 * usually isn't that many.
                 */
                if( synchronizing ) {
                    Delete.tables(OrganisationUnitProgramRelationship.class, OrganisationUnit.class);
                }
                for(OrganisationUnit ou: organisationUnits) {
                    for(String programId : ou.getPrograms()) {
                        OrganisationUnitProgramRelationship orgUnitProgram =
                                new OrganisationUnitProgramRelationship();
                        orgUnitProgram.setOrganisationUnitId(ou.getId());
                        orgUnitProgram.setProgramId(programId);
                        orgUnitProgram.async().save();
                        if(!assignedPrograms.contains(programId))
                            assignedPrograms.add(programId);
                    }
                    ou.async().save();
                }

                flagMetaDataItemLoaded(ASSIGNED_PROGRAMS, true);
                flagMetaDataItemUpdated(ASSIGNED_PROGRAMS, systemInfo.getServerDate());
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.loadProgram ) {
                Program program = (Program) event.getResponseHolder().getItem();


                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));

                //Have to set program reference in ptea manually because it is not referenced in
                //API JSON
                int sortOrder = 0;


                for(ProgramTrackedEntityAttribute ptea: program.getProgramTrackedEntityAttributes()) {
                    ptea.setProgram(program.getId());
                    ptea.setSortOrder(sortOrder);
                    ptea.async().save();
                    sortOrder++;
                }

                program.async().save();
                for( ProgramStage programStage: program.getProgramStages() ) {
                    programStage.async().save();
                    if(programStage.getProgramStageSections() != null && !programStage.getProgramStageSections().isEmpty()) {
                        // due to the way the WebAPI lists programStageSections we have to manually
                        // set id of programStageSection in programStageDataElements to be able to
                        // access it later when loading from local db
                        for(ProgramStageSection programStageSection: programStage.getProgramStageSections()) {
                            programStageSection.async().save();
                            for(ProgramStageDataElement programStageDataElement: programStageSection.getProgramStageDataElements()) {
                                programStageDataElement.setProgramStageSection(programStageSection.id);
                                programStageDataElement.async().save();
                                //todo[simen]: consider implementing override of save function rather
                                //todo: than doing this manually
                            }
                            for(ProgramIndicator programIndicator: programStageSection.getProgramIndicators()) {
                                programIndicator.setProgramStage(programStage.id);
                                programIndicator.setSection(programStageSection.id);
                                programIndicator.async().save();
                            }
                        }
                    } else {
                        for(ProgramStageDataElement programStageDataElement: programStage.
                                getProgramStageDataElements()) {
                            programStageDataElement.async().save();
                        }
                        for(ProgramIndicator programIndicator: programStage.getProgramIndicators()) {
                            programIndicator.setProgramStage(programStage.id);
                            programIndicator.async().save();
                        }
                    }
                }

                flagMetaDataItemLoaded(program.id, true);
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.updateProgram ) {
                Program program = (Program) event.getResponseHolder().getItem();
                boolean noProgram = false;
                if( program.getCreated() == null ) noProgram = true;

                if(noProgram) {/*Means the program didn't need to be updated so we just do nothing*/}
                else {


                    /**Delete everything (except shared things like dataElement and
                     * trackedEntityAttribute) and store it again cause it's easier, it's not that big, and
                     *it rarely happens.
                     * what needs to be deleted is:
                     * ProgramTrackedEntityAttribute (not the TrackedEntityAttribute itself)
                     * ProgramStageDataElement (not the actual DataElement, we can simply update that)
                     * The program stages since they are referenced with lazy loading
                     *  but we need to delete the ProgramStageDataElements because it is the link
                     *  between the program and dataelement
                     *
                     */

                    /*firstly we should get the old program from the database and delete using that
                      reference
                     */
                    Program oldProgram = MetaDataController.getProgram(program.getId());
                    if(oldProgram != null) {
                        for(ProgramTrackedEntityAttribute ptea: oldProgram.getProgramTrackedEntityAttributes()) {
                            ptea.async().delete();
                        }
                        for( ProgramStage programStage: program.getProgramStages() ) {
                            for(ProgramStageDataElement psde: programStage.getProgramStageDataElements() ) {
                                psde.async().delete();
                            }
                            for(ProgramStageSection programStageSection: programStage.getProgramStageSections()) {
                                programStageSection.async().delete();
                            }
                            programStage.async().delete();
                        }
                        for(ProgramIndicator programIndicator: program.getProgramIndicators()) {
                            programIndicator.async().delete();
                        }
                    }

                    /**
                     * Then we store the new program.
                     */

                    //Have to set program reference in ptea manually because it is not referenced in
                    //API JSON
                    int sortOrder = 0;
                    for(ProgramTrackedEntityAttribute ptea: program.getProgramTrackedEntityAttributes()) {
                        ptea.setProgram(program.getId());
                        ptea.setSortOrder(sortOrder);
                        ptea.async().save();
                        sortOrder++;
                    }

                    if(oldProgram== null) {
                        program.async().save();
                    }
                    else {
                        program.async().update();
                    }
                    for( ProgramStage programStage: program.getProgramStages() ) {
                        programStage.async().save();
                        if(programStage.getProgramStageSections() != null && !programStage.getProgramStageSections().isEmpty()) {
                            // due to the way the WebAPI lists programStageSections we have to manually
                            // set id of programStageSection in programStageDataElements to be able to
                            // access it later when loading from local db
                            for(ProgramStageSection programStageSection: programStage.getProgramStageSections()) {
                                programStageSection.async().save();
                                for(ProgramStageDataElement programStageDataElement: programStageSection.getProgramStageDataElements()) {
                                    programStageDataElement.setProgramStageSection(programStageSection.id);
                                    programStageDataElement.async().save();
                                    //todo: consider implementing override of save function rather
                                    //todo: than doing this manually
                                }
                                for(ProgramIndicator programIndicator: programStageSection.getProgramIndicators()) {
                                    programIndicator.setProgramStage(programStage.id);
                                    programIndicator.setSection(programStageSection.id);
                                    programIndicator.async().save();
                                }
                            }
                        } else {
                            for(ProgramStageDataElement programStageDataElement: programStage.
                                    getProgramStageDataElements()) {
                                programStageDataElement.async().save();
                            }
                            for(ProgramIndicator programIndicator: programStage.getProgramIndicators()) {
                                programIndicator.setProgramStage(programStage.id);
                                programIndicator.async().save();
                            }
                        }
                    }
                }

                flagMetaDataItemUpdated(program.id, systemInfo.getServerDate());
                loadItem();
            } else if( event.eventType == BaseEvent.EventType.loadOptionSets ) {
                List<OptionSet> optionSets = ( List<OptionSet> ) event.getResponseHolder().getItem();

                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));
                for(OptionSet os: optionSets ) {
                    int index = 0;
                    for( Option o: os.getOptions()) {
                        o.setSortIndex(index);
                        o.setOptionSet( os.getId() );
                        o.async().save();
                        index ++;
                    }
                    os.async().save();
                }
                flagMetaDataItemLoaded(OPTION_SETS, true);
                loadItem();
            } else if( event.eventType == BaseEvent.EventType.onUpdateOptionSets ) {
                flagMetaDataItemUpdated(OPTION_SETS, systemInfo.getServerDate());
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.loadTrackedEntityAttributes ) {
                List<TrackedEntityAttribute> trackedEntityAttributes = (List<TrackedEntityAttribute>) event.getResponseHolder().getItem();


                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));
                for(TrackedEntityAttribute tea: trackedEntityAttributes) {
                    tea.async().save();
                }
                flagMetaDataItemLoaded(TRACKED_ENTITY_ATTRIBUTES, true);
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.onUpdateTrackedEntityAttributes ) {
                flagMetaDataItemUpdated(TRACKED_ENTITY_ATTRIBUTES, systemInfo.getServerDate());
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.loadConstants ) {
                List<Constant> constants = (List<Constant>) event.getResponseHolder().getItem();

                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));
                for(Constant constant: constants) {
                    constant.async().save();
                }
                flagMetaDataItemLoaded(CONSTANTS, true);
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.updateConstants ) {
                List<Constant> constants = (List<Constant>) event.getResponseHolder().getItem();

                for(Constant constant: constants) {
                    constant.async().save();
                }
                flagMetaDataItemUpdated(CONSTANTS, systemInfo.getServerDate());
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.loadProgramRules ) {
                List<ProgramRule> programRules = (List<ProgramRule>) event.getResponseHolder().getItem();


                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));
                for(ProgramRule programRule: programRules) {
                    programRule.async().save();
                }
                if(!synchronizing) {
                    flagMetaDataItemLoaded(PROGRAMRULES, true);
                } else {
                    flagMetaDataItemUpdated(PROGRAMRULES, systemInfo.getServerDate());
                }
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.loadProgramRuleVariables ) {
                List<ProgramRuleVariable> programRuleVariables = (List<ProgramRuleVariable>) event.getResponseHolder().getItem();

                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));
                for(ProgramRuleVariable programRuleVariable: programRuleVariables) {
                    programRuleVariable.async().save();
                }
                if(!synchronizing) {
                    flagMetaDataItemLoaded(PROGRAMRULEVARIABLES, true);
                } else {
                    flagMetaDataItemUpdated(PROGRAMRULEVARIABLES, systemInfo.getServerDate());
                }
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.loadProgramRuleActions ) {
                List<ProgramRuleAction> programRuleActions = (List<ProgramRuleAction>) event.getResponseHolder().getItem();


                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));
                for(ProgramRuleAction programRuleAction: programRuleActions) {
                    programRuleAction.async().save();
                }
                if(!synchronizing) {
                    flagMetaDataItemLoaded(PROGRAMRULEACTIONS, true);
                } else {
                    flagMetaDataItemUpdated(PROGRAMRULEACTIONS, systemInfo.getServerDate());
                }
                loadItem();
            } else {
                onFinishLoading(false);
            }
        } else {
            //todo handle more effectively
            Log.d(CLASS_TAG, "exception..");
            APIException exception = null;
            if(event.getResponseHolder() != null && event.getResponseHolder().getApiException() != null)
            {
                event.getResponseHolder().getApiException().printStackTrace();
                Log.e(CLASS_TAG, event.getResponseHolder().getApiException().getLocalizedMessage());
                exception = event.getResponseHolder().getApiException();
                if(exception.isConversionError()) {

                } else if (exception.isNetworkError()) {

                } else if (exception.isHttpError()) {

                } else if (exception.isUnknownError()) {

                }
            }
            if(retries >= maxRetries)
            {
                retries = 0;
                onFinishLoading(false);
            }
            else
                retry(exception);
        }
    }

    private void retry(APIException exception) {
        String message = "";
        if(exception!=null) message = exception.getMessage();
        retries++;
        if(!synchronizing) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    loadItem();
                }
            };
            long ms = 30 * 1000;
            Timer timer = new Timer();
            Dhis2.postProgressMessage(context.getString(R.string.loading_failed )+": " + message + ". "
                    +context.getString(R.string.retrying) +": " + context.getString(R.string.retry)
                    + " " + retries + "/" + maxRetries );
            Log.d(CLASS_TAG, "Retry " + retries + "/" + maxRetries + ". Retrying in " +
                    (ms/1000) + " seconds..");
            timer.schedule(task, ms);
        } else {
            onFinishLoading(false);
        }
    }

    /**
     * Flags a MetaData item like Programs or OptionSets to indicate whether or not it has been loaded.
     * Can also be set for a UID for example for an individual Program.
     * @param item
     * @param loaded
     */
    private void flagMetaDataItemLoaded(String item, boolean loaded) {
        if(this.context == null) return;
        Log.d("METADATALOADERisLoaded,", item);
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Dhis2.LOADED+item, loaded);
        editor.commit();
    }

    /**
     * Returns a boolean indicating whether or not a MetaData item has been loaded successfully.
     * @param item
     * @return
     */
    private boolean isMetaDataItemLoaded(String item) {
        if(context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(Dhis2.LOADED+item, false);
    }

    /**
     * sets the date of the last time a meta data item was loaded.
     * @param item
     * @param date
     */
    private void flagMetaDataItemUpdated(String item, String date) {
        if(this.context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Dhis2.UPDATED + item, date);
        editor.commit();
    }

    private String getLastUpdatedDateForMetaDataItem(String item) {
        if(context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Dhis2.UPDATED + item, null);
    }

    /**
     * Clears status and time of loaded meta data items
     * @param context
     */
    void clearMetaDataLoadedFlags(Context context) {
        this.context = context;
        flagMetaDataItemLoaded(ASSIGNED_PROGRAMS, false);
        flagMetaDataItemUpdated(ASSIGNED_PROGRAMS, null);
        List<String> assignedPrograms = MetaDataController.getAssignedPrograms();

        for(String program: assignedPrograms) {
            Log.d(CLASS_TAG, "clearing program: " + program);
            flagMetaDataItemLoaded(program, false);
            flagMetaDataItemUpdated(program, null);
        }
        flagMetaDataItemLoaded(OPTION_SETS, false);
        flagMetaDataItemUpdated(OPTION_SETS, null);
        flagMetaDataItemLoaded(TRACKED_ENTITY_ATTRIBUTES, false);
        flagMetaDataItemUpdated(TRACKED_ENTITY_ATTRIBUTES, null);
        flagMetaDataItemLoaded(CONSTANTS, false);
        flagMetaDataItemUpdated(CONSTANTS, null);

        flagMetaDataItemLoaded(PROGRAMRULES, false);
        flagMetaDataItemUpdated(PROGRAMRULES, null);

        flagMetaDataItemLoaded(PROGRAMRULEVARIABLES, false);
        flagMetaDataItemUpdated(PROGRAMRULEVARIABLES, null);

        flagMetaDataItemLoaded(PROGRAMRULEACTIONS, false);
        flagMetaDataItemUpdated(PROGRAMRULEACTIONS, null);
    }


}
