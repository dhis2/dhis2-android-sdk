/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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
package org.hisp.dhis.android.core.domain.metadata;

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.category.CategoryModuleDownloader;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.constant.ConstantModuleDownloader;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetModuleDownloader;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModuleDownloader;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramModuleDownloader;
import org.hisp.dhis.android.core.settings.SystemSettingModuleDownloader;
import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModuleDownloader;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserModuleDownloader;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;

@Reusable
public class MetadataCall implements Callable<Unit> {

    private final D2CallExecutor d2CallExecutor;

    private final SystemInfoModuleDownloader systemInfoDownloader;
    private final SystemSettingModuleDownloader systemSettingDownloader;
    private final UserModuleDownloader userModuleDownloader;
    private final CategoryModuleDownloader categoryDownloader;
    private final ProgramModuleDownloader programDownloader;
    private final OrganisationUnitModuleDownloader organisationUnitDownloadModule;
    private final DataSetModuleDownloader dataSetDownloader;
    private final ConstantModuleDownloader constantModuleDownloader;
    private final ForeignKeyCleaner foreignKeyCleaner;
    private final SmsModule smsModule;

    @Inject
    public MetadataCall(@NonNull D2CallExecutor d2CallExecutor,
                        @NonNull SystemInfoModuleDownloader systemInfoDownloader,
                        @NonNull SystemSettingModuleDownloader systemSettingDownloader,
                        @NonNull UserModuleDownloader userModuleDownloader,
                        @NonNull CategoryModuleDownloader categoryDownloader,
                        @NonNull ProgramModuleDownloader programDownloader,
                        @NonNull OrganisationUnitModuleDownloader organisationUnitDownloadModule,
                        @NonNull DataSetModuleDownloader dataSetDownloader,
                        @NonNull ConstantModuleDownloader constantModuleDownloader,
                        @NonNull ForeignKeyCleaner foreignKeyCleaner,
                        @NonNull SmsModule smsModule) {
        this.d2CallExecutor = d2CallExecutor;
        this.systemInfoDownloader = systemInfoDownloader;
        this.systemSettingDownloader = systemSettingDownloader;
        this.userModuleDownloader = userModuleDownloader;
        this.categoryDownloader = categoryDownloader;
        this.programDownloader = programDownloader;
        this.organisationUnitDownloadModule = organisationUnitDownloadModule;
        this.dataSetDownloader = dataSetDownloader;
        this.constantModuleDownloader = constantModuleDownloader;
        this.foreignKeyCleaner = foreignKeyCleaner;
        this.smsModule = smsModule;
    }

    @Override
    public Unit call() throws Exception {

        return d2CallExecutor.executeD2CallTransactionally(() -> {
            systemInfoDownloader.downloadMetadata().blockingAwait();

            systemSettingDownloader.downloadMetadata().call();

            User user = userModuleDownloader.downloadMetadata().call();

            List<Program> programs = programDownloader.downloadMetadata().call();

            List<DataSet> dataSets = dataSetDownloader.downloadMetadata().call();

            categoryDownloader.downloadMetadata().call();

            organisationUnitDownloadModule.downloadMetadata(user, programs, dataSets).call();

            constantModuleDownloader.downloadMetadata().call();

            smsModule.configCase().refreshMetadataIdsCallable().call();

            foreignKeyCleaner.cleanForeignKeyErrors();

            return new Unit();
        });
    }
}