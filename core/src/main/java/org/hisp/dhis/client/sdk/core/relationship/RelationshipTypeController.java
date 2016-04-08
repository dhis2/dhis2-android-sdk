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

package org.hisp.dhis.client.sdk.core.relationship;

import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoApiClient;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public final class RelationshipTypeController implements IdentifiableController<RelationshipType> {
    private final TransactionManager transactionManager;
    private final LastUpdatedPreferences lastUpdatedPreferences;
    private final RelationshipTypeApiClient relationshipTypeApiClient;
    private final SystemInfoApiClient systemInfoApiClient;
    private final IdentifiableObjectStore<RelationshipType> mRelationshipTypeStore;

    public RelationshipTypeController(RelationshipTypeApiClient relationshipApiClient,
                                      TransactionManager transactionManager,
                                      IdentifiableObjectStore<RelationshipType>
                                              mRelationshipTypeStore,
                                      LastUpdatedPreferences lastUpdatedPreferences,
                                      SystemInfoApiClient systemInfoApiClient) {
        this.relationshipTypeApiClient = relationshipApiClient;
        this.transactionManager = transactionManager;
        this.mRelationshipTypeStore = mRelationshipTypeStore;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.systemInfoApiClient = systemInfoApiClient;
    }

    private void getRelationshipTypesDataFromServer() throws ApiException {
        ResourceType resource = ResourceType.RELATIONSHIP_TYPES;
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(resource, DateType.SERVER);

        //fetching id and name for all items on server. This is needed in case something is
        // deleted on the server and we want to reflect that locally
        List<RelationshipType> allRelationshipTypes =
                relationshipTypeApiClient.getBasicRelationshipTypes(null);


        //fetch all updated relationshiptypes
        List<RelationshipType> updatedRelationshipTypes =
                relationshipTypeApiClient.getFullRelationshipTypes(lastUpdated);

        //merging updated items with persisted items, and removing ones not present in server.
        List<RelationshipType> existingPersistedAndUpdatedRelationshipTypes =
                ModelUtils.merge(allRelationshipTypes, updatedRelationshipTypes,
                        mRelationshipTypeStore.queryAll());

        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(mRelationshipTypeStore,
                existingPersistedAndUpdatedRelationshipTypes, mRelationshipTypeStore.queryAll()));

        transactionManager.transact(operations);
        lastUpdatedPreferences.save(resource, DateType.SERVER, serverTime);
    }

    @Override
    public void pull(SyncStrategy syncStrategy) throws ApiException {
        getRelationshipTypesDataFromServer();
    }

    @Override
    public void pull(SyncStrategy syncStrategy, Set<String> uids) throws ApiException {

    }
}