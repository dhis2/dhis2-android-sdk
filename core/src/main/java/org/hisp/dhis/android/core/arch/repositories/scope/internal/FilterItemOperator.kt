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
package org.hisp.dhis.android.core.arch.repositories.scope.internal

import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder

enum class FilterItemOperator(val sqlOperator: String, val apiOperator: String, val apiUpperOperator: String) {
    LIKE("LIKE", "like", "LIKE"),
    EQ("=", "eq", "EQ"),
    NOT_EQ("!=", "!eq", "NE"),
    IN("IN", "in", "IN"),
    NOT_IN("NOT IN", "!in", "!in"), // No upper API version for this
    LT("<", "lt", "LT"),
    LE("<=", "le", "LE"),
    GT(">", "gt", "GT"),
    GE(">=", "ge", "GE"),
    SW("LIKE", "sw", "SW"),
    EW("LIKE", "ew", "EW"),
    VOID("", "", ""),
    NULL_OR_BLANK("", "null", "NULL"),
    NOT_NULL_AND_NOT_BLANK("", "!null", "!NULL"),
    ;

    /**
     * Builds the full SQL condition for this operator. NULL_OR_BLANK and NOT_NULL_AND_NOT_BLANK cannot be expressed
     * as "column sqlOperator value" because they need the column name in both sides of the OR/AND condition.
     */
    internal fun getSqlCondition(column: String, valueStr: String? = null): String {
        return when (this) {
            NULL_OR_BLANK -> "($column IS NULL OR $column = '')"
            NOT_NULL_AND_NOT_BLANK -> "($column IS NOT NULL AND $column != '')"
            else -> "$column $sqlOperator $valueStr"
        }
    }

    /**
     * Appends this operator as an EXISTS/NOT EXISTS subquery over a link table. [sub] must be the
     * "SELECT 1 FROM ... WHERE <fixed conditions>" prefix without a trailing AND; the value condition is appended
     * here. NULL_OR_BLANK is expressed as the negation of NOT_NULL_AND_NOT_BLANK so that it also matches rows that
     * are absent from the link table, not only rows present with a null or blank value.
     */
    internal fun getSqlLinkTable(
        where: WhereClauseBuilder,
        sub: String,
        column: String,
        valueStr: String? = null,
    ): WhereClauseBuilder {
        return when (this) {
            NULL_OR_BLANK -> where.appendNotExistsSubQuery("$sub AND ($column IS NOT NULL AND $column != '')")
            NOT_NULL_AND_NOT_BLANK -> where.appendExistsSubQuery("$sub AND ($column IS NOT NULL AND $column != '')")
            else -> where.appendExistsSubQuery("$sub AND $column $sqlOperator $valueStr")
        }
    }
}
