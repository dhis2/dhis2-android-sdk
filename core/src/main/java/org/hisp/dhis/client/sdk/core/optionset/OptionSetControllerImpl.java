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

package org.hisp.dhis.client.sdk.core.optionset;


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
import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OptionSetControllerImpl extends
        AbsSyncStrategyController<OptionSet> implements OptionSetController {
    private final OptionSetApiClient optionSetApiClient;
    private final SystemInfoController systemInfoController;
    private final TransactionManager transactionManager;
    private final OptionStore optionStore;
    private final OptionSetStore optionSetStore;

    public OptionSetControllerImpl(SystemInfoController systemInfoController,
                                   OptionSetApiClient optionSetApiClient,
                                   OptionStore optionStore,
                                   OptionSetStore optionSetStore,
                                   LastUpdatedPreferences lastUpdatedPreferences,
                                   TransactionManager transactionManager) {
        super(ResourceType.OPTION_SETS, optionSetStore, lastUpdatedPreferences);
        this.systemInfoController = systemInfoController;
        this.optionSetApiClient = optionSetApiClient;
        this.optionStore = optionStore;
        this.optionSetStore = optionSetStore;
        this.transactionManager = transactionManager;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.OPTION_SETS, DateType.SERVER);

        List<OptionSet> persistedOptionSets = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<OptionSet> allExistingOptionSets = optionSetApiClient
                .getOptionSets(Fields.BASIC, null, null);

        List<OptionSet> updatedOptionSets = new ArrayList<>();
        if (uids == null) {
            updatedOptionSets.addAll(optionSetApiClient
                    .getOptionSets(Fields.ALL, lastUpdated, null));
        } else {
            // defensive copy
            Set<String> modelsToFetch = new HashSet<>(uids);
            Set<String> modelsToUpdate = ModelUtils.toUidSet(persistedOptionSets);

            modelsToFetch.removeAll(modelsToUpdate);

            if (!modelsToFetch.isEmpty()) {
                updatedOptionSets.addAll(optionSetApiClient
                        .getOptionSets(Fields.ALL, null, modelsToFetch));
            }

            if (!modelsToUpdate.isEmpty()) {
                updatedOptionSets.addAll(optionSetApiClient
                        .getOptionSets(Fields.ALL, lastUpdated, modelsToUpdate));
            }
        }

        List<Option> updatedOptions = new ArrayList<>();
        for (OptionSet updatedOptionSet : updatedOptionSets) {
            updatedOptions.addAll(updatedOptionSet.getOptions());
        }

        List<DbOperation> dbOperations = new ArrayList<>();
        dbOperations.addAll(DbUtils.createOperations(optionStore,
                optionStore.queryAll(), updatedOptions));

        // we will have to perform something similar to what happens in AbsController
        dbOperations.addAll(DbUtils.createOperations(allExistingOptionSets,
                updatedOptionSets, persistedOptionSets, identifiableObjectStore));

        transactionManager.transact(dbOperations);
        lastUpdatedPreferences.save(ResourceType.OPTION_SETS,
                DateType.SERVER, serverTime);
    }


    @Override
    public List<DbOperation> merge(List<OptionSet> updatedOptionSets) throws ApiException {
        List<OptionSet> allExistingOptionSets = optionSetApiClient
                .getOptionSets(Fields.BASIC, null, null);
        List<OptionSet> persistedOptionSets = identifiableObjectStore
                .queryAll();
        List<Option> updatedOptions = new ArrayList<>();

        for (OptionSet optionSet : updatedOptionSets) {
            if (optionSet.getOptions() != null) {
                updatedOptions.addAll(optionSet.getOptions());
            }
        }

        List<DbOperation> dbOperations = new ArrayList<>();
        dbOperations.addAll(DbUtils.createOperations(optionStore,
                optionStore.queryAll(), updatedOptions));
        dbOperations.addAll(DbUtils.createOperations(allExistingOptionSets,
                updatedOptionSets, persistedOptionSets, identifiableObjectStore));

        return dbOperations;
    }
}