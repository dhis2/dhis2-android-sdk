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
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramStageController implements IProgramStageController {

    /* Api clients */
    private final ISystemInfoApiClient systemInfoApiClient;
    private final IProgramStageApiClient programStageApiClient;

    /* Local storage */
    private final IProgramStageStore programStageStore;

    /* Controllers */
    private final IProgramController programController;

    /* Utilities */
    private final ITransactionManager transactionManager;
    private final ILastUpdatedPreferences lastUpdatedPreferences;

    public ProgramStageController(ISystemInfoApiClient systemInfoApiClient,
                                  IProgramStageApiClient programStageApiClient,
                                  IProgramStageStore programStageStore,
                                  IProgramController programController,
                                  ITransactionManager transactionManager,
                                  ILastUpdatedPreferences lastUpdatedPreferences) {
        this.systemInfoApiClient = systemInfoApiClient;
        this.programStageApiClient = programStageApiClient;
        this.programStageStore = programStageStore;
        this.programController = programController;
        this.transactionManager = transactionManager;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
    }

    @Override
    public void sync(SyncStrategy syncStrategy) throws ApiException {
        sync(syncStrategy, null);
    }

    @Override
    public void sync(SyncStrategy syncStrategy, Set<String> uids) throws ApiException {
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_STAGES, DateType.SERVER);

        List<ProgramStage> persistedProgramStages = programStageStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramStage> allExistingProgramStages = programStageApiClient
                .getProgramStages(Fields.BASIC, null);

        String[] uidArray = null;
        if (uids != null) {
            // here we want to get list of ids of program stages which are
            // stored locally and list of program stages which we want to download
            Set<String> persistedProgramStageIds = ModelUtils.toUidSet(persistedProgramStages);
            persistedProgramStageIds.addAll(uids);

            uidArray = persistedProgramStageIds
                    .toArray(new String[persistedProgramStageIds.size()]);
        }

        List<ProgramStage> updatedProgramStages = programStageApiClient.getProgramStages(
                Fields.ALL, lastUpdated, uidArray);

        // Retrieving program uids from program stages
        Set<String> programUids = new HashSet<>();
        List<ProgramStage> mergedProgramStages = ModelUtils.merge(
                allExistingProgramStages, updatedProgramStages, persistedProgramStages);
        for (ProgramStage programStage : mergedProgramStages) {
            programUids.add(programStage.getProgram().getUId());
        }

        // Syncing programs before saving program stages (since
        // program stages are referencing them directly)
        programController.sync(syncStrategy, programUids);

        // we will have to perform something similar to what happens in AbsController
        List<IDbOperation> dbOperations = DbUtils.createOperations(allExistingProgramStages,
                updatedProgramStages, persistedProgramStages, programStageStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_STAGES, DateType.SERVER, serverTime);
    }
}
