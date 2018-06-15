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

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.calls.factories.BasicCallFactory;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2ErrorCode;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;

import retrofit2.Retrofit;

public class SystemInfoCall extends SyncCall<SystemInfo> {
    private final DatabaseAdapter databaseAdapter;
    private final GenericHandler<SystemInfo, SystemInfoModel> systemInfoHandler;
    private final SystemInfoService systemInfoService;
    private final ResourceHandler resourceHandler;

    SystemInfoCall(DatabaseAdapter databaseAdapter,
                   GenericHandler<SystemInfo, SystemInfoModel> systemInfoHandler,
                   SystemInfoService systemInfoService,
                   ResourceHandler resourceHandler) {
        this.databaseAdapter = databaseAdapter;
        this.systemInfoHandler = systemInfoHandler;
        this.systemInfoService = systemInfoService;
        this.resourceHandler = resourceHandler;
    }

    @Override
    public SystemInfo call() throws D2CallException {
        setExecuted();

        SystemInfo systemInfo = new APICallExecutor().executeObjectCall(
                systemInfoService.getSystemInfo(SystemInfo.allFields));

        if (!systemInfo.version().equals("2.29")) {
            throw D2CallException.builder()
                    .isHttpError(false)
                    .errorCode(D2ErrorCode.INVALID_DHIS_VERSION)
                    .errorDescription("Server DHIS version (" + systemInfo.version() + ") not valid. "
                            + "Please use a server with DHIS 2.29")
                    .build();
        }

        insertOrUpdateSystemInfo(systemInfo);
        return systemInfo;
    }

    private void insertOrUpdateSystemInfo(SystemInfo systemInfo) {
        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            systemInfoHandler.handle(systemInfo, new SystemInfoModelBuilder());
            resourceHandler.handleResource(ResourceModel.Type.SYSTEM_INFO, systemInfo.serverDate());
            transaction.setSuccessful();
        } finally {
            transaction.end();
        }
    }

    public static final BasicCallFactory<SystemInfo> FACTORY = new BasicCallFactory<SystemInfo>() {

        @Override
        public Call<SystemInfo> create(DatabaseAdapter databaseAdapter, Retrofit retrofit) {
            return new SystemInfoCall(
                    databaseAdapter,
                    SystemInfoHandler.create(databaseAdapter),
                    retrofit.create(SystemInfoService.class),
                    ResourceHandler.create(databaseAdapter)
            );
        }
    };
}
