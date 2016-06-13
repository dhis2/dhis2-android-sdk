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
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramStageControllerImpl extends
        AbsSyncStrategyController<ProgramStage> implements ProgramStageController {

    /* Controllers */
    private final ProgramController programController;
    private final SystemInfoController systemInfoController;

    /* Api clients */
    private final ProgramStageApiClient programStageApiClient;

    /* Utilities */
    private final TransactionManager transactionManager;

    public ProgramStageControllerImpl(ProgramController programController,
                                      SystemInfoController systemInfoController,
                                      ProgramStageApiClient programStageApiClient,
                                      ProgramStageStore programStageStore,
                                      TransactionManager transactionManager,
                                      LastUpdatedPreferences lastUpdatedPreferences) {
        super(ResourceType.PROGRAM_STAGES, programStageStore, lastUpdatedPreferences);
        this.programController = programController;
        this.systemInfoController = systemInfoController;
        this.programStageApiClient = programStageApiClient;
        this.transactionManager = transactionManager;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_STAGES, DateType.SERVER);

        List<ProgramStage> persistedProgramStages = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramStage> allExistingProgramStages = programStageApiClient
                .getProgramStages(Fields.BASIC, null, null);

        List<ProgramStage> updatedProgramStages = new ArrayList<>();
        if (uids == null) {
            updatedProgramStages.addAll(programStageApiClient.getProgramStages(
                    Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedProgramStages);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedProgramStages.addAll(programStageApiClient.getProgramStages(
                        Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedProgramStages.addAll(programStageApiClient.getProgramStages(
                        Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

        // Retrieving program uids from program stages
        Set<String> programUids = new HashSet<>();
        List<ProgramStage> mergedProgramStages = ModelUtils.merge(
                allExistingProgramStages, updatedProgramStages, persistedProgramStages);

        for (ProgramStage programStage : mergedProgramStages) {
            programUids.add(programStage.getProgram().getUId());
        }

        // Syncing programs before saving program stages (since
        // program stages are referencing them directly)
        programController.pull(strategy, programUids);

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(allExistingProgramStages,
                updatedProgramStages, persistedProgramStages, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_STAGES, DateType.SERVER, serverTime);
    }

    @Override
    public List<DbOperation> merge(List<ProgramStage> updatedProgramStages) throws ApiException {
        List<ProgramStage> allExistingProgramStages = programStageApiClient
                .getProgramStages(Fields.BASIC, null, null);
        List<ProgramStage> persistedProgramStages = identifiableObjectStore.queryAll();

        return DbUtils.createOperations(allExistingProgramStages,
                updatedProgramStages, persistedProgramStages, identifiableObjectStore);
    }
}
