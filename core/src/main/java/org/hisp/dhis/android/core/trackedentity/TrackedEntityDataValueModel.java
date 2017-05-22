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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;

import java.util.Date;

@AutoValue
public abstract class TrackedEntityDataValueModel extends BaseModel {
    public static final String TABLE = "TrackedEntityDataValue";

    public static class Columns extends BaseModel.Columns {
        public static final String EVENT = "event";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String STORED_BY = "storedBy";
        public static final String VALUE = "value";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String PROVIDED_ELSEWHERE = "providedElsewhere";
    }

    @NonNull
    public static TrackedEntityDataValueModel.Builder builder() {
        return new $$AutoValue_TrackedEntityDataValueModel.Builder();
    }

    @NonNull
    public static TrackedEntityDataValueModel create(Cursor cursor) {
        return AutoValue_TrackedEntityDataValueModel.createFromCursor(cursor);
    }

    @NonNull
    @ColumnName(Columns.EVENT)
    public abstract String event();

    @Nullable
    @ColumnName(Columns.CREATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @ColumnName(Columns.LAST_UPDATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(Columns.STORED_BY)
    public abstract String storedBy();

    @Nullable
    @ColumnName(Columns.VALUE)
    public abstract String value();

    @Nullable
    @ColumnName(Columns.PROVIDED_ELSEWHERE)
    public abstract Boolean providedElsewhere();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder created(@Nullable Date created);

        public abstract Builder lastUpdated(@Nullable Date lastUpdated);

        public abstract Builder dataElement(@Nullable String dataElement);

        public abstract Builder storedBy(@Nullable String storedBy);

        public abstract Builder value(@Nullable String value);

        public abstract Builder event(@Nullable String event);

        public abstract Builder providedElsewhere(@Nullable Boolean providedElsewhere);

        public abstract TrackedEntityDataValueModel build();
    }
}
