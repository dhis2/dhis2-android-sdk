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

import java.util.Date;

//TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_TrackedEntityDataValue.Builder.class)
public abstract class TrackedEntityDataValue {
    public final static String JSON_PROPERTY_DATA_ELEMENT = "dataElement";
    public final static String JSON_PROPERTY_STORED_BY = "storedBy";
    public final static String JSON_PROPERTY_VALUE = "value";
    public final static String JSON_PROPERTY_CREATED = "created";
    public final static String JSON_PROPERTY_LAST_UPDATED = "lastUpdated";
    public final static String JSON_PROPERTY_PROVIDED_ELSEWHERE = "providedElsewhere";

    @Nullable
    @JsonIgnore
    public abstract String event();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @JsonProperty(JSON_PROPERTY_STORED_BY)
    public abstract String storedBy();

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE)
    public abstract String value();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PROVIDED_ELSEWHERE)
    public abstract Boolean providedElsewhere();

    public static Builder builder() {
        return new AutoValue_TrackedEntityDataValue.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        @JsonProperty(JSON_PROPERTY_CREATED)
        public abstract Builder created(@Nullable Date created);

        @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
        public abstract Builder lastUpdated(@Nullable Date lastUpdated);

        @JsonProperty(JSON_PROPERTY_DATA_ELEMENT)
        public abstract Builder dataElement(@Nullable String dataElement);

        @JsonProperty(JSON_PROPERTY_STORED_BY)
        public abstract Builder storedBy(@Nullable String storedBy);

        @JsonProperty(JSON_PROPERTY_VALUE)
        public abstract Builder value(@Nullable String value);

        @JsonIgnore
        public abstract Builder event(@Nullable String event);

        @JsonProperty(JSON_PROPERTY_PROVIDED_ELSEWHERE)
        public abstract Builder providedElsewhere(@Nullable Boolean providedElsewhere);

        public abstract TrackedEntityDataValue build();
    }
}
