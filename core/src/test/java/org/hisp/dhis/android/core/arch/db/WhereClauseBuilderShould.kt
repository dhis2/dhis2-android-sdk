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
package org.hisp.dhis.android.core.arch.db

import com.google.common.collect.Lists
import com.google.common.truth.Truth
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.junit.Test

class WhereClauseBuilderShould {
    @Test
    fun build_where_statement_for_one_key_value_pair_with_string_value() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendKeyStringValue("COL", "VAL")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL = 'VAL'")
    }

    @Test
    fun build_where_statement_for_one_key_value_pair_with_int_value() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendKeyNumberValue("COL", 2)
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL = 2")
    }

    @Test
    fun build_where_statement_for_one_key_value_pair_with_greater_or_eq_value() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendKeyGreaterOrEqStringValue("COL", "VAL")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL >= 'VAL'")
    }

    @Test
    fun build_where_statement_for_one_key_value_pair_with_less_than_or_eq_value() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendKeyLessThanOrEqStringValue("COL", "VAL")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL <= 'VAL'")
    }

    @Test
    fun build_where_statement_for_one_key_value_pair_with_greater_or_eq_and_less_than_or_eq_value() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendKeyGreaterOrEqStringValue("COL1", "VAL1")
            .appendKeyLessThanOrEqStringValue("COL2", "VAL2")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL1 >= 'VAL1' AND COL2 <= 'VAL2'")
    }

    @Test
    fun build_where_statement_for_one_key_value_pair_with_like_string_value() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendKeyLikeStringValue("COL", "VAL")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL LIKE 'VAL'")
    }

    @Test
    fun build_where_statement_for_two_key_value_pairs() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendKeyStringValue("COL1", "VAL1")
            .appendKeyStringValue("COL2", "VAL2")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL1 = 'VAL1' AND COL2 = 'VAL2'")
    }

    @Test
    fun build_where_statement_for_two_key_value_pairs_with_or_logic_gate() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendOrKeyStringValue("COL1", "VAL1")
            .appendOrKeyStringValue("COL2", "VAL2")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL1 = 'VAL1' OR COL2 = 'VAL2'")
    }

    @Test
    fun build_where_statement_for_not_in_key_values() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendNotInKeyStringValues("COL1", Lists.newArrayList("VAL1", "VAL2"))
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL1 NOT IN ('VAL1', 'VAL2')")
    }

    @Test
    fun build_where_statement_for_in_key_values() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendInKeyStringValues("COL1", Lists.newArrayList("VAL1", "VAL2"))
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL1 IN ('VAL1', 'VAL2')")
    }

    @Test
    fun build_where_statement_for_in_subquery() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendInSubQuery("COL1", "SELECT uid FROM table")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL1 IN (SELECT uid FROM table)")
    }

    @Test
    fun build_where_statement_for_is_null_value() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendIsNullValue("COL1")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL1 IS NULL")
    }

    @Test
    fun build_where_statement_for_is_not_null_value() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendIsNotNullValue("COL1")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL1 IS NOT NULL")
    }

    @Test
    fun build_where_statement_for_is_null_or_value() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendIsNullOrValue("COL1", "value")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("(COL1 IS NULL OR COL1 = 'value')")
    }

    @Test
    fun build_where_statement_for_complex_queries() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendComplexQuery("COL1 = 'VAL1' OR COL2 = 'VAL2'")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("(COL1 = 'VAL1' OR COL2 = 'VAL2')")
    }

    @Test
    fun build_where_statement_appending_complex_queries_between_others() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendIsNullValue("COL1")
            .appendComplexQuery("COL2 = 'VAL2' OR COL3 = 'VAL3'")
            .build()
        Truth.assertThat(whereStatement)
            .isEqualTo("COL1 IS NULL AND (COL2 = 'VAL2' OR COL3 = 'VAL3')")
    }

    @Test
    fun build_where_statement_appending_or_complex_queries_between_others() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendIsNullValue("COL1")
            .appendOrComplexQuery("COL2 = 'VAL2' OR COL3 = 'VAL3'")
            .build()
        Truth.assertThat(whereStatement)
            .isEqualTo("COL1 IS NULL OR (COL2 = 'VAL2' OR COL3 = 'VAL3')")
    }

    @Test
    fun build_where_statement_appending_operator() {
        val builder = WhereClauseBuilder()
        val whereStatement = builder
            .appendKeyGreaterOrEqStringValue("COL1", "VAL1")
            .appendOperator(" OR ")
            .appendKeyLessThanOrEqStringValue("COL2", "VAL2")
            .build()
        Truth.assertThat(whereStatement).isEqualTo("COL1 >= 'VAL1' OR COL2 <= 'VAL2'")
    }

    @Test(expected = RuntimeException::class)
    fun throw_exception_for_no_pairs() {
        val builder = WhereClauseBuilder()
        builder.build()
    }
}
