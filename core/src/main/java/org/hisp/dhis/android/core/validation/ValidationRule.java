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

package org.hisp.dhis.android.core.validation;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.IntegerArrayColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.LeftValidationRuleExpressionColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.RightValidationRuleExpressionColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.PeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.ValidationRuleImportanceColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.ValidationRuleOperatorColumnAdapter;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.List;

@AutoValue
public abstract class ValidationRule extends BaseNameableObject implements CoreObject {

    @Nullable
    public abstract String instruction();

    @ColumnAdapter(ValidationRuleImportanceColumnAdapter.class)
    public abstract ValidationRuleImportance importance();

    @ColumnAdapter(ValidationRuleOperatorColumnAdapter.class)
    public abstract ValidationRuleOperator operator();

    @ColumnAdapter(PeriodTypeColumnAdapter.class)
    public abstract PeriodType periodType();

    public abstract Boolean skipFormValidation();

    @ColumnAdapter(LeftValidationRuleExpressionColumnAdapter.class)
    public abstract ValidationRuleExpression leftSide();

    @ColumnAdapter(RightValidationRuleExpressionColumnAdapter.class)
    public abstract ValidationRuleExpression rightSide();

    @ColumnAdapter(IntegerArrayColumnAdapter.class)
    public abstract List<Integer> organisationUnitLevels();

    public static Builder builder() {
        return new $$AutoValue_ValidationRule.Builder();
    }

    public static ValidationRule create(Cursor cursor) {
        return $AutoValue_ValidationRule.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseNameableObject.Builder<Builder> {

        public abstract Builder id(Long id);

        public abstract Builder instruction(String instruction);

        public abstract Builder importance(ValidationRuleImportance importance);

        public abstract Builder operator(ValidationRuleOperator operator);

        public abstract Builder periodType(PeriodType periodType);

        public abstract Builder skipFormValidation(Boolean skipFormValidation);

        public abstract Builder leftSide(ValidationRuleExpression leftSide);

        public abstract Builder rightSide(ValidationRuleExpression rightSide);

        public abstract Builder organisationUnitLevels(List<Integer> organisationUnitLevels);

        public abstract ValidationRule build();
    }
}
