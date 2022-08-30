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

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.WHONutritionComponentColumnAdapter;
import org.hisp.dhis.android.core.arch.json.internal.AnalyticsTEIIndicatorDeserializer;
import org.hisp.dhis.android.core.common.CoreObject;

@AutoValue
@JsonDeserialize(using = AnalyticsTEIIndicatorDeserializer.class)
public abstract class AnalyticsTeiIndicator implements CoreObject {

    @Nullable
    public abstract String teiSetting();

    @Nullable
    @ColumnAdapter(WHONutritionComponentColumnAdapter.class)
    public abstract WHONutritionComponent whoComponent();

    @Nullable
    public abstract String programStage();

    public abstract String indicator();

    public abstract Builder toBuilder();

    public static AnalyticsTeiIndicator create(Cursor cursor) {
        return AutoValue_AnalyticsTeiIndicator.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new AutoValue_AnalyticsTeiIndicator.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        public abstract Builder id(Long id);

        public abstract Builder teiSetting(String teiSetting);

        public abstract Builder whoComponent(WHONutritionComponent component);

        public abstract Builder programStage(String programStage);

        public abstract Builder indicator(String indicator);

        public abstract AnalyticsTeiIndicator build();
    }
}