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
import org.hisp.dhis.client.sdk.core.dataelement.DataElementController;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetController;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityControllerImpl;
import org.hisp.dhis.client.sdk.core.user.UserApiClient;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.utils.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.core.common.utils.ModelUtils.toMap;
import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ProgramControllerImpl extends
        AbsSyncStrategyController<Program> implements ProgramController {
    private static final String TAG = ProgramController.class.getSimpleName();

    /* Controllers */
    private SystemInfoController systemInfoController;
    private ProgramStageController programStageController;
    private ProgramStageSectionController programStageSectionController;
    private ProgramStageDataElementController programStageDataElementController;
    private ProgramRuleController programRuleController;
    private DataElementController dataElementController;
    private OptionSetController optionSetController;
    private TrackedEntityControllerImpl trackedEntityControllerImpl;
    private ProgramTrackedEntityAttributeControllerImpl programTrackedEntityAttributeController;

    /* Api clients */
    private final ProgramApiClient programApiClient;
    private final UserApiClient userApiClient;

    /* Utilities */
    private final TransactionManager transactionManager;
    private final Logger logger;

    public ProgramControllerImpl(SystemInfoController systemInfoController,
                                 ProgramStore programStore,
                                 UserApiClient userApiClient, ProgramApiClient programApiClient,
                                 LastUpdatedPreferences lastUpdatedPreferences,
                                 TransactionManager transactionManager,
                                 Logger logger) {
        super(ResourceType.PROGRAMS, programStore, lastUpdatedPreferences);

        this.systemInfoController = systemInfoController;

        this.programApiClient = programApiClient;
        this.userApiClient = userApiClient;

        this.transactionManager = transactionManager;
        this.logger = logger;
    }

    @Override
    protected void synchronize(SyncStrategy syncStrategy, Set<String> uids) {
        synchronizeByLastUpdated(uids);
    }

    @Override
    public void pull(SyncStrategy syncStrategy, ProgramFields fields, Set<String> uids) throws ApiException {
        isNull(fields, "ProgramFields must not be null");

        // delegate call to existing implementation
        if (ProgramFields.ALL.equals(fields)) {
            pull(syncStrategy, uids);
            return;
        }

        // we need to sync all models related to program in one query
        if (ProgramFields.DESCENDANTS.equals(fields)) {
            synchronizeProgramsByVersions(uids);
        }
    }
    
    private void synchronizeByLastUpdated(Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.PROGRAMS, DateType.SERVER);

        List<Program> persistedPrograms = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<Program> allExistingPrograms = programApiClient.getPrograms(Fields.BASIC, null, null);

        List<Program> updatedPrograms = new ArrayList<>();
        if (uids == null) {
            updatedPrograms.addAll(programApiClient.getPrograms(
                    Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedPrograms);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedPrograms.addAll(programApiClient.getPrograms(
                        Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedPrograms.addAll(programApiClient.getPrograms(
                        Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

        // we need to mark assigned programs as "assigned" before storing them
        // TODO remove this call (additional request which performs check if user is assigned)
        Map<String, Program> assignedPrograms = toMap(userApiClient
                .getUserAccount().getPrograms());

        for (Program updatedProgram : updatedPrograms) {
            Program assignedProgram = assignedPrograms.get(updatedProgram.getUId());
            updatedProgram.setIsAssignedToUser(assignedProgram != null);
        }

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(allExistingPrograms,
                updatedPrograms, persistedPrograms, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAMS, DateType.SERVER, serverTime);
    }

    private void synchronizeProgramsByVersions(Set<String> uids) throws ApiException {
        isNull(uids, "Set of uids must not be null");

        if (uids.isEmpty()) {
            throw new IllegalArgumentException("Specify at least one uid of program to sync");
        }

        // updating stuff
        KeyValue<List<Program>, List<DbOperation>> updatedPrograms =
                updatePrograms(uids);
        KeyValue<List<ProgramStage>, List<DbOperation>> updatedStages =
                updateProgramStages(updatedPrograms.getKey());
        KeyValue<List<ProgramStageSection>, List<DbOperation>> updatedSections =
                updateProgramStageSections(updatedStages.getKey());
        KeyValue<List<ProgramStageDataElement>, List<DbOperation>> updatedStageDataElements =
                updateProgramStageDataElements(updatedStages.getKey(), updatedSections.getKey());
        KeyValue<List<DataElement>, List<DbOperation>> updatedDataElements =
                updateDataElements(updatedStageDataElements.getKey());
        KeyValue<List<OptionSet>, List<DbOperation>> updatedOptionSets =
                updateOptionSets(updatedDataElements.getKey());
        KeyValue<List<TrackedEntity>, List<DbOperation>> updatedTrackedEntites =
                updateTrackedEntities(updatedPrograms.getKey());
        KeyValue<List<ProgramTrackedEntityAttribute>, List<DbOperation>> updatedProgramTrackedEntityAttributes =
                updateProgramTrackedEntityAttributes(updatedPrograms.getKey());

        // batching program rule updates
        List<DbOperation> updatedProgramRules = programRuleController.pull(
                updatedPrograms.getKey());

        List<DbOperation> allOperations = new ArrayList<>();
        allOperations.addAll(updatedPrograms.getValue());
        allOperations.addAll(updatedStages.getValue());
        allOperations.addAll(updatedSections.getValue());
        allOperations.addAll(updatedStageDataElements.getValue());
        allOperations.addAll(updatedDataElements.getValue());
        allOperations.addAll(updatedOptionSets.getValue());
        allOperations.addAll(updatedTrackedEntites.getValue());
        allOperations.addAll(updatedProgramRules);

        // transacting all changes in one batch
        transactionManager.transact(allOperations);
    }

    private KeyValue<List<Program>, List<DbOperation>> updatePrograms(Set<String> uids) {
        List<Program> persistedPrograms = identifiableObjectStore.queryAll();
        List<Program> allExistingPrograms = programApiClient.getPrograms(Fields.BASIC, null, null);

        Map<String, Program> persistedProgramsMap = toMap(persistedPrograms);
        Map<String, Program> allExistingProgramsMap = toMap(allExistingPrograms);

        // defensive copy
        Set<String> modelsToFetch = new HashSet<>(uids);
        Set<String> persistedModels = ModelUtils.toUidSet(persistedPrograms);

        // distinguish persisted models from new
        modelsToFetch.removeAll(persistedModels);

        // iterating over defensive copy
        for (String persistedProgramUid : persistedModels) {
            Program persistedProgram = persistedProgramsMap.get(persistedProgramUid);
            Program recentProgram = allExistingProgramsMap.get(persistedProgramUid);

            // we need to filter out those program uids which are up to date
            if (recentProgram.getVersion() > persistedProgram.getVersion()) {
                modelsToFetch.add(persistedProgramUid);
                logger.d(TAG, String.format(
                        Locale.getDefault(), "Program %s will be updated from version %d to %d",
                        persistedProgramUid, persistedProgram.getVersion(), recentProgram.getVersion()));
            } else {
                logger.d(TAG, String.format(
                        Locale.getDefault(), "Program %s with version %d will be ignored",
                        persistedProgramUid, persistedProgram.getVersion()));
            }
        }

        List<Program> updatedPrograms;
        if (modelsToFetch.isEmpty()) {
            // most up to date programs with all fields in place
            updatedPrograms = new ArrayList<>();
        } else {
            updatedPrograms = programApiClient.getPrograms(
                    Fields.DESCENDANTS, null, modelsToFetch);
        }

        // we need to mark assigned programs as "assigned" before storing them
        Map<String, Program> assignedPrograms = toMap(userApiClient
                .getUserAccount().getPrograms());

        for (Program updatedProgram : updatedPrograms) {
            Program assignedProgram = assignedPrograms.get(updatedProgram.getUId());
            updatedProgram.setIsAssignedToUser(assignedProgram != null);
        }

        List<DbOperation> dbOperations = DbUtils.createOperations(allExistingPrograms,
                updatedPrograms, persistedPrograms, identifiableObjectStore);

        // since we care only about updated programs, we don't need to merge persisted programs
        return new KeyValue<>(updatedPrograms, dbOperations);
    }

    private KeyValue<List<ProgramStage>, List<DbOperation>> updateProgramStages(
            List<Program> programs) {
        Map<String, ProgramStage> programStageMap = new HashMap<>();

        // List<ProgramStage> programStages = new ArrayList<>();
        if (programs != null && !programs.isEmpty()) {
            for (Program program : programs) {
                if (program.getProgramStages() == null || program.getProgramStages().isEmpty()) {
                    continue;
                }

                for (ProgramStage programStage : program.getProgramStages()) {
                    programStageMap.put(programStage.getUId(), programStage);
                }
            }
        }

        List<ProgramStage> programStageList = new ArrayList<>(programStageMap.values());

        // if there is nothing to be updated, we don't want to call controller
        List<DbOperation> dbOperations;
        if (programStageList.isEmpty()) {
            dbOperations = new ArrayList<>();
        } else {
            dbOperations = programStageController.merge(programStageList);
        }

        return new KeyValue<>(programStageList, dbOperations);
    }

    private KeyValue<List<ProgramStageSection>, List<DbOperation>> updateProgramStageSections(
            List<ProgramStage> programStages) {
        Map<String, ProgramStageSection> programStageSectionsMap = new HashMap<>();

        if (programStages != null && !programStages.isEmpty()) {
            for (ProgramStage programStage : programStages) {
                if (programStage.getProgramStageSections() == null ||
                        programStage.getProgramStageSections().isEmpty()) {
                    continue;
                }

                for (ProgramStageSection stageSection : programStage.getProgramStageSections()) {
                    programStageSectionsMap.put(stageSection.getUId(), stageSection);
                }
            }
        }

        List<ProgramStageSection> programStageSectionsList =
                new ArrayList<>(programStageSectionsMap.values());

        // if there is nothing to be updated, we don't want to call controller
        List<DbOperation> dbOperations;
        if (programStageSectionsList.isEmpty()) {
            dbOperations = new ArrayList<>();
        } else {
            dbOperations = programStageSectionController.merge(programStageSectionsList);
        }

        return new KeyValue<>(programStageSectionsList, dbOperations);
    }

    private KeyValue<List<ProgramStageDataElement>, List<DbOperation>> updateProgramStageDataElements(
            List<ProgramStage> programStages, List<ProgramStageSection> programStageSections) {
        Map<String, ProgramStageDataElement> stageDataElementMap = new HashMap<>();

        if (programStages != null && !programStages.isEmpty()) {
            for (ProgramStage stage : programStages) {
                if (stage.getProgramStageDataElements() == null ||
                        stage.getProgramStageDataElements().isEmpty()) {
                    continue;
                }

                for (ProgramStageDataElement stageDataElement : stage.getProgramStageDataElements()) {
                    stageDataElementMap.put(stageDataElement.getUId(), stageDataElement);
                }
            }
        }

        List<ProgramStageDataElement> stageDataElements =
                new ArrayList<>(stageDataElementMap.values());

        // if there is nothing to be updated, we don't want to call controller
        List<DbOperation> dbOperations;
        if (stageDataElements.isEmpty()) {
            dbOperations = new ArrayList<>();
        } else {
            dbOperations = programStageDataElementController
                    .merge(programStageSections, stageDataElements);
        }

        return new KeyValue<>(stageDataElements, dbOperations);
    }

    private KeyValue<List<DataElement>, List<DbOperation>> updateDataElements(
            List<ProgramStageDataElement> updatedStageDataElements) {
        Map<String, DataElement> dataElementMap = new HashMap<>();

        if (updatedStageDataElements != null && !updatedStageDataElements.isEmpty()) {
            for (ProgramStageDataElement stageDataElement : updatedStageDataElements) {
                dataElementMap.put(stageDataElement.getDataElement().getUId(),
                        stageDataElement.getDataElement());
            }
        }

        List<DataElement> dataElements = new ArrayList<>(dataElementMap.values());

        // if there is nothing to be updated, we don't want to call controller
        List<DbOperation> dbOperations;
        if (dataElements.isEmpty()) {
            dbOperations = new ArrayList<>();
        } else {
            dbOperations = dataElementController.merge(dataElements);
        }

        return new KeyValue<>(dataElements, dbOperations);
    }

    private KeyValue<List<TrackedEntity>, List<DbOperation>> updateTrackedEntities(
            List<Program> updatedProgram) {
        Map<String, TrackedEntity> trackedEntityMap = new HashMap<>();

        if (updatedProgram != null && !updatedProgram.isEmpty()) {
            for(Program program : updatedProgram) {
                if(program.getTrackedEntity() != null) {
                    trackedEntityMap.put(program.getTrackedEntity().getUId(),
                            program.getTrackedEntity());
                }
            }
        }

        List<TrackedEntity> trackedEntities = new ArrayList<>(trackedEntityMap.values());

        // if there is nothing to be updated, we don't want to call controller
        List<DbOperation> dbOperations;
        if (trackedEntities.isEmpty()) {
            dbOperations = new ArrayList<>();
        } else {
            dbOperations = trackedEntityControllerImpl.merge(trackedEntities);
        }

        return new KeyValue<>(trackedEntities, dbOperations);
    }

    private KeyValue<List<OptionSet>, List<DbOperation>> updateOptionSets(
            List<DataElement> dataElements) {
        Map<String, OptionSet> optionSetMap = new HashMap<>();

        if (dataElements != null && !dataElements.isEmpty()) {
            for (DataElement dataElement : dataElements) {
                OptionSet optionSet = dataElement.getOptionSet();

                if (optionSet != null) {
                    // we need to inverse relationship here
                    List<Option> options = optionSet.getOptions();
                    if (options != null && !options.isEmpty()) {
                        for (Option option : options) {
                            option.setOptionSet(optionSet);
                        }
                    }

                    optionSetMap.put(optionSet.getUId(), optionSet);
                }
            }
        }

        List<OptionSet> optionSets = new ArrayList<>(optionSetMap.values());

        // if there is nothing to be updated, we don't want to call controller
        List<DbOperation> dbOperations;
        if (optionSets.isEmpty()) {
            dbOperations = new ArrayList<>();
        } else {
            dbOperations = optionSetController.merge(optionSets);
        }

        return new KeyValue<>(optionSets, dbOperations);
    }

    private KeyValue<List<ProgramTrackedEntityAttribute>, List<DbOperation>> updateProgramTrackedEntityAttributes(
            List<Program> updatedProgram) {
        Map<String, ProgramTrackedEntityAttribute> programTrackedEntityAttributeMap = new HashMap<>();

        if (updatedProgram != null && !updatedProgram.isEmpty()) {
            for(Program program : updatedProgram) {
                if(program.getProgramTrackedEntityAttributes() != null && !program.getProgramTrackedEntityAttributes().isEmpty()) {
                    for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : program.getProgramTrackedEntityAttributes()) {
                        if(programTrackedEntityAttribute != null) {
                            programTrackedEntityAttributeMap.put(programTrackedEntityAttribute.getUId(),
                                    programTrackedEntityAttribute);
                        }
                    }

                }
            }
        }

        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = new ArrayList<>(programTrackedEntityAttributeMap.values());

        // if there is nothing to be updated, we don't want to call controller
        List<DbOperation> dbOperations;
        if (programTrackedEntityAttributes.isEmpty()) {
            dbOperations = new ArrayList<>();
        } else {
            dbOperations = trackedEntityAttributeController.merge(programTrackedEntityAttributes);
        }

        return new KeyValue<>(programTrackedEntityAttributes, dbOperations);
    }



    public void setSystemInfoController(SystemInfoController InfoController) {
        this.systemInfoController = InfoController;
    }

    public void setProgramStageController(ProgramStageController programStageController) {
        this.programStageController = programStageController;
    }

    public void setProgramStageSectionController(ProgramStageSectionController sectionController) {
        this.programStageSectionController = sectionController;
    }

    public void setProgramStageDataElementController(ProgramStageDataElementController elementController) {
        this.programStageDataElementController = elementController;
    }

    public void setDataElementController(DataElementController dataElementController) {
        this.dataElementController = dataElementController;
    }

    public void setOptionSetController(OptionSetController optionSetController) {
        this.optionSetController = optionSetController;
    }

    public void setProgramRuleController(ProgramRuleController programRuleController) {
        this.programRuleController = programRuleController;
    }

    public void setTrackedEntityControllerImpl(TrackedEntityControllerImpl trackedEntityControllerImpl) {
        this.trackedEntityControllerImpl = trackedEntityControllerImpl;
    }

}
