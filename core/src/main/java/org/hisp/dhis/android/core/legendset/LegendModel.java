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

package org.hisp.dhis.android.core.legendset;

import android.database.Cursor;
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.utils.Utils;

@Deprecated
@AutoValue
public abstract class LegendModel extends BaseIdentifiableObjectModel {

    public static final String TABLE = "Legend";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public final static String START_VALUE = "startValue";
        public final static String END_VALUE = "endValue";
        public final static String COLOR = "color";
        public final static String LEGEND_SET = "legendSet";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), START_VALUE, END_VALUE, COLOR, LEGEND_SET);
        }
    }

    public static LegendModel create(Cursor cursor) {
        return AutoValue_LegendModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $AutoValue_LegendModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.START_VALUE)
    public abstract Double startValue();

    @Nullable
    @ColumnName(Columns.END_VALUE)
    public abstract Double endValue();

    @Nullable
    @ColumnName(Columns.COLOR)
    public abstract String color();

    @Nullable
    @ColumnName(Columns.LEGEND_SET)
    public abstract String legendSet();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {
        public abstract Builder startValue(Double startValue);

        public abstract Builder endValue(Double endValue);

        public abstract Builder color(String color);

        public abstract Builder legendSet(String legendSet);

        public abstract LegendModel build();
    }
}