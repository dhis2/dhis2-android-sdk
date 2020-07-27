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
package org.hisp.dhis.android.core.domain.metadata

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.assertj.core.util.Lists
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.category.internal.CategoryModuleDownloader
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.constant.internal.ConstantModuleDownloader
import org.hisp.dhis.android.core.dataset.internal.DataSetModuleDownloader
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolationTableInfo
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleDownloader
import org.hisp.dhis.android.core.program.internal.ProgramModuleDownloader
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall
import org.hisp.dhis.android.core.settings.internal.SettingModuleDownloader
import org.hisp.dhis.android.core.sms.SmsModule
import org.hisp.dhis.android.core.sms.domain.interactor.ConfigCase
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.UserModuleDownloader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.AdditionalAnswers
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(JUnit4::class)
class MetadataCallShould : BaseCallShould() {
    private val user: User = mock()
    private val rxAPICallExecutor: RxAPICallExecutor = mock()
    private val systemInfoDownloader: SystemInfoModuleDownloader = mock()
    private val systemSettingDownloader: SettingModuleDownloader = mock()
    private val userDownloader: UserModuleDownloader = mock()
    private val categoryDownloader: CategoryModuleDownloader = mock()
    private val programDownloader: ProgramModuleDownloader = mock()
    private val organisationUnitDownloader: OrganisationUnitModuleDownloader = mock()
    private val dataSetDownloader: DataSetModuleDownloader = mock()
    private val constantDownloader: ConstantModuleDownloader = mock()
    private val d2ErrorStore: ObjectStore<D2Error> = mock()
    private val smsModule: SmsModule = mock()
    private val configCase: ConfigCase = mock()
    private val generalSettingCall: GeneralSettingCall = mock()
    private val multiUserDatabaseManager: MultiUserDatabaseManager = mock()
    private val credentialsSecureStore: ObjectKeyValueStore<Credentials> = mock()

    // object to test
    private var metadataCall: MetadataCall? = null

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        // Calls
        whenever(systemInfoDownloader.downloadMetadata()).thenReturn(Completable.complete())
        whenever(systemSettingDownloader.downloadMetadata()).thenReturn(Completable.complete())
        whenever(userDownloader.downloadMetadata()).thenReturn(Single.just(user))
        whenever(programDownloader.downloadMetadata(ArgumentMatchers.anySet())).thenReturn(Single.just(Lists.emptyList()))
        whenever(organisationUnitDownloader.downloadMetadata(ArgumentMatchers.same(user))).thenReturn(Single.just(Lists.emptyList()))
        whenever(dataSetDownloader.downloadMetadata(ArgumentMatchers.anySet())).thenReturn(Single.just(Lists.emptyList()))
        whenever(constantDownloader.downloadMetadata()).thenReturn(Single.just(Lists.emptyList()))
        whenever(categoryDownloader.downloadMetadata()).thenReturn(Completable.complete())
        whenever(smsModule.configCase()).thenReturn(configCase)
        whenever(configCase.refreshMetadataIdsCallable()).thenReturn(Completable.complete())
        whenever(generalSettingCall.isDatabaseEncrypted).thenReturn(Single.just(false))
        whenever(d2ErrorStore.insert(ArgumentMatchers.any(D2Error::class.java))).thenReturn(0L)
        Mockito.`when`<Observable<D2Progress>>(rxAPICallExecutor.wrapObservableTransactionally(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean()))
                .then(AdditionalAnswers.returnsFirstArg<Any>())

        // Metadata call
        metadataCall = MetadataCall(
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
                credentialsSecureStore)
    }

    @Test
    fun succeed_when_endpoint_calls_succeed() {
        metadataCall!!.blockingDownload()
    }

    @Test
    fun fail_when_system_info_call_fail() {
        whenever(systemInfoDownloader.downloadMetadata()).thenReturn(Completable.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun fail_when_system_setting_call_fail() {
        whenever(systemSettingDownloader.downloadMetadata()).thenReturn(Completable.error(d2Error))
        downloadAndAssertError()
    }

    private fun downloadAndAssertError() {
        val testObserver = metadataCall!!.download().test()
        testObserver.assertError(D2Error::class.java)
        testObserver.dispose()
    }

    @Test
    fun fail_when_user_call_fail() {
        whenever(userDownloader.downloadMetadata()).thenReturn(Single.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun fail_when_category_download_call_fail() {
        whenever(categoryDownloader.downloadMetadata()).thenReturn(Completable.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun fail_when_program_call_fail() {
        whenever(programDownloader.downloadMetadata(ArgumentMatchers.anySet())).thenReturn(Single.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun fail_when_organisation_unit_call_fail() {
        whenever(organisationUnitDownloader.downloadMetadata(user)).thenReturn(Single.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun fail_when_dataset_parent_call_fail() {
        whenever(dataSetDownloader.downloadMetadata(ArgumentMatchers.anySet())).thenReturn(Single.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun fail_when_constant_call_fail() {
        whenever(constantDownloader.downloadMetadata()).thenReturn(Single.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun call_wrapObservableTransactionally() {
        metadataCall!!.blockingDownload()
        Mockito.verify(rxAPICallExecutor).wrapObservableTransactionally<D2Progress>(ArgumentMatchers.any(), ArgumentMatchers.eq(true))
    }

    @Test
    fun delete_foreign_key_violations_before_calls() {
        metadataCall!!.blockingDownload()
        Mockito.verify(databaseAdapter).delete(ForeignKeyViolationTableInfo.TABLE_INFO.name())
    }
}