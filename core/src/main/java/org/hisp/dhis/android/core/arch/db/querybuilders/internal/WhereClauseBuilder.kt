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
package org.hisp.dhis.android.core.arch.db.querybuilders.internal

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper

@Suppress("TooManyFunctions")
internal class WhereClauseBuilder {
    private val whereClause = StringBuilder()
    private var addOperator = false
    fun appendKeyStringValue(column: String, value: Any): WhereClauseBuilder {
        return appendKeyValue(column, value, AND, EQ_STR, END_STR)
    }

    fun appendNotKeyStringValue(column: String, value: Any): WhereClauseBuilder {
        return appendKeyValue(column, value, AND, NOT_EQ_STR, END_STR)
    }

    fun appendKeyGreaterOrEqStringValue(column: String, value: Any): WhereClauseBuilder {
        return appendKeyValue(column, value, AND, GREATER_OR_EQ_STR, END_STR)
    }

    fun appendKeyLessThanOrEqStringValue(column: String, value: Any): WhereClauseBuilder {
        return appendKeyValue(column, value, AND, LESS_THAN_OR_EQ_STR, END_STR)
    }

    fun appendKeyLessThanStringValue(column: String, value: Any): WhereClauseBuilder {
        return appendKeyValue(column, value, AND, LESS_THAN_STR, END_STR)
    }

    fun appendOrKeyStringValue(column: String, value: Any): WhereClauseBuilder {
        return appendKeyValue(column, value, OR, EQ_STR, END_STR)
    }

    fun appendKeyLikeStringValue(column: String, value: Any): WhereClauseBuilder {
        return appendKeyValue(column, value, AND, LIKE_STR, END_STR)
    }

    fun appendOrKeyLikeStringValue(column: String, value: Any): WhereClauseBuilder {
        return appendKeyValue(column, value, OR, LIKE_STR, END_STR)
    }

    fun appendKeyNumberValue(column: String, value: Double): WhereClauseBuilder {
        return appendKeyValue(column, value, AND, EQ_NUMBER, "")
    }

    fun appendKeyNumberValue(column: String, value: Int): WhereClauseBuilder {
        return appendKeyValue(column, value, AND, EQ_NUMBER, "")
    }

    fun appendOrKeyNumberValue(column: String, value: Int): WhereClauseBuilder {
        return appendKeyValue(column, value, OR, EQ_NUMBER, "")
    }

    fun appendKeyOperatorValue(column: String, operator: String, value: String): WhereClauseBuilder {
        return appendKeyValue(column, value, AND, " $operator ", "")
    }

    fun appendNotInKeyStringValues(column: String, values: List<String>): WhereClauseBuilder {
        val valuesArray = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
            CollectionsHelper.withSingleQuotationMarksArray(values),
        )
        return appendKeyValue(column, valuesArray, AND, NOT_IN, PARENTHESES_END)
    }

    fun appendInKeyStringValues(column: String, values: Collection<String>?): WhereClauseBuilder {
        val valuesArray = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
            CollectionsHelper.withSingleQuotationMarksArray(values),
        )
        return appendKeyValue(column, valuesArray, AND, IN, PARENTHESES_END)
    }

    fun <E : Enum<*>> appendInKeyEnumValues(column: String, values: List<E>): WhereClauseBuilder {
        val strValues: List<String> = values.map { it.name }
        return appendInKeyStringValues(column, strValues)
    }

    fun appendInSubQuery(column: String, subQuery: String): WhereClauseBuilder {
        return appendKeyValue(column, subQuery, AND, IN, PARENTHESES_END)
    }

    fun appendNotInSubQuery(column: String, subQuery: String): WhereClauseBuilder {
        return appendKeyValue(column, subQuery, AND, NOT_IN, PARENTHESES_END)
    }

    fun appendOrInSubQuery(column: String, subQuery: String): WhereClauseBuilder {
        return appendKeyValue(column, subQuery, OR, IN, PARENTHESES_END)
    }

    fun appendIsNullValue(column: String): WhereClauseBuilder {
        return appendKeyValue(column, "", AND, IS_NULL, "")
    }

    fun appendOrIsNullValue(column: String): WhereClauseBuilder {
        return appendKeyValue(column, "", OR, IS_NULL, "")
    }

    fun appendIsNotNullValue(column: String): WhereClauseBuilder {
        return appendKeyValue(column, "", AND, IS_NOT_NULL, "")
    }

    fun appendIsNullOrValue(column: String, value: String): WhereClauseBuilder {
        val innerClause = WhereClauseBuilder()
            .appendIsNullValue(column)
            .appendOrKeyStringValue(column, value)
            .build()
        return appendComplexQuery(innerClause)
    }

    private fun appendKeyValue(
        column: String,
        value: Any,
        logicGate: String,
        eq: String,
        end: String,
    ): WhereClauseBuilder {
        val andOpt = if (addOperator) logicGate else ""
        addOperator = true
        whereClause.append(andOpt).append(column).append(eq).append(value).append(end)
        return this
    }

    fun appendComplexQuery(complexQuery: String): WhereClauseBuilder {
        return appendComplexQueryWithOperator(complexQuery, AND)
    }

    fun appendOrComplexQuery(complexQuery: String): WhereClauseBuilder {
        return appendComplexQueryWithOperator(complexQuery, OR)
    }

    private fun appendComplexQueryWithOperator(complexQuery: String, operator: String): WhereClauseBuilder {
        val andOpt = if (addOperator) operator else ""
        addOperator = true
        whereClause.append(andOpt).append(PARENTHESES_START).append(complexQuery).append(PARENTHESES_END)
        return this
    }

    fun appendExistsSubQuery(subQuery: String?): WhereClauseBuilder {
        val andOpt = if (addOperator) AND else ""
        addOperator = true
        whereClause.append(andOpt).append(EXISTS).append(PARENTHESES_START).append(subQuery).append(PARENTHESES_END)
        return this
    }

    fun appendOperator(operator: String?): WhereClauseBuilder {
        whereClause.append(operator)
        addOperator = false
        return this
    }

    val isEmpty: Boolean
        get() = whereClause.isEmpty()

    fun build(): String {
        require(!isEmpty) { "No columns added" }
        return whereClause.toString()
    }

    companion object {
        private const val GREATER_OR_EQ_STR = " >= '"
        private const val LESS_THAN_OR_EQ_STR = " <= '"
        private const val LESS_THAN_STR = " < '"
        private const val EQ_STR = " = '"
        private const val NOT_EQ_STR = " != '"
        private const val LIKE_STR = " LIKE '"
        private const val END_STR = "'"
        private const val PARENTHESES_START = "("
        private const val PARENTHESES_END = ")"
        private const val EXISTS = " EXISTS "
        private const val EQ_NUMBER = " = "
        private const val AND = " AND "
        private const val OR = " OR "
        private const val IN = " IN ("
        private const val NOT_IN = " NOT IN ("
        private const val IS_NULL = " IS NULL"
        private const val IS_NOT_NULL = " IS NOT NULL"
    }
}
