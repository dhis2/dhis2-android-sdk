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

package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_TrackedEntityAttributeValue.Builder.class)
public abstract class TrackedEntityAttributeValue {
    private static final String JSON_PROPERTY_ATTRIBUTE = "attribute";
    private static final String JSON_PROPERTY_VALUE = "value";

    @JsonIgnore
    @Nullable
    public abstract TrackedEntityInstance trackedEntityInstance();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ATTRIBUTE)
    public abstract String trackedEntityAttribute();

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE)
    public abstract String value();

    public static Builder builder() {
        return new AutoValue_TrackedEntityAttributeValue.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonIgnore
        public abstract Builder trackedEntityInstance(
                @Nullable TrackedEntityInstance trackedEntityInstance);

        @JsonProperty(JSON_PROPERTY_ATTRIBUTE)
        public abstract Builder trackedEntityAttribute(String trackedEntityAttribute);

        @JsonProperty(JSON_PROPERTY_VALUE)
        public abstract Builder value(String value);

        public abstract TrackedEntityAttributeValue build();
    }
}
