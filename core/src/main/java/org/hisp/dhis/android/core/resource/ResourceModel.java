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
package org.hisp.dhis.android.core.resource;

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
public abstract class ResourceModel extends BaseModel {
    public static final String TABLE = "Resource";

    public static class Columns extends BaseModel.Columns {
        public static final String RESOURCE_TYPE = "resourceType";
        public static final String LAST_SYNCED = "lastSynced";
    }

    public enum Type {
        EVENT,
        SYSTEM_INFO,
        USER,
        ORGANISATION_UNIT,
        PROGRAM,
        OPTION_SET,
        TRACKED_ENTITY,
        TRACKED_ENTITY_INSTANCE,
        CATEGORY,
        CATEGORY_COMBO,
        RELATIONSHIP_TYPE,
        TRACKED_ENTITY_ATTRIBUTE,
        DATA_ELEMENT
    }

    @Nullable
    @ColumnName(Columns.RESOURCE_TYPE)
    public abstract String resourceType();

    @Nullable
    @ColumnName(Columns.LAST_SYNCED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastSynced();

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static ResourceModel create(Cursor cursor) {
        return AutoValue_ResourceModel.createFromCursor(cursor);
    }

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_ResourceModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder resourceType(@Nullable String resourceType);

        public abstract Builder lastSynced(@Nullable Date lastSynced);

        public abstract ResourceModel build();
    }

}
