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
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

import java.util.Date;

@AutoValue
public abstract class ProgramRuleVariable extends BaseIdentifiableObject {
    private static final String PROGRAM_STAGE = "programStage";
    private static final String PROGRAM_RULE_VARIABLE_SOURCE_TYPE = "programRuleVariableSourceType";
    private static final String USE_CODE_FOR_OPTION_SET = "useCodeForOptionSet";
    private static final String PROGRAM = "program";
    private static final String DATA_ELEMENT = "dataElement";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";

    private static final Field<ProgramRuleVariable, String> uid = Field.create(UID);
    private static final Field<ProgramRuleVariable, String> code = Field.create(CODE);
    private static final Field<ProgramRuleVariable, String> name = Field.create(NAME);
    private static final Field<ProgramRuleVariable, String> displayName = Field.create(DISPLAY_NAME);
    private static final Field<ProgramRuleVariable, String> created = Field.create(CREATED);
    private static final Field<ProgramRuleVariable, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<ProgramRuleVariable, Boolean> useCodeForOptionSet
            = Field.create(USE_CODE_FOR_OPTION_SET);
    private static final Field<ProgramRuleVariable, ProgramRuleVariableSourceType> programRuleVariableSourceType
            = Field.create(PROGRAM_RULE_VARIABLE_SOURCE_TYPE);
    private static final NestedField<ProgramRuleVariable, ObjectWithUid> program
            = NestedField.create(PROGRAM);
    private static final NestedField<ProgramRuleVariable, ObjectWithUid> programStage
            = NestedField.create(PROGRAM_STAGE);
    private static final NestedField<ProgramRuleVariable, ObjectWithUid> dataElement
            = NestedField.create(DATA_ELEMENT);
    private static final NestedField<ProgramRuleVariable, ObjectWithUid> trackedEntityAttribute
            = NestedField.create(TRACKED_ENTITY_ATTRIBUTE);
    private static final Field<ProgramRuleVariable, Boolean> deleted
            = Field.create(DELETED);

    static final Fields<ProgramRuleVariable> allFields = Fields.<ProgramRuleVariable>builder().fields(
            uid, code, name, displayName, created, lastUpdated, deleted, programRuleVariableSourceType,
            useCodeForOptionSet, program.with(ObjectWithUid.uid), dataElement.with(ObjectWithUid.uid),
            programStage.with(ObjectWithUid.uid), trackedEntityAttribute.with(ObjectWithUid.uid)).build();

    @Nullable
    @JsonProperty(USE_CODE_FOR_OPTION_SET)
    public abstract Boolean useCodeForOptionSet();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract Program program();

    @Nullable
    @JsonProperty(PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty(DATA_ELEMENT)
    public abstract DataElement dataElement();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_ATTRIBUTE)
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @Nullable
    @JsonProperty(PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
    public abstract ProgramRuleVariableSourceType programRuleVariableSourceType();

    @JsonCreator
    public static ProgramRuleVariable create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(USE_CODE_FOR_OPTION_SET) Boolean useCodeForOptionSet,
            @JsonProperty(PROGRAM) Program program,
            @JsonProperty(PROGRAM_STAGE) ProgramStage programStage,
            @JsonProperty(DATA_ELEMENT) DataElement dataElement,
            @JsonProperty(TRACKED_ENTITY_ATTRIBUTE) TrackedEntityAttribute trackedEntityAttribute,
            @JsonProperty(PROGRAM_RULE_VARIABLE_SOURCE_TYPE) ProgramRuleVariableSourceType sourceType,
            @JsonProperty(DELETED) Boolean deleted) {
        return new AutoValue_ProgramRuleVariable(uid, code, name, displayName, created,
                lastUpdated, deleted, useCodeForOptionSet, program, programStage,
                dataElement, trackedEntityAttribute, sourceType);
    }
}