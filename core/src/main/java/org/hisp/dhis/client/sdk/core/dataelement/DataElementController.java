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

package org.hisp.dhis.client.sdk.core.dataelement;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.controllers.AbsSyncStrategyController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetController;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DataElementController extends AbsSyncStrategyController<DataElement>
        implements IDataElementController {

    /* Controllers */
    private final ISystemInfoController systemInfoController;

    /* Api clients */
    private final IDataElementApiClient dataElementApiClient;

    /* Utilities */
    private final ITransactionManager transactionManager;
    private final IOptionSetController optionSetController;

    public DataElementController(ISystemInfoController systemInfoController,
                                 IOptionSetController optionSetController,
                                 IDataElementApiClient dataElementApiClient,
                                 IDataElementStore dataElementStore,
                                 ILastUpdatedPreferences lastUpdatedPreferences,
                                 ITransactionManager transactionManager) {
        super(ResourceType.DATA_ELEMENTS, dataElementStore, lastUpdatedPreferences);
        this.systemInfoController = systemInfoController;
        this.optionSetController = optionSetController;
        this.dataElementApiClient = dataElementApiClient;
        this.transactionManager = transactionManager;
    }

    @Override
    protected void synchronize(SyncStrategy strategy, Set<String> uids) {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(
                ResourceType.DATA_ELEMENTS, DateType.SERVER);

        List<DataElement> persistedDataElements = identifiableObjectStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<DataElement> allExistingDataElements = dataElementApiClient
                .getDataElements(Fields.BASIC, null, null);

        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of data elements which are
            // stored locally and list of data elements which we want to download
            uidSet = ModelUtils.toUidSet(persistedDataElements);
            uidSet.addAll(uids);
        }

        List<DataElement> updatedDataElements = dataElementApiClient.getDataElements(
                Fields.ALL, lastUpdated, uidSet);

        // Retrieving program stage uids from program stages sections
        Set<String> optionSetUids = new HashSet<>();
        List<DataElement> mergedDataElements = ModelUtils.merge(
                allExistingDataElements, updatedDataElements,
                persistedDataElements);
        for (DataElement dataElement : mergedDataElements) {
            if(dataElement.getOptionSet() != null) {
                optionSetUids.add(dataElement.getOptionSet().getUId());
            }
        }

        // Syncing option sets before saving data elements(since
        // data elements are referencing them directly)
        optionSetController.sync(strategy, optionSetUids);


        // we will have to perform something similar to what happens in AbsController

        System.out.println("allExistingDataElements: " + allExistingDataElements);
        System.out.println("updatedDataElements: " + updatedDataElements);
        System.out.println("DataElements in store: " + persistedDataElements);

        List<IDbOperation> dbOperations = DbUtils.createOperations(allExistingDataElements,
                updatedDataElements, persistedDataElements, identifiableObjectStore);
        transactionManager.transact(dbOperations);
        lastUpdatedPreferences.save(ResourceType.DATA_ELEMENTS, DateType.SERVER, serverTime);
    }
}