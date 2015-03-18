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

package org.hisp.dhis2.android.sdk.controllers.metadata;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadAssignedProgramsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadOptionSetsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadProgramTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadSystemInfoTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadTrackedEntitiesTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadTrackedEntityAttributesTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.UpdateOptionSetsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.UpdateTrackedEntityAttributesTask;
import org.hisp.dhis2.android.sdk.events.BaseEvent;
import org.hisp.dhis2.android.sdk.events.LoadingEvent;
import org.hisp.dhis2.android.sdk.events.MetaDataResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.Option;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis2.android.sdk.persistence.models.Program;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis2.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntity;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis2.android.sdk.utils.APIException;
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

    private Context context;
    boolean loading = false;
    boolean synchronizing = false;
    private int retries = 0;
    private int maxRetries = 9;

    private SystemInfo systemInfo;

    /**
     * Connects to the server and checks for updates in Meta Data. If Meta Data has been updated,
     * changes are downloaded and reflected in the client.
     */
    void synchronizeMetaData(Context context) {
        Log.d(CLASS_TAG, "loading: " + loading);
        if( loading ) return;
        if(Dhis2.getInstance().getDataValueController().isSending()) return;
        synchronizing = true;
        loadMetaData(context);
    }

    /**
     * Loads metaData from the server and stores it in local persistence.
     * By default this method loads metaData required for data entry in Event Capture
     */
    void loadMetaData(Context context) {
        if( loading ) return;
        loading = true;
        Dhis2.postProgressMessage(context.getString(R.string.loading_metadata));
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        String lastUpdated = prefs.getString(Dhis2.LAST_UPDATED_METADATA, null);
        systemInfo = null;
        if(lastUpdated == null || synchronizing)
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
        onFinishLoading(true); //called when everything is loaded.
    }

    private void updateItem() {
        String currentLoadingDate = systemInfo.serverDate;
        if(currentLoadingDate == null) {
            return;
        }
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
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
                            TypeReference<List<OptionSet>> typeRef =
                                    new TypeReference<List<OptionSet>>(){};
                            List<OptionSet> optionSets = Dhis2.getInstance().getObjectMapper().
                                    readValue( node.traverse(), typeRef);
                            holder.setItem(optionSets);
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
                            TypeReference<List<TrackedEntity>> typeRef =
                                    new TypeReference<List<TrackedEntity>>(){};
                            List<TrackedEntity> trackedEntities = Dhis2.getInstance().getObjectMapper().
                                    readValue( node.traverse(), typeRef);
                            holder.setItem(trackedEntities);
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
                            TypeReference<List<TrackedEntityAttribute>> typeRef =
                                    new TypeReference<List<TrackedEntityAttribute>>(){};
                            List<TrackedEntityAttribute> trackedEntityAttributes = Dhis2.getInstance().getObjectMapper().
                                    readValue( node.traverse(), typeRef);
                            holder.setItem(trackedEntityAttributes);
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

    private void onFinishLoading(boolean success) {
        Log.d(CLASS_TAG, "onFinishLoading" + success);
        if( success ) {
            SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            LocalDate localDate = new LocalDate();
            editor.putString(Dhis2.LAST_UPDATED_METADATA, localDate.toString());
            editor.commit();
            if(systemInfo != null) {
                List<SystemInfo> result = Select.all(SystemInfo.class);
                if( result != null && !result.isEmpty() )
                    systemInfo.update(false);
                else systemInfo.save(false);
            }
        } else {

        }

        LoadingEvent event;
        if( !synchronizing ) { //initial load or force load of all data.
            event = new LoadingEvent(BaseEvent.EventType.onLoadingMetaDataFinished); //called in Dhis2 Subscribing method
        } else {
            event = new LoadingEvent(BaseEvent.EventType.onUpdateMetaDataFinished);
            //todo: not yet used but can be used to notify updates in fragments +++
        }
        event.success = success;
        loading = false;
        synchronizing = false;
        Dhis2Application.bus.post(event);
    }

    private void onResponse(MetaDataResponseEvent event) {
        if (event.getResponseHolder().getItem() != null) {
            retries = 0;
            if (event.eventType == BaseEvent.EventType.loadSystemInfo) {
                systemInfo = (SystemInfo) event.getResponseHolder().getItem();

                Log.d(CLASS_TAG, "got system info " + systemInfo.serverDate);
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
                    for(String programId : ou.programs) {
                        OrganisationUnitProgramRelationship orgUnitProgram =
                                new OrganisationUnitProgramRelationship();
                        orgUnitProgram.organisationUnitId = ou.getId();
                        orgUnitProgram.programId = programId;
                        orgUnitProgram.save(true);
                        if(!assignedPrograms.contains(programId))
                            assignedPrograms.add(programId);
                    }
                    ou.save(true);
                }

                flagMetaDataItemLoaded(ASSIGNED_PROGRAMS, true);
                flagMetaDataItemUpdated(ASSIGNED_PROGRAMS, systemInfo.serverDate);
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.loadProgram ) {
                Program program = (Program) event.getResponseHolder().getItem();
                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));

                //Have to set program reference in ptea manually because it is not referenced in
                //API JSON
                for(ProgramTrackedEntityAttribute ptea: program.getProgramTrackedEntityAttributes()) {
                    ptea.setProgram(program.getId());
                    ptea.save(true);
                }

                program.save(true);
                for( ProgramStage programStage: program.getProgramStages() ) {
                    programStage.save(true);
                    for(ProgramStageDataElement programStageDataElement: programStage.
                            getProgramStageDataElements()) {
                        programStageDataElement.save(true);
                    }
                }

                flagMetaDataItemLoaded(program.id, true);
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.updateProgram ) {
                Program program = (Program) event.getResponseHolder().getItem();
                boolean noProgram = false;
                if( program.created == null ) noProgram = true;

                if(noProgram) {/*Means the program didn't need to be updated so we just do nothing*/}
                else {


                    /**Delete everything and store it again cause it's not that big, and
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
                            ptea.delete(false);
                        }
                    }
                    for( ProgramStage programStage: program.getProgramStages() ) {
                        for(ProgramStageDataElement psde: programStage.getProgramStageDataElements() ) {
                            psde.delete(false);
                        }
                        programStage.delete(false);
                    }

                    /**
                     * Then we store the new program.
                     */

                    //Have to set program reference in ptea manually because it is not referenced in
                    //API JSON
                    for(ProgramTrackedEntityAttribute ptea: program.getProgramTrackedEntityAttributes()) {
                        ptea.setProgram(program.getId());
                        ptea.save(false);
                    }

                    program.update(false);
                    for( ProgramStage programStage: program.getProgramStages() ) {
                        programStage.save(false);
                        for(ProgramStageDataElement programStageDataElement: programStage.
                                getProgramStageDataElements()) {
                            programStageDataElement.save(false);
                        }
                    }
                }

                flagMetaDataItemUpdated(program.id, systemInfo.serverDate);
                loadItem();
            } else if( event.eventType == BaseEvent.EventType.loadOptionSets ) {
                List<OptionSet> optionSets = ( List<OptionSet> ) event.getResponseHolder().getItem();
                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));
                for(OptionSet os: optionSets ) {
                    for( Option o: os.options ) {
                        o.setOptionSet( os.getId() );
                        o.save(true);
                    }
                    os.save(true);
                }
                flagMetaDataItemLoaded(OPTION_SETS, true);
                loadItem();
            } else if( event.eventType == BaseEvent.EventType.onUpdateOptionSets ) {
                flagMetaDataItemUpdated(OPTION_SETS, systemInfo.serverDate);
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.loadTrackedEntityAttributes ) {
                List<TrackedEntityAttribute> trackedEntityAttributes = (List<TrackedEntityAttribute>) event.getResponseHolder().getItem();
                Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));
                for(TrackedEntityAttribute tea: trackedEntityAttributes) {
                    tea.save(true);
                }
                flagMetaDataItemLoaded(TRACKED_ENTITY_ATTRIBUTES, true);
                loadItem();
            } else if (event.eventType == BaseEvent.EventType.onUpdateTrackedEntityAttributes ) {
                flagMetaDataItemUpdated(TRACKED_ENTITY_ATTRIBUTES, systemInfo.serverDate);
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

    void clearMetaDataLoadedFlags(Context context) {
        this.context = context;
        flagMetaDataItemLoaded(ASSIGNED_PROGRAMS, false);
        flagMetaDataItemUpdated(ASSIGNED_PROGRAMS, null);
        List<String> assignedPrograms = MetaDataController.getAssignedPrograms();
        for(String program: assignedPrograms) {
            flagMetaDataItemLoaded(program, false);
            flagMetaDataItemUpdated(program, null);
        }
        flagMetaDataItemLoaded(OPTION_SETS, false);
        flagMetaDataItemUpdated(OPTION_SETS, null);
        flagMetaDataItemLoaded(TRACKED_ENTITY_ATTRIBUTES, false);
        flagMetaDataItemUpdated(TRACKED_ENTITY_ATTRIBUTES, null);
    }
}
