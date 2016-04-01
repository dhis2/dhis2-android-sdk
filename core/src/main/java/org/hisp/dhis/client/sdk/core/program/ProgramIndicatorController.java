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
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramIndicatorController extends AbsSyncStrategyController
        <ProgramIndicator> implements IProgramIndicatorController {
    private final ISystemInfoController systemInfoController;
    private final IProgramIndicatorApiClient programindicatorApiClient;
    private final ITransactionManager transactionManager;
    private final IProgramController programController;
    private final IProgramStageController programStageController;
    private final IProgramStageSectionController programStageSectionController;

    public ProgramIndicatorController(IProgramIndicatorStore programIndicatorStore,
                                      ILastUpdatedPreferences lastUpdatedPreferences,
                                      ISystemInfoController systemInfoController,
                                      IProgramIndicatorApiClient programIndicatorApiClient,
                                      ITransactionManager transactionManager,
                                      IProgramController programController,
                                      IProgramStageController programStageController,
                                      IProgramStageSectionController programStageSectionController) {
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

        List<ProgramIndicator> persistedProgramIndicators =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramIndicator> allExistingProgramIndicators = programindicatorApiClient
                .getProgramIndicators(Fields.BASIC, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of program indicators which are
            // stored locally and list of program indicators which we want to download
            uidSet = ModelUtils.toUidSet(persistedProgramIndicators);
            uidSet.addAll(uids);
        }

        List<ProgramIndicator> updatedProgramIndicators = programindicatorApiClient
                .getProgramIndicators(Fields.ALL, lastUpdated, uidSet);

        // Retrieving foreign key uids from programIndicators
        Set<String> programUids = new HashSet<>();
        Set<String> programStageUids = new HashSet<>();
        Set<String> programStageSectionUids = new HashSet<>();

        List<ProgramIndicator> programIndicators = ModelUtils.merge(
                allExistingProgramIndicators, updatedProgramIndicators,
                persistedProgramIndicators);
        for (ProgramIndicator programIndicator : programIndicators) {
            programUids.add(programIndicator.getProgram().getUId());
            programStageUids.add(programIndicator.getProgramStage().getUId());
            programStageSectionUids.add(programIndicator.getProgramStageSection().getUId());

        }

        // checking if progams is synced
        if(!programUids.isEmpty()) {
            programController.sync(strategy, programUids);
        }
        // checking if program stages is synced
        if(!programStageUids.isEmpty()) {
            programStageController.sync(strategy, programStageUids);
        }
        // checking if program stage sections is synced
        if(!programStageSectionUids.isEmpty()) {
            programStageSectionController.sync(strategy, programStageSectionUids);
        }
        // we will have to perform something similar to what happens in AbsController
        List<IDbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramIndicators, updatedProgramIndicators,
                persistedProgramIndicators, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_INDICATORS,
                DateType.SERVER, serverTime);
    }
}
