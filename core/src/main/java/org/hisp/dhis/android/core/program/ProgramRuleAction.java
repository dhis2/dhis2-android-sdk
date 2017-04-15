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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

import java.util.Date;

@AutoValue
public abstract class ProgramRuleAction extends BaseIdentifiableObject {
    private static final String DATA = "data";
    private static final String CONTENT = "content";
    private static final String LOCATION = "location";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
    private static final String PROGRAM_INDICATOR = "programIndicator";
    private static final String PROGRAM_STAGE_SECTION = "programStageSection";
    private static final String PROGRAM_RULE_ACTION_TYPE = "programRuleActionType";
    private static final String PROGRAM_RULE = "programRule";
    private static final String PROGRAM_STAGE = "programStage";
    private static final String DATA_ELEMENT = "dataElement";

    public static final Field<ProgramRuleAction, String> uid = Field.create(UID);
    public static final Field<ProgramRuleAction, String> code = Field.create(CODE);
    public static final Field<ProgramRuleAction, String> name = Field.create(NAME);
    public static final Field<ProgramRuleAction, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<ProgramRuleAction, String> created = Field.create(CREATED);
    public static final Field<ProgramRuleAction, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<ProgramRuleAction, String> data = Field.create(DATA);
    public static final Field<ProgramRuleAction, String> content = Field.create(CONTENT);
    public static final Field<ProgramRuleAction, String> location = Field.create(LOCATION);
    public static final Field<ProgramRuleAction, Boolean> deleted = Field.create(DELETED);


    public static final NestedField<ProgramRuleAction, ProgramRule> programRule = NestedField.create(PROGRAM_RULE);

    public static final NestedField<ProgramRuleAction, TrackedEntityAttribute> trackedEntityAttribute =
            NestedField.create(TRACKED_ENTITY_ATTRIBUTE);

    public static final NestedField<ProgramRuleAction, ProgramIndicator> programIndicator =
            NestedField.create(PROGRAM_INDICATOR);

    public static final NestedField<ProgramRuleAction, ProgramStageSection> programStageSection =
            NestedField.create(PROGRAM_STAGE_SECTION);

    public static final Field<ProgramRuleAction, ProgramRuleActionType> programRuleActionType =
            Field.create(PROGRAM_RULE_ACTION_TYPE);

    public static final NestedField<ProgramRuleAction, ProgramStage> programStage =
            NestedField.create(PROGRAM_STAGE);

    public static final NestedField<ProgramRuleAction, DataElement> dataElement =
            NestedField.create(DATA_ELEMENT);

    @Nullable
    @JsonProperty(DATA)
    public abstract String data();

    @Nullable
    @JsonProperty(CONTENT)
    public abstract String content();

    @Nullable
    @JsonProperty(LOCATION)
    public abstract String location();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_ATTRIBUTE)
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @Nullable
    @JsonProperty(PROGRAM_INDICATOR)
    public abstract ProgramIndicator programIndicator();

    @Nullable
    @JsonProperty(PROGRAM_STAGE_SECTION)
    public abstract ProgramStageSection programStageSection();

    @Nullable
    @JsonProperty(PROGRAM_RULE_ACTION_TYPE)
    public abstract ProgramRuleActionType programRuleActionType();

    @Nullable
    @JsonProperty(PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty(DATA_ELEMENT)
    public abstract DataElement dataElement();

    @Nullable
    @JsonProperty(PROGRAM_RULE)
    public abstract ProgramRule programRule();

    @JsonCreator
    public static ProgramRuleAction create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(DATA) String data,
            @JsonProperty(CONTENT) String content,
            @JsonProperty(LOCATION) String location,
            @JsonProperty(TRACKED_ENTITY_ATTRIBUTE) TrackedEntityAttribute trackedEntityAttribute,
            @JsonProperty(PROGRAM_INDICATOR) ProgramIndicator programIndicator,
            @JsonProperty(PROGRAM_STAGE_SECTION) ProgramStageSection programStageSection,
            @JsonProperty(PROGRAM_RULE_ACTION_TYPE) ProgramRuleActionType programRuleActionType,
            @JsonProperty(PROGRAM_STAGE) ProgramStage programStage,
            @JsonProperty(DATA_ELEMENT) DataElement dataElement,
            @JsonProperty(PROGRAM_RULE) ProgramRule programRule,
            @JsonProperty(DELETED) Boolean deleted) {
        return new AutoValue_ProgramRuleAction(
                uid, code, name, displayName, created, lastUpdated, deleted, data, content, location,
                trackedEntityAttribute, programIndicator, programStageSection, programRuleActionType,
                programStage, dataElement, programRule
        );
    }
}
