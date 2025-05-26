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

import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.ImportCount
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class DataSetCompleteRegistrationImportHandlerShould {
    private val dataSetCompleteRegistrationStore: DataSetCompleteRegistrationStore = mock()
    private val dataValueImportSummary: DataValueImportSummary = mock()
    private val dataSetCompleteRegistration: DataSetCompleteRegistration = mock()

    private lateinit var dataSetCompleteRegistrationImportHandler: DataSetCompleteRegistrationImportHandler

    @Before
    fun setUp() {
        dataSetCompleteRegistrationImportHandler =
            DataSetCompleteRegistrationImportHandler(dataSetCompleteRegistrationStore)
        whenever(dataValueImportSummary.importCount()).thenReturn(ImportCount.EMPTY)
        whenever(dataValueImportSummary.responseType()).thenReturn("ImportSummary")
    }

    @Test
    fun not_perform_any_action_passing_empty_data_value_set() {
        whenever(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.SUCCESS)
        dataSetCompleteRegistrationImportHandler.handleImportSummary(
            emptyList(),
            dataValueImportSummary,
            emptyList(),
            emptyList(),
        )

        verify(dataSetCompleteRegistrationStore, Mockito.never()).setState(
            any<DataSetCompleteRegistration>(),
            any<State>(),
        )
    }

    @Test
    fun mark_as_synced_when_successfully_imported_data_values() {
        val dataSetCompleteRegistrations = mutableListOf(dataSetCompleteRegistration)

        whenever(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.SUCCESS)
        whenever(dataSetCompleteRegistrationStore.isBeingUpload(any<DataSetCompleteRegistration>()))
            .thenReturn(java.lang.Boolean.TRUE)

        dataSetCompleteRegistrationImportHandler.handleImportSummary(
            dataSetCompleteRegistrations,
            dataValueImportSummary,
            emptyList(),
            emptyList(),
        )

        Mockito.verify(dataSetCompleteRegistrationStore, Mockito.times(1))
            .setState(dataSetCompleteRegistration, State.SYNCED)
    }

    @Test
    fun mark_as_error_when_unsuccessfully_imported_data_values() {
        val dataValueCollection = mutableListOf(dataSetCompleteRegistration)

        whenever(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.ERROR)
        whenever(dataSetCompleteRegistrationStore.isBeingUpload(any<DataSetCompleteRegistration>()))
            .thenReturn(java.lang.Boolean.TRUE)

        dataSetCompleteRegistrationImportHandler.handleImportSummary(
            dataValueCollection,
            dataValueImportSummary,
            emptyList(),
            emptyList(),
        )

        Mockito.verify(dataSetCompleteRegistrationStore, Mockito.times(1))
            .setState(dataSetCompleteRegistration, State.ERROR)
    }
}