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

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.Date;

import androidx.annotation.Nullable;

@Deprecated
@AutoValue
public abstract class TrackedEntityAttributeReservedValueModel extends BaseModel {

    public static final String TABLE = "TrackedEntityAttributeReservedValue";

    public static class Columns extends BaseModel.Columns {
        public final static String OWNER_OBJECT = "ownerObject";
        public final static String OWNER_UID = "ownerUid";
        public final static String KEY = "key";
        public final static String VALUE = "value";
        public final static String CREATED = "created";
        public final static String EXPIRY_DATE = "expiryDate";
        public final static String ORGANISATION_UNIT = "organisationUnit";
        public final static String TEMPORAL_VALIDITY_DATE = "temporalValidityDate";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    OWNER_OBJECT, OWNER_UID, KEY, VALUE, CREATED, EXPIRY_DATE, ORGANISATION_UNIT,
                    TEMPORAL_VALIDITY_DATE);
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{OWNER_UID, VALUE, ORGANISATION_UNIT};
        }
    }

    public static TrackedEntityAttributeReservedValueModel create(Cursor cursor) {
        return AutoValue_TrackedEntityAttributeReservedValueModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $AutoValue_TrackedEntityAttributeReservedValueModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.OWNER_OBJECT)
    public abstract String ownerObject();

    @Nullable
    @ColumnName(Columns.OWNER_UID)
    public abstract String ownerUid();

    @Nullable
    @ColumnName(Columns.KEY)
    public abstract String key();

    @Nullable
    @ColumnName(Columns.VALUE)
    public abstract String value();

    @Nullable
    @ColumnName(Columns.CREATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @ColumnName(Columns.EXPIRY_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date expiryDate();

    @Nullable
    @ColumnName(Columns.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @ColumnName(Columns.TEMPORAL_VALIDITY_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date temporalValidityDate();

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder ownerObject(String ownerObject);

        public abstract Builder ownerUid(String ownerUid);

        public abstract Builder key(String key);

        public abstract Builder value(String value);

        public abstract Builder created(Date created);

        public abstract Builder expiryDate(Date expiryDate);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder temporalValidityDate(Date temporalValidityDate);

        public abstract TrackedEntityAttributeReservedValueModel build();
    }
}