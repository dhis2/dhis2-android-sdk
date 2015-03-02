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

package org.hisp.dhis2.android.sdk.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.otto.Subscribe;

import org.hisp.dhis2.android.sdk.controllers.tasks.LoadAssignedProgramsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadDataElementsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadProgramStagesTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadProgramTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadSmallOptionSetsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadSystemInfoTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadTrackedEntitiesTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.UpdateOptionSetsTask;
import org.hisp.dhis2.android.sdk.events.BaseEvent;
import org.hisp.dhis2.android.sdk.events.MessageEvent;
import org.hisp.dhis2.android.sdk.events.MetaDataResponseEvent;
import org.hisp.dhis2.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis2.android.sdk.network.http.Response;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.DataElement;
import org.hisp.dhis2.android.sdk.persistence.models.DataElement$Table;
import org.hisp.dhis2.android.sdk.persistence.models.Option;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet$Table;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnitProgramRelationship$Table;
import org.hisp.dhis2.android.sdk.persistence.models.Program;
import org.hisp.dhis2.android.sdk.persistence.models.Program$Table;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis2.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntity;
import org.hisp.dhis2.android.sdk.persistence.models.User;
import org.hisp.dhis2.android.sdk.utils.APIException;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 19.02.15.
 */
public class MetaDataController {
    private final static String CLASS_TAG = "MetaDataController";
    private final static String LAST_UPDATED = "lastupdated";
    private int requestCounter = -1;
    private List<String> programsToLoad;
    private List<String> programsToUpdate;
    private Context context;
    private boolean loading = false;
    private boolean synchronizing = false;

    public MetaDataController() {
        Dhis2Application.bus.register(this);
    }

    /**
     * Returns a list of programs assigned to the given organisation unit id
     * @param organisationUnitId
     * @param kind set to null to get all programs. Else get kinds Strings from Program.
     * @return
     */
    public static List<Program> getProgramsForOrganisationUnit(String organisationUnitId,
                                                               String kind) {
        List<OrganisationUnitProgramRelationship> organisationUnitProgramRelationships =
                organisationUnitProgramRelationships=
                Select.all(OrganisationUnitProgramRelationship.class,
                        Condition.column(OrganisationUnitProgramRelationship$Table.ORGANISATIONUNITID).
                                is(organisationUnitId));

        List<Program> programs = new ArrayList<Program>();
        for(OrganisationUnitProgramRelationship oupr: organisationUnitProgramRelationships ) {
            List<Condition> conditions = new ArrayList<Condition>();
            conditions.add(Condition.column(Program$Table.ID).is(oupr.programId));
            if(kind!=null) conditions.add(Condition.column(Program$Table.KIND).is(kind));
            List<Program> plist = new Select().from(Program.class).where(conditions.toArray(new Condition[]{})).queryList();
            programs.addAll(plist); //will only be one but Idk how to query for one..
        }
        return programs;
    }

    public static SystemInfo getSystemInfo() {
        List<SystemInfo> result = Select.all(SystemInfo.class);
        if(result != null && result.size() > 0) return result.get(0);
        else return null;
    }

    public static Program getProgram(String programId) {
        List<Program> plist = Select.all(Program.class, Condition.column(Program$Table.ID).
                is(programId));
        if(plist != null && plist.size() > 0) return plist.get(0);
        else return null;
    }

    /**
     * Returns a list of organisation units assigned to the current user
     * @return
     */
    public static List<OrganisationUnit> getAssignedOrganisationUnits() {
        List<OrganisationUnit> organisationUnits = Select.all(OrganisationUnit.class);
        return organisationUnits;
    }

    /**
     * Returns the data element for the given Id or null if the dataElement does not exist
     * @param dataElementId
     * @return
     */
    public static DataElement getDataElement(String dataElementId) {
        List<DataElement> result =
            Select.all(DataElement.class, Condition.column(DataElement$Table.ID).is(dataElementId));
        if(result != null && result.size() > 0)
            return result.get(0);
        else return null;
    }

