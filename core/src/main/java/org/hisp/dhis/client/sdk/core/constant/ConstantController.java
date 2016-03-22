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

package org.hisp.dhis.client.sdk.core.constant;

import org.hisp.dhis.client.sdk.core.common.controllers.IIdentifiableController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.models.constant.Constant;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public final class ConstantController implements IIdentifiableController<Constant> {
    private final IConstantApiClient constantApiClient;
    private final ISystemInfoApiClient systemInfoApiClient;
    private final ILastUpdatedPreferences lastUpdatedPreferences;
    private final IIdentifiableObjectStore<Constant> constantStore;
    private final ITransactionManager transactionManager;

    public ConstantController(IConstantApiClient constantApiClient,
                              ISystemInfoApiClient systemInfoApiClient,
                              ILastUpdatedPreferences lastUpdatedPreferences,
                              IIdentifiableObjectStore<Constant> constantStore,
                              ITransactionManager transactionManager) {
        this.constantApiClient = constantApiClient;
        this.systemInfoApiClient = systemInfoApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.constantStore = constantStore;
        this.transactionManager = transactionManager;
    }

    private void getConstantsDataFromServer() throws ApiException {
        ResourceType resource = ResourceType.CONSTANTS;
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(resource, DateType.SERVER);

        //fetching id and name for all items on server. This is needed in case something is
        // deleted on the server and we want to reflect that locally
        List<Constant> allConstants = constantApiClient.getBasicConstants(null);

        //fetch all updated items
        List<Constant> updatedConstants = constantApiClient.getFullConstants(lastUpdated);

        //merging updated items with persisted items, and removing ones not present in server.
        List<Constant> existingPersistedAndUpdatedConstants =
                ModelUtils.merge(allConstants, updatedConstants, constantStore.queryAll());

        Queue<IDbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(constantStore,
                existingPersistedAndUpdatedConstants, constantStore.queryAll()));

        transactionManager.transact(operations);
        lastUpdatedPreferences.save(resource, DateType.SERVER, serverTime);
    }

    @Override
    public void sync(SyncStrategy syncStrategy) throws ApiException {
        getConstantsDataFromServer();
    }

    @Override
    public void sync(SyncStrategy syncStrategy, Set<String> uids) throws ApiException {

    }
}