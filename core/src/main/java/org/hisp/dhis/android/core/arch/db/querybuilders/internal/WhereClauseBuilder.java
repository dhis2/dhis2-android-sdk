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
package org.hisp.dhis.android.core.arch.db.querybuilders.internal;

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhereClauseBuilder {

    private static final String GREATER_OR_EQ_STR = " >= '";
    private static final String LESS_THAN_OR_EQ_STR = " <= '";
    private static final String LESS_THAN_STR = " < '";
    private static final String EQ_STR = " = '";
    private static final String NOT_EQ_STR = " != '";
    private static final String LIKE_STR = " LIKE '";
    private static final String END_STR = "'";
    private static final String PARENTHESES_START = "(";
    private static final String PARENTHESES_END = ")";

    private static final String EXISTS = " EXISTS ";

    private static final String EQ_NUMBER = " = ";

    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String IN = " IN (";
    private static final String NOT_IN = " NOT IN (";

    private static final String IS_NULL = " IS NULL";
    private static final String IS_NOT_NULL = " IS NOT NULL";

    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder whereClause = new StringBuilder();
    private boolean addOperator;

    public WhereClauseBuilder appendKeyStringValue(String column, Object value) {
        return appendKeyValue(column, value, AND, EQ_STR, END_STR);
    }

    public WhereClauseBuilder appendNotKeyStringValue(String column, Object value) {
        return appendKeyValue(column, value, AND, NOT_EQ_STR, END_STR);
    }

    public WhereClauseBuilder appendKeyGreaterOrEqStringValue(String column, Object value) {
        return appendKeyValue(column, value, AND, GREATER_OR_EQ_STR, END_STR);
    }

    public WhereClauseBuilder appendKeyLessThanOrEqStringValue(String column, Object value) {
        return appendKeyValue(column, value, AND, LESS_THAN_OR_EQ_STR, END_STR);
    }

    public WhereClauseBuilder appendKeyLessThanStringValue(String column, Object value) {
        return appendKeyValue(column, value, AND, LESS_THAN_STR, END_STR);
    }

    public WhereClauseBuilder appendOrKeyStringValue(String column, Object value) {
        return appendKeyValue(column, value, OR, EQ_STR, END_STR);
    }

    public WhereClauseBuilder appendKeyLikeStringValue(String column, Object value) {
        return appendKeyValue(column, value, AND, LIKE_STR, END_STR);
    }

    public WhereClauseBuilder appendOrKeyLikeStringValue(String column, Object value) {
        return appendKeyValue(column, value, OR, LIKE_STR, END_STR);
    }

    public WhereClauseBuilder appendKeyNumberValue(String column, double value) {
        return appendKeyValue(column, value, AND, EQ_NUMBER, "");
    }

    public WhereClauseBuilder appendKeyNumberValue(String column, int value) {
        return appendKeyValue(column, value, AND, EQ_NUMBER, "");
    }

    public WhereClauseBuilder appendOrKeyNumberValue(String column, int value) {
        return appendKeyValue(column, value, OR, EQ_NUMBER, "");
    }

    public WhereClauseBuilder appendKeyOperatorValue(String column, String operator, String value) {
        return appendKeyValue(column, value, AND,  " " + operator + " ", "");
    }

    public WhereClauseBuilder appendNotInKeyStringValues(String column, List<String> values) {
        String valuesArray = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
                CollectionsHelper.withSingleQuotationMarksArray(values));
        return appendKeyValue(column, valuesArray, AND, NOT_IN, PARENTHESES_END);
    }

    public WhereClauseBuilder appendInKeyStringValues(String column, Collection<String> values) {
        String valuesArray = CollectionsHelper.commaAndSpaceSeparatedArrayValues(
                CollectionsHelper.withSingleQuotationMarksArray(values));
        return appendKeyValue(column, valuesArray, AND, IN, PARENTHESES_END);
    }

    public <E extends Enum> WhereClauseBuilder appendInKeyEnumValues(String column, List<E> values) {
        List<String> strValues = new ArrayList<>(values.size());
        for (E e : values) {
            strValues.add(e.name());
        }
        return appendInKeyStringValues(column, strValues);
    }

    public WhereClauseBuilder appendInSubQuery(String column, String subQuery) {
        return appendKeyValue(column, subQuery, AND, IN, PARENTHESES_END);
    }

    public WhereClauseBuilder appendOrInSubQuery(String column, String subQuery) {
        return appendKeyValue(column, subQuery, OR, IN, PARENTHESES_END);
    }

    public WhereClauseBuilder appendIsNullValue(String column) {
        return appendKeyValue(column, "", AND, IS_NULL, "");
    }

    public WhereClauseBuilder appendOrIsNullValue(String column) {
        return appendKeyValue(column, "", OR, IS_NULL, "");
    }

    public WhereClauseBuilder appendIsNotNullValue(String column) {
        return appendKeyValue(column, "", AND, IS_NOT_NULL, "");
    }

    public WhereClauseBuilder appendIsNullOrValue(String column, String value) {
        String innerClause = new WhereClauseBuilder()
                .appendIsNullValue(column)
                .appendOrKeyStringValue(column, value)
                .build();

        return appendComplexQuery(innerClause);
    }

    private WhereClauseBuilder appendKeyValue(String column, Object value, String logicGate, String eq, String end) {
        String andOpt = addOperator ? logicGate : "";
        addOperator = true;
        whereClause.append(andOpt).append(column).append(eq).append(value).append(end);
        return this;
    }

    public WhereClauseBuilder appendComplexQuery(String complexQuery) {
        return appendComplexQueryWithOperator(complexQuery, AND);
    }

    public WhereClauseBuilder appendOrComplexQuery(String complexQuery) {
        return appendComplexQueryWithOperator(complexQuery, OR);
    }

    private WhereClauseBuilder appendComplexQueryWithOperator(String complexQuery, String operator) {
        String andOpt = addOperator ? operator : "";
        addOperator = true;
        whereClause.append(andOpt).append(PARENTHESES_START).append(complexQuery).append(PARENTHESES_END);
        return this;
    }

    public WhereClauseBuilder appendExistsSubQuery(String subQuery) {
        String andOpt = addOperator ? AND : "";
        addOperator = true;
        whereClause.append(andOpt).append(EXISTS).append(PARENTHESES_START).append(subQuery).append(PARENTHESES_END);
        return this;
    }

    public WhereClauseBuilder appendOperator(String operator) {
        whereClause.append(operator);
        addOperator = false;
        return this;
    }

    public boolean isEmpty() {
        return whereClause.length() == 0;
    }

    public String build() {
        if (isEmpty()) {
            throw new RuntimeException("No columns added");
        } else {
            return whereClause.toString();
        }
    }
}