/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.imports.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
@JsonRootName("conflicts")
public abstract class ImportConflict {
    private static final String OBJECT = "object";
    private static final String VALUE = "value";
    private static final String ERROR_CODE = "errorCode";
    private static final String PROPERTY = "property";
    private static final String INDEXES = "indexes";

    @NonNull
    @JsonProperty(OBJECT)
    public abstract String object();

    @NonNull
    @JsonProperty(VALUE)
    public abstract String value();

    @Nullable
    @JsonProperty(ERROR_CODE)
    public abstract String errorCode();

    @Nullable
    @JsonProperty(PROPERTY)
    public abstract String property();

    @Nullable
    @JsonProperty(INDEXES)
    public abstract List<Integer> indexes();

    public static ImportConflict create(
            @JsonProperty(OBJECT) String object,
            @JsonProperty(VALUE) String value) {
        return builder()
                .object(object)
                .value(value)
                .build();
    }

    @JsonCreator
    public static ImportConflict create(
            @JsonProperty(OBJECT) String object,
            @JsonProperty(VALUE) String value,
            @JsonProperty(ERROR_CODE) String errorCode,
            @JsonProperty(PROPERTY) String property,
            @JsonProperty(INDEXES) List<Integer> indexes) {
        return builder()
                .object(object)
                .value(value)
                .errorCode(errorCode)
                .property(property)
                .indexes(indexes)
                .build();
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_ImportConflict.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder object(String object);

        public abstract Builder value(String value);

        public abstract Builder errorCode(String errorCode);

        public abstract Builder property(String property);

        public abstract Builder indexes(List<Integer> indexes);

        public abstract ImportConflict build();
    }
}
