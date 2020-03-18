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

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.internal.CategoryModuleDownloader;
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.constant.internal.ConstantModuleDownloader;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.internal.DataSetModuleDownloader;
import org.hisp.dhis.android.core.domain.metadata.internal.MetadataHelper;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolationTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleDownloader;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.internal.ProgramModuleDownloader;
import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall;
import org.hisp.dhis.android.core.settings.internal.SettingModuleDownloader;
import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.internal.UserModuleDownloader;

import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.Observable;

@Reusable
@SuppressWarnings("PMD.ExcessiveImports")
public class MetadataCall {

    private final RxAPICallExecutor rxCallExecutor;

    private final SystemInfoModuleDownloader systemInfoDownloader;
    private final SettingModuleDownloader systemSettingDownloader;
    private final UserModuleDownloader userModuleDownloader;
    private final CategoryModuleDownloader categoryDownloader;
    private final ProgramModuleDownloader programDownloader;
    private final OrganisationUnitModuleDownloader organisationUnitModuleDownloader;
    private final DataSetModuleDownloader dataSetDownloader;
    private final ConstantModuleDownloader constantModuleDownloader;
    private final SmsModule smsModule;
    private final DatabaseAdapter databaseAdapter;
    private final GeneralSettingCall generalSettingCall;
    private final MultiUserDatabaseManager multiUserDatabaseManager;
    private final ObjectKeyValueStore<Credentials> credentialsSecureStore;

    @Inject
    MetadataCall(@NonNull RxAPICallExecutor rxCallExecutor,
                 @NonNull SystemInfoModuleDownloader systemInfoDownloader,
                 @NonNull SettingModuleDownloader systemSettingDownloader,
                 @NonNull UserModuleDownloader userModuleDownloader,
                 @NonNull CategoryModuleDownloader categoryDownloader,
                 @NonNull ProgramModuleDownloader programDownloader,
                 @NonNull OrganisationUnitModuleDownloader organisationUnitModuleDownloader,
                 @NonNull DataSetModuleDownloader dataSetDownloader,
                 @NonNull ConstantModuleDownloader constantModuleDownloader,
                 @NonNull SmsModule smsModule,
                 @NonNull DatabaseAdapter databaseAdapter,
                 @NonNull GeneralSettingCall generalSettingCall,
                 @NonNull MultiUserDatabaseManager multiUserDatabaseManager,
                 @NonNull ObjectKeyValueStore<Credentials> credentialsSecureStore) {
        this.rxCallExecutor = rxCallExecutor;
        this.systemInfoDownloader = systemInfoDownloader;
        this.systemSettingDownloader = systemSettingDownloader;
        this.userModuleDownloader = userModuleDownloader;
        this.categoryDownloader = categoryDownloader;
        this.programDownloader = programDownloader;
        this.organisationUnitModuleDownloader = organisationUnitModuleDownloader;
        this.dataSetDownloader = dataSetDownloader;
        this.constantModuleDownloader = constantModuleDownloader;
        this.smsModule = smsModule;
        this.databaseAdapter = databaseAdapter;
        this.generalSettingCall = generalSettingCall;
        this.multiUserDatabaseManager = multiUserDatabaseManager;
        this.credentialsSecureStore = credentialsSecureStore;
    }

    public Observable<D2Progress> download() {
        D2ProgressManager progressManager = new D2ProgressManager(9);

        return changeEncryptionIfRequired().andThen(
                rxCallExecutor.wrapObservableTransactionally(
                        systemInfoDownloader.downloadMetadata().andThen(Observable.create(emitter -> {

                            databaseAdapter.delete(ForeignKeyViolationTableInfo.TABLE_INFO.name());

                            emitter.onNext(progressManager.increaseProgress(SystemInfo.class, false));

                            systemSettingDownloader.downloadMetadata().call();
                            emitter.onNext(progressManager.increaseProgress(SystemSetting.class, false));


                            User user = userModuleDownloader.downloadMetadata().call();
                            emitter.onNext(progressManager.increaseProgress(User.class, false));


                            List<OrganisationUnit> orgUnits = organisationUnitModuleDownloader.downloadMetadata(user)
                                    .call();
                            emitter.onNext(progressManager.increaseProgress(OrganisationUnit.class, false));


                            programDownloader.downloadMetadata(MetadataHelper.getOrgUnitsProgramUids(orgUnits)).call();
                            emitter.onNext(progressManager.increaseProgress(Program.class, false));


                            dataSetDownloader.downloadMetadata(MetadataHelper.getOrgUnitsDataSetUids(orgUnits)).call();
                            emitter.onNext(progressManager.increaseProgress(DataSet.class, false));


                            categoryDownloader.downloadMetadata().call();
                            emitter.onNext(progressManager.increaseProgress(Category.class, false));


                            constantModuleDownloader.downloadMetadata().call();
                            emitter.onNext(progressManager.increaseProgress(Constant.class, false));


                            smsModule.configCase().refreshMetadataIdsCallable().call();
                            emitter.onNext(progressManager.increaseProgress(SmsModule.class, false));

                            emitter.onComplete();

                        })), true));
    }

    private Completable changeEncryptionIfRequired() {
        return generalSettingCall.isDatabaseEncrypted()
                .doOnSuccess(encrypt ->
                        multiUserDatabaseManager.changeEncryptionIfRequired(credentialsSecureStore.get(), encrypt))
                .ignoreElement()
                .onErrorComplete();
    }

    public void blockingDownload() {
        download().blockingSubscribe();
    }
}