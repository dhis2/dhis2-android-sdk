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
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementController;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeController;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProgramRuleVariableController extends AbsSyncStrategyController
        <ProgramRuleVariable> implements IProgramRuleVariableController {
    private final IProgramRuleVariableApiClient programRuleVariableApiClient;
    private final ITransactionManager transactionManager;
    private final ISystemInfoController systemInfoController;
    private final IProgramController programController;
    private final IProgramStageController programStageController;
    private final IDataElementController dataElementController;
    private final ITrackedEntityAttributeController trackedEntityAttributeController;

    public ProgramRuleVariableController(IProgramRuleVariableApiClient programRuleVariableApiClient,
                                         ITransactionManager transactionManager,
                                         ILastUpdatedPreferences lastUpdatedPreferences,
                                         ISystemInfoController systemInfoController,
                                         IProgramRuleVariableStore programRuleVariableStore,
                                         IProgramController programController,
                                         IProgramStageController programStageController,
                                         IDataElementController dataElementController,
                                         ITrackedEntityAttributeController trackedEntityAttributeController) {
        super(ResourceType.PROGRAM_RULE_VARIABLES, programRuleVariableStore, lastUpdatedPreferences);
        this.programRuleVariableApiClient = programRuleVariableApiClient;
        this.transactionManager = transactionManager;
        this.systemInfoController = systemInfoController;
        this.programController = programController;
        this.programStageController = programStageController;
        this.dataElementController = dataElementController;
        this.trackedEntityAttributeController = trackedEntityAttributeController;

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
        List<ProgramRuleVariable> allExistingProgramRuleVariables = programRuleVariableApiClient
                .getProgramRuleVariables(Fields.BASIC, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of program stage sections which are
            // stored locally and list of program stage sections which we want to download
            uidSet = ModelUtils.toUidSet(persistedProgramRuleVariables);
            uidSet.addAll(uids);
        }

        List<ProgramRuleVariable> updatedProgramRuleVariables = programRuleVariableApiClient
                .getProgramRuleVariables(Fields.ALL, lastUpdated, uidSet);

        // Retrieving foreign key uids from programRuleVariables
        Set<String> dataElementUids = new HashSet<>();
        Set<String> trackedEntityAttributeUids = new HashSet<>();
        Set<String> programStageUids = new HashSet<>();
        Set<String> programUids = new HashSet<>();
        List<ProgramRuleVariable> programRuleVariables = ModelUtils.merge(
                allExistingProgramRuleVariables, updatedProgramRuleVariables,
                persistedProgramRuleVariables);
        for (ProgramRuleVariable programRuleVariable : programRuleVariables) {
            dataElementUids.add(programRuleVariable.getDataElement().getUId());
            trackedEntityAttributeUids.add(programRuleVariable.getTrackedEntityAttribute().getUId());
            programStageUids.add(programRuleVariable.getProgramStage().getUId());
            programUids.add(programRuleVariable.getProgram().getUId());
        }

        // checking if programs is synced.
        if(!programUids.isEmpty()) {
            programController.pull(strategy, programUids);
        }
        // checking if program stages is synced
        if(!programStageUids.isEmpty()) {
            programStageController.pull(strategy, programStageUids);
        }
        // checking if data elements is synced
        if(!dataElementUids.isEmpty()) {
            dataElementController.pull(strategy, dataElementUids);
        }
        // checking if tracked entity attributes is synced
        // trackedEntityAttributeUids will always be empty if user has access to programs without
        // registration!
        if(!trackedEntityAttributeUids.isEmpty()) {
            trackedEntityAttributeController.pull(strategy, trackedEntityAttributeUids);
        }


        // we will have to perform something similar to what happens in AbsController
        List<IDbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramRuleVariables, updatedProgramRuleVariables,
                persistedProgramRuleVariables, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_RULE_VARIABLES,
                DateType.SERVER, serverTime);
    }
}