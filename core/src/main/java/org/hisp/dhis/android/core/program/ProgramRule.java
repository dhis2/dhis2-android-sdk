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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.util.Collections;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramRule.Builder.class)
public abstract class ProgramRule extends BaseIdentifiableObject {
    private static final String JSON_PROPERTY_PROGRAM_STAGE = "programStage";
    private static final String JSON_PROPERTY_PROGRAM = "program";
    private static final String JSON_PROPERTY_PRIORITY = "priority";
    private static final String JSON_PROPERTY_CONDITION = "condition";
    private static final String JSON_PROPERTY_PROGRAM_RULE_ACTIONS = "programRuleActions";

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM)
    public abstract Program program();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PRIORITY)
    public abstract Integer priority();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONDITION)
    public abstract String condition();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_RULE_ACTIONS)
    public abstract List<ProgramRuleAction> programRuleActions();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
        public abstract Builder programStage(@Nullable ProgramStage programStage);

        @JsonProperty(JSON_PROPERTY_PROGRAM)
        public abstract Builder program(@Nullable Program program);

        @JsonProperty(JSON_PROPERTY_PRIORITY)
        public abstract Builder priority(@Nullable Integer priority);

        @JsonProperty(JSON_PROPERTY_CONDITION)
        public abstract Builder condition(@Nullable String condition);

        @JsonProperty(JSON_PROPERTY_PROGRAM_RULE_ACTIONS)
        public abstract Builder programRuleActions(@Nullable List<ProgramRuleAction> programRuleActions);

        abstract ProgramRule autoBuild();

        abstract List<ProgramRuleAction> programRuleActions();

        public ProgramRule build() {
            if (programRuleActions() != null) {
                programRuleActions(Collections.unmodifiableList(programRuleActions()));
            }

            return autoBuild();
        }
    }
}