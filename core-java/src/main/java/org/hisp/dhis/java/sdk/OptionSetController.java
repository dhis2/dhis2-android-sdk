/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.java.sdk;

import org.hisp.dhis.java.sdk.common.ResourceController;
import org.hisp.dhis.java.sdk.core.network.APIException;
import org.hisp.dhis.java.sdk.core.network.IDhisApi;
import org.hisp.dhis.java.sdk.core.api.preferences.DateTimeManager;
import org.hisp.dhis.java.sdk.core.models.ResourceType;
import org.hisp.dhis.java.sdk.core.api.utils.DbUtils;
import org.hisp.dhis.java.sdk.common.IIdentifiableObjectStore;
import org.hisp.dhis.java.sdk.common.meta.IDbOperation;
import org.hisp.dhis.java.sdk.optionset.IOptionStore;
import org.hisp.dhis.java.sdk.models.optionset.Option;
import org.hisp.dhis.java.sdk.models.optionset.OptionSet;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.java.sdk.core.api.utils.NetworkUtils.unwrapResponse;
import static org.hisp.dhis.java.sdk.models.common.base.BaseIdentifiableObject.merge;

public final class OptionSetController extends ResourceController<OptionSet> {

    private final static String OPTIONSETS = "optionSets";
    private final IDhisApi mDhisApi;

    private final IOptionStore mOptionStore;
    private final IIdentifiableObjectStore<OptionSet> mOptionSetStore;

    public OptionSetController(IDhisApi dhisApi, IOptionStore mOptionStore,
                               IIdentifiableObjectStore<OptionSet> mOptionSetStore) {
        this.mDhisApi = dhisApi;
        this.mOptionStore = mOptionStore;
        this.mOptionSetStore = mOptionSetStore;
    }

    private void getOptionSetDataFromServer() throws APIException {
        ResourceType resource = ResourceType.OPTIONSETS;
        DateTime serverTime = mDhisApi.getSystemInfo().getServerDate();
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(resource);
        List<OptionSet> allOptionSets = NetworkUtils.unwrapResponse(mDhisApi
                .getOptionSets(getBasicQueryMap()), OPTIONSETS);
        List<OptionSet> updatedOptionSets = NetworkUtils.unwrapResponse(mDhisApi
                .getOptionSets(getAllFieldsQueryMap(lastUpdated)), OPTIONSETS);
        linkOptionsWithOptionSets(updatedOptionSets);
        List<OptionSet> existingPersistedAndUpdatedOptionSets =
                merge(allOptionSets, updatedOptionSets, mOptionSetStore.queryAll());

        List<IDbOperation> operations = new ArrayList<>();
        List<OptionSet> persistedOptionSets = mOptionSetStore.queryAll();
        if (existingPersistedAndUpdatedOptionSets != null && !existingPersistedAndUpdatedOptionSets.isEmpty()) {
            for (OptionSet optionSet: existingPersistedAndUpdatedOptionSets) {
                if (optionSet == null || optionSet.getOptions() == null) {
                    continue;
                }
                OptionSet persistedOptionSet = mOptionSetStore.queryByUid(optionSet.getUId());
                List<Option> persistedOptions;
                if(persistedOptionSet != null) {
                    persistedOptions = persistedOptionSet.getOptions();
                } else {
                    persistedOptions = new ArrayList<>();
                }
                operations.addAll(DbUtils.createOperations(mOptionStore, persistedOptions, optionSet.getOptions()));
            }
        }
        operations.addAll(DbUtils.createOperations(mOptionSetStore, persistedOptionSets, existingPersistedAndUpdatedOptionSets));

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(resource, serverTime);
    }

    private void linkOptionsWithOptionSets(List<OptionSet> optionSets) {
        // Building option to optionset relationship.
        if (optionSets != null && !optionSets.isEmpty()) {
            for (OptionSet optionSet : optionSets) {
                if (optionSet == null || optionSet.getOptions() == null) {
                    continue;
                }
                int sortOrder = 0;
                for (Option option : optionSet.getOptions()) {
                    option.setUId(optionSet.getUId() + option.getCode());//options don't have uid, but uid is used in createOperations
                    option.setLastUpdated(new DateTime());//same with these dates
                    option.setCreated(new DateTime());
                    option.setOptionSet(optionSet.getUId());
                    option.setSortOrder(sortOrder);
                    sortOrder++;
                }
            }
        }
    }

    @Override
    public void sync() throws APIException {
        getOptionSetDataFromServer();
    }
}