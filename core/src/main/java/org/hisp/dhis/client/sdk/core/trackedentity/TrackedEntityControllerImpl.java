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

package org.hisp.dhis.client.sdk.core.trackedentity;

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
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TrackedEntityControllerImpl extends AbsSyncStrategyController<TrackedEntity> implements TrackedEntityController {
    private final TrackedEntityApiClient trackedEntityApiClient;
    private final TransactionManager transactionManager;
    private final LastUpdatedPreferences lastUpdatedPreferences;
    private final SystemInfoController systemInfoController;
    private final TrackedEntityStore trackedEntityStore;
    public TrackedEntityControllerImpl(TrackedEntityApiClient trackedEntityApiClient,
                                       TransactionManager transactionManager,
                                       LastUpdatedPreferences lastUpdatedPreferences,
                                       TrackedEntityStore trackedEntityStore,
                                       SystemInfoController systemInfoController) {
        super(ResourceType.TRACKED_ENTITIES, trackedEntityStore, lastUpdatedPreferences);
        this.trackedEntityApiClient = trackedEntityApiClient;
        this.transactionManager = transactionManager;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.systemInfoController = systemInfoController;
        this.trackedEntityStore = trackedEntityStore;
    }


    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.TRACKED_ENTITIES, DateType.SERVER);

        List<TrackedEntity> persistedTrackedEntities =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<TrackedEntity> allExistingTrackedEntities = trackedEntityApiClient
                .getTrackedEntities(Fields.BASIC, null);

        List<TrackedEntity> updatedTrackedEntities = new ArrayList<>();
        if (uids == null) {
            updatedTrackedEntities.addAll(trackedEntityApiClient
                    .getTrackedEntities(Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedTrackedEntities);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedTrackedEntities.addAll(trackedEntityApiClient
                        .getTrackedEntities(Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedTrackedEntities.addAll(trackedEntityApiClient
                        .getTrackedEntities(Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

        // Retrieving program stage uids from program stages sections
        Set<String> trackedEntityUids = new HashSet<>();
        List<TrackedEntity> mergedTrackedEntities = ModelUtils.merge(
                allExistingTrackedEntities, updatedTrackedEntities,
                persistedTrackedEntities);
        for (TrackedEntity trackedEntity : mergedTrackedEntities) {
            trackedEntityUids.add(trackedEntity.getUId());
        }

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingTrackedEntities, updatedTrackedEntities,
                persistedTrackedEntities, identifiableObjectStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.TRACKED_ENTITIES,
                DateType.SERVER, serverTime);
    }

    @Override
    public List<DbOperation> merge(List<TrackedEntity> trackedEntities) throws ApiException {
        List<TrackedEntity> allExistingTrackedEntities =
                trackedEntityApiClient.getTrackedEntities(Fields.BASIC, null);
        List<TrackedEntity> persistedTrackedEntities =
                identifiableObjectStore.queryAll();

        return DbUtils.createOperations(allExistingTrackedEntities, trackedEntities,
                persistedTrackedEntities, identifiableObjectStore);
    }
}