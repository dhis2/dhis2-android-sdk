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

import org.hisp.dhis.client.sdk.core.common.controllers.IDataController;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.utils.IModelUtils;
import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class TrackedEntityController implements IDataController<TrackedEntity> {
    private final ITrackedEntityApiClient trackedEntityApiClient;
    private final ITransactionManager transactionManager;
    private final ILastUpdatedPreferences lastUpdatedPreferences;
    private final ISystemInfoApiClient systemInfoApiClient;
    private final IIdentifiableObjectStore<TrackedEntity> trackedEntityStore;
    private final IModelUtils modelUtils;

    public TrackedEntityController(ITrackedEntityApiClient trackedEntityApiClient,
                                   ITransactionManager transactionManager,
                                   ILastUpdatedPreferences lastUpdatedPreferences,
                                   IIdentifiableObjectStore<TrackedEntity> trackedEntityStore,
                                   ISystemInfoApiClient systemInfoApiClient, IModelUtils modelUtils) {
        this.trackedEntityApiClient = trackedEntityApiClient;
        this.transactionManager = transactionManager;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.systemInfoApiClient = systemInfoApiClient;
        this.trackedEntityStore = trackedEntityStore;
        this.modelUtils = modelUtils;
    }


    private void getTrackedEntityAttributesFromServer() throws ApiException {
        ResourceType resource = ResourceType.TRACKED_ENTITIES;
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(resource);

        // fetching id and name for all items on server. This is needed in case something is
        // deleted on the server and we want to reflect that locally

        List<TrackedEntity> allTrackedEntities
                = trackedEntityApiClient.getBasicTrackedEntities(null);

        //fetch all updated items
        List<TrackedEntity> updatedTrackedEntities
                = trackedEntityApiClient.getFullTrackedEntities(lastUpdated);

        //merging updated items with persisted items, and removing ones not present in server.
        List<TrackedEntity> existingPersistedAndUpdatedTrackedEntities =
                modelUtils.merge(allTrackedEntities, updatedTrackedEntities,
                        trackedEntityStore.queryAll());

        Queue<IDbOperation> operations = new LinkedList<>();
        operations.addAll(transactionManager.createOperations(trackedEntityStore,
                existingPersistedAndUpdatedTrackedEntities, trackedEntityStore.queryAll()));

        transactionManager.transact(operations);
        lastUpdatedPreferences.save(resource, serverTime);
    }

    @Override
    public void sync() throws ApiException {
        getTrackedEntityAttributesFromServer();
    }
}