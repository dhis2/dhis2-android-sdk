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

package org.hisp.dhis.android.core.settings;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.AnalyticsTeiWHONutritionGenderColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.WHONutritionChartTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreAnalyticsTeiWHODataItemColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;

@AutoValue
@JsonDeserialize(builder = AutoValue_AnalyticsTeiWHONutritionData.Builder.class)
public abstract class AnalyticsTeiWHONutritionData implements CoreObject {

    @Nullable
    public abstract String teiSetting();

    @ColumnAdapter(WHONutritionChartTypeColumnAdapter.class)
    public abstract WHONutritionChartType chartType();

    @ColumnAdapter(AnalyticsTeiWHONutritionGenderColumnAdapter.class)
    public abstract AnalyticsTeiWHONutritionGender gender();

    @ColumnAdapter(IgnoreAnalyticsTeiWHODataItemColumnAdapter.class)
    public abstract AnalyticsTeiWHONutritionItem x();

    @ColumnAdapter(IgnoreAnalyticsTeiWHODataItemColumnAdapter.class)
    public abstract AnalyticsTeiWHONutritionItem y();

    public static AnalyticsTeiWHONutritionData create(Cursor cursor) {
        return AutoValue_AnalyticsTeiWHONutritionData.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_AnalyticsTeiWHONutritionData.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder id(Long id);

        public abstract Builder teiSetting(String teiSetting);

        public abstract Builder chartType(WHONutritionChartType chartType);

        public abstract Builder gender(AnalyticsTeiWHONutritionGender gender);

        public abstract Builder x(AnalyticsTeiWHONutritionItem x);

        public abstract Builder y(AnalyticsTeiWHONutritionItem y);

        public abstract AnalyticsTeiWHONutritionData build();
    }
}