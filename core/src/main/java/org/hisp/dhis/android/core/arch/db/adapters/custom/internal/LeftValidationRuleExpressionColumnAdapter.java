/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.db.adapters.custom.internal;

import android.content.ContentValues;
import android.database.Cursor;

import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter;

import org.hisp.dhis.android.core.validation.MissingValueStrategy;
import org.hisp.dhis.android.core.validation.ValidationRuleExpression;

import static org.hisp.dhis.android.core.validation.ValidationRuleTableInfo.Columns.LEFT_SIDE_DESCRIPTION;
import static org.hisp.dhis.android.core.validation.ValidationRuleTableInfo.Columns.LEFT_SIDE_EXPRESSION;
import static org.hisp.dhis.android.core.validation.ValidationRuleTableInfo.Columns.LEFT_SIDE_MISSING_VALUE_STRATEGY;

public class LeftValidationRuleExpressionColumnAdapter implements ColumnTypeAdapter<ValidationRuleExpression> {

    @Override
    public ValidationRuleExpression fromCursor(Cursor cursor, String columnName) {
        int expressionColumnIndex = cursor.getColumnIndex(LEFT_SIDE_EXPRESSION);
        String expressionStr = cursor.getString(expressionColumnIndex);
        int descriptionColumnIndex = cursor.getColumnIndex(LEFT_SIDE_DESCRIPTION);
        String descriptionStr = cursor.getString(descriptionColumnIndex);
        int missingValueStrategyColumnIndex = cursor.getColumnIndex(LEFT_SIDE_MISSING_VALUE_STRATEGY);
        String missingValueStrategyStr = cursor.getString(missingValueStrategyColumnIndex);

        MissingValueStrategy missingValueStrategy = null;
        if (missingValueStrategyStr != null) {
            try {
                missingValueStrategy = MissingValueStrategy.valueOf(missingValueStrategyStr);
            } catch (Exception exception) {
                throw new RuntimeException("Unknown Missing value strategy", exception);
            }
        }

        return ValidationRuleExpression.builder()
                .expression(expressionStr)
                .description(descriptionStr)
                .missingValueStrategy(missingValueStrategy).build();
    }

    @Override
    public void toContentValues(ContentValues values, String columnName, ValidationRuleExpression value) {
        if (value != null) {
            values.put(LEFT_SIDE_EXPRESSION, value.expression());
            values.put(LEFT_SIDE_DESCRIPTION, value.description());
            if (value.missingValueStrategy() != null) {
                values.put(LEFT_SIDE_MISSING_VALUE_STRATEGY, value.missingValueStrategy().name());
            }
        }
    }
}