package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.controllers.AbsSyncStrategyController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeController;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProgramTrackedEntityAttributeControllerImpl extends
        AbsSyncStrategyController<ProgramTrackedEntityAttribute> implements ProgramTrackedEntityAttributeController {
    private final ProgramTrackedEntityAttributeApiClient programTrackedEntityAttributeApiClient;
    private final SystemInfoController systemInfoController;
    private final TrackedEntityAttributeController trackedEntityAttributeController;
    private final TransactionManager transactionManager;
    private final ProgramController programController;
    public ProgramTrackedEntityAttributeControllerImpl(IdentifiableObjectStore<ProgramTrackedEntityAttribute> identifiableObjectStore,
                                                       LastUpdatedPreferences lastUpdatedPreferences,
                                                       SystemInfoController systemInfoController,
                                                       ProgramTrackedEntityAttributeApiClient programTrackedEntityAttributeApiClient,
                                                       TrackedEntityAttributeController trackedEntityAttributeController,
                                                       TransactionManager transactionManager,
                                                       ProgramController programController) {
        super(ResourceType.PROGRAM_TRACKED_ENTITY_ATTRIBUTES, identifiableObjectStore, lastUpdatedPreferences);
        this.programTrackedEntityAttributeApiClient = programTrackedEntityAttributeApiClient;
        this.systemInfoController = systemInfoController;
        this.trackedEntityAttributeController = trackedEntityAttributeController;
        this.transactionManager = transactionManager;
        this.programController = programController;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.PROGRAM_TRACKED_ENTITY_ATTRIBUTES, DateType.SERVER);

        List<ProgramTrackedEntityAttribute> persistedProgramTrackedEntityAttributes =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<ProgramTrackedEntityAttribute> allExistingProgramTrackedEntityAttributes = programTrackedEntityAttributeApiClient
                .getProgramTrackedEntityAttributes(Fields.BASIC, null);

        List<ProgramTrackedEntityAttribute> updatedProgramTrackedEntityAttributes = new ArrayList<>();
        if (uids == null) {
            updatedProgramTrackedEntityAttributes.addAll(programTrackedEntityAttributeApiClient
                    .getProgramTrackedEntityAttributes(Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedProgramTrackedEntityAttributes);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedProgramTrackedEntityAttributes.addAll(programTrackedEntityAttributeApiClient
                        .getProgramTrackedEntityAttributes(Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedProgramTrackedEntityAttributes.addAll(programTrackedEntityAttributeApiClient
                        .getProgramTrackedEntityAttributes(Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

        // Retrieving tracked entity attribute uids and program uids from program tracked entity attributes
        Set<String> trackedEntityAttributeUids = new HashSet<>();
        Set<String> programUids = new HashSet<>();
        List<ProgramTrackedEntityAttribute> mergedProgramTrackedEntityAttributes = ModelUtils.merge(
                allExistingProgramTrackedEntityAttributes, updatedProgramTrackedEntityAttributes,
                persistedProgramTrackedEntityAttributes);
        for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : mergedProgramTrackedEntityAttributes) {
            trackedEntityAttributeUids.add(programTrackedEntityAttribute.getTrackedEntityAttribute().getUId());
            programUids.add(programTrackedEntityAttribute.getProgram().getUId());
        }

        // Syncing tracked entity attributes before saving program tracked entity attributes(since
        // program tracked entity attributes are referencing them directly)
        programController.pull(strategy, programUids);
        trackedEntityAttributeController.pull(strategy, trackedEntityAttributeUids);

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingProgramTrackedEntityAttributes, updatedProgramTrackedEntityAttributes,
                persistedProgramTrackedEntityAttributes, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.PROGRAM_TRACKED_ENTITY_ATTRIBUTES,
                DateType.SERVER, serverTime);
    }

    @Override
    public List<DbOperation> merge(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) throws ApiException {
        List<ProgramTrackedEntityAttribute> allExistingProgramTrackedEntityAttributes =
                programTrackedEntityAttributeApiClient.getProgramTrackedEntityAttributes(Fields.BASIC, null, null);
        List<ProgramTrackedEntityAttribute> persistedProgramTrackedEntityAttributes =
                identifiableObjectStore.queryAll();

        return DbUtils.createOperations(allExistingProgramTrackedEntityAttributes, programTrackedEntityAttributes,
                persistedProgramTrackedEntityAttributes, identifiableObjectStore);
    }
}
