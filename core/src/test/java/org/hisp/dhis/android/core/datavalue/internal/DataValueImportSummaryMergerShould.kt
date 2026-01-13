/*
 *  Copyright (c) 2004-2025, University of Oslo
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
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary
import org.hisp.dhis.android.core.imports.internal.ImportConflict
import org.hisp.dhis.android.core.imports.internal.ImportCount
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DataValueImportSummaryMergerShould {

    private val merger = DataValueImportSummaryMerger()

    @Test
    fun return_empty_when_both_summaries_are_null() {
        val result = merger.merge(null, null)

        assertThat(result).isEqualTo(DataValueImportSummary.EMPTY)
    }

    @Test
    fun return_new_summary_when_existing_is_null() {
        val newSummary = createSummary(1, 2, 3, 4, ImportStatus.SUCCESS)

        val result = merger.merge(null, newSummary)

        assertThat(result).isEqualTo(newSummary)
    }

    @Test
    fun return_existing_summary_when_new_is_null() {
        val existingSummary = createSummary(1, 2, 3, 4, ImportStatus.SUCCESS)

        val result = merger.merge(existingSummary, null)

        assertThat(result).isEqualTo(existingSummary)
    }

    @Test
    fun merge_import_counts_correctly() {
        val existing = createSummary(1, 2, 3, 4, ImportStatus.SUCCESS)
        val new = createSummary(5, 6, 7, 8, ImportStatus.SUCCESS)

        val result = merger.merge(existing, new)

        assertThat(result.importCount().imported()).isEqualTo(6)
        assertThat(result.importCount().updated()).isEqualTo(8)
        assertThat(result.importCount().ignored()).isEqualTo(10)
        assertThat(result.importCount().deleted()).isEqualTo(12)
    }

    @Test
    fun return_error_status_when_any_summary_has_error() {
        val existing = createSummary(1, 0, 0, 0, ImportStatus.SUCCESS)
        val new = createSummary(0, 1, 0, 0, ImportStatus.ERROR)

        val result = merger.merge(existing, new)

        assertThat(result.importStatus()).isEqualTo(ImportStatus.ERROR)
    }

    @Test
    fun return_error_status_when_existing_has_error() {
        val existing = createSummary(1, 0, 0, 0, ImportStatus.ERROR)
        val new = createSummary(0, 1, 0, 0, ImportStatus.SUCCESS)

        val result = merger.merge(existing, new)

        assertThat(result.importStatus()).isEqualTo(ImportStatus.ERROR)
    }

    @Test
    fun return_warning_status_when_any_summary_has_warning_and_no_errors() {
        val existing = createSummary(1, 0, 0, 0, ImportStatus.SUCCESS)
        val new = createSummary(0, 1, 0, 0, ImportStatus.WARNING)

        val result = merger.merge(existing, new)

        assertThat(result.importStatus()).isEqualTo(ImportStatus.WARNING)
    }

    @Test
    fun return_success_status_when_both_summaries_are_successful() {
        val existing = createSummary(1, 0, 0, 0, ImportStatus.SUCCESS)
        val new = createSummary(0, 1, 0, 0, ImportStatus.SUCCESS)

        val result = merger.merge(existing, new)

        assertThat(result.importStatus()).isEqualTo(ImportStatus.SUCCESS)
    }

    @Test
    fun prioritize_error_over_warning() {
        val existing = createSummary(1, 0, 0, 0, ImportStatus.WARNING)
        val new = createSummary(0, 1, 0, 0, ImportStatus.ERROR)

        val result = merger.merge(existing, new)

        assertThat(result.importStatus()).isEqualTo(ImportStatus.ERROR)
    }

    @Test
    fun return_null_conflicts_when_both_have_no_conflicts() {
        val existing = createSummary(1, 0, 0, 0, ImportStatus.SUCCESS)
        val new = createSummary(0, 1, 0, 0, ImportStatus.SUCCESS)

        val result = merger.merge(existing, new)

        assertThat(result.importConflicts()).isNull()
    }

    @Test
    fun merge_conflicts_from_both_summaries() {
        val conflict1 = ImportConflict.create("object1", "value1")
        val conflict2 = ImportConflict.create("object2", "value2")
        val existing = createSummary(imported = 1, status = ImportStatus.WARNING, conflicts = listOf(conflict1))
        val new = createSummary(updated = 1, status = ImportStatus.WARNING, conflicts = listOf(conflict2))

        val result = merger.merge(existing, new)

        assertThat(result.importConflicts()).hasSize(2)
        assertThat(result.importConflicts()).contains(conflict1)
        assertThat(result.importConflicts()).contains(conflict2)
    }

    @Test
    fun return_existing_conflicts_when_new_has_none() {
        val conflict = ImportConflict.create("object1", "value1")
        val existing = createSummary(imported = 1, status = ImportStatus.WARNING, conflicts = listOf(conflict))
        val new = createSummary(updated = 1)

        val result = merger.merge(existing, new)

        assertThat(result.importConflicts()).hasSize(1)
        assertThat(result.importConflicts()).contains(conflict)
    }

    @Test
    fun return_new_conflicts_when_existing_has_none() {
        val conflict = ImportConflict.create("object1", "value1")
        val existing = createSummary(imported = 1)
        val new = createSummary(updated = 1, status = ImportStatus.WARNING, conflicts = listOf(conflict))

        val result = merger.merge(existing, new)

        assertThat(result.importConflicts()).hasSize(1)
        assertThat(result.importConflicts()).contains(conflict)
    }

    private fun createSummary(
        imported: Int = 0,
        updated: Int = 0,
        ignored: Int = 0,
        deleted: Int = 0,
        status: ImportStatus = ImportStatus.SUCCESS,
        conflicts: List<ImportConflict>? = null,
    ): DataValueImportSummary {
        return DataValueImportSummary.create(
            ImportCount.create(imported, updated, ignored, deleted),
            status,
            "ImportSummary",
            null,
            conflicts,
        )
    }
}
