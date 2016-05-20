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
import org.hisp.dhis.client.sdk.core.dataelement.DataElementController;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeController;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProgramRuleVariableControllerImpl
        extends AbsSyncStrategyController<ProgramRuleVariable>
        implements ProgramRuleVariableController {

    private final ProgramRuleVariableApiClient programRuleVariableApiClient;
    private final TransactionManager transactionManager;
    private final SystemInfoController systemInfoController;
    private final ProgramController programController;
    private final ProgramStageController programStageController;
    private final DataElementController dataElementController;
    private final TrackedEntityAttributeController trackedEntityAttributeController;

    public ProgramRuleVariableControllerImpl(ProgramRuleVariableApiClient variableApiClient,
                                             TransactionManager transactionManager,
                                             LastUpdatedPreferences lastUpdatedPreferences,
                                             SystemInfoController systemInfoController,
                                             ProgramRuleVariableStore programRuleVariableStore,
                                             ProgramController programController,
                                             ProgramStageController programStageController,
                                             DataElementController dataElementController,
                                             TrackedEntityAttributeController attributeController) {
        super(ResourceType.PROGRAM_RULE_VARIABLES, programRuleVariableStore, lastUpdatedPreferences);

        this.programRuleVariableApiClient = variableApiClient;
        this.transactionManager = transactionManager;
        this.systemInfoController = systemInfoController;
        this.programController = programController;
        this.programStageController = programStageController;
        this.dataElementController = dataElementController;
        this.trackedEntityAttributeController = attributeController;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_RULE_VARIABLES, DateType.SERVER);

        List<ProgramRuleVariable> persistedProgramRuleVariables =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramRuleVariable> allExistingProgramRuleVariables =
                programRuleVariableApiClient.getProgramRuleVariables(Fields.BASIC, null);


        List<ProgramRuleVariable> updatedProgramRuleVariables = new ArrayList<>();
        if (uids == null) {
            updatedProgramRuleVariables.addAll(programRuleVariableApiClient
                    .getProgramRuleVariables(Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedProgramRuleVariables);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedProgramRuleVariables.addAll(programRuleVariableApiClient
                        .getProgramRuleVariables(Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedProgramRuleVariables.addAll(programRuleVariableApiClient
                        .getProgramRuleVariables(Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

        // Retrieving foreign key uids from programRuleVariables
        Set<String> dataElementUids = new HashSet<>();
        Set<String> trackedEntityAttributeUids = new HashSet<>();
        Set<String> programStageUids = new HashSet<>();
        Set<String> programUids = new HashSet<>();

        List<ProgramRuleVariable> programRuleVariables = ModelUtils.merge(
                allExistingProgramRuleVariables, updatedProgramRuleVariables,
                persistedProgramRuleVariables);

        for (ProgramRuleVariable programRuleVariable : programRuleVariables) {
            if (programRuleVariable.getDataElement() != null) {
                dataElementUids.add(programRuleVariable.getDataElement().getUId());
            }

            if (programRuleVariable.getTrackedEntityAttribute() != null) {
                trackedEntityAttributeUids.add(programRuleVariable
                        .getTrackedEntityAttribute().getUId());
            }

            if (programRuleVariable.getProgramStage() != null) {
                programStageUids.add(programRuleVariable.getProgramStage().getUId());
            }

            if (programRuleVariable.getProgram() != null) {
                programUids.add(programRuleVariable.getProgram().getUId());
            }
        }

        // checking if programs is synced.
        if (!programUids.isEmpty()) {
            programController.pull(strategy, programUids);
        }

        // checking if program stages is synced
        if (!programStageUids.isEmpty()) {
            programStageController.pull(strategy, programStageUids);
        }

        // checking if data elements is synced
        if (!dataElementUids.isEmpty()) {
            dataElementController.pull(strategy, dataElementUids);
        }

        // checking if tracked entity attributes is synced trackedEntityAttributeUids will
        // always be empty if user has access to programs without registration!
        if (!trackedEntityAttributeUids.isEmpty()) {
            trackedEntityAttributeController.pull(strategy, trackedEntityAttributeUids);
        }

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramRuleVariables, updatedProgramRuleVariables,
                persistedProgramRuleVariables, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_RULE_VARIABLES,
                DateType.SERVER, serverTime);
    }

    @Override
    public void pull(SyncStrategy strategy, List<Program> programList) {
        List<ProgramRuleVariable> variablesAssignedToPrograms = programRuleVariableApiClient
                .getProgramRuleVariablesByPrograms(Fields.BASIC, null, programList);
        Set<String> variableUids = ModelUtils.toUidSet(variablesAssignedToPrograms);

        // delegate syncing to another pull method
        pull(strategy, variableUids);
    }
}