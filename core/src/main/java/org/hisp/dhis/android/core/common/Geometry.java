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

package org.hisp.dhis.android.core.common;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbGeometryTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.json.internal.GeometryTypeDeserializer;
import org.hisp.dhis.android.core.arch.json.internal.GeometryTypeSerializer;
import org.hisp.dhis.android.core.arch.json.internal.JsonElementStringConverter;
import org.hisp.dhis.android.core.arch.json.internal.StringJsonElementSerializer;

@AutoValue
@JsonDeserialize(builder = AutoValue_Geometry.Builder.class)
public abstract class Geometry {

    @Nullable
    @JsonProperty()
    @JsonSerialize(using = GeometryTypeSerializer.class)
    @ColumnAdapter(DbGeometryTypeColumnAdapter.class)
    public abstract FeatureType type();

    @Nullable
    @JsonProperty()
    @JsonSerialize(using = StringJsonElementSerializer.class)
    public abstract String coordinates();

    @NonNull
    public static Geometry create(Cursor cursor) {
        return AutoValue_Geometry.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_Geometry.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {

        @JsonDeserialize(using = GeometryTypeDeserializer.class)
        public abstract Builder type(FeatureType type);

        @JsonDeserialize(converter = JsonElementStringConverter.class)
        public abstract Builder coordinates(String coordinates);

        public abstract Geometry build();
    }
}