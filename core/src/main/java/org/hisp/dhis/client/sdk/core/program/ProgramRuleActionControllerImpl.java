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
import org.hisp.dhis.client.sdk.core.dataelement.DataElementController;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeController;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProgramRuleActionControllerImpl extends AbsSyncStrategyController
        <ProgramRuleAction> implements ProgramRuleActionController {
    private final ProgramRuleActionApiClient programRuleActionApiClient;
    private final TransactionManager transactionManager;
    private final SystemInfoController systemInfoController;
    private final ProgramStageController programStageController;
    private final ProgramStageSectionController programStageSectionController;
    private final DataElementController dataElementController;
    private final TrackedEntityAttributeController trackedEntityAttributeController;
    private final ProgramRuleController programRuleController;
    private final ProgramIndicatorController programIndicatorController;

    public ProgramRuleActionControllerImpl(ProgramRuleActionApiClient programRuleActionApiClient,
                                           TransactionManager transactionManager,
                                           SystemInfoController systemInfoController,
                                           LastUpdatedPreferences lastUpdatedPreferences,
                                           ProgramRuleActionStore programRuleActionStore,
                                           ProgramStageController programStageController,
                                           ProgramStageSectionController programStageSectionController,
                                           DataElementController dataElementController,
                                           TrackedEntityAttributeController trackedEntityAttributeController,
                                           ProgramRuleController programRuleController,
                                           ProgramIndicatorController programIndicatorController) {
        super(ResourceType.PROGRAM_RULE_ACTIONS, programRuleActionStore, lastUpdatedPreferences);
        this.programRuleActionApiClient = programRuleActionApiClient;
        this.transactionManager = transactionManager;
        this.systemInfoController = systemInfoController;
        this.programStageController = programStageController;
        this.programStageSectionController = programStageSectionController;
        this.dataElementController = dataElementController;
        this.trackedEntityAttributeController = trackedEntityAttributeController;
        this.programRuleController = programRuleController;
        this.programIndicatorController = programIndicatorController;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_RULE_ACTIONS, DateType.SERVER);

        List<ProgramRuleAction> persistedProgramRuleActions =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramRuleAction> allExistingProgramRuleActions = programRuleActionApiClient
                .getProgramRuleActions(Fields.BASIC, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of program stage sections which are
            // stored locally and list of program stage sections which we want to download
            uidSet = ModelUtils.toUidSet(persistedProgramRuleActions);
            uidSet.addAll(uids);
        }

        List<ProgramRuleAction> updatedProgramRuleActions = programRuleActionApiClient
                .getProgramRuleActions(Fields.ALL, lastUpdated, uidSet);

        // Retrieving foreign key uids from programRuleActions
        Set<String> dataElementUids = new HashSet<>();
        Set<String> trackedEntityAttributeUids = new HashSet<>();
        Set<String> programStageUids = new HashSet<>();
        Set<String> programIndicatorUids = new HashSet<>();
        Set<String> programRuleUids = new HashSet<>();
        Set<String> programStageSectionUids = new HashSet<>();
        List<ProgramRuleAction> programRuleActions = ModelUtils.merge(
                allExistingProgramRuleActions, updatedProgramRuleActions,
                persistedProgramRuleActions);
        for (ProgramRuleAction programRuleAction : programRuleActions) {
            if(programRuleAction.getDataElement() != null) {
                dataElementUids.add(programRuleAction.getDataElement().getUId());
            }
            if(programRuleAction.getTrackedEntityAttribute() != null ) {
                trackedEntityAttributeUids.add(programRuleAction.getTrackedEntityAttribute().getUId());
            }
            if(programRuleAction.getProgramStage() != null) {
                programStageUids.add(programRuleAction.getProgramStage().getUId());
            }
            if(programRuleAction.getProgramIndicator() != null) {
                programIndicatorUids.add(programRuleAction.getProgramIndicator().getUId());
            }
            if(programRuleAction.getProgramRule() != null) {
                programRuleUids.add(programRuleAction.getProgramRule().getUId());
            }
            if(programRuleAction.getProgramStageSection() != null) {
                programStageSectionUids.add(programRuleAction.getProgramStageSection().getUId());
            }
        }


        // checking if program stages is synced
        if(!programStageUids.isEmpty()) {
            programStageController.pull(strategy, programStageUids);
        }
        // checking if program stage sections is synced
        if(!programStageSectionUids.isEmpty()) {
            programStageSectionController.pull(strategy, programStageSectionUids);
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
        // checking if program rules is synced
        if(!programRuleUids.isEmpty()) {
            programRuleController.pull(strategy, programRuleUids);
        }
        // checking if program indicators is synced
        if(!programIndicatorUids.isEmpty()) {
            programIndicatorController.pull(strategy, programIndicatorUids);
        }


        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramRuleActions, updatedProgramRuleActions,
                persistedProgramRuleActions, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_RULE_ACTIONS,
                DateType.SERVER, serverTime);
    }
}