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
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramIndicatorControllerImpl
        extends AbsSyncStrategyController<ProgramIndicator> implements ProgramIndicatorController {

    private final SystemInfoController systemInfoController;
    private final ProgramIndicatorApiClient programindicatorApiClient;
    private final TransactionManager transactionManager;
    private final ProgramController programController;
    private final ProgramStageController programStageController;
    private final ProgramStageSectionController programStageSectionController;

    public ProgramIndicatorControllerImpl(SystemInfoController systemInfoController,
                                          ProgramController programController,
                                          ProgramStageController programStageController,
                                          ProgramStageSectionController programStageSectionController,
                                          ProgramIndicatorApiClient programIndicatorApiClient,
                                          LastUpdatedPreferences lastUpdatedPreferences,
                                          TransactionManager transactionManager,
                                          ProgramIndicatorStore programIndicatorStore) {
        super(ResourceType.PROGRAM_INDICATORS, programIndicatorStore, lastUpdatedPreferences);
        this.systemInfoController = systemInfoController;
        this.programindicatorApiClient = programIndicatorApiClient;
        this.transactionManager = transactionManager;
        this.programController = programController;
        this.programStageController = programStageController;
        this.programStageSectionController = programStageSectionController;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_INDICATORS, DateType.SERVER);

        List<ProgramIndicator> persistedProgramIndicators = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramIndicator> allExistingProgramIndicators = programindicatorApiClient
                .getProgramIndicators(Fields.BASIC, null);

        List<ProgramIndicator> updatedProgramIndicators = new ArrayList<>();
        if (uids == null) {
            updatedProgramIndicators.addAll(programindicatorApiClient
                    .getProgramIndicators(Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedProgramIndicators);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedProgramIndicators.addAll(programindicatorApiClient
                        .getProgramIndicators(Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedProgramIndicators.addAll(programindicatorApiClient
                        .getProgramIndicators(Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

        // Retrieving foreign key uids from programIndicators
        Set<String> programUids = new HashSet<>();
        Set<String> programStageUids = new HashSet<>();
        Set<String> programStageSectionUids = new HashSet<>();

        List<ProgramIndicator> programIndicators = ModelUtils.merge(
                allExistingProgramIndicators, updatedProgramIndicators,
                persistedProgramIndicators);

        for (ProgramIndicator programIndicator : programIndicators) {
            if (programIndicator.getProgram() != null) {
                programUids.add(programIndicator.getProgram().getUId());
            }

            if (programIndicator.getProgramStage() != null) {
                programStageUids.add(programIndicator.getProgramStage().getUId());
            }

            if (programIndicator.getProgramStageSection() != null) {
                programStageSectionUids.add(programIndicator.getProgramStageSection().getUId());
            }

        }

        // checking if progams is synced
        if (!programUids.isEmpty()) {
            programController.pull(strategy, programUids);
        }
        // checking if program stages is synced
        if (!programStageUids.isEmpty()) {
            programStageController.pull(strategy, programStageUids);
        }
        // checking if program stage sections is synced
        if (!programStageSectionUids.isEmpty()) {
            programStageSectionController.pull(strategy, programStageSectionUids);
        }
        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramIndicators, updatedProgramIndicators,
                persistedProgramIndicators, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_INDICATORS,
                DateType.SERVER, serverTime);
    }
}
