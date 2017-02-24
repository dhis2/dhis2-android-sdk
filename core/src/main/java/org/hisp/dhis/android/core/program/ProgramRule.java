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

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class ProgramRule extends BaseIdentifiableObject {
    private static final String PROGRAM_STAGE = "programStage";
    private static final String PROGRAM = "program";
    private static final String PRIORITY = "priority";
    private static final String CONDITION = "condition";
    private static final String PROGRAM_RULE_ACTIONS = "programRuleActions";

    public static final Field<ProgramRule, String> uid
            = Field.create(UID);
    public static final Field<ProgramRule, String> code
            = Field.create(CODE);
    public static final Field<ProgramRule, String> name
            = Field.create(NAME);
    public static final Field<ProgramRule, String> displayName
            = Field.create(DISPLAY_NAME);
    public static final Field<ProgramRule, String> created =
            Field.create(CREATED);
    public static final Field<ProgramRule, String> lastUpdated =
            Field.create(LAST_UPDATED);
    public static final Field<ProgramRule, Integer> priority
            = Field.create(PRIORITY);
    public static final Field<ProgramRule, String> condition
            = Field.create(CONDITION);
    public static final NestedField<ProgramRule, Program> program
            = NestedField.create(PROGRAM);
    public static final NestedField<ProgramRule, ProgramStage> programStage
            = NestedField.create(PROGRAM_STAGE);
    public static final NestedField<ProgramRule, ProgramRuleAction> programRuleActions
            = NestedField.create(PROGRAM_RULE_ACTIONS);
    public static final Field<ProgramRule, Boolean> deleted
            = Field.create(DELETED);


    @Nullable
    @JsonProperty(PRIORITY)
    public abstract Integer priority();

    @Nullable
    @JsonProperty(CONDITION)
    public abstract String condition();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract Program program();

    @Nullable
    @JsonProperty(PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty(PROGRAM_RULE_ACTIONS)
    public abstract List<ProgramRuleAction> programRuleActions();

    @JsonCreator
    public static ProgramRule create(@JsonProperty(UID) String uid,
                                     @JsonProperty(CODE) String code,
                                     @JsonProperty(NAME) String name,
                                     @JsonProperty(DISPLAY_NAME) String displayName,
                                     @JsonProperty(CREATED) Date created,
                                     @JsonProperty(LAST_UPDATED) Date lastUpdated,
                                     @JsonProperty(PRIORITY) Integer priority,
                                     @JsonProperty(CONDITION) String condition,
                                     @JsonProperty(PROGRAM) Program program,
                                     @JsonProperty(PROGRAM_STAGE) ProgramStage programStage,
                                     @JsonProperty(PROGRAM_RULE_ACTIONS) List<ProgramRuleAction> programRuleActions,
                                     @JsonProperty(DELETED) Boolean deleted) {
        return new AutoValue_ProgramRule(
                uid, code, name, displayName,
                created, lastUpdated, deleted, priority, condition,
                program, programStage, programRuleActions);
    }
}