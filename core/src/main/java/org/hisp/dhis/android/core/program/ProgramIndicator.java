/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.program;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramIndicator.Builder.class)
public abstract class ProgramIndicator extends BaseNameableObject {
    private static final String DISPLAY_IN_FORM = "displayInForm";
    private static final String EXPRESSION = "expression";
    private static final String DIMENSION_ITEM = "dimensionItem";
    private static final String FILTER = "filter";
    private static final String DECIMALS = "decimals";
    private static final String PROGRAM = "program";

    public static final Field<ProgramIndicator, String> uid = Field.create(UID);
    public static final Field<ProgramIndicator, String> code = Field.create(CODE);
    public static final Field<ProgramIndicator, String> name = Field.create(NAME);
    public static final Field<ProgramIndicator, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<ProgramIndicator, String> created = Field.create(CREATED);
    public static final Field<ProgramIndicator, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<ProgramIndicator, Boolean> deleted = Field.create(DELETED);
    public static final Field<ProgramIndicator, String> shortName = Field.create(SHORT_NAME);
    public static final Field<ProgramIndicator, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);
    public static final Field<ProgramIndicator, String> description = Field.create(DESCRIPTION);
    public static final Field<ProgramIndicator, String> displayDescription = Field.create(DISPLAY_DESCRIPTION);
    public static final Field<ProgramIndicator, Boolean> displayInForm = Field.create(DISPLAY_IN_FORM);
    public static final Field<ProgramIndicator, String> expression = Field.create(EXPRESSION);
    public static final Field<ProgramIndicator, String> dimensionItem = Field.create(DIMENSION_ITEM);
    public static final Field<ProgramIndicator, String> filter = Field.create(FILTER);
    public static final Field<ProgramIndicator, Integer> decimals = Field.create(DECIMALS);
    public static final NestedField<ProgramIndicator, Program> program = NestedField.create(PROGRAM);

    @Nullable
    @JsonProperty(DISPLAY_IN_FORM)
    public abstract Boolean displayInForm();

    @Nullable
    @JsonProperty(EXPRESSION)
    public abstract String expression();

    @Nullable
    @JsonProperty(DIMENSION_ITEM)
    public abstract String dimensionItem();

    @Nullable
    @JsonProperty(FILTER)
    public abstract String filter();

    @Nullable
    @JsonProperty(DECIMALS)
    public abstract Integer decimals();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract Program program();

    abstract ProgramIndicator.Builder toBuilder();

    static ProgramIndicator.Builder builder() {
        return new AutoValue_ProgramIndicator.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends
            BaseNameableObject.Builder<ProgramIndicator.Builder> {

        private static final String DECIMALS = "decimals";
        private static final String PROGRAM = "program";

        @JsonProperty(DISPLAY_IN_FORM)
        public abstract ProgramIndicator.Builder displayInForm(
                @Nullable Boolean displayInForm);

        @JsonProperty(EXPRESSION)
        public abstract ProgramIndicator.Builder expression(
                @Nullable String expression);

        @JsonProperty(DIMENSION_ITEM)
        public abstract ProgramIndicator.Builder dimensionItem(
                @Nullable String dimensionItem);

        @JsonProperty(FILTER)
        public abstract ProgramIndicator.Builder filter(
                @Nullable String filter);

        @JsonProperty(DECIMALS)
        public abstract ProgramIndicator.Builder decimals(
                @Nullable Integer decimals);

        @JsonProperty(PROGRAM)
        public abstract ProgramIndicator.Builder program(
                @Nullable Program program);

        public abstract ProgramIndicator build();
    }
}