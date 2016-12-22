/*
 * Copyright (c) 2016, University of Oslo
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

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramIndicator.Builder.class)
public abstract class ProgramIndicator extends BaseNameableObject {
    private static final String JSON_PROPERTY_DISPLAY_IN_FORM = "displayInForm";
    private static final String JSON_PROPERTY_EXPRESSION = "expression";
    private static final String JSON_PROPERTY_DIMENSION_ITEM = "dimensionItem";
    private static final String JSON_PROPERTY_FILTER = "filter";
    private static final String JSON_PROPERTY_DECIMALS = "decimals";

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_IN_FORM)
    public abstract Boolean displayInForm();

    @Nullable
    @JsonProperty(JSON_PROPERTY_EXPRESSION)
    public abstract String expression();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DIMENSION_ITEM)
    public abstract String dimensionItem();

    @Nullable
    @JsonProperty(JSON_PROPERTY_FILTER)
    public abstract String filter();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DECIMALS)
    public abstract Integer decimals();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_DISPLAY_IN_FORM)
        public abstract Builder displayInForm(@Nullable Boolean displayInForm);

        @JsonProperty(JSON_PROPERTY_EXPRESSION)
        public abstract Builder expression(@Nullable String expression);

        @JsonProperty(JSON_PROPERTY_DIMENSION_ITEM)
        public abstract Builder dimensionItem(@Nullable String dimensionItem);

        @JsonProperty(JSON_PROPERTY_FILTER)
        public abstract Builder filter(@Nullable String filter);

        @JsonProperty(JSON_PROPERTY_DECIMALS)
        public abstract Builder decimals(@Nullable Integer decimals);

        abstract ProgramIndicator build();
    }
}