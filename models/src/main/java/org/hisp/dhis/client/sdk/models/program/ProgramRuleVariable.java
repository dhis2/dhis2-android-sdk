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

package org.hisp.dhis.client.sdk.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramRuleVariable.Builder.class)
public abstract class ProgramRuleVariable extends BaseIdentifiableObject {
    private static final String JSON_PROPERTY_PROGRAM_STAGE = "programStage";
    private static final String JSON_PROPERTY_PROGRAM_RULE_VARIABLE_SOURCE_TYPE = "programRuleVariableSourceType";
    private static final String JSON_PROPERTY_USE_CODE_FOR_OPTION_SET = "useCodeForOptionSet";
    private static final String JSON_PROPERTY_PROGRAM = "program";
    private static final String JSON_PROPERTY_DATA_ELEMENT = "dataElement";
    private static final String JSON_PROPERTY_TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
    public abstract ProgramRuleVariableSourceType programRuleVariableSourceType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_USE_CODE_FOR_OPTION_SET)
    public abstract Boolean useCodeForOptionSet();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM)
    public abstract Program program();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
    public abstract DataElement dataElement();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_ATTRIBUTE)
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
        public abstract Builder programStage(@Nullable ProgramStage programStage);

        @JsonProperty(JSON_PROPERTY_PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
        public abstract Builder programRuleVariableSourceType(
                @Nullable ProgramRuleVariableSourceType programRuleVariableSourceType);

        @JsonProperty(JSON_PROPERTY_USE_CODE_FOR_OPTION_SET)
        public abstract Builder useCodeForOptionSet(@Nullable Boolean useCodeForOptionSet);

        @JsonProperty(JSON_PROPERTY_PROGRAM)
        public abstract Builder program(@Nullable Program program);

        @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
        public abstract Builder dataElement(@Nullable DataElement dataElement);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_ATTRIBUTE)
        public abstract Builder trackedEntityAttribute(
                @Nullable TrackedEntityAttribute trackedEntityAttribute);

        public abstract ProgramRuleVariable build();
    }
}