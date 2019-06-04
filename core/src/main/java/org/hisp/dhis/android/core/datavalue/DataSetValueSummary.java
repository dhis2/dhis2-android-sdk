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

package org.hisp.dhis.android.core.datavalue;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DbStateColumnAdapter;

@AutoValue
public abstract class DataSetValueSummary implements Model {

    @NonNull
    public abstract String dataSet();

    @NonNull
    public abstract String period();

    @NonNull
    public abstract String organisationUnit();

    @NonNull
    public abstract String attributeOptionCombo();

    @Nullable
    @ColumnAdapter(DbStateColumnAdapter.class)
    public abstract State state();

    @NonNull
    public static DataSetValueSummary create(Cursor cursor) {
        return AutoValue_DataSetValueSummary.createFromCursor(cursor);
    }

    public abstract DataSetValueSummary.Builder toBuilder();

    public static DataSetValueSummary.Builder builder() {
        return new $AutoValue_DataSetValueSummary.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder dataSet(String dataSet);

        public abstract Builder period(String period);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder attributeOptionCombo(String attributeOptionCombo);

        public abstract Builder state(State state);

        public abstract DataSetValueSummary build();
    }
}
