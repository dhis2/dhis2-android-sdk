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

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.internal.CategoryModuleDownloader
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.constant.internal.ConstantModuleDownloader
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.internal.DataSetModuleDownloader
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.internal.IndicatorModuleDownloader
import org.hisp.dhis.android.core.legendset.LegendSet
import org.hisp.dhis.android.core.legendset.internal.LegendSetModuleDownloader
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolationTableInfo
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleDownloader
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorModuleDownloader
import org.hisp.dhis.android.core.program.internal.ProgramModuleDownloader
import org.hisp.dhis.android.core.settings.SystemSetting
import org.hisp.dhis.android.core.settings.internal.GeneralSettingCall
import org.hisp.dhis.android.core.settings.internal.SettingModuleDownloader
import org.hisp.dhis.android.core.sms.SmsModule
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.UserModuleDownloader
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.internal.VisualizationModuleDownloader

@Suppress("LongParameterList")
@Reusable
internal class MetadataCall @Inject constructor(
    private val rxCallExecutor: RxAPICallExecutor,
    private val systemInfoDownloader: SystemInfoModuleDownloader,
    private val systemSettingDownloader: SettingModuleDownloader,
    private val userModuleDownloader: UserModuleDownloader,
    private val categoryDownloader: CategoryModuleDownloader,
    private val programDownloader: ProgramModuleDownloader,
    private val organisationUnitModuleDownloader: OrganisationUnitModuleDownloader,
    private val dataSetDownloader: DataSetModuleDownloader,
    private val visualizationDownloader: VisualizationModuleDownloader,
    private val constantModuleDownloader: ConstantModuleDownloader,
    private val indicatorModuleDownloader: IndicatorModuleDownloader,
    private val programIndicatorModuleDownloader: ProgramIndicatorModuleDownloader,
    private val smsModule: SmsModule,
    private val databaseAdapter: DatabaseAdapter,
    private val generalSettingCall: GeneralSettingCall,
    private val multiUserDatabaseManager: MultiUserDatabaseManager,
    private val credentialsSecureStore: CredentialsSecureStore,
    private val legendSetModuleDownloader: LegendSetModuleDownloader
) {

    companion object {
        const val CALLS_COUNT = 11
    }

    fun download(): Observable<D2Progress> {
        val progressManager = D2ProgressManager(CALLS_COUNT)
        return changeEncryptionIfRequired().andThen(
            rxCallExecutor.wrapObservableTransactionally(
                Observable.merge(
                    systemInfoDownloader.downloadWithProgressManager(progressManager),
                    executeIndependentCalls(progressManager),
                    executeUserCallAndChildren(progressManager)
                ),
                true
            )
        )
    }

    private fun executeIndependentCalls(progressManager: D2ProgressManager): Observable<D2Progress> {
        return Single.merge(
            Single.fromCallable {
                databaseAdapter.delete(ForeignKeyViolationTableInfo.TABLE_INFO.name())
                progressManager.increaseProgress(SystemInfo::class.java, false)
            },
            systemSettingDownloader.downloadMetadata().toSingle {
                progressManager.increaseProgress(SystemSetting::class.java, false)
            },
            constantModuleDownloader.downloadMetadata().map {
                progressManager.increaseProgress(Constant::class.java, false)
            },
            smsModule.configCase().refreshMetadataIdsCallable().toSingle {
                progressManager.increaseProgress(SmsModule::class.java, false)
            }
        ).toObservable()
    }

    private fun executeUserCallAndChildren(progressManager: D2ProgressManager): Observable<D2Progress> {
        return userModuleDownloader.downloadMetadata()
            .flatMapCompletable { user: User ->
                organisationUnitModuleDownloader.downloadMetadata(user)
            }.andThen(
                Single.concatArray(
                    Single.just(progressManager.increaseProgress(User::class.java, false)),
                    Single.just(progressManager.increaseProgress(OrganisationUnit::class.java, false)),
                    programDownloader.downloadMetadata().toSingle {
                        progressManager.increaseProgress(Program::class.java, false)
                    },
                    dataSetDownloader.downloadMetadata().toSingle {
                        progressManager.increaseProgress(DataSet::class.java, false)
                    },
                    categoryDownloader.downloadMetadata().toSingle {
                        progressManager.increaseProgress(Category::class.java, false)
                    },
                    visualizationDownloader.downloadMetadata().map {
                        progressManager.increaseProgress(Visualization::class.java, false)
                    },
                    programIndicatorModuleDownloader.downloadMetadata().toSingle {
                        progressManager.increaseProgress(ProgramIndicator::class.java, false)
                    },
                    indicatorModuleDownloader.downloadMetadata().toSingle {
                        progressManager.increaseProgress(Indicator::class.java, false)
                    },
                    legendSetModuleDownloader.downloadMetadata().toSingle {
                        progressManager.increaseProgress(LegendSet::class.java, false)
                    }
                ).toObservable()
            )
    }

    private fun changeEncryptionIfRequired(): Completable {
        return generalSettingCall.isDatabaseEncrypted()
            .doOnSuccess { encrypt: Boolean ->
                multiUserDatabaseManager.changeEncryptionIfRequired(credentialsSecureStore.get(), encrypt)
            }
            .ignoreElement()
            .onErrorComplete()
    }

    fun blockingDownload() {
        download().blockingSubscribe()
    }
}
