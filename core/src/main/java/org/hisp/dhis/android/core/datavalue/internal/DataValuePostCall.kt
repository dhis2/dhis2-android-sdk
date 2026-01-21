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
package org.hisp.dhis.android.core.datavalue.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.errorIfOnline
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.forcedOrOwn
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueInternalAccessor
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class DataValuePostCall(
    private val networkHandler: DataValueNetworkHandler,
    private val dataValueImportHandler: DataValueImportHandler,
    private val fileResourcePostCall: DataValueFileResourcePostCall,
    private val dataValueStore: DataValueStore,
    private val versionManager: DHISVersionManagerImpl,
) {
    fun uploadDataValues(dataValues: List<DataValue>): Flow<D2Progress> = flow {
        if (dataValues.isEmpty()) {
            return@flow
        }

        val progressManager = D2ProgressManager(1)
        val result = fileResourcePostCall.uploadFileResource(dataValues)
        val validDataValues = result.dataValues

        try {
            if (versionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_39)) {
                uploadByDataSet(validDataValues)
            } else {
                uploadLegacy(validDataValues)
            }
        } finally {
            fileResourcePostCall.updateFileResourceStates(result.fileResources)
        }
        emit(progressManager.increaseProgress(DataValue::class.java, true))
    }

    private suspend fun uploadByDataSet(dataValues: List<DataValue>) {
        var lastError: D2Error? = null

        dataValues
            .groupBy { DataValueInternalAccessor.accessSourceDataSet(it) }
            .forEach { (dataSetUid, values) ->
                lastError = uploadDataValueSet(DataValueSet(dataValues = values, dataSet = dataSetUid)) ?: lastError
            }

        lastError?.let { throw it }
    }

    private suspend fun uploadDataValueSet(dataValueSet: DataValueSet): D2Error? {
        return try {
            markObjectsAs(dataValueSet.dataValues, State.UPLOADING)

            var error: D2Error? = null
            networkHandler.postDataValuesWebResponse(dataValueSet).fold(
                onSuccess = { dataValueImportHandler.handleImportSummary(dataValueSet, it.response) },
                onFailure = {
                    markObjectsAs(dataValueSet.dataValues, errorIfOnline(it))
                    error = it
                },
            )
            error
        } catch (e: D2Error) {
            markObjectsAs(dataValueSet.dataValues, errorIfOnline(e))
            e
        }
    }

    private suspend fun uploadLegacy(dataValues: List<DataValue>) {
        markObjectsAs(dataValues, State.UPLOADING)
        val dataValueSet = DataValueSet(dataValues)

        val result = if (versionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_38)) {
            networkHandler.postDataValuesWebResponse(dataValueSet).map { it.response }
        } else {
            networkHandler.postDataValues(dataValueSet)
        }

        result.fold(
            onSuccess = { dataValueImportHandler.handleImportSummary(dataValueSet, it) },
            onFailure = {
                markObjectsAs(dataValues, errorIfOnline(it))
                throw it
            },
        )
    }

    private suspend fun markObjectsAs(dataValues: Collection<DataValue>, forcedState: State?) {
        val updatedDataValues = dataValues.map { dataValue ->
            dataValue.toBuilder().syncState(forcedOrOwn(dataValue, forcedState)).build()
        }
        dataValueStore.update(updatedDataValues)
    }
}
