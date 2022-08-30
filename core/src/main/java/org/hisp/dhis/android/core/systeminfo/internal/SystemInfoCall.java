/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.systeminfo.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.call.internal.CompletableProvider;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.systeminfo.DHISVersion;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;

@Reusable
public class SystemInfoCall implements CompletableProvider {
    private final DatabaseAdapter databaseAdapter;
    private final Handler<SystemInfo> systemInfoHandler;
    private final SystemInfoService systemInfoService;
    private final ResourceHandler resourceHandler;
    private final DHISVersionManagerImpl versionManager;
    private final RxAPICallExecutor apiCallExecutor;

    @Inject
    SystemInfoCall(DatabaseAdapter databaseAdapter,
                   Handler<SystemInfo> systemInfoHandler,
                   SystemInfoService systemInfoService,
                   ResourceHandler resourceHandler,
                   DHISVersionManagerImpl versionManager,
                   RxAPICallExecutor apiCallExecutor) {
        this.databaseAdapter = databaseAdapter;
        this.systemInfoHandler = systemInfoHandler;
        this.systemInfoService = systemInfoService;
        this.resourceHandler = resourceHandler;
        this.versionManager = versionManager;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public Completable getCompletable(boolean storeError) {
        return apiCallExecutor.wrapSingle(systemInfoService.getSystemInfo(SystemInfoFields.allFields), storeError)
                .doOnSuccess(systemInfo -> {
                    if (DHISVersion.isAllowedVersion(systemInfo.version())) {
                        versionManager.setVersion(systemInfo.version());
                    } else {
                        throw D2Error.builder()
                                .errorComponent(D2ErrorComponent.SDK)
                                .errorCode(D2ErrorCode.INVALID_DHIS_VERSION)
                                .errorDescription("Server DHIS version (" + systemInfo.version() + ") not valid. "
                                        + "Allowed versions: "
                                        + CollectionsHelper.commaAndSpaceSeparatedArrayValues(
                                                DHISVersion.allowedVersionsAsStr()))
                                .build();
                    }

                    insertOrUpdateSystemInfo(systemInfo);
                }).ignoreElement();
    }

    private void insertOrUpdateSystemInfo(SystemInfo systemInfo) {
        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            systemInfoHandler.handle(systemInfo);
            resourceHandler.setServerDate(systemInfo.serverDate());
            resourceHandler.handleResource(Resource.Type.SYSTEM_INFO);
            transaction.setSuccessful();
        } finally {
            transaction.end();
        }
    }
}
