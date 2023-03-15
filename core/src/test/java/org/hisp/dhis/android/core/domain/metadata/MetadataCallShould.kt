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
package org.hisp.dhis.android.core.domain.metadata

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.BaseD2Progress
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.category.internal.CategoryModuleDownloader
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.constant.internal.ConstantModuleDownloader
import org.hisp.dhis.android.core.dataset.internal.DataSetModuleDownloader
import org.hisp.dhis.android.core.indicator.internal.IndicatorModuleDownloader
import org.hisp.dhis.android.core.legendset.internal.LegendSetModuleDownloader
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolationTableInfo
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleDownloader
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorModuleDownloader
import org.hisp.dhis.android.core.program.internal.ProgramModuleDownloader
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall
import org.hisp.dhis.android.core.settings.internal.SettingModuleDownloader
import org.hisp.dhis.android.core.sms.SmsModule
import org.hisp.dhis.android.core.sms.domain.interactor.ConfigCase
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.UserModuleDownloader
import org.hisp.dhis.android.core.visualization.internal.VisualizationModuleDownloader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.AdditionalAnswers
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
    private val visualizationDownloader: VisualizationModuleDownloader = mock()
    private val constantDownloader: ConstantModuleDownloader = mock()
    private val indicatorDownloader: IndicatorModuleDownloader = mock()
    private val programIndicatorModuleDownloader: ProgramIndicatorModuleDownloader = mock()
    private val smsModule: SmsModule = mock()
    private val configCase: ConfigCase = mock()
    private val generalSettingCall: GeneralSettingCall = mock()
    private val multiUserDatabaseManager: MultiUserDatabaseManager = mock()
    private val credentialsSecureStore: CredentialsSecureStore = mock()
    private val legendSetModuleDownloader: LegendSetModuleDownloader = mock()

    // object to test
    private var metadataCall: MetadataCall? = null

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        // Calls
        whenever(systemInfoDownloader.downloadWithProgressManager(any()))
            .thenReturn(Observable.just(BaseD2Progress.empty(10)))
        whenever(systemSettingDownloader.downloadMetadata()).thenReturn(Completable.complete())
        whenever(userDownloader.downloadMetadata()).thenReturn(Single.just(user))
        whenever(programDownloader.downloadMetadata()).thenReturn(
            Completable.complete()
        )
        whenever(organisationUnitDownloader.downloadMetadata(same(user))).thenReturn(
            Completable.complete()
        )
        whenever(dataSetDownloader.downloadMetadata()).thenReturn(
            Completable.complete()
        )
        whenever(programIndicatorModuleDownloader.downloadMetadata()).thenReturn(Completable.complete())
        whenever(visualizationDownloader.downloadMetadata()).thenReturn(
            Single.just(emptyList())
        )
        whenever(legendSetModuleDownloader.downloadMetadata()).thenReturn(Completable.complete())
        whenever(constantDownloader.downloadMetadata()).thenReturn(Single.just(emptyList()))
        whenever(indicatorDownloader.downloadMetadata()).thenReturn(Completable.complete())
        whenever(categoryDownloader.downloadMetadata()).thenReturn(Completable.complete())
        whenever(smsModule.configCase()).thenReturn(configCase)
        whenever(configCase.refreshMetadataIdsCallable()).thenReturn(Completable.complete())
        whenever(generalSettingCall.isDatabaseEncrypted()).thenReturn(Single.just(false))
        Mockito.`when`<Observable<D2Progress>>(
            rxAPICallExecutor.wrapObservableTransactionally(
                any(),
                any()
            )
        ).then(AdditionalAnswers.returnsFirstArg<Any>())

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
            visualizationDownloader,
            constantDownloader,
            indicatorDownloader,
            programIndicatorModuleDownloader,
            smsModule,
            databaseAdapter,
            generalSettingCall,
            multiUserDatabaseManager,
            credentialsSecureStore,
            legendSetModuleDownloader,
        )
    }

    @Test
    fun succeed_when_endpoint_calls_succeed() {
        metadataCall!!.blockingDownload()
    }

    @Test
    fun fail_when_system_info_call_fail() {
        whenever(systemInfoDownloader.downloadWithProgressManager(any())).thenReturn(Observable.error(d2Error))
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
    fun fail_when_visualization_download_call_fail() {
        whenever(visualizationDownloader.downloadMetadata()).thenReturn(Single.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun fail_when_program_call_fail() {
        whenever(programDownloader.downloadMetadata()).thenReturn(Completable.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun fail_when_organisation_unit_call_fail() {
        whenever(organisationUnitDownloader.downloadMetadata(user)).thenReturn(Completable.error(d2Error))
        downloadAndAssertError()
    }

    @Test
    fun fail_when_dataset_parent_call_fail() {
        whenever(dataSetDownloader.downloadMetadata()).thenReturn(Completable.error(d2Error))
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
        Mockito.verify(rxAPICallExecutor).wrapObservableTransactionally<D2Progress>(
            any(),
            eq(true)
        )
    }

    @Test
    fun delete_foreign_key_violations_before_calls() {
        metadataCall!!.blockingDownload()
        Mockito.verify(databaseAdapter).delete(ForeignKeyViolationTableInfo.TABLE_INFO.name())
    }
}
