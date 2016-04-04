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
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgramStageDataElementController extends AbsSyncStrategyController
        <ProgramStageDataElement> implements IProgramStageDataElementController {

    /* Controllers */
    private final ISystemInfoController systemInfoController;
    private final IProgramStageController stageController;
    private final IProgramStageSectionController stageSectionController;
    private final IDataElementController dataElementController;

    /* Api clients */
    private final IProgramStageSectionApiClient stageSectionApiClient;
    private final IProgramStageDataElementApiClient stageDataElementApiClient;

    /* Utilities */
    private final ITransactionManager transactionManager;

    public ProgramStageDataElementController(ISystemInfoController systemInfoController,
                                             IProgramStageController stageController,
                                             IProgramStageSectionController stageSectionController,
                                             IDataElementController dataElementController,
                                             IProgramStageSectionApiClient stageSectionApiClient,
                                             IProgramStageDataElementApiClient elementApiClient,
                                             IProgramStageDataElementStore stageDataElementStore,
                                             ITransactionManager transactionManager,
                                             ILastUpdatedPreferences preferences) {
        super(ResourceType.PROGRAM_STAGE_DATA_ELEMENTS, stageDataElementStore, preferences);

        this.systemInfoController = systemInfoController;
        this.stageController = stageController;
        this.stageSectionController = stageSectionController;
        this.dataElementController = dataElementController;
        this.stageSectionApiClient = stageSectionApiClient;
        this.stageDataElementApiClient = elementApiClient;
        this.transactionManager = transactionManager;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType
                .PROGRAM_STAGE_DATA_ELEMENTS, DateType.SERVER);

        List<ProgramStageDataElement> programStageDataElements =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramStageDataElement> allExistingStageDataElements =
                stageDataElementApiClient.getProgramStageDataElements(Fields.BASIC, null, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of program stage data elements which are
            // stored locally and list of program stage data elements which we want to download
            uidSet = ModelUtils
                    .toUidSet(programStageDataElements);
            uidSet.addAll(uids);
        }

        List<ProgramStageDataElement> updatedStageDataElements = stageDataElementApiClient
                .getProgramStageDataElements(Fields.ALL, lastUpdated, uidSet);

        // Inverse relationships from sections to stage data elements
        List<ProgramStageSection> stageSectionToDataElementsRelationships = stageSectionApiClient
                .getProgramStageSections(Fields.ALL, ModelUtils.toUidSet(updatedStageDataElements));
        updatedStageDataElements = inverseSectionToElementRelationships(
                stageSectionToDataElementsRelationships, updatedStageDataElements);

        // Retrieving data element uids from program stage data elements
        Set<String> dataElementUids = new HashSet<>();
        Set<String> programStageUids = new HashSet<>();
        Set<String> programStageSectionUids = new HashSet<>();

        List<ProgramStageDataElement> mergedProgramStageDataElements = ModelUtils.merge(
                allExistingStageDataElements, updatedStageDataElements, programStageDataElements);
        for (ProgramStageDataElement programStageDataElement : mergedProgramStageDataElements) {
            if (programStageDataElement.getProgramStageSection() != null) {
                programStageSectionUids.add(programStageDataElement
                        .getProgramStageSection().getUId());
            }

            programStageUids.add(programStageDataElement.getProgramStage().getUId());
            dataElementUids.add(programStageDataElement.getDataElement().getUId());
        }

        stageController.pullUpdates(strategy, programStageUids);
        dataElementController.pullUpdates(strategy, dataElementUids);
        stageSectionController.pullUpdates(strategy, programStageSectionUids);

        List<IDbOperation> dbOperations = DbUtils.createOperations(allExistingStageDataElements,
                updatedStageDataElements, programStageDataElements, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_STAGE_DATA_ELEMENTS,
                DateType.SERVER, serverTime);
    }

    /* We need to inverse relationships between ProgramStageSection and ProgramStageDataElement */
    private static List<ProgramStageDataElement> inverseSectionToElementRelationships(
            List<ProgramStageSection> sections, List<ProgramStageDataElement> updatedElements) {

        if (sections == null || sections.isEmpty() ||
                updatedElements == null || updatedElements.isEmpty()) {
            return updatedElements;
        }

        Map<String, ProgramStageDataElement> stageDataElementMap =
                ModelUtils.toMap(updatedElements);
        for (ProgramStageSection stageSection : sections) {
            if (stageSection.getProgramStageDataElements() == null ||
                    stageSection.getProgramStageDataElements().isEmpty()) {
                continue;
            }

            for (ProgramStageDataElement element : stageSection.getProgramStageDataElements()) {
                ProgramStageDataElement updatedDataElement =
                        stageDataElementMap.get(element.getUId());
                updatedDataElement.setProgramStageSection(stageSection);
            }
        }

        return updatedElements;
    }
}
