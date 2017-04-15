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

package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.option.OptionSet;

import java.util.Date;

@AutoValue
public abstract class TrackedEntityAttribute extends BaseNameableObject {

    private static final String PATTERN = "pattern";
    private static final String SORT_ORDER_IN_LIST_NO_PROGRAM = "sortOrderInListNoProgram";
    private static final String OPTION_SET = "optionSet";
    private static final String VALUE_TYPE = "valueType";
    private static final String EXPRESSION = "expression";
    private static final String SEARCH_SCOPE = "searchScope";
    private static final String PROGRAM_SCOPE = "programScope";
    private static final String DISPLAY_IN_LIST_NO_PROGRAM = "displayInListNoProgram";
    private static final String GENERATED = "generated";
    private static final String DISPLAY_ON_VISIT_SCHEDULE = "displayOnVisitSchedule";
    private static final String ORG_UNIT_SCOPE = "orgunitScope";
    private static final String UNIQUE = "unique";
    private static final String INHERIT = "inherit";

    public static final Field<TrackedEntityAttribute, String> uid
            = Field.create(UID);
    public static final Field<TrackedEntityAttribute, String> code
            = Field.create(CODE);
    public static final Field<TrackedEntityAttribute, String> name
            = Field.create(NAME);
    public static final Field<TrackedEntityAttribute, String> displayName
            = Field.create(DISPLAY_NAME);
    public static final Field<TrackedEntityAttribute, String> created
            = Field.create(CREATED);
    public static final Field<TrackedEntityAttribute, String> lastUpdated
            = Field.create(LAST_UPDATED);
    public static final Field<TrackedEntityAttribute, String> shortName
            = Field.create(SHORT_NAME);
    public static final Field<TrackedEntityAttribute, String> displayShortName
            = Field.create(DISPLAY_SHORT_NAME);
    public static final Field<TrackedEntityAttribute, String> description
            = Field.create(DESCRIPTION);
    public static final Field<TrackedEntityAttribute, String> displayDescription
            = Field.create(DISPLAY_DESCRIPTION);
    public static final Field<TrackedEntityAttribute, String> pattern
            = Field.create(PATTERN);
    public static final Field<TrackedEntityAttribute, String> sortOrderInListNoProgram
            = Field.create(SORT_ORDER_IN_LIST_NO_PROGRAM);
    public static final NestedField<TrackedEntityAttribute, OptionSet> optionSet
            = NestedField.create(OPTION_SET);
    public static final Field<TrackedEntityAttribute, ValueType> valueType
            = Field.create(VALUE_TYPE);
    public static final Field<TrackedEntityAttribute, String> expression
            = Field.create(EXPRESSION);
    public static final Field<TrackedEntityAttribute, TrackedEntityAttributeSearchScope> searchScope
            = Field.create(SEARCH_SCOPE);
    public static final Field<TrackedEntityAttribute, Boolean> programScope
            = Field.create(PROGRAM_SCOPE);
    public static final Field<TrackedEntityAttribute, Boolean> displayInListNoProgram
            = Field.create(DISPLAY_IN_LIST_NO_PROGRAM);
    public static final Field<TrackedEntityAttribute, Boolean> generated
            = Field.create(GENERATED);
    public static final Field<TrackedEntityAttribute, Boolean> displayOnVisitSchedule
            = Field.create(DISPLAY_ON_VISIT_SCHEDULE);
    public static final Field<TrackedEntityAttribute, Boolean> orgUnitScope
            = Field.create(ORG_UNIT_SCOPE);
    public static final Field<TrackedEntityAttribute, Boolean> unique
            = Field.create(UNIQUE);
    public static final Field<TrackedEntityAttribute, Boolean> inherit
            = Field.create(INHERIT);

    @Nullable
    @JsonProperty(PATTERN)
    public abstract String pattern();

    @Nullable
    @JsonProperty(SORT_ORDER_IN_LIST_NO_PROGRAM)
    public abstract Integer sortOrderInListNoProgram();

    @Nullable
    @JsonProperty(OPTION_SET)
    public abstract OptionSet optionSet();

    @Nullable
    @JsonProperty(VALUE_TYPE)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty(EXPRESSION)
    public abstract String expression();

    @Nullable
    @JsonProperty(SEARCH_SCOPE)
    public abstract TrackedEntityAttributeSearchScope searchScope();

    @Nullable
    @JsonProperty(PROGRAM_SCOPE)
    public abstract Boolean programScope();

    @Nullable
    @JsonProperty(DISPLAY_IN_LIST_NO_PROGRAM)
    public abstract Boolean displayInListNoProgram();

    @Nullable
    @JsonProperty(GENERATED)
    public abstract Boolean generated();

    @Nullable
    @JsonProperty(DISPLAY_ON_VISIT_SCHEDULE)
    public abstract Boolean displayOnVisitSchedule();

    @Nullable
    @JsonProperty(ORG_UNIT_SCOPE)
    public abstract Boolean orgUnitScope();

    @Nullable
    @JsonProperty(UNIQUE)
    public abstract Boolean unique();

    @Nullable
    @JsonProperty(INHERIT)
    public abstract Boolean inherit();

    @JsonCreator
    public static TrackedEntityAttribute create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(SHORT_NAME) String shortName,
            @JsonProperty(DISPLAY_SHORT_NAME) String displayShortName,
            @JsonProperty(DESCRIPTION) String description,
            @JsonProperty(DISPLAY_DESCRIPTION) String displayDescription,
            @JsonProperty(PATTERN) String pattern,
            @JsonProperty(SORT_ORDER_IN_LIST_NO_PROGRAM) int sortOrderInListNoProgram,
            @JsonProperty(OPTION_SET) OptionSet optionSet,
            @JsonProperty(VALUE_TYPE) ValueType valueType,
            @JsonProperty(EXPRESSION) String expression,
            @JsonProperty(SEARCH_SCOPE) TrackedEntityAttributeSearchScope searchScope,
            @JsonProperty(PROGRAM_SCOPE) boolean programScope,
            @JsonProperty(DISPLAY_IN_LIST_NO_PROGRAM) boolean displayInListNoProgram,
            @JsonProperty(GENERATED) boolean generated,
            @JsonProperty(DISPLAY_ON_VISIT_SCHEDULE) boolean displayOnVisitSchedule,
            @JsonProperty(ORG_UNIT_SCOPE) boolean orgUnitScope,
            @JsonProperty(UNIQUE) boolean unique,
            @JsonProperty(INHERIT) boolean inherit,
            @JsonProperty(DELETED) Boolean deleted
    ) {
        return new AutoValue_TrackedEntityAttribute(
                uid, code, name, displayName, created, lastUpdated, deleted,
                shortName, displayShortName, description, displayDescription,
                pattern, sortOrderInListNoProgram, optionSet, valueType, expression, searchScope,
                programScope, displayInListNoProgram, generated, displayOnVisitSchedule,
                orgUnitScope, unique, inherit);
    }

}