    public static User getUser() {
        List<User> users = Select.all(User.class);
        if (users.size() == 0) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * Returns an option set for the given Id or null of the option set doesn't exist.
     * @param optionSetId
     * @return
     */
    public static OptionSet getOptionSet(String optionSetId) {
        List<OptionSet> result = Select.all(OptionSet.class, Condition.column(OptionSet$Table.ID).
                is(optionSetId));
        if(result!=null && result.size() > 0)
            return result.get(0);
        else return null;
    }

    /**
     * Connects to the server and checks for updates in Meta Data. If Meta Data has been updated,
     * changes are downloaded and reflected in the client.
     */
    public void synchronizeMetaData(Context context) {
        Log.e(CLASS_TAG, "loading: " + loading);
        if( loading ) return;
        if(Dhis2.getInstance().getDataValueController().isSending()) return;
        synchronizing = true;
        loadMetaData(context);
    }

    /**
     * Loads metaData from the server and stores it in local persistence.
     * By default this method loads metaData required for data entry in Event Capture
     */
    public void loadMetaData(Context context) {
        if( loading ) return;
        loading = true;
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        String lastUpdated = prefs.getString(LAST_UPDATED, null);
        if(lastUpdated == null || synchronizing)
            loadAssignedPrograms();
        else
            onFinishLoading();
    }

    private void loadSystemInfo() {
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
                        Dhis2Application.bus.post(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        Dhis2Application.bus.post(event);
                    }
                });
        task.execute();
    }

    /**
     * Loads a list of assigned organisation units with their corresponding assigned programs.
     */
    private void loadAssignedPrograms() {
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
                Dhis2Application.bus.post(event);
            }

            @Override
            public void onFailure(APIException exception) {
                holder.setApiException(exception);
                Dhis2Application.bus.post(event);
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
                        Dhis2Application.bus.post(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        Dhis2Application.bus.post(event);
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
                        Dhis2Application.bus.post(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        Dhis2Application.bus.post(event);
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
                        Dhis2Application.bus.post(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        Dhis2Application.bus.post(event);
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
                        Dhis2Application.bus.post(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        Dhis2Application.bus.post(event);
                    }
                });
        task.execute();
    }

    /**
     * Loads option sets from the server
     * This is separated into loading OptionSets with few options and OptionSets with many Options
     * OptionSets with few options can be loaded in one request, while larger OptionSets need to
     * be loaded separately, like for example ICD10.
     */
    private void loadOptionSets() {
        loadSmallOptionSets();
    }

    /**
     * Loads Option Sets that have a low number of Options
     */
    private void loadSmallOptionSets() {
        final ResponseHolder<List<OptionSet>> holder = new ResponseHolder<>();
        final MetaDataResponseEvent<List<OptionSet>> event = new
                MetaDataResponseEvent<>
                (BaseEvent.EventType.loadSmallOptionSet);
        event.setResponseHolder(holder);
        LoadSmallOptionSetsTask task = new LoadSmallOptionSetsTask(NetworkManager.getInstance(),
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
                        Dhis2Application.bus.post(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        Dhis2Application.bus.post(event);
                    }
                });
        task.execute();
    }

    /**
     * Loads Option Sets that have a large number of options.
     */
    private void loadLargeOptionSets() {
        //TODO: implement.
        loadSystemInfo();
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
                        Dhis2Application.bus.post(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        Dhis2Application.bus.post(event);
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
                        Dhis2Application.bus.post(event);
                    }

                    @Override
                    public void onFailure(APIException exception) {
                        holder.setApiException(exception);
                        Dhis2Application.bus.post(event);
                    }
                });
        task.execute();
    }

    private void finishLoading() {
        Log.e(CLASS_TAG, "finishLoading");
        loading = false;
        synchronizing = false;
        MessageEvent event = new MessageEvent(BaseEvent.EventType.onLoadingMetaDataFinished);
        Dhis2Application.bus.post(event);
    }

    private void onFinishLoading() {
        Log.d(CLASS_TAG, "onFinishLoading");
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        LocalDate localDate = new LocalDate();
        editor.putString(LAST_UPDATED, localDate.toString());
        editor.commit();
        finishLoading();
    }

    @Subscribe
    public void onResponse(MetaDataResponseEvent event) {
        Log.d(CLASS_TAG, "onResponse");
        if (event.getResponseHolder().getItem() != null) {
            if (event.eventType == BaseEvent.EventType.loadSystemInfo) {
                SystemInfo systemInfo = (SystemInfo) event.getResponseHolder().getItem();
                systemInfo.save(false);
                Log.d(CLASS_TAG, "got system info " + systemInfo.serverDate);
                onFinishLoading();
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
                    Program oldProgram = getProgram(program.getId());
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
            } else if( event.eventType == BaseEvent.EventType.loadSmallOptionSet ) {
                List<OptionSet> optionSets = ( List<OptionSet> ) event.getResponseHolder().getItem();
                for(OptionSet os: optionSets ) {
                    for( Option o: os.options ) {
                        o.setOptionSet( os.getId() );
                        o.save(false);
                    }
                    os.save(false);
                }
                loadLargeOptionSets();
            } else if( event.eventType == BaseEvent.EventType.onUpdateOptionSets ) {
                loadSystemInfo();
            } else if( event.eventType == BaseEvent.EventType.loadDataElements ) { /*deprecated*/
                List<DataElement> dataElements = ( List<DataElement> ) event.getResponseHolder().getItem();
                for(DataElement de: dataElements ) {
                    de.save(false);
                }
            }
        } else {
            //todo handle more effectively
            if(event.getResponseHolder() != null && event.getResponseHolder().getApiException() != null)
                event.getResponseHolder().getApiException().printStackTrace();
            onFinishLoading();
        }
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isSynchronizing() {
        return synchronizing;
    }
}
