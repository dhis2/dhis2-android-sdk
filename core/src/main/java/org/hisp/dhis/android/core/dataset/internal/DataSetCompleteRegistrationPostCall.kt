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
package org.hisp.dhis.android.core.dataset.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.net.HttpURLConnection
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.errorIfOnline
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.forcedOrOwn
import org.hisp.dhis.android.core.category.CategoryOptionComboCollectionRepository
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummaryWebResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager

@Reusable
internal class DataSetCompleteRegistrationPostCall @Inject constructor(
    private val dataSetCompleteRegistrationService: DataSetCompleteRegistrationService,
    private val dataSetCompleteRegistrationImportHandler: DataSetCompleteRegistrationImportHandler,
    private val apiCallExecutor: APICallExecutor,
    private val categoryOptionComboCollectionRepository: CategoryOptionComboCollectionRepository,
    private val dataSetCompleteRegistrationStore: DataSetCompleteRegistrationStore,
    private val versionManager: DHISVersionManager
) {
    fun uploadDataSetCompleteRegistrations(
        dataSetCompleteRegistrations: List<DataSetCompleteRegistration>
    ): Observable<D2Progress> {
        return Observable.defer {
            if (dataSetCompleteRegistrations.isEmpty()) {
                return@defer Observable.empty<D2Progress>()
            } else {
                val toPostDataSetCompleteRegistrations: MutableList<DataSetCompleteRegistration> = ArrayList()
                val toDeleteDataSetCompleteRegistrations: MutableList<DataSetCompleteRegistration> = ArrayList()
                for (dscr in dataSetCompleteRegistrations) {
                    if (dscr.deleted()!!) {
                        toDeleteDataSetCompleteRegistrations.add(dscr)
                    } else {
                        toPostDataSetCompleteRegistrations.add(dscr)
                    }
                }
                val progressManager = D2ProgressManager(1)
                return@defer Observable.create { emitter: ObservableEmitter<D2Progress> ->
                    uploadInternal(
                        progressManager,
                        emitter,
                        toPostDataSetCompleteRegistrations,
                        toDeleteDataSetCompleteRegistrations
                    )
                }
            }
        }
    }

    @Throws(D2Error::class)
    private fun uploadInternal(
        progressManager: D2ProgressManager,
        emitter: ObservableEmitter<D2Progress>,
        toPostDataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
        toDeleteDataSetCompleteRegistrations: List<DataSetCompleteRegistration>
    ) {
        val payload = DataSetCompleteRegistrationPayload(toPostDataSetCompleteRegistrations)
        val dataValueImportSummary: DataValueImportSummary =
            if (toPostDataSetCompleteRegistrations.isEmpty()) {
                DataValueImportSummary.EMPTY
            } else {
                markObjectsAs(toPostDataSetCompleteRegistrations, State.UPLOADING)
                try {
                    postCompleteRegistrations(payload)!!
                } catch (e: D2Error) {
                    markObjectsAs(toPostDataSetCompleteRegistrations, errorIfOnline(e))
                    throw e
                }
            }

        val deletedDataSetCompleteRegistrations: MutableList<DataSetCompleteRegistration> = ArrayList()
        val withErrorDataSetCompleteRegistrations: MutableList<DataSetCompleteRegistration> = ArrayList()
        for (dataSetCompleteRegistration in toDeleteDataSetCompleteRegistrations) {
            try {
                val coc = categoryOptionComboCollectionRepository
                    .withCategoryOptions()
                    .uid(dataSetCompleteRegistration.attributeOptionCombo())
                    .blockingGet()
                markObjectsAs(toDeleteDataSetCompleteRegistrations, State.UPLOADING)
                apiCallExecutor.executeObjectCallWithEmptyResponse(
                    dataSetCompleteRegistrationService.deleteDataSetCompleteRegistration(
                        dataSetCompleteRegistration.dataSet(),
                        dataSetCompleteRegistration.period(),
                        dataSetCompleteRegistration.organisationUnit(),
                        coc.categoryCombo()!!.uid(),
                        CollectionsHelper.semicolonSeparatedCollectionValues(getUids(coc.categoryOptions()!!)),
                        false
                    )
                )
                deletedDataSetCompleteRegistrations.add(dataSetCompleteRegistration)
            } catch (d2Error: D2Error) {
                withErrorDataSetCompleteRegistrations.add(dataSetCompleteRegistration)
            }
        }
        dataSetCompleteRegistrationImportHandler.handleImportSummary(
            payload, dataValueImportSummary, deletedDataSetCompleteRegistrations,
            withErrorDataSetCompleteRegistrations
        )
        emitter.onNext(progressManager.increaseProgress(DataSetCompleteRegistration::class.java, true))
        emitter.onComplete()
    }

    private fun postCompleteRegistrations(payload: DataSetCompleteRegistrationPayload): DataValueImportSummary? {
        return if (versionManager.isGreaterOrEqualThan(DHISVersion.V2_38)) {
            apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                dataSetCompleteRegistrationService.postDataSetCompleteRegistrationsWebResponse(payload),
                listOf(HttpURLConnection.HTTP_CONFLICT),
                DataValueImportSummaryWebResponse::class.java
            ).response()
        } else {
            apiCallExecutor.executeObjectCall(
                dataSetCompleteRegistrationService.postDataSetCompleteRegistrations(payload)
            )
        }
    }

    private fun markObjectsAs(
        dataSetCompleteRegistrations: Collection<DataSetCompleteRegistration>,
        forcedState: State?
    ) {
        for (dscr in dataSetCompleteRegistrations) {
            dataSetCompleteRegistrationStore.setState(dscr, forcedOrOwn(dscr, forcedState))
        }
    }
}
