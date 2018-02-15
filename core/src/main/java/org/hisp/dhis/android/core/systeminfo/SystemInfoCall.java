/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.systeminfo;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;

import java.io.IOException;

import retrofit2.Response;

public class SystemInfoCall implements Call<Response<SystemInfo>> {
    private final DatabaseAdapter databaseAdapter;
    private final SystemInfoStore systemInfoStore;
    private final SystemInfoService systemInfoService;
    private final ResourceStore resourceStore;
    private boolean isExecuted;
    private final SystemInfoQuery query;

    public SystemInfoCall(DatabaseAdapter databaseAdapter,
            SystemInfoStore systemInfoStore,
            SystemInfoService systemInfoService,
            ResourceStore resourceStore, @NonNull SystemInfoQuery query) {
        this.databaseAdapter = databaseAdapter;
        this.systemInfoStore = systemInfoStore;
        this.systemInfoService = systemInfoService;
        this.resourceStore = resourceStore;
        this.query = query;

    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }

    }

    @Override
    public Response<SystemInfo> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }

            isExecuted = true;
        }

        Response<SystemInfo> response = getSystemInfo();
        if (response.isSuccessful()) {
            insertOrUpdateSystemInfo(response);
        }

        return response;
    }

    private void insertOrUpdateSystemInfo(Response<SystemInfo> response) {
        SystemInfoHandler systemInfoHandler = new SystemInfoHandler(systemInfoStore);
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            if (response.body() != null) {
                SystemInfo systemInfo = response.body();
                systemInfoHandler.handleSystemInfo(systemInfo);

                resourceHandler.handleResource(ResourceModel.Type.SYSTEM_INFO,
                        systemInfo.serverDate());
            }

            transaction.setSuccessful();
        } finally {
            transaction.end();
        }
    }

    private Response<SystemInfo> getSystemInfo() throws IOException {
        return systemInfoService.getSystemInfo(
                Fields.<SystemInfo>builder().fields(
                        SystemInfo.serverDateTime,
                        SystemInfo.dateFormat,
                        SystemInfo.version,
                        SystemInfo.contextPath
                ).build(),
                query.isTranslationOn(), query.translationLocale()
        ).execute();
    }
}
