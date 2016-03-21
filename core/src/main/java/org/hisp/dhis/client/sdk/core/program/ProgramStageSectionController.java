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
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramStageSectionController implements IProgramStageSectionController {
    /* Api clients */
    private final ISystemInfoApiClient systemInfoApiClient;
    private final IProgramStageSectionApiClient programStageSectionApiClient;

    /* Local storage */
    private final IProgramStageSectionStore programStageSectionStore;
    private final IProgramStageController programStageController;

    /* Utilities */
    private final ITransactionManager transactionManager;
    private final ILastUpdatedPreferences lastUpdatedPreferences;

    public ProgramStageSectionController(ISystemInfoApiClient systemInfoApiClient,
                                         IProgramStageSectionApiClient programStageSectionApiClient,
                                         IProgramStageSectionStore programStageSectionStore,
                                         IProgramStageController programStageController,
                                         ITransactionManager transactionManager,
                                         ILastUpdatedPreferences lastUpdatedPreferences) {
        this.systemInfoApiClient = systemInfoApiClient;
        this.programStageSectionApiClient = programStageSectionApiClient;
        this.programStageSectionStore = programStageSectionStore;
        this.programStageController = programStageController;
        this.transactionManager = transactionManager;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
    }

    @Override
    public void sync() throws ApiException {
        sync(null);
    }

    @Override
    public void sync(Set<String> uids) throws ApiException {
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.PROGRAM_STAGE_SECTIONS);

        List<ProgramStageSection> persistedProgramStageSections =
                programStageSectionStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramStageSection> allExistingProgramStageSections = programStageSectionApiClient
                .getProgramStageSections(Fields.BASIC, null);

        String[] uidArray = null;
        if (uids != null) {
            // here we want to get list of ids of program stage sections which are
            // stored locally and list of program stage sections which we want to download
            Set<String> persistedProgramStageSectionIds = ModelUtils
                    .toUidSet(persistedProgramStageSections);
            persistedProgramStageSectionIds.addAll(uids);

            uidArray = persistedProgramStageSectionIds
                    .toArray(new String[persistedProgramStageSectionIds.size()]);
        }

        List<ProgramStageSection> updatedProgramStageSections = programStageSectionApiClient
                .getProgramStageSections(Fields.ALL, lastUpdated, uidArray);

        // Retrieving program stage uids from program stages sections
        Set<String> programStageSectionUids = new HashSet<>();
        List<ProgramStageSection> mergedProgramStageSections = ModelUtils.merge(
                allExistingProgramStageSections, updatedProgramStageSections,
                persistedProgramStageSections);
        for (ProgramStageSection programStageSection : mergedProgramStageSections) {
            programStageSectionUids.add(programStageSection.getProgramStage().getUId());
        }

        // Syncing programs before saving program stages (since
        // program stages are referencing them directly)
        programStageController.sync(programStageSectionUids);

        // we will have to perform something similar to what happens in AbsController
        List<IDbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramStageSections, updatedProgramStageSections,
                persistedProgramStageSections, programStageSectionStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_STAGE_SECTIONS, serverTime);
    }
}
