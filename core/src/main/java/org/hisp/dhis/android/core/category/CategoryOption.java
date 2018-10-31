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

package org.hisp.dhis.android.core.category;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.data.database.AccessColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;

import java.util.Date;

@AutoValue
@JsonDeserialize(builder = AutoValue_CategoryOption.Builder.class)
public abstract class CategoryOption extends BaseNameableObject implements Model {

    // TODO move to base class after whole object refactor
    @Override
    @Nullable
    @ColumnName(BaseModel.Columns.ID)
    @JsonIgnore()
    public abstract Long id();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date startDate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date endDate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(AccessColumnAdapter.class)
    public abstract Access access();


    public abstract ContentValues toContentValues();

    static CategoryOption create(Cursor cursor) {
        return $AutoValue_CategoryOption.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_CategoryOption.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder startDate(@Nullable Date startDate);

        public abstract Builder endDate(@Nullable Date endDate);

        public abstract Builder access(@Nullable Access access);

        abstract CategoryOption autoBuild();

        public CategoryOption build() {
            return autoBuild();
        }
    }
}