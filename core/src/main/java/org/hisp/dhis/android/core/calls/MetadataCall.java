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
package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.modules.Downloader;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitDownloadModule;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserDownloadModule;

import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
public class MetadataCall extends SyncCall<Unit> {

    private final D2CallExecutor d2CallExecutor;

    private final Downloader<SystemInfo> systemInfoDownloader;
    private final Downloader<SystemSetting> systemSettingDownloader;
    private final UserDownloadModule userDownloadModule;
    private final Downloader<Unit> categoryDownloader;
    private final Downloader<List<Program>> programDownloader;
    private final OrganisationUnitDownloadModule organisationUnitDownloadModule;
    private final Downloader<List<DataSet>> dataSetDownloader;
    private final ForeignKeyCleaner foreignKeyCleaner;

    public MetadataCall(@NonNull D2CallExecutor d2CallExecutor,
                        @NonNull Downloader<SystemInfo> systemInfoDownloader,
                        @NonNull Downloader<SystemSetting> systemSettingDownloader,
                        @NonNull UserDownloadModule userDownloadModule,
                        @NonNull Downloader<Unit> categoryDownloader,
                        @NonNull Downloader<List<Program>> programDownloader,
                        @NonNull OrganisationUnitDownloadModule organisationUnitDownloadModule,
                        @NonNull Downloader<List<DataSet>> dataSetDownloader,
                        @NonNull ForeignKeyCleaner foreignKeyCleaner) {
        this.d2CallExecutor = d2CallExecutor;
        this.systemInfoDownloader = systemInfoDownloader;
        this.systemSettingDownloader = systemSettingDownloader;
        this.userDownloadModule = userDownloadModule;
        this.categoryDownloader = categoryDownloader;
        this.programDownloader = programDownloader;
        this.organisationUnitDownloadModule = organisationUnitDownloadModule;
        this.dataSetDownloader = dataSetDownloader;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    @Override
    public Unit call() throws Exception {
        setExecuted();

        return d2CallExecutor.executeD2CallTransactionally(new Callable<Unit>() {
            @Override
            public Unit call() throws Exception {
                systemInfoDownloader.download().call();

                systemSettingDownloader.download().call();

                User user = userDownloadModule.downloadUserAndAuthority().call();

                List<Program> programs = programDownloader.download().call();

                List<DataSet> dataSets = dataSetDownloader.download().call();

                categoryDownloader.download().call();

                organisationUnitDownloadModule.download(user, programs, dataSets).call();

                foreignKeyCleaner.cleanForeignKeyErrors();

                return new Unit();
            }
        });
    }

    public static MetadataCall create(GenericCallData genericCallData,
                                      D2InternalModules internalModules) {

        return new MetadataCall(
                new D2CallExecutor(genericCallData.databaseAdapter()),
                internalModules.systemInfo,
                internalModules.systemSetting,
                internalModules.user,
                internalModules.category,
                internalModules.program,
                internalModules.organisationUnit,
                internalModules.dataSet,
                ForeignKeyCleanerImpl.create(genericCallData.databaseAdapter())
        );
    }
}