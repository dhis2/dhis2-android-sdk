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
package org.hisp.dhis.android.core.dataset.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.internal.HttpStatusCodes
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.errorIfOnline
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.forcedOrOwn
import org.hisp.dhis.android.core.category.CategoryOptionComboCollectionRepository
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.koin.core.annotation.Singleton
import java.net.HttpURLConnection

@Singleton
internal class DataSetCompleteRegistrationPostCall(
    private val networkHandler: DataSetCompleteRegistrationNetworkHandler,
    private val dataSetCompleteRegistrationImportHandler: DataSetCompleteRegistrationImportHandler,
    private val categoryOptionComboCollectionRepository: CategoryOptionComboCollectionRepository,
    private val dataSetCompleteRegistrationStore: DataSetCompleteRegistrationStore,
    private val versionManager: DHISVersionManager,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) {
    fun uploadDataSetCompleteRegistrations(
        dataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
    ): Flow<D2Progress> {
        if (dataSetCompleteRegistrations.isEmpty()) {
            return emptyFlow()
        }
        val toPostDataSetCompleteRegistrations = dataSetCompleteRegistrations.filterNot { it.deleted() == true }
        val toDeleteDataSetCompleteRegistrations = dataSetCompleteRegistrations.filter { it.deleted() == true }
        val progressManager = D2ProgressManager(1)
        return uploadInternal(
            progressManager,
            toPostDataSetCompleteRegistrations,
            toDeleteDataSetCompleteRegistrations,
        )
    }

    @Throws(D2Error::class)
    private fun uploadInternal(
        progressManager: D2ProgressManager,
        toPostDataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
        toDeleteDataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
    ) = flow {
        val dataValueImportSummary: DataValueImportSummary =
            if (toPostDataSetCompleteRegistrations.isEmpty()) {
                DataValueImportSummary.EMPTY
            } else {
                markObjectsAs(toPostDataSetCompleteRegistrations, State.UPLOADING)
                try {
                    postCompleteRegistrations(toPostDataSetCompleteRegistrations).getOrThrow()
                } catch (e: D2Error) {
                    markObjectsAs(toPostDataSetCompleteRegistrations, errorIfOnline(e))
                    throw e
                }
            }

        val deletedDataSetCompleteRegistrations: MutableList<DataSetCompleteRegistration> = ArrayList()
        val withErrorDataSetCompleteRegistrations: MutableList<DataSetCompleteRegistration> = ArrayList()
        for (dataSetCompleteRegistration in toDeleteDataSetCompleteRegistrations) {
            val coc = categoryOptionComboCollectionRepository
                .withCategoryOptions()
                .uid(dataSetCompleteRegistration.attributeOptionCombo())
                .blockingGet()
            markObjectsAs(toDeleteDataSetCompleteRegistrations, State.UPLOADING)
            coroutineAPICallExecutor.wrap {
                networkHandler.deleteDataSetCompleteRegistration(
                    dataSetCompleteRegistration.dataSet(),
                    dataSetCompleteRegistration.period(),
                    dataSetCompleteRegistration.organisationUnit(),
                    coc!!.categoryCombo()!!.uid(),
                    CollectionsHelper.semicolonSeparatedCollectionValues(getUids(coc.categoryOptions()!!)),
                    false,
                )
            }.fold(
                onSuccess = { result ->
                    if (result.status.value in HttpStatusCodes.SUCCESS_MIN..HttpStatusCodes.SUCCESS_MAX) {
                        deletedDataSetCompleteRegistrations.add(dataSetCompleteRegistration)
                    } else {
                        withErrorDataSetCompleteRegistrations.add(dataSetCompleteRegistration)
                    }
                },

                onFailure = {
                    withErrorDataSetCompleteRegistrations.add(dataSetCompleteRegistration)
                },
            )
        }
        dataSetCompleteRegistrationImportHandler.handleImportSummary(
            toPostDataSetCompleteRegistrations,
            dataValueImportSummary,
            deletedDataSetCompleteRegistrations,
            withErrorDataSetCompleteRegistrations,
        )
        emit(progressManager.increaseProgress(DataSetCompleteRegistration::class.java, true))
    }

    private suspend fun postCompleteRegistrations(
        dataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
    ): Result<DataValueImportSummary, D2Error> {
        return coroutineAPICallExecutor.wrap(
            acceptedErrorCodes = listOf(HttpURLConnection.HTTP_CONFLICT),
            errorClass = DataValueImportSummary::class.java,
        ) {
            networkHandler.postDataSetCompleteRegistrations(dataSetCompleteRegistrations)
        }
    }

    private fun markObjectsAs(
        dataSetCompleteRegistrations: Collection<DataSetCompleteRegistration>,
        forcedState: State?,
    ) {
        for (dscr in dataSetCompleteRegistrations) {
            dataSetCompleteRegistrationStore.setState(dscr, forcedOrOwn(dscr, forcedState))
        }
    }
}
