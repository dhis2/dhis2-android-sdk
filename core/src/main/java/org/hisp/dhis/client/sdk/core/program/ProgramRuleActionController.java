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

package org.hisp.dhis.client.sdk.core.program;

import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.core.common.controllers.IDataController;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.models.utils.IModelUtils;
import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class ProgramRuleActionController implements IDataController<ProgramRuleAction> {
    private final IProgramRuleActionApiClient programRuleActionApiClient;
    private final ITransactionManager transactionManager;
    private final ISystemInfoApiClient systemInfoApiClient;
    private final ILastUpdatedPreferences lastUpdatedPreferences;
    private final IIdentifiableObjectStore<ProgramRuleAction> mProgramRuleActionStore;
    private final IModelUtils modelUtils;

    public ProgramRuleActionController(IProgramRuleActionApiClient programRuleActionApiClient,
                                       ITransactionManager transactionManager,
                                       ISystemInfoApiClient systemInfoApiClient,
                                       ILastUpdatedPreferences lastUpdatedPreferences,
                                       IIdentifiableObjectStore<ProgramRuleAction> mProgramRuleActionStore,
                                       IModelUtils modelUtils) {
        this.programRuleActionApiClient = programRuleActionApiClient;
        this.transactionManager = transactionManager;
        this.systemInfoApiClient = systemInfoApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.mProgramRuleActionStore = mProgramRuleActionStore;
        this.modelUtils = modelUtils;
    }

    private void getProgramRuleVariablesDataFromServer() throws ApiException {
        ResourceType resource = ResourceType.PROGRAM_RULE_ACTIONS;
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(resource);

        //fetching id and name for all items on server. This is needed in case something is
        // deleted on the server and we want to reflect that locally
        List<ProgramRuleAction> allProgramRuleActions = programRuleActionApiClient.getBasicProgramRuleActions(null);

        //fetch all updated items
        List<ProgramRuleAction> updatedProgramRuleActions = programRuleActionApiClient.getFullProgramRuleActions(lastUpdated);
        //merging updated items with persisted items, and removing ones not present in server.
        List<ProgramRuleAction> existingPersistedAndUpdatedProgramRuleActions =
                modelUtils.merge(allProgramRuleActions, updatedProgramRuleActions, mProgramRuleActionStore.
                        queryAll());

        Queue<IDbOperation> operations = new LinkedList<>();
        operations.addAll(transactionManager.createOperations(mProgramRuleActionStore,
                existingPersistedAndUpdatedProgramRuleActions, mProgramRuleActionStore.queryAll()));

        transactionManager.transact(operations);
        lastUpdatedPreferences.save(resource, serverTime, null);
    }

    @Override
    public void sync() throws ApiException {
        getProgramRuleVariablesDataFromServer();
    }
}