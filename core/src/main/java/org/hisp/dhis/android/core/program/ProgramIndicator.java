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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.legendset.LegendSet;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class ProgramIndicator extends BaseNameableObject {
    private static final String DISPLAY_IN_FORM = "displayInForm";
    private static final String EXPRESSION = "expression";
    private static final String DIMENSION_ITEM = "dimensionItem";
    private static final String FILTER = "filter";
    private static final String DECIMALS = "decimals";
    private static final String PROGRAM = "program";
    private static final String LEGEND_SETS = "legendSets";

    public static final Field<ProgramIndicator, String> uid = Field.create(UID);
    private static final Field<ProgramIndicator, String> code = Field.create(CODE);
    private static final Field<ProgramIndicator, String> name = Field.create(NAME);
    private static final Field<ProgramIndicator, String> displayName = Field.create(DISPLAY_NAME);
    private static final Field<ProgramIndicator, String> created = Field.create(CREATED);
    private static final Field<ProgramIndicator, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<ProgramIndicator, Boolean> deleted = Field.create(DELETED);
    private static final Field<ProgramIndicator, String> shortName = Field.create(SHORT_NAME);
    private static final Field<ProgramIndicator, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);
    private static final Field<ProgramIndicator, String> description = Field.create(DESCRIPTION);
    private static final Field<ProgramIndicator, String> displayDescription = Field.create(DISPLAY_DESCRIPTION);
    private static final Field<ProgramIndicator, Boolean> displayInForm = Field.create(DISPLAY_IN_FORM);
    private static final Field<ProgramIndicator, String> expression = Field.create(EXPRESSION);
    private static final Field<ProgramIndicator, String> dimensionItem = Field.create(DIMENSION_ITEM);
    private static final Field<ProgramIndicator, String> filter = Field.create(FILTER);
    private static final Field<ProgramIndicator, Integer> decimals = Field.create(DECIMALS);
    public static final NestedField<ProgramIndicator, ObjectWithUid> program = NestedField.create(PROGRAM);
    private static final NestedField<ProgramIndicator, LegendSet> legendSets = NestedField.create(LEGEND_SETS);

    static final Fields<ProgramIndicator> allFields = Fields.<ProgramIndicator>builder().fields(
            uid, code, name, displayName, created, lastUpdated, shortName, displayShortName,
            description, displayDescription, deleted, decimals, dimensionItem, displayInForm, expression, filter,
            program.with(ObjectWithUid.uid), legendSets.with(LegendSet.allFields)
    ).build();

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

    @Nullable
    @JsonProperty(LEGEND_SETS)
    public abstract List<LegendSet> legendSets();

    @JsonCreator
    public static ProgramIndicator create(
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
            @JsonProperty(DISPLAY_IN_FORM) Boolean displayInForm,
            @JsonProperty(EXPRESSION) String expression,
            @JsonProperty(DIMENSION_ITEM) String dimensionItem,
            @JsonProperty(FILTER) String filter,
            @JsonProperty(DECIMALS) Integer decimals,
            @JsonProperty(DELETED) Boolean deleted,
            @JsonProperty(PROGRAM) Program program,
            @JsonProperty(LEGEND_SETS) List<LegendSet> legendSets
    ) {
        return new AutoValue_ProgramIndicator(
                uid, code, name, displayName, created, lastUpdated, deleted,
                shortName, displayShortName, description, displayDescription,
                displayInForm, expression, dimensionItem, filter, decimals, program, legendSets);
    }

}