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

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.errorIfOnline
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper.forcedOrOwn
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.ImportCount
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

        markObjectsAs(validDataValues, State.UPLOADING)
        try {
            val dataValueSet = DataValueSet(validDataValues)
            executePostCall(dataValueSet).fold(
                onSuccess = {
                    dataValueImportHandler.handleImportSummary(dataValueSet, it)
                },
                onFailure = {
                    throw it
                },
            )
        } catch (e: D2Error) {
            markObjectsAs(validDataValues, errorIfOnline(e))
            throw e
        } finally {
            fileResourcePostCall.updateFileResourceStates(result.fileResources)
        }
        emit(progressManager.increaseProgress(DataValue::class.java, true))
    }

    private suspend fun executePostCall(dataValueSet: DataValueSet): Result<DataValueImportSummary, D2Error> {
        return if (versionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_39)) {
            uploadByDataSet(dataValueSet)
        } else if (versionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_38)) {
            networkHandler.postDataValuesWebResponse(dataValueSet).map { it.response }
        } else {
            networkHandler.postDataValues(dataValueSet)
        }
    }

    private suspend fun uploadByDataSet(dataValueSet: DataValueSet): Result<DataValueImportSummary, D2Error> {
        val groupedByDataSet = dataValueSet.dataValues.groupBy { it.dataSet() }

        // Handle values without dataSet (legacy data) - log warning but still attempt upload
        val valuesWithoutDataSet = groupedByDataSet[null] ?: emptyList()
        if (valuesWithoutDataSet.isNotEmpty()) {
            Log.w("DataValuePostCall", "Found ${valuesWithoutDataSet.size} DataValue(s) without dataSet. " +
                    "This may fail on DHIS2 v43+ servers.",
                )
        }

        var combinedSummary: DataValueImportSummary? = null
        for ((dataSetUid, values) in groupedByDataSet) {
            val setWithDataSet = DataValueSet(
                dataValues = values,
                dataSet = dataSetUid,
            )
            val result = networkHandler.postDataValuesWithDataSet(setWithDataSet)

            result.fold(
                onSuccess = { webResponse ->
                    combinedSummary = mergeSummaries(combinedSummary, webResponse.response)
                },
                onFailure = {
                    return Result.Failure(it)
                },
            )
        }

        return Result.Success(combinedSummary ?: DataValueImportSummary.EMPTY)
    }

    @Suppress("ComplexMethod", "ReturnCount")
    private fun mergeSummaries(
        existing: DataValueImportSummary?,
        newSummary: DataValueImportSummary?,
    ): DataValueImportSummary {
        if (existing == null) return newSummary ?: DataValueImportSummary.EMPTY
        if (newSummary == null) return existing

        val existingCounts = existing.importCount()
        val newCounts = newSummary.importCount()

        val mergedImportStatus = mergeImportStatus(existing.importStatus(), newSummary.importStatus())
        val mergedImportCount = ImportCount.create(
            existingCounts.imported() + newCounts.imported(),
            existingCounts.updated() + newCounts.updated(),
            existingCounts.ignored() + newCounts.ignored(),
            existingCounts.deleted() + newCounts.deleted(),
        )

        return DataValueImportSummary.create(
            mergedImportCount,
            mergedImportStatus,
            "ImportSummary",
            null,
            null,
        )
    }

    private fun mergeImportStatus(status1: ImportStatus?, status2: ImportStatus?): ImportStatus {
        return when {
            status1 == ImportStatus.ERROR || status2 == ImportStatus.ERROR -> ImportStatus.ERROR
            status1 == ImportStatus.WARNING || status2 == ImportStatus.WARNING -> ImportStatus.WARNING
            else -> ImportStatus.SUCCESS
        }
    }

    private suspend fun markObjectsAs(dataValues: Collection<DataValue>, forcedState: State?) {
        val updatedDataValues = dataValues.map { dataValue ->
            dataValue.toBuilder().syncState(forcedOrOwn(dataValue, forcedState)).build()
        }
        dataValueStore.update(updatedDataValues)
    }
}
