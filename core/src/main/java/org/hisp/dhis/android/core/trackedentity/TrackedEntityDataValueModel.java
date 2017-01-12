/*
 * Copyright (c) 2016, University of Oslo
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

import java.util.Date;

import static org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueContract.Columns.CREATED;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueContract.Columns.DATA_ELEMENT;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueContract.Columns.EVENT;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueContract.Columns.LAST_UPDATED;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueContract.Columns.PROVIDED_ELSEWHERE;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueContract.Columns.STORED_BY;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueContract.Columns.VALUE;

//TODO: Tests
@AutoValue
public abstract class TrackedEntityDataValueModel {

    @NonNull
    public static TrackedEntityDataValueModel.Builder builder() {
        return new $$AutoValue_TrackedEntityDataValueModel.Builder();
    }

    @NonNull
    public static TrackedEntityDataValueModel create(Cursor cursor) {
        return AutoValue_TrackedEntityDataValueModel.createFromCursor(cursor);
    }

    @NonNull
    @ColumnName(EVENT)
    public abstract String event();

    @Nullable
    @ColumnName(CREATED)
    public abstract Date created();

    @Nullable
    @ColumnName(LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @ColumnName(DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(STORED_BY)
    public abstract String storedBy();

    @Nullable
    @ColumnName(VALUE)
    public abstract String value();

    @Nullable
    @ColumnName(PROVIDED_ELSEWHERE)
    public abstract Boolean providedElsewhere();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder {

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
