/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.KeyValue;
import org.hisp.dhis.client.sdk.core.common.controllers.AbsSyncStrategyController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.core.user.UserApiClient;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.utils.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.core.common.utils.ModelUtils.toMap;
import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ProgramControllerImpl extends
        AbsSyncStrategyController<Program> implements ProgramController {
    private static final String TAG = ProgramController.class.getSimpleName();

    /* Controllers */
    private final SystemInfoController systemInfoController;
    private final ProgramStageController programStageController;
    private final ProgramStageSectionController programStageSectionController;

    /* Api clients */
    private final ProgramApiClient programApiClient;
    private final UserApiClient userApiClient;

    /* Utilities */
    private final TransactionManager transactionManager;
    private final Logger logger;

    public ProgramControllerImpl(SystemInfoController systemInfoController,
                                 ProgramStageController programStageController,
                                 ProgramStageSectionController programStageSectionController,
                                 UserApiClient userApiClient, ProgramStore programStore,
                                 TransactionManager transactionManager,
                                 LastUpdatedPreferences lastUpdatedPreferences,
                                 ProgramApiClient programApiClient,
                                 Logger logger) {
        super(ResourceType.PROGRAMS, programStore, lastUpdatedPreferences);

        this.systemInfoController = systemInfoController;
        this.programStageController = programStageController;
        this.programStageSectionController = programStageSectionController;

        this.programApiClient = programApiClient;
        this.userApiClient = userApiClient;

        this.transactionManager = transactionManager;
        this.logger = logger;
    }

    @Override
    protected void synchronize(SyncStrategy syncStrategy, Set<String> uids) {
        synchronizeByLastUpdated(uids);
    }

    @Override
    public void pull(SyncStrategy syncStrategy, ProgramFields fields, Set<String> uids) throws ApiException {
        isNull(fields, "ProgramFields must not be null");

        // delegate call to existing implementation
        if (ProgramFields.BASIC.equals(fields)) {
            pull(syncStrategy, uids);
            return;
        }


        // we need to sync all models related to program in one query
        if (ProgramFields.DESCENDANTS.equals(fields)) {
            synchronizeProgramsByVersions(uids);
        }
    }

    private void synchronizeByLastUpdated(Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.PROGRAMS, DateType.SERVER);

        List<Program> persistedPrograms = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<Program> allExistingPrograms = programApiClient.getPrograms(Fields.BASIC, null, null);

        List<Program> updatedPrograms = new ArrayList<>();
        if (uids == null) {
            updatedPrograms.addAll(programApiClient.getPrograms(
                    Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedPrograms);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedPrograms.addAll(programApiClient.getPrograms(
                        Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedPrograms.addAll(programApiClient.getPrograms(
                        Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

        // we need to mark assigned programs as "assigned" before storing them
        // TODO remove this call (additional request which performs check if user is assigned)
        Map<String, Program> assignedPrograms = toMap(userApiClient
                .getUserAccount().getPrograms());

        for (Program updatedProgram : updatedPrograms) {
            Program assignedProgram = assignedPrograms.get(updatedProgram.getUId());
            updatedProgram.setIsAssignedToUser(assignedProgram != null);
        }

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(allExistingPrograms,
                updatedPrograms, persistedPrograms, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAMS, DateType.SERVER, serverTime);
    }

    private void synchronizeProgramsByVersions(Set<String> uids) throws ApiException {
        isNull(uids, "Set of uids must not be null");

        if (uids.isEmpty()) {
            throw new IllegalArgumentException("Specify at least one uid of program to sync");
        }

        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();

        // updating stuff
        KeyValue<List<Program>, List<DbOperation>> updatedPrograms =
                updatePrograms(uids);
        KeyValue<List<ProgramStage>, List<DbOperation>> updatedStages =
                updateProgramStages(updatedPrograms.getKey());
        KeyValue<List<ProgramStageSection>, List<DbOperation>> updatedSections =
                updateProgramStageSections(updatedStages.getKey());
        KeyValue<List<ProgramStageDataElement>, List<DbOperation>> updatedDataElements =
                updateProgramStageDataElements(updatedStages.getKey(), updatedSections.getKey());

        List<DbOperation> allOperations = new ArrayList<>();
        allOperations.addAll(updatedPrograms.getValue());
        allOperations.addAll(updatedStages.getValue());
        allOperations.addAll(updatedSections.getValue());
        allOperations.addAll(updatedDataElements.getValue());

        // transacting all changes in one batch
        transactionManager.transact(allOperations);
        lastUpdatedPreferences.save(ResourceType.PROGRAMS, DateType.SERVER, serverTime);
    }

    private KeyValue<List<Program>, List<DbOperation>> updatePrograms(Set<String> uids) {
        List<Program> persistedPrograms = identifiableObjectStore.queryAll();
        List<Program> allExistingPrograms = programApiClient.getPrograms(Fields.BASIC, null, null);

        Map<String, Program> persistedProgramsMap = toMap(persistedPrograms);
        Map<String, Program> allExistingProgramsMap = toMap(allExistingPrograms);

        // defensive copy
        Set<String> modelsToFetch = new HashSet<>(uids);
        Set<String> persistedModels = ModelUtils.toUidSet(persistedPrograms);

        // distinguish persisted models from new
        modelsToFetch.removeAll(persistedModels);

        // iterating over defensive copy
        for (String persistedProgramUid : persistedModels) {
            Program persistedProgram = persistedProgramsMap.get(persistedProgramUid);
            Program recentProgram = allExistingProgramsMap.get(persistedProgramUid);

            // we need to filter out those program uids which are up to date
            if (recentProgram.getVersion() > persistedProgram.getVersion()) {
                modelsToFetch.add(persistedProgramUid);
                logger.d(TAG, String.format(
                        Locale.getDefault(), "Program %s will be updated from version %d to %d",
                        persistedProgramUid, persistedProgram.getVersion(), recentProgram.getVersion()));
            } else {
                logger.d(TAG, String.format(
                        Locale.getDefault(), "Program %s with version %d will be ignored",
                        persistedProgramUid, persistedProgram.getVersion()));
            }
        }

        // most up to date programs with all fields in place
        List<Program> updatedPrograms = programApiClient.getPrograms(
                Fields.DESCENDANTS, null, modelsToFetch);

        // we need to mark assigned programs as "assigned" before storing them
        // TODO remove this call (additional request which performs check if user is assigned)
        Map<String, Program> assignedPrograms = toMap(userApiClient
                .getUserAccount().getPrograms());

        for (Program updatedProgram : updatedPrograms) {
            Program assignedProgram = assignedPrograms.get(updatedProgram.getUId());
            updatedProgram.setIsAssignedToUser(assignedProgram != null);
        }

//        List<Program> mergedPrograms = ModelUtils.merge(
//                allExistingPrograms, updatedPrograms, persistedPrograms);
        List<DbOperation> dbOperations = DbUtils.createOperations(allExistingPrograms,
                updatedPrograms, persistedPrograms, identifiableObjectStore);

        // since we care only about updated programs, we don't need to merge persisted programs
        return new KeyValue<>(updatedPrograms, dbOperations);
    }

    private KeyValue<List<ProgramStage>, List<DbOperation>> updateProgramStages(
            List<Program> programs) {
        List<ProgramStage> programStages = new ArrayList<>();

        if (programs != null && !programs.isEmpty()) {
            for (Program program : programs) {
                programStages.addAll(program.getProgramStages());
            }
        }

        return new KeyValue<>(programStages, programStageController.merge(programStages));
    }

    private KeyValue<List<ProgramStageSection>, List<DbOperation>> updateProgramStageSections(
            List<ProgramStage> programStages) {
        List<ProgramStageSection> programStageSections = new ArrayList<>();

        if (programStages != null && !programStages.isEmpty()) {
            for (ProgramStage programStage : programStages) {
                programStageSections.addAll(programStage.getProgramStageSections());
            }
        }

        return new KeyValue<>(programStageSections,
                programStageSectionController.merge(programStageSections));
    }

    private KeyValue<List<ProgramStageDataElement>, List<DbOperation>> updateProgramStageDataElements(
            List<ProgramStage> programStages, List<ProgramStageSection> programStageSections) {
        return null;
    }
}
