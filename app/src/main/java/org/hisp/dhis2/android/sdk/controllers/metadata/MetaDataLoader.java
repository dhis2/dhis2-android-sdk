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
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadAssignedProgramsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadDataElementsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadOptionSetsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadProgramStagesTask;
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
import org.hisp.dhis2.android.sdk.persistence.models.DataElement;
import org.hisp.dhis2.android.sdk.persistence.models.Option;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis2.android.sdk.persistence.models.Program;
import org.hisp.dhis2.android.sdk.persistence.models.Program$Table;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis2.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntity;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis2.android.sdk.utils.APIException;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles loading of MetaData, including synchronization efforts
 * @author Simen Skogly Russnes on 04.03.15.
 */
public class MetaDataLoader {

    private static final String CLASS_TAG = "MetaDataLoader";

    private int requestCounter = -1;
    private List<String> programsToLoad;
    private List<String> programsToUpdate;
    private Context context;
    boolean loading = false;
    boolean synchronizing = false;
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
     * Initiates loading of programs specified by ids
     */
    private void loadPrograms(ArrayList<String> programIds) {
        if( synchronizing ) {
            //firstly determine if there are any new programs, ie programs that haven't been loaded
            //before.
            //then check the version for the other programs and update if it has changed.
            List<String> newPrograms = new ArrayList<>();
            programsToUpdate = new ArrayList<>();
            for( String programId: programIds ) {
                List<Program> program = Select.all(Program.class, Condition.column(Program$Table.ID).is(programId));
                if(program == null || program.size() <= 0) newPrograms.add(programId);
                else programsToUpdate.add(programId);
            }
            programsToLoad = newPrograms;
        } else {
            programsToLoad = programIds;
        }
        requestCounter = programsToLoad.size();
        if(requestCounter > 0)
            loadProgram(programsToLoad.get(requestCounter - 1));
        else if(synchronizing) updatePrograms();
        else loadOptionSets();
    }

