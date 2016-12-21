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

package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.option.OptionSet;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_TrackedEntityAttribute.Builder.class)
public abstract class TrackedEntityAttribute extends BaseNameableObject {
    private static final String JSON_PROPERTY_TRACKED_ENTITY = "trackedEntity";
    private static final String JSON_PROPERTY_PROGRAM_SCOPE = "programScope";
    private static final String JSON_PROPERTY_DISPLAY_IN_LIST_NO_PROGRAM = "displayInListNoProgram";
    private static final String JSON_PROPERTY_PATTERN = "pattern";
    private static final String JSON_PROPERTY_SORT_ORDER_IN_LIST_NO_PROGRAM = "sortOrderInListNoProgram";
    private static final String JSON_PROPERTY_OPTION_SET = "optionSet";
    private static final String JSON_PROPERTY_GENERATED = "generated";
    private static final String JSON_PROPERTY_DISPLAY_ON_VISIT_SCHEDULE = "displayOnVisitSchedule";
    private static final String JSON_PROPERTY_VALUE_TYPE = "valueType";
    private static final String JSON_PROPERTY_ORGUNIT_SCOPE = "orgunitScope";
    private static final String JSON_PROPERTY_EXPRESSION = "expression";
    private static final String JSON_PROPERTY_SEARCH_SCOPE = "searchScope";
    private static final String JSON_PROPERTY_UNIQUE = "unique";
    private static final String JSON_PROPERTY_INHERIT = "inherit";

    @Nullable
    @JsonProperty(JSON_PROPERTY_PATTERN)
    public abstract String pattern();

    @Nullable
    @JsonProperty(JSON_PROPERTY_SORT_ORDER_IN_LIST_NO_PROGRAM)
    public abstract Integer sortOrderInListNoProgram();

    @Nullable
    @JsonProperty(JSON_PROPERTY_OPTION_SET)
    public abstract OptionSet optionSet();

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_EXPRESSION)
    public abstract String expression();

    @Nullable
    @JsonProperty(JSON_PROPERTY_SEARCH_SCOPE)
    public abstract TrackedEntityAttributeSearchScope searchScope();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_SCOPE)
    public abstract Boolean programScope();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_IN_LIST_NO_PROGRAM)
    public abstract Boolean displayInListNoProgram();

    @Nullable
    @JsonProperty(JSON_PROPERTY_GENERATED)
    public abstract Boolean generated();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_ON_VISIT_SCHEDULE)
    public abstract Boolean displayOnVisitSchedule();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ORGUNIT_SCOPE)
    public abstract Boolean orgunitScope();

    @Nullable
    @JsonProperty(JSON_PROPERTY_UNIQUE)
    public abstract Boolean unique();

    @Nullable
    @JsonProperty(JSON_PROPERTY_INHERIT)
    public abstract Boolean inherit();

    public static Builder builder() {
        return new AutoValue_TrackedEntityAttribute.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {
        @JsonProperty(JSON_PROPERTY_PATTERN)
        public abstract Builder pattern(@Nullable String pattern);

        @JsonProperty(JSON_PROPERTY_SORT_ORDER_IN_LIST_NO_PROGRAM)
        public abstract Builder sortOrderInListNoProgram(@Nullable Integer sortInProgram);

        @JsonProperty(JSON_PROPERTY_OPTION_SET)
        public abstract Builder optionSet(@Nullable OptionSet optionSet);

        @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
        public abstract Builder valueType(@Nullable ValueType valueType);

        @JsonProperty(JSON_PROPERTY_EXPRESSION)
        public abstract Builder expression(@Nullable String expression);

        @JsonProperty(JSON_PROPERTY_SEARCH_SCOPE)
        public abstract Builder searchScope(@Nullable TrackedEntityAttributeSearchScope searchScope);

        @JsonProperty(JSON_PROPERTY_PROGRAM_SCOPE)
        public abstract Builder programScope(@Nullable Boolean programScope);

        @JsonProperty(JSON_PROPERTY_DISPLAY_IN_LIST_NO_PROGRAM)
        public abstract Builder displayInListNoProgram(@Nullable Boolean displayInListNoProgram);

        @JsonProperty(JSON_PROPERTY_GENERATED)
        public abstract Builder generated(@Nullable Boolean generated);

        @JsonProperty(JSON_PROPERTY_DISPLAY_ON_VISIT_SCHEDULE)
        public abstract Builder displayOnVisitSchedule(@Nullable Boolean displayOnVisitSchedule);

        @JsonProperty(JSON_PROPERTY_ORGUNIT_SCOPE)
        public abstract Builder orgunitScope(@Nullable Boolean orgUnitScope);

        @JsonProperty(JSON_PROPERTY_UNIQUE)
        public abstract Builder unique(@Nullable Boolean unique);

        @JsonProperty(JSON_PROPERTY_INHERIT)
        public abstract Builder inherit(@Nullable Boolean inherit);

        public abstract TrackedEntityAttribute build();
    }
}
