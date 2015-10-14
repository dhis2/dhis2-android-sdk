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

package org.hisp.dhis.android.sdk.corejava;

import org.hisp.dhis.android.sdk.corejava.common.ResourceController;
import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.core.network.IDhisApi;
import org.hisp.dhis.android.sdk.core.api.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.core.models.ResourceType;
import org.hisp.dhis.android.sdk.corejava.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.constant.Constant;
import org.joda.time.DateTime;

import java.util.List;

import static org.hisp.dhis.android.sdk.core.api.utils.NetworkUtils.unwrapResponse;
import static org.hisp.dhis.android.sdk.models.common.base.BaseIdentifiableObject.merge;

public final class ConstantController extends ResourceController<Constant> {

    private final static String CONSTANTS = "constants";
    private final IDhisApi mDhisApi;
    private final IIdentifiableObjectStore<Constant> mConstantStore;

    public ConstantController(IDhisApi mDhisApi, IIdentifiableObjectStore<Constant> constantStore) {
        this.mDhisApi = mDhisApi;
        this.mConstantStore = constantStore;
    }

    private void getConstantsDataFromServer() throws APIException {
        ResourceType resource = ResourceType.CONSTANTS;
        DateTime serverTime = mDhisApi.getSystemInfo().getServerDate();
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(resource);

        //fetching id and name for all items on server. This is needed in case something is
        // deleted on the server and we want to reflect that locally
        List<Constant> allConstants = NetworkUtils.unwrapResponse(mDhisApi
                .getConstants(getBasicQueryMap()), CONSTANTS);
        //fetch all updated items
        List<Constant> updatedConstants = NetworkUtils.unwrapResponse(mDhisApi
                .getConstants(getAllFieldsQueryMap(lastUpdated)), CONSTANTS);
        //merging updated items with persisted items, and removing ones not present in server.
        List<Constant> existingPersistedAndUpdatedConstants =
                merge(allConstants, updatedConstants, mConstantStore.
                        queryAll());
        saveResourceDataFromServer(resource, mConstantStore,
                existingPersistedAndUpdatedConstants, mConstantStore.queryAll(),
                serverTime);
    }

    @Override
    public void sync() throws APIException {
        getConstantsDataFromServer();
    }
}