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

package org.hisp.dhis.android.core.validation;

import android.database.Cursor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.MissingValueStrategyColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.PeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.ValidationRuleImportanceColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.ValidationRuleOperatorColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreValidationRuleExpressionColumnAdapter;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.period.PeriodType;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_ValidationRule.Builder.class)
public abstract class ValidationRule extends BaseNameableObject implements CoreObject {

    @JsonProperty()
    public abstract String instruction();

    @JsonProperty()
    @ColumnAdapter(ValidationRuleImportanceColumnAdapter.class)
    public abstract ValidationRuleImportance importance();

    @JsonProperty()
    @ColumnAdapter(ValidationRuleOperatorColumnAdapter.class)
    public abstract ValidationRuleOperator operator();

    @JsonProperty()
    @ColumnAdapter(PeriodTypeColumnAdapter.class)
    public abstract PeriodType periodType();

    @JsonProperty()
    public abstract Boolean skipFormValidation();

    @JsonProperty()
    @ColumnAdapter(IgnoreValidationRuleExpressionColumnAdapter.class)
    public abstract ValidationRuleExpression leftSide();

    @JsonProperty()
    public abstract String leftSideExpression();

    @JsonProperty()
    public abstract String leftSideDescription();

    @JsonProperty()
    @ColumnAdapter(MissingValueStrategyColumnAdapter.class)
    public abstract MissingValueStrategy leftSideMissingValueStrategy();

    @JsonProperty()
    @ColumnAdapter(IgnoreValidationRuleExpressionColumnAdapter.class)
    public abstract ValidationRuleExpression rightSide();

    @JsonProperty()
    public abstract String rightSideExpression();

    @JsonProperty()
    public abstract String rightSideDescription();

    @JsonProperty()
    @ColumnAdapter(MissingValueStrategyColumnAdapter.class)
    public abstract MissingValueStrategy rightSideMissingValueStrategy();


    // TODO OrganisationUnitLevels

    public static Builder builder() {
        return new $$AutoValue_ValidationRule.Builder();
    }

    public static ValidationRule create(Cursor cursor) {
        ValidationRule rule = $AutoValue_ValidationRule.createFromCursor(cursor);
        ValidationRuleExpression leftSide = ValidationRuleExpression.builder()
                .description(rule.leftSideDescription())
                .expression(rule.leftSideExpression())
                .missingValueStrategy(rule.leftSideMissingValueStrategy())
                .build();
        ValidationRuleExpression rightSide = ValidationRuleExpression.builder()
                .description(rule.rightSideDescription())
                .expression(rule.rightSideExpression())
                .missingValueStrategy(rule.rightSideMissingValueStrategy())
                .build();
        return rule.toBuilder().leftSide(leftSide).rightSide(rightSide).build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseNameableObject.Builder<Builder> {

        public abstract Builder id(Long id);

        public abstract Builder instruction(String instruction);

        public abstract Builder importance(ValidationRuleImportance importance);

        public abstract Builder operator(ValidationRuleOperator operator);

        public abstract Builder periodType(PeriodType periodType);

        public abstract Builder skipFormValidation(Boolean skipFormValidation);

        public abstract Builder leftSide(ValidationRuleExpression leftSide);

        abstract Builder leftSideExpression(String leftSideExpression);

        abstract Builder leftSideDescription(String leftSideDescription);

        abstract Builder leftSideMissingValueStrategy(MissingValueStrategy leftSideMissingValueStrategy);

        public abstract Builder rightSide(ValidationRuleExpression rightSide);

        abstract Builder rightSideExpression(String rightSideExpression);

        abstract Builder rightSideDescription(String rightSideDescription);

        abstract Builder rightSideMissingValueStrategy(MissingValueStrategy rightSideMissingValueStrategy);

        public abstract ValidationRule build();
    }
}