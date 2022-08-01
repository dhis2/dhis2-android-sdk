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

package org.hisp.dhis.android.core.dataset;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreBooleanColumnAdapter;
import org.hisp.dhis.android.core.common.BaseDeletableDataObject;
import org.hisp.dhis.android.core.common.State;

import java.util.Date;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_DataSetCompleteRegistration.Builder.class)
public abstract class DataSetCompleteRegistration extends BaseDeletableDataObject {

    @JsonProperty
    public abstract String period();

    @JsonProperty
    public abstract String dataSet();

    @JsonProperty
    public abstract String organisationUnit();

    @JsonProperty
    public abstract String attributeOptionCombo();

    @Nullable
    @JsonProperty
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date date();

    @Nullable
    @JsonProperty
    public abstract String storedBy();

    @Nullable
    @JsonProperty
    @ColumnAdapter(IgnoreBooleanColumnAdapter.class)
    abstract Boolean completed();

    @NonNull
    public static DataSetCompleteRegistration create(Cursor cursor) {
        return AutoValue_DataSetCompleteRegistration.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $$AutoValue_DataSetCompleteRegistration.Builder();
    }


    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseDeletableDataObject.Builder<DataSetCompleteRegistration.Builder> {

        public Builder() {
            syncState(State.SYNCED);
        }

        public abstract Builder period(@NonNull String period);

        public abstract Builder dataSet(@NonNull String dataSet);

        public abstract Builder organisationUnit(@NonNull String organisationUnit);

        public abstract Builder attributeOptionCombo(@NonNull String attributeOptionCombo);

        @JsonFormat(pattern = "yyyy-MM-dd")
        public abstract Builder date(@Nullable Date date);

        public abstract Builder storedBy(@Nullable String storedBy);

        abstract Builder completed(@Nullable Boolean completed);

        abstract DataSetCompleteRegistration autoBuild();

        // Auxiliary fields to access values
        abstract Boolean deleted();
        abstract Boolean completed();

        public DataSetCompleteRegistration build() {
            if (completed() == null) {
                completed(true);
            }
            if (deleted() == null) {
                deleted(!completed());
            }
            return autoBuild();
        }
    }
}
