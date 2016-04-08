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
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class OptionSetController extends AbsSyncStrategyController<OptionSet>
        implements IOptionSetController {
    private final IOptionSetApiClient optionSetApiClient;
    private final ISystemInfoController systemInfoController;
    private final TransactionManager transactionManager;
    private final OptionStore optionStore;
    private final OptionSetStore optionSetStore;

    public OptionSetController(ISystemInfoController systemInfoController,
                               IOptionSetApiClient optionSetApiClient,
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


//    private void linkOptionsWithOptionSets(List<OptionSet> optionSets) {
//        // Building option to optionset relationship.
//        if (optionSets != null && !optionSets.isEmpty()) {
//            for (OptionSet optionSet : optionSets) {
//                if (optionSet == null || optionSet.getOptions() == null) {
//                    continue;
//                }
//                int sortOrder = 0;
//                for (Option option : optionSet.getOptions()) {
//                    option.setUId(optionSet.getUId() + option.getCode());//options don't have
//                    // uid, but uid is used in createOperations
//                    option.setLastUpdated(new DateTime());//same with these dates
//                    option.setCreated(new DateTime());
//                    option.setOptionSet(optionSet);
//                    option.setSortOrder(sortOrder);
//                    sortOrder++;
//                }
//            }
//        }
//    }
//
//
//    @Override
//    protected void synchronizes(SyncStrategy strategy, Set<String> uids) {
//        ResourceType resource = ResourceType.OPTION_SETS;
//        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
//        DateTime lastUpdated = lastUpdatedPreferences.get(resource, DateType.SERVER);
//        List<OptionSet> allOptionSets = optionSetApiClient.getOptionSets(Fields.BASIC,
// lastUpdated, null);
//        List<OptionSet> updatedOptionSets = optionSetApiClient.getOptionSets(lastUpdated);
////        linkOptionsWithOptionSets(updatedOptionSets);
//        List<OptionSet> existingPersistedAndUpdatedOptionSets =
//                ModelUtils.merge(allOptionSets, updatedOptionSets, optionSetStore.queryAll());
//
//        List<DbOperation> operations = new ArrayList<>();
//        List<OptionSet> persistedOptionSets = optionSetStore.queryAll();
//        if (existingPersistedAndUpdatedOptionSets != null &&
//                !existingPersistedAndUpdatedOptionSets.isEmpty()) {
//            for (OptionSet optionSet : existingPersistedAndUpdatedOptionSets) {
//                if (optionSet == null || optionSet.getOptions() == null) {
//                    continue;
//                }
//                OptionSet persistedOptionSet = optionSetStore.queryByUid(optionSet.getUId());
//                List<Option> persistedOptions;
//                if (persistedOptionSet != null) {
//                    persistedOptions = persistedOptionSet.getOptions();
//                } else {
//                    persistedOptions = new ArrayList<>();
//                }
//                operations.addAll(DbUtils.createOperations(optionStore,
//                        persistedOptions, optionSet.getOptions()));
//            }
//        }
//        operations.addAll(DbUtils.createOperations(optionSetStore,
//                persistedOptionSets, existingPersistedAndUpdatedOptionSets));
//
////        DbUtils.applyBatch(operations);
//        transactionManager.transact(operations);
//        lastUpdatedPreferences.save(ResourceType.OPTION_SETS, DateType.SERVER, serverTime);
//    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.OPTION_SETS, DateType.SERVER);

        List<OptionSet> persistedOptionSets =
                identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<OptionSet> allExistingOptionSets = optionSetApiClient
                .getOptionSets(Fields.BASIC, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of option sets which are
            // stored locally and list of option sets which we want to download
            uidSet = ModelUtils.toUidSet(persistedOptionSets);
            uidSet.addAll(uids);
        }

        List<OptionSet> updatedOptionSets = optionSetApiClient
                .getOptionSets(Fields.ALL, lastUpdated, uidSet);


        List<OptionSet> mergedOptionSets = ModelUtils.merge(
                allExistingOptionSets, updatedOptionSets,
                persistedOptionSets);


        List<DbOperation> optionDbOperations = new ArrayList<>();

        if (mergedOptionSets != null &&
                !mergedOptionSets.isEmpty()) {
            for (OptionSet optionSet : mergedOptionSets) {
                if (optionSet == null || optionSet.getOptions() == null) {
                    continue;
                }
                OptionSet persistedOptionSet = optionSetStore.queryByUid(optionSet.getUId());
                List<Option> persistedOptions;
                if (persistedOptionSet != null) {
                    persistedOptions = persistedOptionSet.getOptions();
                } else {
                    persistedOptions = new ArrayList<>();
                }
                optionDbOperations.addAll(DbUtils.createOperations(optionStore,
                        persistedOptions, optionSet.getOptions()));
            }
        }


        // we will have to perform something similar to what happens in AbsController

        List<DbOperation> dbOperations = DbUtils.createOperations(
                allExistingOptionSets, updatedOptionSets,
                persistedOptionSets, identifiableObjectStore);
        transactionManager.transact(optionDbOperations);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.OPTION_SETS,
                DateType.SERVER, serverTime);
    }
}