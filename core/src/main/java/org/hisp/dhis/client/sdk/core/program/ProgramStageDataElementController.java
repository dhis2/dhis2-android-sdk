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
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramStageDataElementController extends AbsSyncStrategyController
        <ProgramStageDataElement> implements IProgramStageDataElementController {

    /* Api clients */
    private final ISystemInfoApiClient systemInfoApiClient;
    private final IProgramStageDataElementApiClient programStageDataElementApiClient;

    /* Local storage */

    private final IDataElementController dataElementController;

    /* Utilities */
    private final ITransactionManager transactionManager;

    public ProgramStageDataElementController(ISystemInfoApiClient systemInfoApiClient,
                                             IProgramStageDataElementApiClient programStageDataElementApiClient,
                                             IProgramStageDataElementStore programStageDataElementStore,
                                             IDataElementController dataElementController,
                                             ITransactionManager transactionManager,
                                             ILastUpdatedPreferences lastUpdatedPreferences) {
        super(ResourceType.PROGRAM_STAGE_DATA_ELEMENTS,
                programStageDataElementStore, lastUpdatedPreferences);
        this.systemInfoApiClient = systemInfoApiClient;
        this.programStageDataElementApiClient = programStageDataElementApiClient;
        this.dataElementController = dataElementController;
        this.transactionManager = transactionManager;
    }


    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.PROGRAM_STAGE_DATA_ELEMENTS, DateType.SERVER);

        List<ProgramStageDataElement> programStageDataElements =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramStageDataElement> allExistingProgramStageDataElements = programStageDataElementApiClient
                .getProgramStageDataElements(Fields.BASIC, null);

        String[] uidArray = null;
        if (uids != null) {
            // here we want to get list of ids of program stage data elements which are
            // stored locally and list of program stage data elements which we want to download
            Set<String> persistedProgramStageDataElementIds = ModelUtils
                    .toUidSet(programStageDataElements);
            persistedProgramStageDataElementIds.addAll(uids);

            uidArray = persistedProgramStageDataElementIds
                    .toArray(new String[persistedProgramStageDataElementIds.size()]);
        }

        List<ProgramStageDataElement> updatedProgramStageDataElements = programStageDataElementApiClient
                .getProgramStageDataElements(Fields.ALL, lastUpdated, uidArray);

        // Retrieving data element uids from program stage data elements
        Set<String> dataElementUids = new HashSet<>();
        List<ProgramStageDataElement> mergedProgramStageDataElements = ModelUtils.merge(
                allExistingProgramStageDataElements, updatedProgramStageDataElements,
                programStageDataElements);
        for (ProgramStageDataElement programStageDataElement : mergedProgramStageDataElements) {
            dataElementUids.add(programStageDataElement.getDataElement().getUId());
        }

        // Syncing data elements before saving program stage data elements(since
        // program stage data elements are referencing them directly)
        dataElementController.sync(strategy, dataElementUids);

        // we will have to perform something similar to what happens in AbsController
        System.out.println("allExistingPSDE: " + allExistingProgramStageDataElements);
        System.out.println("updatedPSDE: " + updatedProgramStageDataElements);
        System.out.println("PSDE in store: " + programStageDataElements);

        List<IDbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramStageDataElements, updatedProgramStageDataElements,
                programStageDataElements, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_STAGE_DATA_ELEMENTS,
                DateType.SERVER, serverTime);
    }
}
