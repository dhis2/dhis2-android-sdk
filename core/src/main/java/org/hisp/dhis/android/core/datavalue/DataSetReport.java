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
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DbPeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbStateColumnAdapter;
import org.hisp.dhis.android.core.datavalue.internal.DataSetReportSQLStatementBuilder;
import org.hisp.dhis.android.core.period.PeriodType;

@AutoValue
public abstract class DataSetReport implements Model {

    /* ColumnName set explicitly because it is used in paging as the PAGING_KEY */
    @Override
    @Nullable
    @ColumnName(DataSetReportSQLStatementBuilder.DATAVALUE_ID)
    public abstract Long id();

    @NonNull
    public abstract String dataSetUid();

    @NonNull
    public abstract String dataSetDisplayName();

    @NonNull
    public abstract String period();

    @NonNull
    @ColumnAdapter(DbPeriodTypeColumnAdapter.class)
    public abstract PeriodType periodType();

    @NonNull
    public abstract String organisationUnitUid();

    @NonNull
    public abstract String organisationUnitDisplayName();

    @NonNull
    public abstract String attributeOptionComboUid();

    @NonNull
    public abstract String attributeOptionComboDisplayName();

    @NonNull
    public abstract Integer valueCount();

    @Nullable
    @ColumnAdapter(DbStateColumnAdapter.class)
    public abstract State state();

    @NonNull
    public static DataSetReport create(Cursor cursor) {
        return AutoValue_DataSetReport.createFromCursor(cursor);
    }

    public abstract DataSetReport.Builder toBuilder();

    public static DataSetReport.Builder builder() {
        return new $AutoValue_DataSetReport.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder dataSetUid(String dataSetUid);

        public abstract Builder dataSetDisplayName(String dataSetDisplayName);

        public abstract Builder period(String period);

        public abstract Builder periodType(PeriodType periodType);

        public abstract Builder organisationUnitUid(String organisationUnitUid);

        public abstract Builder organisationUnitDisplayName(String organisationUnitDisplayName);

        public abstract Builder attributeOptionComboUid(String attributeOptionComboUid);

        public abstract Builder attributeOptionComboDisplayName(String attributeOptionComboDisplayName);

        public abstract Builder valueCount(Integer valueCount);

        public abstract Builder state(State state);

        public abstract DataSetReport build();
    }
}
