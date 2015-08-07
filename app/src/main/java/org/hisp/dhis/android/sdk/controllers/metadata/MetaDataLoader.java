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

import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.ApiEndpointContainer;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadAssignedProgramsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadConstantsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadOptionSetsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadProgramRuleActionsTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadProgramRuleVariablesTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadProgramRulesTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadProgramTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadRelationshipTypesTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadSystemInfoTask;
import org.hisp.dhis.android.sdk.controllers.tasks.LoadTrackedEntityAttributesTask;
import org.hisp.dhis.android.sdk.events.BaseEvent;
import org.hisp.dhis.android.sdk.events.InvalidateEvent;
import org.hisp.dhis.android.sdk.events.MetaDataResponseEvent;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.Option;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicatorToSectionRelationship;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleAction;
import org.hisp.dhis.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageSection;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.RelationshipType;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.utils.APIException;
import org.hisp.dhis.android.sdk.utils.support.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.hisp.dhis.android.sdk.controllers.LoadFlagContainer.LoadFlag;

/**
 * Handles loading of MetaData, including synchronization efforts
 *
 * @author Simen Skogly Russnes on 04.03.15.
 */
public final class MetaDataLoader {

    private static final String CLASS_TAG = "MetaDataLoader";

    private static MetaDataLoader metaDataLoader;

    static {
        metaDataLoader = new MetaDataLoader();
    }

    public static MetaDataLoader getInstance() {
        return metaDataLoader;
    }

    boolean loading = false;
    private int retries = 0;
    private static final int maxRetries = 9;
    private SystemInfo systemInfo;

    private MetaDataLoader() {}

    /**
     * Connects to the server and checks for updates in Meta Data. If Meta Data has been updated,
     * changes are downloaded and reflected in the client.
     */
    static void synchronizeMetaData(Context context, ApiRequestCallback callback) {
        Log.d(CLASS_TAG, "check if already loading: " + Dhis2.isLoading());
        if (Dhis2.isLoading()) {
            callback.onFailure(null);
        }
        loadMetaData(context, callback, true);
    }

    /**
     * Loads metaData from the server and stores it in local persistence.
     * By default this method loads metaData required for data entry in Event Capture
     */
    static void loadMetaData(Context context, ApiRequestCallback callback, boolean update) {
        if (Dhis2.isLoading()) {
            callback.onFailure(null);
            return;
        }
        Dhis2.postProgressMessage(context.getString(R.string.loading_metadata));
        FinishLoadingCallback finishLoadingCallback = new FinishLoadingCallback(callback, context);
        loadSystemInfo(finishLoadingCallback, context, update);
    }

    private static class FinishLoadingCallback implements ApiRequestCallback {

        private final ApiRequestCallback callback;
        private final Context context;

        private FinishLoadingCallback(ApiRequestCallback callback, Context context) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            LocalDate localDate = new LocalDate();
            editor.putString(Dhis2.LAST_UPDATED_METADATA, localDate.toString());
            editor.commit();
            if (getInstance().systemInfo != null) {
                List<SystemInfo> result = new Select().from(SystemInfo.class).queryList();
                if (result != null && !result.isEmpty()) {
                    getInstance().systemInfo.async().update();
                } else {
                    getInstance().systemInfo.async().save();
                }
            }
            finish();
            callback.onSuccess(responseHolder);
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            finish();
            callback.onFailure(responseHolder);
        }

