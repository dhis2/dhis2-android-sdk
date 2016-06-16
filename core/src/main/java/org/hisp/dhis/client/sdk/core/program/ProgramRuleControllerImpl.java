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
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProgramRuleControllerImpl
        extends AbsSyncStrategyController<ProgramRule> implements ProgramRuleController {
    private final TransactionManager transactionManager;
    private final SystemInfoController systemInfoController;
    private final ProgramRuleApiClient programRuleApiClient;
    private final ProgramController programController;
    private final ProgramStageController programStageController;
    private ProgramRuleActionController programRuleActionController;
    private ProgramRuleVariableController programRuleVariableController;

    public ProgramRuleControllerImpl(SystemInfoController systemInfoController,
                                     ProgramController programController,
                                     ProgramStageController programStageController,
                                     ProgramRuleApiClient programRuleApiClient,
                                     ProgramRuleStore programRuleStore,
                                     LastUpdatedPreferences lastUpdatedPreferences,
                                     TransactionManager transactionManager) {
        super(ResourceType.PROGRAM_RULES, programRuleStore, lastUpdatedPreferences);
        this.transactionManager = transactionManager;
        this.systemInfoController = systemInfoController;
        this.programRuleApiClient = programRuleApiClient;
        this.programController = programController;
        this.programStageController = programStageController;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        synchronizeByLastUpdated(strategy, uids);
    }

    @Override
    public void pull(SyncStrategy strategy, ProgramFields fields,
                     List<Program> programList) throws ApiException {
        if (ProgramFields.ALL.equals(fields)) {
            List<ProgramRule> programRulesAssignedToPrograms = programRuleApiClient
                    .getProgramRulesByPrograms(Fields.BASIC, null, programList);
            Set<String> programRuleUids = ModelUtils.toUidSet(programRulesAssignedToPrograms);

            // delegate syncing to another pull method
            pull(strategy, programRuleUids);
        } else if (ProgramFields.DESCENDANTS.equals(fields)) {
            List<DbOperation> dbOperations = pull(programList);
            transactionManager.transact(dbOperations);
        }
    }

    @Override
    public List<DbOperation> pull(List<Program> programs) throws ApiException {
        KeyValue<List<ProgramRule>, List<DbOperation>> updatedRules =
                updateProgramRules(programs);
        KeyValue<List<ProgramRuleAction>, List<DbOperation>> updatedRuleActions =
                updateRuleActions(updatedRules.getKey());
        List<DbOperation> updatedRuleVariables =
                programRuleVariableController.pull(programs);

        List<DbOperation> dbOperations = new ArrayList<>();
        dbOperations.addAll(updatedRules.getValue());
        dbOperations.addAll(updatedRuleActions.getValue());
        dbOperations.addAll(updatedRuleVariables);

        return dbOperations;
    }

    private KeyValue<List<ProgramRule>, List<DbOperation>> updateProgramRules(
            List<Program> programs) {
        List<ProgramRule> persistedProgramRules = identifiableObjectStore.queryAll();
        List<ProgramRule> allExistingProgramRules = programRuleApiClient
                .getProgramRules(Fields.BASIC, null);

        List<ProgramRule> updatedProgramRules = programRuleApiClient
                .getProgramRulesByPrograms(Fields.DESCENDANTS, null, programs);

        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramRules, updatedProgramRules,
                persistedProgramRules, identifiableObjectStore);

        return new KeyValue<>(updatedProgramRules, dbOperations);
    }

    private KeyValue<List<ProgramRuleAction>, List<DbOperation>> updateRuleActions(
            List<ProgramRule> programRules) {
        Map<String, ProgramRuleAction> actionMap = new HashMap<>();

        if (programRules != null && !programRules.isEmpty()) {
            for (ProgramRule programRule : programRules) {
                if (programRule.getProgramRuleActions() == null ||
                        programRule.getProgramRuleActions().isEmpty()) {
                    continue;
                }

                for (ProgramRuleAction programRuleAction : programRule.getProgramRuleActions()) {
                    actionMap.put(programRuleAction.getUId(), programRuleAction);
                }
            }
        }

        List<ProgramRuleAction> programRuleActions = new ArrayList<>(actionMap.values());
        return new KeyValue<>(programRuleActions,
                programRuleActionController.merge(programRuleActions));
    }

    private void synchronizeByLastUpdated(
            SyncStrategy strategy, Set<String> uids) throws ApiException {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_RULES, DateType.SERVER);

        List<ProgramRule> persistedProgramRules = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramRule> allExistingProgramRules = programRuleApiClient
                .getProgramRules(Fields.BASIC, null);

        List<ProgramRule> updatedProgramRules = new ArrayList<>();
        if (uids == null) {
            updatedProgramRules.addAll(programRuleApiClient.getProgramRules(
                    Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedProgramRules);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedProgramRules.addAll(programRuleApiClient.getProgramRules(
                        Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedProgramRules.addAll(programRuleApiClient.getProgramRules(
                        Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

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

    public void setProgramRuleActionController(ProgramRuleActionController programRuleActionController) {
        this.programRuleActionController = programRuleActionController;
    }

    public void setProgramRuleVariableController(ProgramRuleVariableController programRuleVariableController) {
        this.programRuleVariableController = programRuleVariableController;
    }
}