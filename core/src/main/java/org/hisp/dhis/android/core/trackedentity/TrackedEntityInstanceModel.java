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

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableDataModel;

@AutoValue
public abstract class TrackedEntityInstanceModel extends BaseIdentifiableDataModel {
    public static final String TABLE = "TrackedEntityInstance";

    public static class Columns extends BaseIdentifiableDataModel.Columns {
        public static final String UID = "uid";
        public static final String CREATED_AT_CLIENT = "createdAtClient";
        public static final String LAST_UPDATED_AT_CLIENT = "lastUpdatedAtClient";
        public static final String ORGANISATION_UNIT = "organisationUnit";
        public static final String TRACKED_ENTITY = "trackedEntity";
    }

    @NonNull
    public static TrackedEntityInstanceModel.Builder builder() {
        return new $$AutoValue_TrackedEntityInstanceModel.Builder();
    }

    @NonNull
    public static TrackedEntityInstanceModel create(Cursor cursor) {
        return AutoValue_TrackedEntityInstanceModel.createFromCursor(cursor);
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.UID)
    public abstract String uid();

    @Nullable
    @ColumnName(Columns.CREATED_AT_CLIENT)
    public abstract String createdAtClient();

    @Nullable
    @ColumnName(Columns.LAST_UPDATED_AT_CLIENT)
    public abstract String lastUpdatedAtClient();

    @Nullable
    @ColumnName(Columns.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY)
    public abstract String trackedEntity();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableDataModel.Builder<Builder> {
        public abstract Builder uid(@NonNull String uid);

        public abstract Builder createdAtClient(@Nullable String createdAtClient);

        public abstract Builder lastUpdatedAtClient(@Nullable String lastUpdatedAtClient);

        public abstract Builder organisationUnit(@Nullable String organisationUnit);

        public abstract Builder trackedEntity(@Nullable String trackedEntity);

        public abstract TrackedEntityInstanceModel build();
    }
}
