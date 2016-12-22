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
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramRuleAction.Builder.class)
public abstract class ProgramRuleAction extends BaseIdentifiableObject {
    private static final String JSON_PROPERTY_DATA = "data";
    private static final String JSON_PROPERTY_CONTENT = "content";
    private static final String JSON_PROPERTY_LOCATION = "location";
    private static final String JSON_PROPERTY_ATTRIBUTE = "trackedEntityAttribute";
    private static final String JSON_PROPERTY_PROGRAM_INDICATOR = "programIndicator";
    private static final String JSON_PROPERTY_PROGRAM_STAGE_SECTION = "programStageSection";
    private static final String JSON_PROPERTY_PROGRAM_RULE_ACTION_TYPE = "programRuleActionType";
    private static final String JSON_PROPERTY_PROGRAM_STAGE = "programStage";
    private static final String JSON_PROPERTY_DATA_ELEMENT = "dataElement";

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA)
    public abstract String data();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CONTENT)
    public abstract String content();

    @Nullable
    @JsonProperty(JSON_PROPERTY_LOCATION)
    public abstract String location();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ATTRIBUTE)
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_INDICATOR)
    public abstract ProgramIndicator programIndicator();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE_SECTION)
    public abstract ProgramStageSection programStageSection();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_RULE_ACTION_TYPE)
    public abstract ProgramRuleActionType programRuleActionType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
    public abstract ProgramStage programStage();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
    public abstract DataElement dataElement();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_DATA)
        public abstract Builder data(@Nullable String data);

        @JsonProperty(JSON_PROPERTY_CONTENT)
        public abstract Builder content(@Nullable String content);

        @JsonProperty(JSON_PROPERTY_LOCATION)
        public abstract Builder location(@Nullable String location);

        @JsonProperty(JSON_PROPERTY_ATTRIBUTE)
        public abstract Builder trackedEntityAttribute(
                @Nullable TrackedEntityAttribute trackedEntityAttribute);

        @JsonProperty(JSON_PROPERTY_PROGRAM_INDICATOR)
        public abstract Builder programIndicator(@Nullable ProgramIndicator programIndicator);

        @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE_SECTION)
        public abstract Builder programStageSection(
                @Nullable ProgramStageSection programStageSection);

        @JsonProperty(JSON_PROPERTY_PROGRAM_RULE_ACTION_TYPE)
        public abstract Builder programRuleActionType(
                @Nullable ProgramRuleActionType programRuleActionType);

        @JsonProperty(JSON_PROPERTY_PROGRAM_STAGE)
        public abstract Builder programStage(@Nullable ProgramStage programStage);

        @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
        public abstract Builder dataElement(@Nullable DataElement dataElement);

        public abstract ProgramRuleAction build();
    }
}
