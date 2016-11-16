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
import org.hisp.dhis.client.sdk.core.dataelement.DataElementController;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgramStageDataElementControllerImpl
        extends AbsSyncStrategyController<ProgramStageDataElement> implements ProgramStageDataElementController {

    /* Controllers */
    private final SystemInfoController systemInfoController;
    private final ProgramStageController stageController;
    private final ProgramStageSectionController stageSectionController;
    private final DataElementController dataElementController;

    /* Api clients */
    private final ProgramStageSectionApiClient stageSectionApiClient;
    private final ProgramStageDataElementApiClient stageDataElementApiClient;

    /* Utilities */
    private final TransactionManager transactionManager;

    public ProgramStageDataElementControllerImpl(SystemInfoController systemInfoController,
                                                 ProgramStageController stageController,
                                                 ProgramStageSectionController stageSectionController,
                                                 DataElementController dataElementController,
                                                 ProgramStageSectionApiClient stageSectionApiClient,
                                                 ProgramStageDataElementApiClient elementApiClient,
                                                 ProgramStageDataElementStore stageDataElementStore,
                                                 TransactionManager transactionManager,
                                                 LastUpdatedPreferences preferences) {
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
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_STAGE_DATA_ELEMENTS,
                DateType.SERVER);

        List<ProgramStageDataElement> programStageDataElements = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramStageDataElement> allExistingStageDataElements =
                stageDataElementApiClient.getProgramStageDataElements(Fields.BASIC, null, null);

        List<ProgramStageDataElement> updatedStageDataElements = new ArrayList<>();
        if (uids == null) {
            updatedStageDataElements.addAll(stageDataElementApiClient
                    .getProgramStageDataElements(Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(programStageDataElements);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedStageDataElements.addAll(stageDataElementApiClient
                        .getProgramStageDataElements(Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedStageDataElements.addAll(stageDataElementApiClient
                        .getProgramStageDataElements(Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

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
                programStageSectionUids.add(
                        programStageDataElement.getProgramStageSection().getUId());
            }

            if (programStageDataElement.getProgramStage() != null &&
                    programStageDataElement.getDataElement() != null) {
                programStageUids.add(programStageDataElement.getProgramStage().getUId());
                dataElementUids.add(programStageDataElement.getDataElement().getUId());
            }
        }

        stageController.pull(strategy, programStageUids);
        dataElementController.pull(strategy, dataElementUids);
        stageSectionController.pull(strategy, programStageSectionUids);

        List<DbOperation> dbOperations = DbUtils.createOperations(allExistingStageDataElements,
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
                updatedDataElement.setSortOrderWithinProgramStageSection(element.getSortOrderWithinProgramStageSection());
            }
        }

        return updatedElements;
    }

    @Override
    public List<DbOperation> merge(List<ProgramStageSection> sections,
                                   List<ProgramStageDataElement> elements) throws ApiException {

        // inverse program stage section relationship
        elements = inverseSectionToElementRelationships(sections, elements);

        List<ProgramStageDataElement> allExistingStageDataElements =
                stageDataElementApiClient.getProgramStageDataElements(Fields.BASIC, null, null);
        List<ProgramStageDataElement> programStageDataElements = identifiableObjectStore.queryAll();


        return DbUtils.createOperations(allExistingStageDataElements,
                elements, programStageDataElements, identifiableObjectStore);
    }
}
