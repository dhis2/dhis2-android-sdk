/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.option;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DbValueTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreOptionListAdapter;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_OptionSet.Builder.class)
public abstract class OptionSet extends BaseIdentifiableObject implements Model {

    // TODO move to base class after whole object refactor
    @Override
    @Nullable
    @ColumnName(BaseModel.Columns.ID)
    @JsonIgnore()
    public abstract Long id();

    @Nullable
    @JsonProperty()
    public abstract Integer version();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbValueTypeColumnAdapter.class)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreOptionListAdapter.class)
    public abstract List<Option> options();

    public static Builder builder() {
        return new $$AutoValue_OptionSet.Builder();
    }

    static OptionSet create(Cursor cursor) {
        return $AutoValue_OptionSet.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder version(@Nullable Integer version);

        public abstract Builder valueType(@Nullable ValueType valueType);

        public abstract Builder options(@Nullable List<Option> options);

        public abstract OptionSet build();
    }
}
