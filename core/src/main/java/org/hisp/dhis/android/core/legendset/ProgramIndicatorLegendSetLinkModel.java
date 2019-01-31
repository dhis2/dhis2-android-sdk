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
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.utils.Utils;

@AutoValue
public abstract class ProgramIndicatorLegendSetLinkModel extends BaseModel {
    public static final String TABLE = "ProgramIndicatorLegendSetLink";

    public static class Columns extends BaseModel.Columns {
        public static final String PROGRAM_INDICATOR = "programIndicator";
        public static final String LEGEND_SET = "legendSet";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    PROGRAM_INDICATOR, LEGEND_SET);
        }
    }

    public static ProgramIndicatorLegendSetLinkModel create(Cursor cursor) {
        return AutoValue_ProgramIndicatorLegendSetLinkModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramIndicatorLegendSetLinkModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.PROGRAM_INDICATOR)
    public abstract String programIndicator();

    @Nullable
    @ColumnName(Columns.LEGEND_SET)
    public abstract String legendSet();

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder programIndicator(String programIndicator);

        public abstract Builder legendSet(String legendSet);

        public abstract ProgramIndicatorLegendSetLinkModel build();
    }
}
