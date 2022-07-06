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
package org.hisp.dhis.android.core.datavalue.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.net.HttpURLConnection
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.errorIfOnline
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.forcedOrOwn
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummaryWebResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager

@Reusable
internal class DataValuePostCall @Inject constructor(
    private val dataValueService: DataValueService,
    private val dataValueImportHandler: DataValueImportHandler,
    private val fileResourcePostCall: DataValueFileResourcePostCall,
    private val apiCallExecutor: APICallExecutor,
    private val dataValueStore: DataValueStore,
    private val versionManager: DHISVersionManager
) {
    fun uploadDataValues(dataValues: List<DataValue>): Observable<D2Progress> {
        return Observable.defer {
            if (dataValues.isEmpty()) {
                return@defer Observable.empty<D2Progress>()
            } else {
                val progressManager = D2ProgressManager(1)
                return@defer Observable.create { emitter: ObservableEmitter<D2Progress> ->
                    val result = fileResourcePostCall.uploadFileResource(dataValues)
                    val validDataValues = result.dataValues

                    markObjectsAs(validDataValues, State.UPLOADING)
                    try {
                        val dataValueSet = DataValueSet(validDataValues)
                        val dataValueImportSummary = executePostCall(dataValueSet)
                        dataValueImportHandler.handleImportSummary(dataValueSet, dataValueImportSummary)
                        fileResourcePostCall.updateFileResourceStates(result.fileResources)
                    } catch (e: D2Error) {
                        markObjectsAs(validDataValues, errorIfOnline(e))
                        fileResourcePostCall.updateFileResourceStates(result.fileResources)
                        throw e
                    }
                    emitter.onNext(progressManager.increaseProgress(DataValue::class.java, true))
                    emitter.onComplete()
                }
            }
        }
    }

    private fun executePostCall(dataValueSet: DataValueSet): DataValueImportSummary? {
        return if (versionManager.isGreaterOrEqualThan(DHISVersion.V2_38)) {
            apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                dataValueService.postDataValuesWebResponse(dataValueSet),
                listOf(HttpURLConnection.HTTP_CONFLICT),
                DataValueImportSummaryWebResponse::class.java
            ).response()
        } else {
            apiCallExecutor.executeObjectCall(
                dataValueService.postDataValues(dataValueSet)
            )
        }
    }

    private fun markObjectsAs(dataValues: Collection<DataValue?>, forcedState: State?) {
        for (dataValue in dataValues) {
            dataValueStore.setState(dataValue, forcedOrOwn(dataValue!!, forcedState))
        }
    }
}
