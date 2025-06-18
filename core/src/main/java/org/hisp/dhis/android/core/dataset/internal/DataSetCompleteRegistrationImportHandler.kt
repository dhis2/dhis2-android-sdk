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

import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.ImportConflict
import org.hisp.dhis.android.core.imports.internal.ImportCount
import org.koin.core.annotation.Singleton

@Singleton
internal class DataSetCompleteRegistrationImportHandler(
    private val dataSetCompleteRegistrationStore: DataSetCompleteRegistrationStore,
) {
    suspend fun handleImportSummary(
        toPostDataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
        dataValueImportSummary: DataValueImportSummary,
        deletedDataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
        withErrorDataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
    ): DataValueImportSummary {
        val newState = if (dataValueImportSummary.importStatus() == ImportStatus.ERROR) State.ERROR else State.SYNCED
        for (dataSetCompleteRegistration in toPostDataSetCompleteRegistrations) {
            if (dataSetCompleteRegistrationStore.isBeingUpload(dataSetCompleteRegistration)) {
                dataSetCompleteRegistrationStore.setState(dataSetCompleteRegistration, newState)
            }
        }
        val deletedDatasetConflicts = handleDeletedDataSetCompleteRegistrations(
            deletedDataSetCompleteRegistrations,
            withErrorDataSetCompleteRegistrations,
        )
        val importConflicts = dataValueImportSummary.importConflicts() ?: emptyList()

        val allConflicts = deletedDatasetConflicts + importConflicts

        return recreateDataValueImportSummary(
            dataValueImportSummary,
            allConflicts,
            deletedDataSetCompleteRegistrations.size,
        )
    }

    private suspend fun handleDeletedDataSetCompleteRegistrations(
        deletedDataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
        withErrorDataSetCompleteRegistrations: List<DataSetCompleteRegistration>,
    ): List<ImportConflict> {
        val conflicts = withErrorDataSetCompleteRegistrations
            .filter { dataSetCompleteRegistrationStore.isBeingUpload(it) }
            .map {
                dataSetCompleteRegistrationStore.setState(it, State.ERROR)
                ImportConflict.create(
                    it.toString(),
                    "Error marking as incomplete",
                )
            }

        deletedDataSetCompleteRegistrations
            .filter { dataSetCompleteRegistrationStore.isBeingUpload(it) }
            .forEach { dataSetCompleteRegistrationStore.deleteWhereIfExists(it) }

        return conflicts
    }

    private fun recreateDataValueImportSummary(
        dataValueImportSummary: DataValueImportSummary,
        conflicts: List<ImportConflict>,
        deletedDataSetCompleteRegistrationsSize: Int,
    ): DataValueImportSummary {
        val ic = dataValueImportSummary.importCount()
        return DataValueImportSummary.create(
            ImportCount.create(
                ic.imported(),
                ic.updated(),
                ic.deleted() + deletedDataSetCompleteRegistrationsSize,
                ic.ignored(),
            ),
            dataValueImportSummary.importStatus(),
            dataValueImportSummary.responseType(),
            dataValueImportSummary.reference(),
            conflicts.ifEmpty { null },
        )
    }
}
