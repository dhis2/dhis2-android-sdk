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

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class DataValueImportHandlerShould {
    private val dataValueStore: DataValueStore = mock()
    private val dataValueConflictParser: DataValueConflictParser = mock()
    private val dataValueConflictStore: DataValueConflictStore = mock()
    private val dataValueImportSummary: DataValueImportSummary = mock()
    private val dataValue: DataValue = mock()
    private val builder: DataValue.Builder = mock()

    // object to test
    private lateinit var dataValueSet: DataValueSet
    private lateinit var dataValueImportHandler: DataValueImportHandler

    @Before
    fun setUp() {
        whenever(dataValue.attributeOptionCombo()).thenReturn("attributeOptionCombo")
        whenever(dataValue.categoryOptionCombo()).thenReturn("categoryOptionCombo")
        whenever(dataValue.dataElement()).thenReturn("dataElement")
        whenever(dataValue.period()).thenReturn("period")
        whenever(dataValue.organisationUnit()).thenReturn("organisationUnit")

        // Mock builder chain
        whenever(dataValue.toBuilder()).thenReturn(builder)
        whenever(builder.syncState(any())).thenReturn(builder)
        whenever(builder.build()).thenReturn(dataValue)

        dataValueSet = DataValueSet(listOf(dataValue))
        dataValueImportHandler = DataValueImportHandler(
            dataValueStore,
            dataValueConflictParser, dataValueConflictStore,
        )
    }

    @Test
    fun passingNullDataValueSet_shouldNotPerformAnyAction() = runTest {
        dataValueImportHandler.handleImportSummary(null, dataValueImportSummary)

        verify(dataValueStore, never()).update(any<Collection<DataValue>>())
    }

    @Test
    fun passingNullImportSummary_shouldNotPerformAnyAction() = runTest {
        dataValueImportHandler.handleImportSummary(dataValueSet, null)

        verify(dataValueStore, never()).update(any<Collection<DataValue>>())
    }

    @Test
    fun successfullyImportedDataValues_shouldBeMarkedAsSynced() = runTest {
        whenever(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.SUCCESS)
        whenever(dataValueStore.isDataValueBeingUpload(any<DataValue>())).thenReturn(true)
        whenever(dataValueStore.isDeleted(any<DataValue>())).thenReturn(false)

        dataValueImportHandler.handleImportSummary(dataValueSet, dataValueImportSummary)

        verify(dataValueStore, times(1)).update(any<Collection<DataValue>>())
    }

    @Test
    fun successfullyImportedAndDeletedDataValues_shouldBeDeleted() = runTest {
        whenever(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.SUCCESS)
        whenever(dataValueStore.isDataValueBeingUpload(any())).thenReturn(true)
        whenever(dataValueStore.isDeleted(any())).thenReturn(true)

        dataValueImportHandler.handleImportSummary(dataValueSet, dataValueImportSummary)

        verify(dataValueStore, never()).update(any<Collection<DataValue>>())
        verify(dataValueStore, times(1)).deleteWhere(dataValue)
    }

    @Test
    fun unsuccessfullyImportedDataValues_shouldBeMarkedAsError() = runTest {
        whenever(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.ERROR)
        whenever(dataValueStore.isDataValueBeingUpload(any())).thenReturn(true)

        dataValueImportHandler.handleImportSummary(dataValueSet, dataValueImportSummary)

        verify(dataValueStore, times(1)).update(any<Collection<DataValue>>())
    }
}
