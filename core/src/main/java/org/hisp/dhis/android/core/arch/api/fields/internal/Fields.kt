/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.arch.api.fields.internal;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;

@AutoValue
public abstract class Fields<T> {

    @NonNull
    public abstract List<Property<T, ?>> fields();

    @NonNull
    public static <K> Fields.Builder<K> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private final List<Property<T, ?>> fields;

        Builder() {
            this.fields = new ArrayList<>();
        }

        @SafeVarargs
        public final Builder<T> fields(@NonNull Property<T, ?>... properties) {
            if (properties == null || properties.length == 0) {
                throw new IllegalArgumentException("properties == null or properties.length == 0");
            }

            fields.addAll(Arrays.asList(properties));
            return this;
        }

        public final Builder<T> fields(@NonNull Collection<Property<T, ?>> properties) {
            if (properties == null || properties.isEmpty()) {
                throw new IllegalArgumentException("properties null or empty collection");
            }

            fields.addAll(properties);
            return this;
        }

        public final Fields<T> build() {
            return new AutoValue_Fields<>(Collections.unmodifiableList(fields));
        }
    }
}
