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

import org.assertj.core.util.Lists;
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore;
import org.hisp.dhis.android.core.category.internal.CategoryModuleDownloader;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager;
import org.hisp.dhis.android.core.constant.internal.ConstantModuleDownloader;
import org.hisp.dhis.android.core.dataset.internal.DataSetModuleDownloader;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolationTableInfo;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleDownloader;
import org.hisp.dhis.android.core.program.internal.ProgramModuleDownloader;
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall;
import org.hisp.dhis.android.core.settings.internal.SettingModuleDownloader;
import org.hisp.dhis.android.core.sms.SmsModule;
import org.hisp.dhis.android.core.sms.domain.interactor.ConfigCase;
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.internal.UserModuleDownloader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MetadataCallShould extends BaseCallShould {

    @Mock
    private User user;

    @Mock
    private RxAPICallExecutor rxAPICallExecutor;

    @Mock
    private SystemInfoModuleDownloader systemInfoDownloader;

    @Mock
    private SettingModuleDownloader systemSettingDownloader;

    @Mock
    private UserModuleDownloader userDownloader;

    @Mock
    private CategoryModuleDownloader categoryDownloader;

    @Mock
    private ProgramModuleDownloader programDownloader;

    @Mock
    private OrganisationUnitModuleDownloader organisationUnitDownloader;

    @Mock
    private DataSetModuleDownloader dataSetDownloader;

    @Mock
    private ConstantModuleDownloader constantDownloader;

    @Mock
    private ObjectStore<D2Error> d2ErrorStore;

    @Mock
    private SmsModule smsModule;

    @Mock
    private ConfigCase configCase;

    @Mock
    private GeneralSettingCall generalSettingCall;

    @Mock
    private MultiUserDatabaseManager multiUserDatabaseManager;

    @Mock
    private ObjectKeyValueStore<Credentials> credentialsSecureStore;

    // object to test
    private MetadataCall metadataCall;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        // Calls
        when(systemInfoDownloader.downloadMetadata()).thenReturn(Completable.complete());
        when(systemSettingDownloader.downloadMetadata()).thenReturn(Completable.complete());
        when(userDownloader.downloadMetadata()).thenReturn(Single.just(user));
        when(programDownloader.downloadMetadata(anySet())).thenReturn(Single.just(Lists.emptyList()));
        when(organisationUnitDownloader.downloadMetadata(same(user))).thenReturn(Single.just(Lists.emptyList()));
        when(dataSetDownloader.downloadMetadata(anySet())).thenReturn(Single.just(Lists.emptyList()));
        when(constantDownloader.downloadMetadata()).thenReturn(Single.just(Lists.emptyList()));
        when(categoryDownloader.downloadMetadata()).thenReturn(Completable.complete());
        when(smsModule.configCase()).thenReturn(configCase);
        when(configCase.refreshMetadataIdsCallable()).thenReturn(Completable.complete());
        when(generalSettingCall.isDatabaseEncrypted()).thenReturn(Single.just(false));

        when(d2ErrorStore.insert(any(D2Error.class))).thenReturn(0L);

        when(rxAPICallExecutor.wrapObservableTransactionally(any(Observable.class), anyBoolean()))
                .then(AdditionalAnswers.returnsFirstArg());

        // Metadata call
        metadataCall = new MetadataCall(
                rxAPICallExecutor,
                systemInfoDownloader,
                systemSettingDownloader,
                userDownloader,
                categoryDownloader,
                programDownloader,
                organisationUnitDownloader,
                dataSetDownloader,
                constantDownloader,
                smsModule,
                databaseAdapter,
                generalSettingCall,
                multiUserDatabaseManager,
                credentialsSecureStore);
    }

    @Test
    public void succeed_when_endpoint_calls_succeed() {
        metadataCall.blockingDownload();
    }

    @Test
    public void fail_when_system_info_call_fail() {
        when(systemInfoDownloader.downloadMetadata()).thenReturn(Completable.error(d2Error));
        downloadAndAssertError();
    }

    @Test
    public void fail_when_system_setting_call_fail() {
        when(systemSettingDownloader.downloadMetadata()).thenReturn(Completable.error(d2Error));
        downloadAndAssertError();
    }

    private void downloadAndAssertError() {
        TestObserver<D2Progress> testObserver = metadataCall.download().test();
        testObserver.assertError(D2Error.class);
        testObserver.dispose();
    }

    @Test
    public void fail_when_user_call_fail() {
        when(userDownloader.downloadMetadata()).thenReturn(Single.error(d2Error));
        downloadAndAssertError();
    }

    @Test
    public void fail_when_category_download_call_fail() {
        when(categoryDownloader.downloadMetadata()).thenReturn(Completable.error(d2Error));
        downloadAndAssertError();
    }

    @Test
    public void fail_when_program_call_fail() {
        when(programDownloader.downloadMetadata(anySet())).thenReturn(Single.error(d2Error));
        downloadAndAssertError();
    }

    @Test
    public void fail_when_organisation_unit_call_fail() {
        when(organisationUnitDownloader.downloadMetadata(user)).thenReturn(Single.error(d2Error));
        downloadAndAssertError();
    }

    @Test
    public void fail_when_dataset_parent_call_fail() {
        when(dataSetDownloader.downloadMetadata(anySet())).thenReturn(Single.error(d2Error));
        downloadAndAssertError();
    }

    @Test
    public void fail_when_constant_call_fail() {
        when(constantDownloader.downloadMetadata()).thenReturn(Single.error(d2Error));
        downloadAndAssertError();
    }

    @Test
    public void call_wrapObservableTransactionally() {
        metadataCall.blockingDownload();
        verify(rxAPICallExecutor).wrapObservableTransactionally(any(), eq(true));
    }

    @Test
    public void delete_foreign_key_violations_before_calls() {
        metadataCall.blockingDownload();
        verify(databaseAdapter).delete(ForeignKeyViolationTableInfo.TABLE_INFO.name());
    }
}