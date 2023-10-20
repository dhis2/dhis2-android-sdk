/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
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
import org.hisp.dhis.android.core.expressiondimensionitem.ExpressionDimensionItem
import org.hisp.dhis.android.core.expressiondimensionitem.internal.ExpressionDimensionItemModuleDownloader
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.internal.IndicatorModuleDownloader
import org.hisp.dhis.android.core.legendset.LegendSet
import org.hisp.dhis.android.core.legendset.internal.LegendSetModuleDownloader
import org.hisp.dhis.android.core.maintenance.D2Error
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
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader
import org.hisp.dhis.android.core.usecase.UseCaseModuleDownloader
import org.hisp.dhis.android.core.usecase.stock.StockUseCase
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.internal.UserModuleDownloader
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.internal.VisualizationModuleDownloader
import org.koin.core.annotation.Singleton

@Suppress("LongParameterList")
@Singleton
internal class MetadataCall(
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val systemInfoDownloader: SystemInfoModuleDownloader,
    private val systemSettingDownloader: SettingModuleDownloader,
    private val useCaseDownloader: UseCaseModuleDownloader,
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
    private val legendSetModuleDownloader: LegendSetModuleDownloader,
    private val expressionDimensionItemModuleDownloader: ExpressionDimensionItemModuleDownloader,
) {

    companion object {
        const val CALLS_COUNT = 15
    }

    @Suppress("TooGenericExceptionCaught")
    fun download(): Flow<D2Progress> = channelFlow {
        val progressManager = D2ProgressManager(CALLS_COUNT)

        changeEncryptionIfRequiredCoroutines()

        coroutineAPICallExecutor.wrapTransactionally(cleanForeignKeyErrors = true) {
            try {
                systemInfoDownloader.downloadWithProgressManager(progressManager).also { send(it) }
                executeIndependentCalls(progressManager).collect { send(it) }
                executeUserCallAndChildren(progressManager).collect { send(it) }
            } catch (e: Exception) {
                if (e !is D2Error && e.cause is D2Error) {
                    throw e.cause!!
                } else {
                    throw e
                }
            }
        }
    }

    private fun executeIndependentCalls(progressManager: D2ProgressManager): Flow<D2Progress> = flow {
        databaseAdapter.delete(ForeignKeyViolationTableInfo.TABLE_INFO.name())

        systemSettingDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(SystemSetting::class.java, false))

        useCaseDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(StockUseCase::class.java, false))

        constantModuleDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(Constant::class.java, false))

        smsModule.configCase().refreshMetadataIdsCallable().blockingAwait()
        emit(progressManager.increaseProgress(SmsModule::class.java, false))
    }

    private fun executeUserCallAndChildren(progressManager: D2ProgressManager): Flow<D2Progress> = flow {
        val user = userModuleDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(User::class.java, false))

        organisationUnitModuleDownloader.downloadMetadata(user).blockingAwait()
        emit(progressManager.increaseProgress(OrganisationUnit::class.java, false))

        programDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(Program::class.java, false))

        dataSetDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(DataSet::class.java, false))

        categoryDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(Category::class.java, false))

        visualizationDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(Visualization::class.java, false))

        programIndicatorModuleDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(ProgramIndicator::class.java, false))

        indicatorModuleDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(Indicator::class.java, false))

        legendSetModuleDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(LegendSet::class.java, false))

        expressionDimensionItemModuleDownloader.downloadMetadata()
        emit(progressManager.increaseProgress(ExpressionDimensionItem::class.java, true))
    }

    private suspend fun changeEncryptionIfRequiredCoroutines() {
        // TODO explore the possibility of "CoroutineExceptionHandler"

        try {
            val encrypt = generalSettingCall.isDatabaseEncrypted()
            multiUserDatabaseManager.changeEncryptionIfRequired(
                credentialsSecureStore.get(),
                encrypt,
            )
        } catch (ignored: Exception) {
        }
    }

    fun blockingDownload() {
        runBlocking { download().collect() }
    }
}
