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
import org.hisp.dhis.java.sdk.common.IIdentifiableObjectStore;
import org.hisp.dhis.java.sdk.models.program.ProgramRule;
import org.joda.time.DateTime;

import java.util.List;

import static org.hisp.dhis.java.sdk.core.api.utils.NetworkUtils.unwrapResponse;
import static org.hisp.dhis.java.sdk.models.common.base.BaseIdentifiableObject.merge;

public final class ProgramRuleController extends ResourceController<ProgramRule> {

    private final static String PROGRAMRULES = "programRules";
    private final IDhisApi mDhisApi;
    private final IIdentifiableObjectStore<ProgramRule> mProgramRuleStore;

    public ProgramRuleController(IDhisApi mDhisApi, IIdentifiableObjectStore<ProgramRule> mProgramRuleStore) {
        this.mDhisApi = mDhisApi;
        this.mProgramRuleStore = mProgramRuleStore;
    }

    private void getProgramRulesDataFromServer() throws APIException {
        ResourceType resource = ResourceType.PROGRAMRULES;
        DateTime serverTime = mDhisApi.getSystemInfo().getServerDate();
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(resource);

        //fetching id and name for all items on server. This is needed in case something is
        // deleted on the server and we want to reflect that locally
        List<ProgramRule> allProgramRules = NetworkUtils.unwrapResponse(mDhisApi
                .getProgramRules(getBasicQueryMap()), PROGRAMRULES);
        //fetch all updated items
        List<ProgramRule> updatedProgramRules = NetworkUtils.unwrapResponse(mDhisApi
                .getProgramRules(getAllFieldsQueryMap(lastUpdated)), PROGRAMRULES);
        //merging updated items with persisted items, and removing ones not present in server.
        List<ProgramRule> existingPersistedAndUpdatedProgramRules =
                merge(allProgramRules, updatedProgramRules, mProgramRuleStore.
                        queryAll());
        saveResourceDataFromServer(resource, mProgramRuleStore,
                existingPersistedAndUpdatedProgramRules, mProgramRuleStore.queryAll(),
                serverTime);
    }

    @Override
    public void sync() throws APIException {
        getProgramRulesDataFromServer();
    }
}