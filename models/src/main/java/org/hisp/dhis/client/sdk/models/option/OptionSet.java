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

package org.hisp.dhis.client.sdk.models.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.ValueType;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_OptionSet.Builder.class)
public abstract class OptionSet extends BaseIdentifiableObject {
    private static final String JSON_PROPERTY_VERSION = "version";
    private static final String JSON_PROPERTY_VALUE_TYPE = "valueType";
    private static final String JSON_PROPERTY_OPTIONS = "options";

    @Nullable
    @JsonProperty(JSON_PROPERTY_VERSION)
    public abstract Integer version();

    @Nullable
    @JsonProperty(JSON_PROPERTY_OPTIONS)
    public abstract List<Option> options();

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
    public abstract ValueType valueType();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_OptionSet.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_VERSION)
        public abstract Builder version(@Nullable Integer version);

        @JsonProperty(JSON_PROPERTY_OPTIONS)
        public abstract Builder options(@Nullable List<Option> options);

        @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
        public abstract Builder valueType(@Nullable ValueType valueType);

        abstract List<Option> options();

        abstract OptionSet autoBuild();

        public OptionSet build() {
            if (options() != null) {
                options(Collections.unmodifiableList(options()));
            }

            return autoBuild();
        }
    }
}
