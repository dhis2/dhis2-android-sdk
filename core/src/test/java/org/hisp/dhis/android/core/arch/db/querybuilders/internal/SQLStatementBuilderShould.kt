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
package org.hisp.dhis.android.core.arch.db.querybuilders.internal

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.category.CategoryTableInfo
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SQLStatementBuilderShould {
    private val builder = SQLStatementBuilderImpl(TABLE_NAME, columns, columns, false)

    @Test
    fun generate_insert_statement() {
        Truth.assertThat(builder.insert()).isEqualTo(
            "INSERT INTO Test_Table (Test_Column_Name1, Test_Column_Name2) VALUES (?, ?);",
        )
    }

    @Test
    fun generate_update_statement() {
        Truth.assertThat(builder.update()).isEqualTo(
            "UPDATE Test_Table SET Test_Column_Name1=?, Test_Column_Name2=? WHERE uid=?;",
        )
    }

    @Test
    fun generate_delete_statement() {
        Truth.assertThat(builder.deleteById()).isEqualTo(
            "DELETE FROM Test_Table WHERE uid=?;",
        )
    }

    @Test
    fun generate_select_where_statement() {
        Truth.assertThat(builder.selectWhere("WH_CLAUSE")).isEqualTo(
            "SELECT * FROM Test_Table WHERE WH_CLAUSE;",
        )
    }

    @Test
    fun generate_select_where_with_limit_statement() {
        Truth.assertThat(builder.selectWhere("WH_CLAUSE", 3)).isEqualTo(
            "SELECT * FROM Test_Table WHERE WH_CLAUSE LIMIT 3;",
        )
    }

    @Test
    fun generate_count_where_statement() {
        Truth.assertThat(builder.countWhere("WH_CLAUSE")).isEqualTo(
            "SELECT COUNT(*) FROM Test_Table WHERE WH_CLAUSE;",
        )
    }

    @Test
    fun generate_count_statement() {
        Truth.assertThat(builder.count()).isEqualTo(
            "SELECT COUNT(*) FROM Test_Table;",
        )
    }

    @Test
    fun generate_select_by_uid_statement() {
        Truth.assertThat(builder.selectByUid()).isEqualTo(
            "SELECT * FROM Test_Table WHERE uid=?;",
        )
    }

    @Test
    fun generate_select_children_with_link_table() {
        Truth.assertThat(builder.selectChildrenWithLinkTable(CHILD_PROJECTION, "UID", null))
            .isEqualTo(
                "SELECT c.* FROM Test_Table AS l, Category AS c WHERE l." + COL_2 +
                    "=c.uid AND l." + COL_1 + "='UID';",
            )
    }

    @Test
    fun generate_select_children_with_link_table_and_where_clause() {
        Truth.assertThat(builder.selectChildrenWithLinkTable(CHILD_PROJECTION, "UID", "l.bla=1"))
            .isEqualTo(
                "SELECT c.* FROM Test_Table AS l, Category AS c WHERE l." + COL_2 +
                    "=c.uid AND l." + COL_1 + "='UID' AND l.bla=1;",
            )
    }

    @Test
    fun generate_select_children_with_link_table_with_sort_order() {
        val builderWithSortOrder = SQLStatementBuilderImpl(TABLE_NAME, columns, columns, true)
        Truth.assertThat(
            builderWithSortOrder.selectChildrenWithLinkTable(
                CHILD_PROJECTION,
                "UID",
                null,
            ),
        ).isEqualTo(
            "SELECT c.* FROM Test_Table AS l, Category AS c WHERE l." + COL_2 +
                "=c.uid AND l." + COL_1 + "='UID' ORDER BY sortOrder;",
        )
    }

    companion object {
        private const val TABLE_NAME = "Test_Table"
        private const val COL_1 = "Test_Column_Name1"
        private const val COL_2 = "Test_Column_Name2"

        private val columns = arrayOf(COL_1, COL_2)

        private val CHILD_PROJECTION = LinkTableChildProjection(
            CategoryTableInfo.TABLE_INFO,
            COL_1,
            COL_2,
        )
    }
}
