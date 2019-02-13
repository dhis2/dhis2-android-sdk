/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;

import java.util.Date;

@AutoValue
@JsonDeserialize(builder = AutoValue_TrackedEntityAttributeReservedValue.Builder.class)
public abstract class TrackedEntityAttributeReservedValue implements Model {

    @Nullable
    @JsonProperty()
    public abstract String ownerObject();

    @Nullable
    @JsonProperty()
    public abstract String ownerUid();

    @Nullable
    @JsonProperty()
    public abstract String key();

    @Nullable
    @JsonProperty()
    public abstract String value();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date expiryDate();

    @Nullable
    @JsonIgnore()
    public abstract String organisationUnit();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date temporalValidityDate();

    public static Builder builder() {
        return new $$AutoValue_TrackedEntityAttributeReservedValue.Builder();
    }

    public static TrackedEntityAttributeReservedValue create(Cursor cursor) {
        return $AutoValue_TrackedEntityAttributeReservedValue.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder ownerObject(String ownerObject);

        public abstract Builder ownerUid(String ownerUid);

        public abstract Builder key(String key);

        public abstract Builder value(String value);

        public abstract Builder created(Date created);

        public abstract Builder expiryDate(Date expiryDate);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder temporalValidityDate(Date temporalValidityDate);

        public abstract TrackedEntityAttributeReservedValue build();
    }
}