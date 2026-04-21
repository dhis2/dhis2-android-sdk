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
package org.hisp.dhis.android.core.category.internal

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.persistence.category.CategoryCategoryComboLinkTableInfo
import org.hisp.dhis.android.persistence.category.CategoryComboTableInfo
import org.hisp.dhis.android.persistence.category.CategoryOptionComboTableInfo
import org.junit.Test

class CategoryComboUidsSeekerMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun seek_category_combos_uids() = runTest {
        val seeker: CategoryComboUidsSeeker = koin.get()
        val categories = seeker.seekUids()
        assertThat(categories.size).isEqualTo(2)
        assertThat(categories.contains("m2jTvAj5kkm")).isTrue()

        // Default category combo (p0KPaWEg3cf).
        assertThat(categories.contains("p0KPaWEg3cf")).isTrue()
    }

    @Test
    fun seek_uids_picks_enrollment_category_combo_column() = runTest {
        val seeker: CategoryComboUidsSeeker = koin.get()
        val programStore: ProgramStore = koin.get()
        val categoryComboStore: CategoryComboStore = koin.get()

        val testProgramUid = "testProgSeekr1"
        val distinctEnrollmentCcUid = "testEnrollCC01"

        categoryComboStore.insert(
            CategoryCombo.builder()
                .uid(distinctEnrollmentCcUid)
                .isDefault(false)
                .build(),
        )
        programStore.insert(
            Program.builder()
                .uid(testProgramUid)
                .categoryCombo(ObjectWithUid.create("m2jTvAj5kkm"))
                .enrollmentCategoryCombo(ObjectWithUid.create(distinctEnrollmentCcUid))
                .build(),
        )

        try {
            val categories = seeker.seekUids()
            assertThat(categories).contains(distinctEnrollmentCcUid)
        } finally {
            programStore.deleteIfExists(testProgramUid)
            categoryComboStore.deleteIfExists(distinctEnrollmentCcUid)
        }
    }

    @Test
    fun cover_all_foreign_key_references_to_category_combo() = runTest {
        val categoryComboTable = CategoryComboTableInfo.TABLE_INFO.name()
        val categoryOptionComboTable = CategoryOptionComboTableInfo.TABLE_INFO.name()
        val categoryCategoryComboLinkTable = CategoryCategoryComboLinkTableInfo.TABLE_INFO.name()

        // Tables that belong to the CategoryCombo subgraph. FKs from these to CategoryCombo are
        // internal wiring (child rows, link tables) and are not separate metadata objects that
        // need to be seeded from elsewhere, so they are excluded from the coverage check.
        val excludedTables = setOf(
            categoryComboTable,
            categoryOptionComboTable,
            categoryCategoryComboLinkTable,
        )

        val sqliteInternalTables = setOf("android_metadata", "room_master_table", "sqlite_sequence")
        val schemaRows = databaseAdapter.getCurrentDatabase().d2Dao().getSchemaRows()
        val tableNames = schemaRows
            .filter { it.sql?.trimStart()?.startsWith("CREATE TABLE", ignoreCase = true) == true }
            .map { it.name }
            .filter { it !in sqliteInternalTables && !it.startsWith("sqlite_") }
            .filter { it !in excludedTables }

        val discoveredReferences = mutableSetOf<Pair<String, String>>()
        for (table in tableNames) {
            val fkRows = databaseAdapter.rawQueryWithTypedValues("PRAGMA foreign_key_list(`$table`);")
            for (row in fkRows) {
                val values = row.values.toList()
                if (values.size >= 5 && values[2].toString() == categoryComboTable) {
                    val fromColumn = values[3].toString()
                    discoveredReferences.add(table to fromColumn)
                }
            }
        }

        val coveredReferences = CategoryComboUidsSeeker.categoryComboReferences
            .flatMap { (column, tables) -> tables.map { it to column } }
            .toSet()

        val uncovered = discoveredReferences - coveredReferences
        assertWithMessage(
            "FK columns targeting CategoryCombo that are not swept by CategoryComboUidsSeeker. " +
                "Add each (table, column) pair to CategoryComboUidsSeeker.categoryComboReferences.",
        ).that(uncovered).isEmpty()
    }
}
