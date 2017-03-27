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
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

import java.util.Date;

@AutoValue
public abstract class ProgramTrackedEntityAttribute extends BaseNameableObject {
    private static final String MANDATORY = "mandatory";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
    private static final String ALLOW_FUTURE_DATE = "allowFutureDate";
    private static final String DISPLAY_IN_LIST = "displayInList";
    private static final String PROGRAM = "program";
    private static final String SORT_ORDER = "sortOrder";

    public static final Field<ProgramTrackedEntityAttribute, String> uid
            = Field.create(UID);
    public static final Field<ProgramTrackedEntityAttribute, String> code
            = Field.create(CODE);
    public static final Field<ProgramTrackedEntityAttribute, String> name
            = Field.create(NAME);
    public static final Field<ProgramTrackedEntityAttribute, String> displayName
            = Field.create(DISPLAY_NAME);
    public static final Field<ProgramTrackedEntityAttribute, String> created
            = Field.create(CREATED);
    public static final Field<ProgramTrackedEntityAttribute, String> lastUpdated
            = Field.create(LAST_UPDATED);
    public static final Field<ProgramTrackedEntityAttribute, String> shortName
            = Field.create(SHORT_NAME);
    public static final Field<ProgramTrackedEntityAttribute, String> displayShortName
            = Field.create(DISPLAY_SHORT_NAME);
    public static final Field<ProgramTrackedEntityAttribute, String> description
            = Field.create(DESCRIPTION);
    public static final Field<ProgramTrackedEntityAttribute, String> displayDescription
            = Field.create(DISPLAY_DESCRIPTION);
    public static final Field<ProgramTrackedEntityAttribute, String> mandatory
            = Field.create(MANDATORY);
    public static final NestedField<ProgramTrackedEntityAttribute, TrackedEntityAttribute> trackedEntityAttribute
            = NestedField.create(TRACKED_ENTITY_ATTRIBUTE);
    public static final NestedField<ProgramTrackedEntityAttribute, Program> program
            = NestedField.create(PROGRAM);
    public static final Field<ProgramTrackedEntityAttribute, Boolean> allowFutureDate
            = Field.create(ALLOW_FUTURE_DATE);
    public static final Field<ProgramTrackedEntityAttribute, Boolean> displayInList
            = Field.create(DISPLAY_IN_LIST);
    public static final Field<ProgramTrackedEntityAttribute, Boolean> deleted
            = Field.create(DELETED);
    public static final Field<ProgramTrackedEntityAttribute, Integer> sortOrder
            = Field.create(SORT_ORDER);

    @Nullable
    @JsonProperty(MANDATORY)
    public abstract Boolean mandatory();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_ATTRIBUTE)
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @Nullable
    @JsonProperty(ALLOW_FUTURE_DATE)
    public abstract Boolean allowFutureDate();

    @Nullable
    @JsonProperty(DISPLAY_IN_LIST)
    public abstract Boolean displayInList();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract Program program();

    @Nullable
    @JsonProperty(SORT_ORDER)
    public abstract Integer sortOrder();

    @JsonCreator
    public static ProgramTrackedEntityAttribute create(
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
            @JsonProperty(MANDATORY) Boolean mandatory,
            @JsonProperty(TRACKED_ENTITY_ATTRIBUTE) TrackedEntityAttribute trackedEntityAttribute,
            @JsonProperty(ALLOW_FUTURE_DATE) Boolean allowFutureDate,
            @JsonProperty(DISPLAY_IN_LIST) Boolean displayInList,
            @JsonProperty(PROGRAM) Program program,
            @JsonProperty(SORT_ORDER) Integer sortOrder,
            @JsonProperty(DELETED) Boolean deleted
    ) {
        return new AutoValue_ProgramTrackedEntityAttribute(
                uid, code, name, displayName, created, lastUpdated, deleted,
                shortName, displayShortName, description, displayDescription,
                mandatory, trackedEntityAttribute, allowFutureDate, displayInList, program,
                sortOrder);
    }

}