    /**
     * Loads a program from the server based on given id
     * @param id id of program
     */
    private void loadProgram(String id) {
        int current = programsToLoad.size() - requestCounter;
        current++;
        Dhis2.postProgressMessage(context.getString(R.string.loading_program) + " " + current + "/"
                + programsToLoad.size());
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
     * Queries the server and updates already downloaded Programs that need updating.
     */
    private void updatePrograms() {
        requestCounter = -1;
        if( programsToUpdate != null )
            requestCounter = programsToUpdate.size();
        if( requestCounter > 0 ) {
            updateProgram( programsToUpdate.get( requestCounter-1 ) );
        } else {
            loadOptionSets();
        }
    }

    /**
     * Queries the server and updates a program if it is necessary
     */
    private void updateProgram(String id) {
        final ResponseHolder<Program> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<Program> event = new
                MetaDataResponseEvent<>(BaseEvent.EventType.updateProgram);
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
                }, id, true);
        task.execute();
    }

    /**
     * deprecated. Required program stages are loaded with LoadPrograms
     * Loads all program stages
     */
    private void loadProgramStages() {
        final ResponseHolder<List<ProgramStage>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<ProgramStage>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadProgramStages);
        event.setResponseHolder(holder);
        LoadProgramStagesTask task = new LoadProgramStagesTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<ProgramStage>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("programStages");
                            TypeReference<List<ProgramStage>> typeRef =
                                    new TypeReference<List<ProgramStage>>(){};
                            List<ProgramStage> programStages = Dhis2.getInstance().getObjectMapper().
                                    readValue( node.traverse(), typeRef);
                            holder.setItem(programStages);
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

    /**
     * deprecated. Required data elements are loaded with LoadPrograms
     */
    private void loadDataElements() {
        final ResponseHolder<List<DataElement>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<DataElement>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadDataElements);
        event.setResponseHolder(holder);
        LoadDataElementsTask task = new LoadDataElementsTask(NetworkManager.getInstance(),
                new ApiRequestCallback<List<DataElement>>() {
                    @Override
                    public void onSuccess(Response response) {
                        holder.setResponse(response);
                        try {
                            JsonNode node = Dhis2.getInstance().getObjectMapper().
                                    readTree(response.getBody());
                            node = node.get("dataElements");
                            TypeReference<List<DataElement>> typeRef =
                                    new TypeReference<List<DataElement>>(){};
                            List<DataElement> dataElements = Dhis2.getInstance().getObjectMapper().
                                    readValue( node.traverse(), typeRef);
                            holder.setItem(dataElements);
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
        }

        LoadingEvent event;
        if( !synchronizing ) { //initial load or force load of all data.
            Dhis2.setHasLoadedInitialDataPart(context, true, Dhis2.INITIAL_DATA_LOADED_PART_METADATA);
            event = new LoadingEvent(BaseEvent.EventType.onLoadingMetaDataFinished); //called in Dhis2 Subscribing method
            event.success = success;
        } else {
            event = new LoadingEvent(BaseEvent.EventType.onUpdateMetaDataFinished);
            //todo: not yet used but will be used to notify updates in fragments +++
        }
        loading = false;
        synchronizing = false;
        Dhis2Application.bus.post(event);
    }

    private void onResponse(MetaDataResponseEvent event) {
        if (event.getResponseHolder().getItem() != null) {
            if (event.eventType == BaseEvent.EventType.loadSystemInfo) {
                systemInfo = (SystemInfo) event.getResponseHolder().getItem();

                Log.d(CLASS_TAG, "got system info " + systemInfo.serverDate);
                loadAssignedPrograms();
            } else if (event.eventType == BaseEvent.EventType.loadAssignedPrograms) {
                List<OrganisationUnit> organisationUnits = ( List<OrganisationUnit> )
                        event.getResponseHolder().getItem();
                ArrayList<String> assignedPrograms = new ArrayList<String>();

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
                        orgUnitProgram.save(false);
                        if(!assignedPrograms.contains(programId))
                            assignedPrograms.add(programId);
                    }
                    ou.save(false);
                }
                loadPrograms(assignedPrograms);
            } else if (event.eventType == BaseEvent.EventType.loadProgram ) {
                Program program = (Program) event.getResponseHolder().getItem();

                //Have to set program reference in ptea manually because it is not referenced in
                //API JSON
                for(ProgramTrackedEntityAttribute ptea: program.getProgramTrackedEntityAttributes()) {
                    ptea.setProgram(program.getId());
                    ptea.save(false);
                }

                program.save(false);
                for( ProgramStage programStage: program.getProgramStages() ) {
                    programStage.save(false);
                    for(ProgramStageDataElement programStageDataElement: programStage.
                            getProgramStageDataElements()) {
                        programStageDataElement.save(false);
                    }
                }

                requestCounter--;
                if(requestCounter>0) {
                    loadProgram(programsToLoad.get(requestCounter - 1));
                } else {
                    if( synchronizing ) updatePrograms();
                    else loadOptionSets();//loadProgramStages();
                }
            } else if (event.eventType == BaseEvent.EventType.updateProgram ) {
                Program program = (Program) event.getResponseHolder().getItem();
                boolean noProgram = false;
                if( program.id == null ) noProgram = true;

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
                    for(ProgramTrackedEntityAttribute ptea: oldProgram.getProgramTrackedEntityAttributes()) {
                        ptea.delete(false);
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

                requestCounter--;
                if(requestCounter>0) {
                    updateProgram(programsToUpdate.get(requestCounter - 1));
                } else {
                    updateOptionSets();
                }
            } else if( event.eventType == BaseEvent.EventType.loadProgramStages ) { /*deprecated*/
                List<ProgramStage> programStages = ( List<ProgramStage> ) event.
                        getResponseHolder().getItem();
                for(ProgramStage ps: programStages ) {
                    ps.save(false);
                    for(ProgramStageDataElement programStageDataElement: ps.getProgramStageDataElements()) {
                        programStageDataElement.save(false);
                    }
                }
                loadOptionSets();
            } else if( event.eventType == BaseEvent.EventType.loadOptionSets ) {
                List<OptionSet> optionSets = ( List<OptionSet> ) event.getResponseHolder().getItem();
                for(OptionSet os: optionSets ) {
                    Log.d(CLASS_TAG, "saving option set " + os.id);
                    for( Option o: os.options ) {
                        o.setOptionSet( os.getId() );
                        o.save(false);
                    }
                    os.save(false);
                }
                loadTrackedEntityAttributes();
            } else if( event.eventType == BaseEvent.EventType.onUpdateOptionSets ) {
                updateTrackedEntityAttributes();
            } else if( event.eventType == BaseEvent.EventType.loadDataElements ) { /*deprecated*/
                List<DataElement> dataElements = ( List<DataElement> ) event.getResponseHolder().getItem();
                for(DataElement de: dataElements ) {
                    de.save(false);
                }
            } else if (event.eventType == BaseEvent.EventType.loadTrackedEntityAttributes ) {
                List<TrackedEntityAttribute> trackedEntityAttributes = (List<TrackedEntityAttribute>) event.getResponseHolder().getItem();
                for(TrackedEntityAttribute tea: trackedEntityAttributes) {
                    tea.save(false);
                }
                onFinishLoading(true);
            } else if (event.eventType == BaseEvent.EventType.onUpdateTrackedEntityAttributes ) {
                onFinishLoading(true);
            }
        } else {
            //todo handle more effectively
            if(event.getResponseHolder() != null && event.getResponseHolder().getApiException() != null)
                event.getResponseHolder().getApiException().printStackTrace();
            onFinishLoading(false);
        }
    }
}
