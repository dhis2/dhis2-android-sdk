package org.hisp.dhis2.android.sdk.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.otto.Subscribe;

import org.hisp.dhis2.android.sdk.controllers.tasks.LoadAssignedProgramsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadDataElementsTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadProgramStagesTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadProgramTask;
import org.hisp.dhis2.android.sdk.controllers.tasks.LoadSmallOptionSetsTask;
import org.hisp.dhis2.android.sdk.events.BaseEvent;
import org.hisp.dhis2.android.sdk.events.MessageEvent;
import org.hisp.dhis2.android.sdk.events.ResponseEvent;
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
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStageDataElement$Table;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis2.android.sdk.utils.APIException;
import org.joda.time.DateTime;
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
    private List<String> assignedPrograms;
    private Context context;

    public MetaDataController() {
        Dhis2Application.bus.register(this);
    }

    /**
     * Returns a list of programs assigned to the given organisation unit id
     * @param organisationUnitId
     * @return
     */
    public static List<Program> getProgramsForOrganisationUnit(String organisationUnitId) {
        List<OrganisationUnitProgramRelationship> organisationUnitProgramRelationships =
                Select.all(OrganisationUnitProgramRelationship.class,
                Condition.column(OrganisationUnitProgramRelationship$Table.ORGANISATIONUNITID).
                        is(organisationUnitId));
        List<Program> programs = new ArrayList<Program>();
        for(OrganisationUnitProgramRelationship oupr: organisationUnitProgramRelationships ) {
            List<Program> plist = Select.all(Program.class,
                    Condition.column(Program$Table.ID).is(oupr.programId));
            programs.addAll(plist); //will only be one but Idk how to query for one..
        }
        return programs;
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
     * Loads metaData from the server and stores it in local persistence.
     * By default this method loads metaData required for data entry in Event Capture
     */
    public void loadMetaData(Context context) {
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        String lastUpdated = prefs.getString(LAST_UPDATED, null);
        if(lastUpdated == null)
            loadAssignedPrograms();
        else
            finishLoading();
    }

    /**
     * Loads a list of assigned organisation units with their corresponding assigned programs.
     */
    private void loadAssignedPrograms() {
        final ResponseHolder<List<OrganisationUnit>> holder = new ResponseHolder<List<OrganisationUnit>>();
        final ResponseEvent<List<OrganisationUnit>> event = new
                ResponseEvent<List<OrganisationUnit>>(ResponseEvent.EventType.loadAssignedPrograms);
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
                        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
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
        requestCounter = programIds.size();
        assignedPrograms = programIds;
        loadProgram(assignedPrograms.get(requestCounter-1));
    }

    /**
     * Loads a program from the server based on given id
     * @param id
     */
    private void loadProgram(String id) {
        final ResponseHolder<Program> holder = new ResponseHolder<>();
        final ResponseEvent<Program> event = new
                ResponseEvent<Program>(ResponseEvent.EventType.loadProgram);
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
                }, id);
        task.execute();
    }

    /**
     * Loads all program stages
     */
    private void loadProgramStages() {
        final ResponseHolder<List<ProgramStage>> holder = new ResponseHolder<>();
        final ResponseEvent<List<ProgramStage>> event = new
                ResponseEvent<List<ProgramStage>>
                (ResponseEvent.EventType.loadProgramStages);
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
        final ResponseEvent<List<OptionSet>> event = new
                ResponseEvent<List<OptionSet>>
                (ResponseEvent.EventType.loadSmallOptionSet);
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
        //TODO: implement
        loadDataElements();
    }

    private void loadDataElements() {
        final ResponseHolder<List<DataElement>> holder = new ResponseHolder<>();
        final ResponseEvent<List<DataElement>> event = new
                ResponseEvent<List<DataElement>>
                (ResponseEvent.EventType.loadDataElements);
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

    private void finishLoading() {

        MessageEvent event = new MessageEvent(ResponseEvent.EventType.onLoadingMetaDataFinished);
        Dhis2Application.bus.post(event);
    }

    private void onFinishLoading() {
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        LocalDate localDate = new LocalDate();
        Log.e("ddd", localDate.toString());
        //dateTime.g
        editor.putString(LAST_UPDATED, localDate.toString());
        editor.commit();
        finishLoading();
    }

    @Subscribe
    public void onResponse(ResponseEvent event) {
        Log.e(CLASS_TAG, "onResponse");
        if (event.getResponseHolder().getItem() != null) {
            if (event.eventType == ResponseEvent.EventType.loadAssignedPrograms) {
                List<OrganisationUnit> organisationUnits = ( List<OrganisationUnit> )
                        event.getResponseHolder().getItem();
                ArrayList<String> assignedPrograms = new ArrayList<String>();

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
            } else if (event.eventType == ResponseEvent.EventType.loadProgram ) {
                Program program = (Program) event.getResponseHolder().getItem();

                //Have to set program reference in ptea manually because it is not referenced in
                //API JSON
                for(ProgramTrackedEntityAttribute ptea: program.getProgramTrackedEntityAttributes())
                    ptea.setProgram( program.getId() );

                program.save(false);

                requestCounter--;
                if(requestCounter>0) {
                    loadProgram(assignedPrograms.get(requestCounter));
                } else {
                    loadProgramStages();
                }
            } else if( event.eventType == ResponseEvent.EventType.loadProgramStages ) {
                List<ProgramStage> programStages = ( List<ProgramStage> ) event.
                        getResponseHolder().getItem();
                for(ProgramStage ps: programStages ) {
                    ps.save(false);
                    for(ProgramStageDataElement programStageDataElement: ps.getProgramStageDataElements()) {
                        programStageDataElement.save(false);
                    }
                }
                loadOptionSets();
            } else if( event.eventType == ResponseEvent.EventType.loadSmallOptionSet ) {
                List<OptionSet> optionSets = ( List<OptionSet> ) event.getResponseHolder().getItem();
                for(OptionSet os: optionSets ) {
                    for( Option o: os.options ) {
                        o.setOptionSet( os.getId() );
                        o.save(false);
                    }
                    os.save(false);
                }
                loadLargeOptionSets();
            } else if( event.eventType == ResponseEvent.EventType.loadDataElements ) {
                List<DataElement> dataElements = ( List<DataElement> ) event.getResponseHolder().getItem();
                for(DataElement de: dataElements ) {
                    de.save(false);
                }
                onFinishLoading();
            }
        } else {
            event.getResponseHolder().getApiException().printStackTrace();
        }
    }
}
