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
package org.hisp.dhis.android.core.common.objectstyle.internal

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.NameableWithStyleColumns
import org.hisp.dhis.android.core.utils.integration.mock.TestDatabaseAdapterFactory
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class TablesWithStyleShould {

    private val databaseAdapter = TestDatabaseAdapterFactory.get()
    private val d2Dao = databaseAdapter.getCurrentDatabase().d2Dao()

    private val excludedTables: List<String> = listOf()

    @Test
    fun check_content_of_styled_tables() = runTest {
        val tableList = d2Dao.stringListRawQuery(
            SimpleSQLiteQuery("SELECT name FROM sqlite_master WHERE type='table'"),
        )

        val tablesWithStyle = tableList
            .filterNot { excludedTables.contains(it) }
            .filter { table ->
                val tableInfoRows = d2Dao.getTableInfo(SimpleSQLiteQuery("PRAGMA table_info('$table')"))
                val columns = tableInfoRows.map { it.name }
                columns.contains(NameableWithStyleColumns.ICON)
            }

        val missingTablesInList = tablesWithStyle.minus(TableWithObjectStyle.allTableNames.toSet())
        val exceedingTablesInList = TableWithObjectStyle.allTableNames.minus(tablesWithStyle.toSet())

        if (missingTablesInList.isNotEmpty() || exceedingTablesInList.isNotEmpty()) {
            missingTablesInList.forEach {
                println(
                    "Table $it is not in TableWithStyle list. " +
                        "Add it to the list or to the excluded tables in the test",
                )
            }

            exceedingTablesInList.forEach {
                println(
                    "Table $it is in the TableWithStyle list but has no style column. " +
                        "Remove it from the list.",
                )
            }

            fail("Tables with style don't match with tables in TableWithStyle list.")
        }
    }
}
