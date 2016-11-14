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

package org.hisp.dhis.android.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.models.common.BaseNameableObject;
import org.hisp.dhis.android.models.common.ValueType;
import org.hisp.dhis.android.models.trackedentity.TrackedEntityAttribute;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramTrackedEntityAttribute.Builder.class)
public abstract class ProgramTrackedEntityAttribute extends BaseNameableObject {
    private static final String JSON_PROPERTY_MANDATORY = "mandatory";
    private static final String JSON_PROPERTY_TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
    private static final String JSON_PROPERTY_VALUE_TYPE = "valueType";
    private static final String JSON_PROPERTY_ALLOW_FUTURE_DATE = "allowFutureDate";
    private static final String JSON_PROPERTY_DISPLAY_IN_LIST = "displayInList";

    @Nullable
    @JsonProperty(JSON_PROPERTY_MANDATORY)
    public abstract Boolean mandatory();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_ATTRIBUTE)
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ALLOW_FUTURE_DATE)
    public abstract Boolean allowFutureDate();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_IN_LIST)
    public abstract Boolean displayInList();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {
        @JsonProperty(JSON_PROPERTY_MANDATORY)
        public abstract Builder mandatory(@Nullable Boolean mandatory);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_ATTRIBUTE)
        public abstract Builder trackedEntityAttribute(
                @Nullable TrackedEntityAttribute trackedEntityAttribute);

        @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
        public abstract Builder valueType(@Nullable ValueType valueType);

        @JsonProperty(JSON_PROPERTY_ALLOW_FUTURE_DATE)
        public abstract Builder allowFutureDate(@Nullable Boolean allowFutureFate);

        @JsonProperty(JSON_PROPERTY_DISPLAY_IN_LIST)
        public abstract Builder displayInList(@Nullable Boolean displayInList);

        abstract ProgramTrackedEntityAttribute build();
    }
}
