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

package org.hisp.dhis.android.core.datavalue;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.common.BaseDeletableDataObject;
import org.hisp.dhis.android.core.common.State;

import java.util.Date;

@AutoValue
public abstract class DataValue extends BaseDeletableDataObject {

    @Nullable
    public abstract String dataElement();

    @Nullable
    public abstract String period();

    @Nullable
    public abstract String organisationUnit();

    @Nullable
    public abstract String categoryOptionCombo();

    @Nullable
    public abstract String attributeOptionCombo();

    @Nullable
    public abstract String value();

    @Nullable
    public abstract String storedBy();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    public abstract String comment();

    @Nullable
    public abstract Boolean followUp();

    @NonNull
    public static DataValue create(Cursor cursor) {
        return AutoValue_DataValue.createFromCursor(cursor);
    }

    public abstract DataValue.Builder toBuilder();

    public static DataValue.Builder builder() {
        return new $$AutoValue_DataValue.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder extends BaseDeletableDataObject.Builder<DataValue.Builder> {

        public Builder() {
            syncState(State.SYNCED);
        }

        public abstract DataValue.Builder dataElement(@NonNull String dataElement);

        public abstract DataValue.Builder period(@NonNull String period);

        public abstract DataValue.Builder organisationUnit(@NonNull String organisationUnit);

        public abstract DataValue.Builder categoryOptionCombo(@NonNull String categoryOptionCombo);

        public abstract DataValue.Builder attributeOptionCombo(@NonNull String attributeOptionCombo);

        public abstract DataValue.Builder value(@Nullable String value);

        public abstract DataValue.Builder storedBy(@Nullable String storedBy);

        public abstract DataValue.Builder created(@NonNull Date created);

        public abstract DataValue.Builder lastUpdated(@NonNull Date lastUpdated);

        public abstract DataValue.Builder comment(@Nullable String comment);

        public abstract DataValue.Builder followUp(@Nullable Boolean followUp);

        abstract DataValue autoBuild();

        // Auxiliary fields
        abstract Boolean deleted();

        public DataValue build() {
            if (deleted() == null) {
                deleted(false);
            }
            return autoBuild();
        }
    }
}
