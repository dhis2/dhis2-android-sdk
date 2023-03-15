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

package org.hisp.dhis.android.core.domain.aggregated.data.internal;

import android.database.Cursor;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.PeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Date;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_AggregatedDataSync.Builder.class)
abstract class AggregatedDataSync extends BaseObject {

    @NonNull
    abstract String dataSet();

    @NonNull
    @ColumnAdapter(PeriodTypeColumnAdapter.class)
    abstract PeriodType periodType();

    @NonNull
    abstract Integer pastPeriods();

    @NonNull
    abstract Integer futurePeriods();

    @NonNull
    abstract Integer dataElementsHash();

    @NonNull
    abstract Integer organisationUnitsHash();

    @NonNull
    @ColumnAdapter(DbDateColumnAdapter.class)
    abstract Date lastUpdated();

    @NonNull
    static AggregatedDataSync create(Cursor cursor) {
        return AutoValue_AggregatedDataSync.createFromCursor(cursor);
    }

    static Builder builder() {
        return new $$AutoValue_AggregatedDataSync.Builder();
    }

    abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    static abstract class Builder extends BaseObject.Builder<Builder> {

        abstract Builder dataSet(String dataSet);

        abstract Builder periodType(PeriodType periodType);

        abstract Builder pastPeriods(Integer pastPeriods);

        abstract Builder futurePeriods(Integer futurePeriods);

        abstract Builder dataElementsHash(Integer dataElementsHash);

        abstract Builder organisationUnitsHash(Integer organisationUnitHash);

        abstract Builder lastUpdated(Date lastUpdated);

        abstract AggregatedDataSync build();
    }
}