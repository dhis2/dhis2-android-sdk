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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummaryWebResponse
import org.hisp.dhis.android.core.imports.internal.ImportCount
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

@RunWith(JUnit4::class)
class DataValuePostCallShould {
    private val networkHandler: DataValueNetworkHandler = mock()
    private val dataValueImportHandler: DataValueImportHandler = mock()
    private val fileResourcePostCall: DataValueFileResourcePostCall = mock()
    private val dataValueStore: DataValueStore = mock()
    private val versionManager: DHISVersionManagerImpl = mock()
    private val summaryMerger = DataValueImportSummaryMerger()

    private lateinit var dataValuePostCall: DataValuePostCall

    private val successWebResponse = DataValueImportSummaryWebResponse(
        response = DataValueImportSummary.create(
            ImportCount.create(1, 0, 0, 0),
            ImportStatus.SUCCESS,
            "ImportSummary",
            null,
            null,
        ),
        httpStatus = "OK",
        httpStatusCode = 200,
        status = "OK",
        message = "Import was successful.",
    )

    @Before
    fun setUp() {
        dataValuePostCall = DataValuePostCall(
            networkHandler,
            dataValueImportHandler,
            fileResourcePostCall,
            dataValueStore,
            versionManager,
            summaryMerger,
        )
    }

    @Test
    fun upload_datavalues_grouped_by_dataset_for_v39_plus() = runTest {
        val dataValues = listOf(
            createDataValue("dataSet1", "value1"),
            createDataValue("dataSet1", "value2"),
            createDataValue("dataSet2", "value3"),
        )

        givenVersion(isV39Plus = true, isV38Plus = true)
        givenFileResourceReturns(dataValues)

        whenever(networkHandler.postDataValuesWithDataSet(any()))
            .thenReturn(Result.Success(successWebResponse))

        dataValuePostCall.uploadDataValues(dataValues).first()

        verify(networkHandler, times(2)).postDataValuesWithDataSet(any())
        verify(networkHandler).postDataValuesWithDataSet(
            argThat { this.dataSet == "dataSet1" && this.dataValues.size == 2 },
        )
        verify(networkHandler).postDataValuesWithDataSet(
            argThat { this.dataSet == "dataSet2" && this.dataValues.size == 1 },
        )
    }

    @Test
    fun upload_datavalues_without_grouping_for_v38() = runTest {
        val dataValues = listOf(
            createDataValue("dataSet1", "value1"),
            createDataValue("dataSet1", "value2"),
            createDataValue("dataSet2", "value3"),
        )

        givenVersion(isV39Plus = false, isV38Plus = true)
        givenFileResourceReturns(dataValues)
        whenever(networkHandler.postDataValuesWebResponse(any()))
            .thenReturn(Result.Success(successWebResponse))

        dataValuePostCall.uploadDataValues(dataValues).first()

        verify(networkHandler, times(1)).postDataValuesWebResponse(any())
        verify(networkHandler, never()).postDataValuesWithDataSet(any())
    }

    @Test
    fun upload_datavalues_using_legacy_endpoint_for_pre_v38() = runTest {
        val dataValues = listOf(createDataValue(null, "value1"))

        givenVersion(isV39Plus = false, isV38Plus = false)
        givenFileResourceReturns(dataValues)
        whenever(networkHandler.postDataValues(any()))
            .thenReturn(Result.Success(successWebResponse.response))

        dataValuePostCall.uploadDataValues(dataValues).first()

        verify(networkHandler, times(1)).postDataValues(any())
        verify(networkHandler, never()).postDataValuesWebResponse(any())
        verify(networkHandler, never()).postDataValuesWithDataSet(any())
    }

    @Test
    fun handle_datavalues_without_dataset_in_v39_plus() = runTest {
        val dataValues = listOf(
            createDataValue(null, "value1"),
            createDataValue("dataSet1", "value2"),
        )

        givenVersion(isV39Plus = true, isV38Plus = true)
        givenFileResourceReturns(dataValues)

        whenever(networkHandler.postDataValuesWithDataSet(any()))
            .thenReturn(Result.Success(successWebResponse))

        dataValuePostCall.uploadDataValues(dataValues).first()

        verify(networkHandler, times(2)).postDataValuesWithDataSet(any())
    }

    @Test
    fun process_all_uploads_before_returning_error_on_partial_failure() = runTest {
        val dataValues = listOf(
            createDataValue("dataSet1", "value1"),
            createDataValue("dataSet2", "value2"),
            createDataValue("dataSet3", "value3"),
        )

        val error = D2Error.builder()
            .errorComponent(D2ErrorComponent.Server)
            .errorCode(D2ErrorCode.API_RESPONSE_PROCESS_ERROR)
            .errorDescription("Upload failed for dataSet2")
            .build()

        givenVersion(isV39Plus = true, isV38Plus = true)
        givenFileResourceReturns(dataValues)

        // dataSet1 succeeds, dataSet2 fails, dataSet3 succeeds
        whenever(networkHandler.postDataValuesWithDataSet(argThat { dataSet == "dataSet1" }))
            .thenReturn(Result.Success(successWebResponse))
        whenever(networkHandler.postDataValuesWithDataSet(argThat { dataSet == "dataSet2" }))
            .thenReturn(Result.Failure(error))
        whenever(networkHandler.postDataValuesWithDataSet(argThat { dataSet == "dataSet3" }))
            .thenReturn(Result.Success(successWebResponse))

        try {
            dataValuePostCall.uploadDataValues(dataValues).first()
            fail("Should have thrown D2Error")
        } catch (e: D2Error) {
            assertThat(e.errorDescription()).contains("dataSet2")
        }

        // Verify all three uploads were attempted
        verify(networkHandler, times(3)).postDataValuesWithDataSet(any())
    }

    private suspend fun givenVersion(isV39Plus: Boolean, isV38Plus: Boolean) {
        whenever(versionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_39)).thenReturn(isV39Plus)
        whenever(versionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_38)).thenReturn(isV38Plus)
    }

    private suspend fun givenFileResourceReturns(dataValues: List<DataValue>) {
        whenever(fileResourcePostCall.uploadFileResource(any())).thenReturn(
            DataValueFileResourcePostCallResult(dataValues, emptyList()),
        )
    }

    private fun createDataValue(dataSet: String?, value: String): DataValue {
        return DataValue.builder()
            .dataElement("dataElement")
            .period("202312")
            .organisationUnit("orgUnit")
            .categoryOptionCombo("coc")
            .attributeOptionCombo("aoc")
            .sourceDataSet(dataSet)
            .value(value)
            .syncState(State.TO_POST)
            .created(Date())
            .lastUpdated(Date())
            .build()
    }
}
