/*
 *  Copyright (c) 2004-2026, University of Oslo
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

import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.ImportConflict
import org.hisp.dhis.android.core.imports.internal.ImportCount
import org.koin.core.annotation.Singleton

@Singleton
internal class DataValueImportSummaryMerger {

    fun merge(
        existing: DataValueImportSummary?,
        newSummary: DataValueImportSummary?,
    ): DataValueImportSummary {
        return when {
            existing == null -> newSummary ?: DataValueImportSummary.EMPTY
            newSummary == null -> existing
            else -> DataValueImportSummary.create(
                mergeImportCounts(existing.importCount(), newSummary.importCount()),
                mergeImportStatus(existing.importStatus(), newSummary.importStatus()),
                "ImportSummary",
                null,
                mergeImportConflicts(existing.importConflicts(), newSummary.importConflicts()),
            )
        }
    }

    private fun mergeImportCounts(
        existingCounts: ImportCount,
        newCounts: ImportCount,
    ): ImportCount {
        return ImportCount.create(
            existingCounts.imported() + newCounts.imported(),
            existingCounts.updated() + newCounts.updated(),
            existingCounts.ignored() + newCounts.ignored(),
            existingCounts.deleted() + newCounts.deleted(),
        )
    }

    private fun mergeImportStatus(status1: ImportStatus?, status2: ImportStatus?): ImportStatus {
        return when {
            status1 == ImportStatus.ERROR || status2 == ImportStatus.ERROR -> ImportStatus.ERROR
            status1 == ImportStatus.WARNING || status2 == ImportStatus.WARNING -> ImportStatus.WARNING
            else -> ImportStatus.SUCCESS
        }
    }

    private fun mergeImportConflicts(
        existingConflicts: List<ImportConflict>?,
        newConflicts: List<ImportConflict>?,
    ): List<ImportConflict>? {
        val existing = existingConflicts.orEmpty()
        val new = newConflicts.orEmpty()

        return if (existing.isEmpty() && new.isEmpty()) {
            null
        } else {
            existing + new
        }
    }
}
