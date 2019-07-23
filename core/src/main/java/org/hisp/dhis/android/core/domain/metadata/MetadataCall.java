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

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.internal.CategoryModuleDownloader;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.constant.internal.ConstantModuleDownloader;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.internal.DataSetModuleDownloader;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleDownloader;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.internal.ProgramModuleDownloader;
import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.core.settings.internal.SystemSettingModuleDownloader;
import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.internal.UserModuleDownloader;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Reusable
public class MetadataCall {

    private final RxAPICallExecutor rxCallExecutor;

    private final SystemInfoModuleDownloader systemInfoDownloader;
    private final SystemSettingModuleDownloader systemSettingDownloader;
    private final UserModuleDownloader userModuleDownloader;
    private final CategoryModuleDownloader categoryDownloader;
    private final ProgramModuleDownloader programDownloader;
    private final OrganisationUnitModuleDownloader organisationUnitDownloadModule;
    private final DataSetModuleDownloader dataSetDownloader;
    private final ConstantModuleDownloader constantModuleDownloader;
    private final SmsModule smsModule;

    @Inject
    public MetadataCall(@NonNull RxAPICallExecutor rxCallExecutor,
                        @NonNull SystemInfoModuleDownloader systemInfoDownloader,
                        @NonNull SystemSettingModuleDownloader systemSettingDownloader,
                        @NonNull UserModuleDownloader userModuleDownloader,
                        @NonNull CategoryModuleDownloader categoryDownloader,
                        @NonNull ProgramModuleDownloader programDownloader,
                        @NonNull OrganisationUnitModuleDownloader organisationUnitDownloadModule,
                        @NonNull DataSetModuleDownloader dataSetDownloader,
                        @NonNull ConstantModuleDownloader constantModuleDownloader,
                        @NonNull SmsModule smsModule) {
        this.rxCallExecutor = rxCallExecutor;
        this.systemInfoDownloader = systemInfoDownloader;
        this.systemSettingDownloader = systemSettingDownloader;
        this.userModuleDownloader = userModuleDownloader;
        this.categoryDownloader = categoryDownloader;
        this.programDownloader = programDownloader;
        this.organisationUnitDownloadModule = organisationUnitDownloadModule;
        this.dataSetDownloader = dataSetDownloader;
        this.constantModuleDownloader = constantModuleDownloader;
        this.smsModule = smsModule;
    }

    public Observable<D2Progress> download() {
        D2ProgressManager progressManager = new D2ProgressManager(9);

        Single<D2Progress> systemInfoDownload = systemInfoDownloader.downloadMetadata().toSingle(() ->
                progressManager.increaseProgress(SystemInfo.class, false));

        return rxCallExecutor.wrapObservableTransactionally(
                systemInfoDownload.flatMapObservable(systemInfoProgress -> Observable.create(emitter -> {

                    emitter.onNext(systemInfoProgress);

                    systemSettingDownloader.downloadMetadata().call();
                    emitter.onNext(progressManager.increaseProgress(SystemSetting.class, false));


                    User user = userModuleDownloader.downloadMetadata().call();
                    emitter.onNext(progressManager.increaseProgress(User.class, false));


                    List<Program> programs = programDownloader.downloadMetadata().call();
                    emitter.onNext(progressManager.increaseProgress(Program.class, false));


                    List<DataSet> dataSets = dataSetDownloader.downloadMetadata().call();
                    emitter.onNext(progressManager.increaseProgress(DataSet.class, false));


                    categoryDownloader.downloadMetadata().call();
                    emitter.onNext(progressManager.increaseProgress(Category.class, false));


                    organisationUnitDownloadModule.downloadMetadata(user, programs, dataSets).call();
                    emitter.onNext(progressManager.increaseProgress(OrganisationUnit.class, false));


                    constantModuleDownloader.downloadMetadata().call();
                    emitter.onNext(progressManager.increaseProgress(Constant.class, false));


                    smsModule.configCase().refreshMetadataIdsCallable().call();
                    emitter.onNext(progressManager.increaseProgress(SmsModule.class, false));

                    emitter.onComplete();

                })), true);
    }

    public void blockingDownload() {
        download().blockingSubscribe();
    }
}