        private void finish() {
            getInstance().loading = false;
            InvalidateEvent event = new InvalidateEvent(InvalidateEvent.EventType.metaDataLoaded);
            Dhis2Application.getEventBus().post(event);
        }
    }

    /**
     * determines if a meta data item should be loaded. Either because it hasnt been loaded before,
     * or because it needs to be updated based on time.
     * @param flag
     * @param update
     * @return
     */
    private static boolean shouldLoad( Context context, String flag, boolean update ) {
        if( !update && !isMetaDataItemLoaded(context, flag) ) {
            return true;
        } else if ( update ) {
            String currentLoadingDate = getInstance().systemInfo.getServerDate();
            if ( currentLoadingDate == null ) {
                return false;
            }
            String lastUpdatedString = getLastUpdatedDateForMetaDataItem(context, flag);
            String pattern = DateUtils.LONG_DATE_FORMAT.toPattern();
            DateTime currentDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(currentLoadingDate);
            if ( lastUpdatedString == null ) {
                return true;
            }
            DateTime updatedDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(lastUpdatedString);
            if ( updatedDateTime.isBefore(currentDateTime) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * determines if a meta data item should be loaded. Either because it hasnt been loaded before,
     * or because it needs to be updated based on time.
     * @param flag
     * @param update
     * @return
     */
    private static boolean shouldLoad( Context context, LoadFlag flag, boolean update ) {
        return shouldLoad(context, flag.name(), update);
    }

    /**
     * Loads a metadata item that is scheduled to be loaded but has not yet been.
     */
    private static void loadItem(ApiRequestCallback callback, Context context, boolean synchronizing) {
        //some items depend on each other. Programs depend on AssignedPrograms because we need
        //the ids of programs to load.
        if (Dhis2.isLoadFlagEnabled(context, LoadFlag.ASSIGNED_PROGRAMS)) {

            if ( shouldLoad(context, LoadFlag.ASSIGNED_PROGRAMS, synchronizing) ) {
                loadAssignedPrograms(callback, context, synchronizing);
                return;
            }
        }
        if (Dhis2.isLoadFlagEnabled(context, LoadFlag.PROGRAMS)) {
            List<String> assignedPrograms = MetaDataController.getAssignedPrograms();
            if (assignedPrograms != null) {
                for (String program : assignedPrograms) {
                    if ( shouldLoad(context, program, synchronizing) ) {
                        loadProgram(callback, context, program, synchronizing);
                        return;
                    }
                }
            }
        }
        if (Dhis2.isLoadFlagEnabled(context, LoadFlag.OPTION_SETS)) {
            if ( shouldLoad(context, LoadFlag.OPTION_SETS, synchronizing) ) {
                loadOptionSets(callback, context, synchronizing);
                return;
            }
        }
        if (Dhis2.isLoadFlagEnabled(context, LoadFlag.TRACKED_ENTITY_ATTRIBUTES)) {
            if ( shouldLoad(context, LoadFlag.TRACKED_ENTITY_ATTRIBUTES, synchronizing) ) {
                loadTrackedEntityAttributes(callback, context, synchronizing);
                return;
            }
        }
        if (Dhis2.isLoadFlagEnabled(context, LoadFlag.CONSTANTS)) {
            if ( shouldLoad(context, LoadFlag.CONSTANTS, synchronizing) ) {
                loadConstants(callback, context, synchronizing);
                return;
            }
        }
        if (Dhis2.isLoadFlagEnabled(context, LoadFlag.PROGRAMRULES)) {
            if ( shouldLoad(context, LoadFlag.PROGRAMRULES, synchronizing) ) {
                loadProgramRules(callback, context, synchronizing);
                return;
            }
        }
        if (Dhis2.isLoadFlagEnabled(context, LoadFlag.PROGRAMRULEVARIABLES)) {
            if ( shouldLoad(context, LoadFlag.PROGRAMRULEVARIABLES, synchronizing) ) {
                loadProgramRuleVariables(callback, context, synchronizing);
                return;
            }
        }
        if (Dhis2.isLoadFlagEnabled(context, LoadFlag.PROGRAMRULEACTIONS)) {
            if ( shouldLoad(context, LoadFlag.PROGRAMRULEACTIONS, synchronizing) ) {
                loadProgramRuleActions(callback, context, synchronizing);
                return;
            }
        }
        if (Dhis2.isLoadFlagEnabled(context, LoadFlag.RELATIONSHIPTYPES)) {
            if ( shouldLoad(context, LoadFlag.RELATIONSHIPTYPES, synchronizing) ) {
                loadRelationshipTypes(callback, context, synchronizing);
                return;
            }
        }
        callback.onSuccess(null);
    }

    /**
     * resets the saved date for last updated meta data
     *
     * @param context
     */
    static void resetLastUpdated(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Dhis2.LAST_UPDATED_METADATA, null);
        editor.commit();
    }

    static private void loadSystemInfo(ApiRequestCallback callback, Context context, boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_server_info));
        final MetaDataResponseEvent<SystemInfo> event = new
                MetaDataResponseEvent<>(BaseEvent.EventType.loadSystemInfo);
        LoadSystemInfoItemCallback loadSystemInfoItemCallback = new
                LoadSystemInfoItemCallback(callback, context, null, update);
        LoadSystemInfoTask task = new LoadSystemInfoTask(NetworkManager.getInstance(),
                new ParseApiItemCallback<>(loadSystemInfoItemCallback, SystemInfo.class));
        task.execute();
    }

    private static class LoadSystemInfoItemCallback extends LoadItemCallback {
        private LoadSystemInfoItemCallback(ApiRequestCallback callback, Context context, String flag, boolean synchronizing) {
            super(callback, context, flag, synchronizing);
        }

        @Override
        protected void save(Object item) {
            try {
                SystemInfo systemInfo = (SystemInfo) item;
                getInstance().systemInfo = systemInfo;
            } catch ( ClassCastException e ) {
                e.printStackTrace();
            }
        }

        @Override
        protected void flag(String identifier) {
            //do nothing
        }
    }

    /**
     * Loads a list of assigned organisation units with their corresponding assigned programs.
     */
    private static void loadAssignedPrograms(ApiRequestCallback callback, Context context, boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_assigned_programs));
        LoadAssignedProgramsItemListCallback loadAssignedProgramsItemListCallback =
                new LoadAssignedProgramsItemListCallback(callback, context, LoadFlag.ASSIGNED_PROGRAMS, update);
        LoadAssignedProgramsTask task = new LoadAssignedProgramsTask(NetworkManager.getInstance(),
                new ParseApiListCallback<>(loadAssignedProgramsItemListCallback, ApiEndpointContainer.ORGANISATIONUNITS, OrganisationUnit.class));
        task.execute();
    }

    private static class LoadAssignedProgramsItemListCallback extends LoadItemListCallback {

        private LoadAssignedProgramsItemListCallback(ApiRequestCallback callback, Context context, LoadFlag loadFlag, boolean synchronizing) {
            super(callback, context, loadFlag, synchronizing);
        }

        @Override
        protected void saveItemList(List<BaseModel> items) {

            /**
             * If we are synchronizing we simply delete all the previously stored
             * OrganisationUnitProgramRelationships and save the new ones since there
             * usually isn't that many.
             */
            if (synchronizing) {
                Delete.tables(OrganisationUnitProgramRelationship.class, OrganisationUnit.class);
            }
            ArrayList<String> assignedPrograms = new ArrayList<String>();
            for (BaseModel item : items) {
                try {
                    OrganisationUnit ou = (OrganisationUnit) item;
                    for (String programId : ou.getPrograms()) {
                        OrganisationUnitProgramRelationship orgUnitProgram =
                                new OrganisationUnitProgramRelationship();
                        orgUnitProgram.setOrganisationUnitId(ou.getId());
                        orgUnitProgram.setProgramId(programId);
                        orgUnitProgram.async().save();
                        if (!assignedPrograms.contains(programId))
                            assignedPrograms.add(programId);
                    }
                    ou.async().save();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    super.saveItemList(items);
                }
            }
        }

        @Override
        protected void flag(String identifier) {
            flagMetaDataItemLoaded(context, identifier, true);
            flagMetaDataItemUpdated(context, identifier, getInstance().systemInfo.getServerDate());
        }
    }

    /**
     * Loads a program from the server based on given id
     *
     * @param id id of program
     */
    private static void loadProgram(ApiRequestCallback callback, Context context, String id, boolean update) {

        Dhis2.postProgressMessage(context.getString(R.string.loading_program) + ": " + id);
        LoadProgramItemCallback loadProgramItemCallback = new LoadProgramItemCallback(callback, context, id, update);
        LoadProgramTask task = new LoadProgramTask(NetworkManager.getInstance(),
                new ParseApiItemCallback<>(loadProgramItemCallback, Program.class), id, update);
        task.execute();
    }

    private static class LoadProgramItemCallback extends LoadItemCallback {

        private LoadProgramItemCallback(ApiRequestCallback callback, Context context, String itemName, boolean synchronizing) {
            super(callback, context, itemName, synchronizing);
        }

        @Override
        protected void save(Object object) {
            try {
                Program program = (Program) object;
                if( synchronizing ) {
                    update(program);
                } else {
                    program.async().save();
                }

                int sortOrder = 0;
                for (ProgramTrackedEntityAttribute ptea : program.getProgramTrackedEntityAttributes()) {
                    ptea.setProgram(program.getId());
                    ptea.setSortOrder(sortOrder);
                    ptea.async().save();
                    sortOrder++;
                }

                for (ProgramStage programStage : program.getProgramStages()) {
                    programStage.async().save();
                    if (programStage.getProgramStageSections() != null && !programStage.getProgramStageSections().isEmpty()) {
                        // due to the way the WebAPI lists programStageSections we have to manually
                        // set id of programStageSection in programStageDataElements to be able to
                        // access it later when loading from local db
                        for (ProgramStageSection programStageSection : programStage.getProgramStageSections()) {
                            programStageSection.async().save();
                            for (ProgramStageDataElement programStageDataElement : programStageSection.getProgramStageDataElements()) {
                                programStageDataElement.setProgramStageSection(programStageSection.getId());
                                programStageDataElement.async().save();
                                //todo[simen]: consider implementing override of save function rather
                                //todo: than doing this manually
                            }
                            for (ProgramIndicator programIndicator : programStageSection.getProgramIndicators()) {
                                // we need to save program indicator synchronously in order to guarantee
                                // its availability in database for ProgramIndicatorToSectionRelationship
                                programIndicator.save();

                                // relation to stage
                                saveStageRelation(programIndicator, programStage.getId());

                                // relation to section
                                saveStageRelation(programIndicator, programStageSection.getId());
                            }
                        }
                    } else {
                        for (ProgramStageDataElement programStageDataElement : programStage.
                                getProgramStageDataElements()) {
                            programStageDataElement.async().save();
                        }
                        for (ProgramIndicator programIndicator : programStage.getProgramIndicators()) {
                            // we need to save program indicator synchronously in order to guarantee
                            // its availability in database for ProgramIndicatorToSectionRelationship
                            programIndicator.save();

                            // relation to stage
                            saveStageRelation(programIndicator, programStage.getId());
                        }
                    }
                }
            } catch ( ClassCastException e ) {
                e.printStackTrace();
            }
        }

        private void saveStageRelation(ProgramIndicator programIndicator, String programSection) {
            ProgramIndicatorToSectionRelationship stageRelation = new ProgramIndicatorToSectionRelationship();
            stageRelation.setProgramIndicator(programIndicator);
            stageRelation.setProgramSection(programSection);
            stageRelation.async().save();
        }

        private void update(Program program) {
            if ( program.getCreated() != null ) {
            /* Else it means the response was empty so program didn't need to be updated so we just do nothing */

                /**Delete everything (except shared things like dataElement and
                 * trackedEntityAttribute) and store it again cause it's easier, it's not that big, and
                 * it rarely happens.
                 * what needs to be deleted is:
                 * ProgramTrackedEntityAttribute (not the TrackedEntityAttribute itself)
                 * ProgramStageDataElement (not the actual DataElement, we can simply update that)
                 * The program stages since they are referenced with lazy loading
                 *  but we need to delete the ProgramStageDataElements because it is the link
                 *  between the program and dataelement
                 */

            /*firstly we should get the old program from the database and delete using that
              reference
             */
                Program oldProgram = MetaDataController.getProgram(program.getId());
                if (oldProgram != null) {
                    for (ProgramTrackedEntityAttribute ptea : oldProgram.getProgramTrackedEntityAttributes()) {
                        ptea.delete();
                    }
                    for (ProgramStage programStage : program.getProgramStages()) {
                        for (ProgramStageDataElement psde : programStage.getProgramStageDataElements()) {
                            psde.delete();
                        }
                        for (ProgramStageSection programStageSection : programStage.getProgramStageSections()) {
                            programStageSection.delete();
                        }
                        programStage.delete();
                    }
                    for (ProgramIndicator programIndicator : program.getProgramIndicators()) {
                        programIndicator.delete();
                    }
                    program.async().update();
                } else {
                    program.async().save();
                }
            }
        }
    }

    /**
     * Loads all option sets from the server
     */
    private static void loadOptionSets(ApiRequestCallback callback, Context context, boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_optionsets));
        LoadItemListCallback loadItemListCallback = new LoadOptionSetsItemListCallback(callback, context, LoadFlag.OPTION_SETS, update);
        LoadOptionSetsTask task = new LoadOptionSetsTask(NetworkManager.getInstance(),
                new ParseApiListCallback<>(loadItemListCallback, ApiEndpointContainer.OPTION_SETS, OptionSet.class), update);
        task.execute();
    }

    /**
     * Custom callback for saving OptionSets. This exists because the options in the options sets
     * need to manually be assigned an index for sorting. //todo: this could be replaced by an
     * auto-increment integer in the database maybe?
     */
    private static class LoadOptionSetsItemListCallback extends LoadItemListCallback {

        private LoadOptionSetsItemListCallback(ApiRequestCallback callback, Context context, LoadFlag loadFlag, boolean synchronizing) {
            super(callback, context, loadFlag, synchronizing);
        }

        @Override
        protected void saveItemList(List<BaseModel> items) {
            for(BaseModel item: items) {
                try {
                    OptionSet optionSet = (OptionSet) item;
                    int index = 0;
                    for (Option o : optionSet.getOptions()) {
                        o.setSortIndex(index);
                        o.setOptionSet(optionSet.getId());
                        o.async().save();
                        index++;
                    }
                    optionSet.async().save();
                } catch ( ClassCastException e ) {
                    e.printStackTrace();
                    super.saveItemList(items);
                }
            }
        }
    }

    private static void loadTrackedEntityAttributes(ApiRequestCallback callback, Context context, boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_trackedentityattributes));
        LoadItemListCallback loadItemListCallback = new LoadItemListCallback(callback, context, LoadFlag.TRACKED_ENTITY_ATTRIBUTES, update);
        LoadTrackedEntityAttributesTask task = new LoadTrackedEntityAttributesTask(NetworkManager.getInstance(),
                new ParseApiListCallback<>(loadItemListCallback, ApiEndpointContainer.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttribute.class), update);
        task.execute();
    }

    private static void loadConstants(ApiRequestCallback callback, Context context, boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_constants));
        LoadItemListCallback loadItemListCallback = new LoadItemListCallback(callback, context, LoadFlag.CONSTANTS, update);
        LoadConstantsTask task = new LoadConstantsTask(NetworkManager.getInstance(),
                new ParseApiListCallback<>(loadItemListCallback, ApiEndpointContainer.CONSTANTS, Constant.class), update);
        task.execute();
    }

    private static void loadProgramRules(ApiRequestCallback callback, Context context, boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_programrules));
        LoadItemListCallback loadItemListCallback = new LoadItemListCallback(callback, context, LoadFlag.PROGRAMRULES, update);
        LoadProgramRulesTask task = new LoadProgramRulesTask(NetworkManager.getInstance(),
                new ParseApiListCallback<>(loadItemListCallback, ApiEndpointContainer.PROGRAMRULES, ProgramRule.class), update);
        task.execute();
    }

    private static void loadProgramRuleVariables(ApiRequestCallback callback, Context context, boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_programrulevariables));
        LoadItemListCallback loadItemListCallback = new LoadItemListCallback(callback, context, LoadFlag.PROGRAMRULEVARIABLES, update);
        LoadProgramRuleVariablesTask task = new LoadProgramRuleVariablesTask(NetworkManager.getInstance(),
                new ParseApiListCallback<>(loadItemListCallback, ApiEndpointContainer.PROGRAMRULEVARIABLES, ProgramRuleVariable.class), update);
        task.execute();
    }

    private static void loadProgramRuleActions(ApiRequestCallback callback, Context context, boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_programruleactions));
        LoadItemListCallback loadItemListCallback = new LoadItemListCallback(callback, context, LoadFlag.PROGRAMRULEACTIONS, update);
        LoadProgramRuleActionsTask task = new LoadProgramRuleActionsTask(NetworkManager.getInstance(),
                new ParseApiListCallback<>(loadItemListCallback, ApiEndpointContainer.PROGRAMRULEACTIONS, ProgramRuleAction.class), update);
        task.execute();
    }

    private static void loadRelationshipTypes(ApiRequestCallback callback, Context context, boolean update) {
        Dhis2.postProgressMessage(context.getString(R.string.loading_relationshiptypes));
        LoadItemListCallback loadItemListCallback = new LoadItemListCallback(callback, context, LoadFlag.RELATIONSHIPTYPES, update);
        LoadRelationshipTypesTask task = new LoadRelationshipTypesTask(NetworkManager.getInstance(),
                new ParseApiListCallback<>(loadItemListCallback, ApiEndpointContainer.RELATIONSHIPTYPES, RelationshipType.class), update);
        task.execute();
    }

    private static class LoadItemCallback implements ApiRequestCallback {

        protected final ApiRequestCallback callback;
        protected final Context context;

        /**
         * The flag to be used to save load status and update date to shared preferences
         */
        private final String flag;

        /**
         * Indicates whether or not the item is being loaded independent of last updated or not.
         * True if updating based on last updated
         */
        protected final boolean synchronizing;

        private LoadItemCallback(ApiRequestCallback callback, Context context, String flag, boolean synchronizing) {
            this.callback = callback;
            this.context = context;
            this.flag = flag;
            this.synchronizing = synchronizing;
        }

        private LoadItemCallback(ApiRequestCallback callback, Context context, LoadFlag flag, boolean synchronizing) {
            this.callback = callback;
            this.context = context;
            this.flag = flag.name();
            this.synchronizing = synchronizing;
        }

        @Override
        public void onSuccess(ResponseHolder responseHolder) {
            if( responseHolder.getItem() != null ) {
                resetRetries();
                if( context != null ) {
                    Dhis2.postProgressMessage(context.getString(R.string.saving_data_locally));
                }
                save(responseHolder.getItem());
                String identifier = getIdentifier();
                flag(identifier);
                loadItem(callback, context, synchronizing);
            } else {
                handleException(responseHolder, context, callback, synchronizing);
            }
        }

        @Override
        public void onFailure(ResponseHolder responseHolder) {
            callback.onFailure(responseHolder);
        }

        protected void save(Object object) {
            try {
                BaseModel item = (BaseModel) object;
                item.async().save();
            } catch ( ClassCastException e ) {
                e.printStackTrace();
            }
        }

        protected void flag(String identifier) {
            if( !synchronizing ) {
                flagMetaDataItemLoaded(context, identifier, true);
            } else {
                flagMetaDataItemUpdated(context, identifier, getInstance().systemInfo.getServerDate());
            }
        }

        protected void resetRetries() {
            getInstance().retries = 0;
        }

        protected String getIdentifier() {
            return flag;
        }
    }

    private static class LoadItemListCallback extends LoadItemCallback {

        private LoadItemListCallback(ApiRequestCallback callback, Context context, LoadFlag loadFlag, boolean synchronizing) {
            super(callback, context, loadFlag, synchronizing);
        }

        @Override
        protected void save(Object object) {
            try {
                List<BaseModel> items = (List<BaseModel>) object;
                saveItemList(items);
            } catch ( ClassCastException e ) {
                e.printStackTrace();
            }
        }

        protected void saveItemList(List<BaseModel> items) {
            for( BaseModel item: items ) {
                item.async().save();
            }
        }
    }

    private static class ParseApiListCallback<T> extends ParseApiCallback<T> {

        protected final String nodeName;

        public ParseApiListCallback(ApiRequestCallback callback, String nodeName, Class<T> type) {
            super(callback, type);
            this.nodeName = nodeName;
        }

        @Override
        protected void parse(ResponseHolder responseHolder) throws IOException {
            JsonNode node = Dhis2.getInstance().getObjectMapper().
                    readTree(responseHolder.getResponse().getBody());
            node = node.get(nodeName);

            if (node == null) { /* in case there are no items */
                responseHolder.setItem(new ArrayList<T>());
            } else {
                Iterator<JsonNode> nodes = node.elements();
                List<T> items = new ArrayList<>();
                while(nodes.hasNext()) {
                    JsonNode indexNode = nodes.next();
                    T item = Dhis2.getInstance().getObjectMapper().
                            readValue(indexNode.toString(), type);
                    items.add(item);
                }
                responseHolder.setItem(items);
            }

        }
    }

    private static class ParseApiItemCallback<T> extends ParseApiCallback<T> {

        public ParseApiItemCallback(ApiRequestCallback callback, Class<T> type) {
            super(callback, type);
        }

        protected void parse(ResponseHolder<T> responseHolder) throws IOException {
            T item = Dhis2.getInstance().getObjectMapper().readValue(responseHolder.getResponse().getBody(), type);
            responseHolder.setItem(item);
        }
    }

    private abstract static class ParseApiCallback<T> implements ApiRequestCallback<T> {

        private final ApiRequestCallback callback;
        protected final Class<T> type;

        private ParseApiCallback(ApiRequestCallback callback, Class<T> type) {
            this.callback = callback;
            this.type = type;
        }

        @Override
        public void onSuccess(ResponseHolder<T> responseHolder) {
            try {
                parse(responseHolder);
            } catch (IOException e) {
                e.printStackTrace();
                responseHolder.setApiException(APIException.conversionError(responseHolder.getResponse().getUrl(), responseHolder.getResponse(), e));
            }
            callback.onSuccess(responseHolder);
        }

        @Override
        public void onFailure(ResponseHolder<T> responseHolder) {
            callback.onFailure(responseHolder);
        }

        protected abstract void parse(ResponseHolder<T> responseHolder) throws IOException;
    }

    private static void handleException(ResponseHolder holder, Context context, ApiRequestCallback callback, boolean synchronizing) {
        //todo handle more effectively
        Log.d(CLASS_TAG, "exception..");
        APIException exception = null;
        if (holder != null && holder.getApiException() != null) {
            holder.getApiException().printStackTrace();
            Log.e(CLASS_TAG, holder.getApiException().getLocalizedMessage(), holder.getApiException());
            exception = holder.getApiException();
            if (exception.isConversionError()) {
                //todo: do something
            } else if (exception.isNetworkError()) {
                //todo: do something
            } else if (exception.isHttpError()) {
                //todo: do something
            } else if (exception.isUnknownError()) {
                //todo: do something
            }
        }
        if (getInstance().retries >= maxRetries) {
            getInstance().retries = 0;
            callback.onFailure(holder);
        } else {
            retry(callback, context, synchronizing, exception);
        }
    }

    private static void retry(final ApiRequestCallback callback, final Context context, final boolean synchronizing, APIException exception) {
        String message = "";
        if ( exception != null ) {
            message = exception.getMessage();
        }
        getInstance().retries++;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                loadItem(callback, context, synchronizing);
            }
        };
        long ms = 30 * 1000;
        Timer timer = new Timer();
        Dhis2.postProgressMessage(context.getString(R.string.loading_failed) + ": " + message + ". "
                + context.getString(R.string.retrying) + ": " + context.getString(R.string.retry)
                + " " + getInstance().retries + "/" + maxRetries);
        Log.d(CLASS_TAG, "Retry " + getInstance().retries + "/" + maxRetries + ". Retrying in " +
                (ms / 1000) + " seconds..");
        timer.schedule(task, ms);
    }

    /**
     * Flags a MetaData item like Programs or OptionSets to indicate whether or not it has been loaded.
     * Can also be set for a UID for example for an individual Program.
     *
     * @param item
     * @param loaded
     */
    private static void flagMetaDataItemLoaded(Context context, String item, boolean loaded) {
        if (context == null) return;
        Log.d("METADATALOADERisLoaded,", item);
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Dhis2.LOADED + item, loaded);
        editor.commit();
    }

    /**
     * Returns a boolean indicating whether or not a MetaData item has been loaded successfully.
     *
     * @param flag
     * @return
     */
    private static boolean isMetaDataItemLoaded(Context context, String flag) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(Dhis2.LOADED + flag, false);
    }

    /**
     * Returns a boolean indicating whether or not a MetaData item has been loaded successfully.
     *
     * @param flag
     * @return
     */
    private static boolean isMetaDataItemLoaded(Context context, LoadFlag flag) {
        return isMetaDataItemLoaded(context, flag.name());
    }

    /**
     * sets the date of the last time a meta data item was loaded.
     *
     * @param item
     * @param date
     */
    private static void flagMetaDataItemUpdated(Context context, String item, String date) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Dhis2.UPDATED + item, date);
        editor.commit();
    }

    /**
     * sets the date of the last time a meta data item was loaded.
     *
     * @param item
     * @param date
     */
    private static void flagMetaDataItemUpdated(Context context, LoadFlag item, String date) {
        flagMetaDataItemUpdated(context, item.name(), date);
    }

    private static String getLastUpdatedDateForMetaDataItem(Context context, String item) {
        if (context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences(Dhis2.PREFS_NAME, Context.MODE_PRIVATE);
        Date date = DateUtils.parseDate(prefs.getString(Dhis2.UPDATED + item, null));
        return DateUtils.getLongDateString(date);
    }

    private static String getLastUpdatedDateForMetaDataItem(Context context, LoadFlag item) {
        return getLastUpdatedDateForMetaDataItem(context, item.name());
    }

    /**
     * Clears status and time of loaded meta data items
     *
     * @param context
     */
    static void clearMetaDataLoadedFlags(Context context) {
        clearMetaDataLoadedFlag(context, LoadFlag.ASSIGNED_PROGRAMS);
        List<String> assignedPrograms = MetaDataController.getAssignedPrograms();
        for (String program : assignedPrograms) {
            clearMetaDataLoadedFlag(context, program);
        }
        clearMetaDataLoadedFlag(context, LoadFlag.OPTION_SETS);
        clearMetaDataLoadedFlag(context, LoadFlag.TRACKED_ENTITY_ATTRIBUTES);
        clearMetaDataLoadedFlag(context, LoadFlag.CONSTANTS);
        clearMetaDataLoadedFlag(context, LoadFlag.PROGRAMRULES);
        clearMetaDataLoadedFlag(context, LoadFlag.PROGRAMRULEVARIABLES);
        clearMetaDataLoadedFlag(context, LoadFlag.PROGRAMRULEACTIONS);
        clearMetaDataLoadedFlag(context, LoadFlag.RELATIONSHIPTYPES);
    }

    private static void clearMetaDataLoadedFlag(Context context, LoadFlag flag) {
        clearMetaDataLoadedFlag(context, flag.name());
    }

    private static void clearMetaDataLoadedFlag(Context context, String flag) {
        flagMetaDataItemLoaded(context, flag, false);
        flagMetaDataItemUpdated(context, flag, null);
    }
}
