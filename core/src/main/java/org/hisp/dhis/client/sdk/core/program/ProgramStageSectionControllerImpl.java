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
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramStageSectionControllerImpl extends AbsSyncStrategyController<ProgramStageSection>
        implements ProgramStageSectionController {

    /* Controllers */
    private final SystemInfoController systemInfoController;
    private final ProgramStageController programStageController;

    /* Api clients */
    private final ProgramStageSectionApiClient programStageSectionApiClient;

    /* Utilities */
    private final TransactionManager transactionManager;

    public ProgramStageSectionControllerImpl(ProgramStageController programStageController,
                                             SystemInfoController systemInfoController,
                                             ProgramStageSectionApiClient programStageSectionApiClient,
                                             ProgramStageSectionStore sectionStore,
                                             TransactionManager transactionManager,
                                             LastUpdatedPreferences lastUpdatedPreferences) {
        super(ResourceType.PROGRAM_STAGE_SECTIONS, sectionStore, lastUpdatedPreferences);

        this.programStageSectionApiClient = programStageSectionApiClient;
        this.systemInfoController = systemInfoController;
        this.programStageController = programStageController;
        this.transactionManager = transactionManager;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_STAGE_SECTIONS, DateType.SERVER);

        List<ProgramStageSection> persistedProgramStageSections =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramStageSection> allExistingProgramStageSections = programStageSectionApiClient
                .getProgramStageSections(Fields.BASIC, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of program stage sections which are
            // stored locally and list of program stage sections which we want to download
            uidSet = ModelUtils.toUidSet(persistedProgramStageSections);
            uidSet.addAll(uids);
        }

        List<ProgramStageSection> updatedProgramStageSections = programStageSectionApiClient
                .getProgramStageSections(Fields.ALL, lastUpdated, uidSet);

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
        programStageController.pull(strategy, programStageSectionUids);

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramStageSections, updatedProgramStageSections,
                persistedProgramStageSections, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_STAGE_SECTIONS,
                DateType.SERVER, serverTime);
    }
}
