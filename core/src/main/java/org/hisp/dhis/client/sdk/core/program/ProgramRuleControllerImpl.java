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
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProgramRuleControllerImpl
        extends AbsSyncStrategyController<ProgramRule> implements ProgramRuleController {
    private final TransactionManager transactionManager;
    private final SystemInfoController systemInfoController;
    private final ProgramRuleApiClient programRuleApiClient;
    private final ProgramController programController;
    private final ProgramStageController programStageController;

    public ProgramRuleControllerImpl(TransactionManager transactionManager,
                                     LastUpdatedPreferences lastUpdatedPreferences,
                                     ProgramRuleStore programRuleStore,
                                     SystemInfoController systemInfoController,
                                     ProgramRuleApiClient programRuleApiClient,
                                     ProgramController programController,
                                     ProgramStageController programStageController) {
        super(ResourceType.PROGRAM_RULES, programRuleStore, lastUpdatedPreferences);
        this.transactionManager = transactionManager;
        this.systemInfoController = systemInfoController;
        this.programRuleApiClient = programRuleApiClient;
        this.programController = programController;
        this.programStageController = programStageController;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_RULES, DateType.SERVER);

        List<ProgramRule> persistedProgramRules =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramRule> allExistingProgramRules = programRuleApiClient
                .getProgramRules(Fields.BASIC, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of program stage sections which are
            // stored locally and list of program stage sections which we want to download
            uidSet = ModelUtils.toUidSet(persistedProgramRules);
            uidSet.addAll(uids);
        }

        List<ProgramRule> updatedProgramRules = programRuleApiClient
                .getProgramRules(Fields.ALL, lastUpdated, uidSet);

        // Retrieving foreign key uids from programRules
        Set<String> programStageUids = new HashSet<>();
        Set<String> programUids = new HashSet<>();

        List<ProgramRule> programRules = ModelUtils.merge(
                allExistingProgramRules, updatedProgramRules, persistedProgramRules);
        for (ProgramRule programRule : programRules) {
            if (programRule.getProgramStage() != null) {
                programStageUids.add(programRule.getProgramStage().getUId());
            }
            if (programRule.getProgram() != null) {
                programUids.add(programRule.getProgram().getUId());
            }
        }

        // checking if programs are synced
        if (!programUids.isEmpty()) {
            programController.pull(strategy, programUids);
        }
        // checking if program stages is synced
        if (!programStageUids.isEmpty()) {
            programStageController.pull(strategy, programStageUids);
        }

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramRules, updatedProgramRules,
                persistedProgramRules, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_RULES,
                DateType.SERVER, serverTime);
    }

    @Override
    public void pull(SyncStrategy strategy, List<Program> programList) {
        List<ProgramRule> programRulesAssignedToPrograms = programRuleApiClient
                .getProgramRules(Fields.BASIC, null, programList);
        Set<String> programRuleUids = ModelUtils.toUidSet(programRulesAssignedToPrograms);

        // delegate syncing to another pull method
        pull(strategy, programRuleUids);
    }
}