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

package org.hisp.dhis.android.core.indicator;

import android.database.Cursor;
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.utils.Utils;

@Deprecated
@AutoValue
public abstract class IndicatorModel extends BaseNameableObjectModel {

    public static final String TABLE = "Indicator";

    public static class Columns extends BaseNameableObjectModel.Columns {
        public final static String ANNUALIZED = "annualized";
        public final static String INDICATOR_TYPE = "indicatorType";
        public final static String NUMERATOR = "numerator";
        public final static String NUMERATOR_DESCRIPTION = "numeratorDescription";
        public final static String DENOMINATOR = "denominator";
        public final static String DENOMINATOR_DESCRIPTION = "denominatorDescription";
        public final static String URL = "url";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    ANNUALIZED, INDICATOR_TYPE, NUMERATOR, NUMERATOR_DESCRIPTION,
                    DENOMINATOR, DENOMINATOR_DESCRIPTION, URL);
        }
    }

    public static IndicatorModel create(Cursor cursor) {
        return AutoValue_IndicatorModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $AutoValue_IndicatorModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.ANNUALIZED)
    public abstract Boolean annualized();

    @Nullable
    @ColumnName(Columns.INDICATOR_TYPE)
    public abstract String indicatorType();

    @Nullable
    @ColumnName(Columns.NUMERATOR)
    public abstract String numerator();

    @Nullable
    @ColumnName(Columns.NUMERATOR_DESCRIPTION)
    public abstract String numeratorDescription();

    @Nullable
    @ColumnName(Columns.DENOMINATOR)
    public abstract String denominator();

    @Nullable
    @ColumnName(Columns.DENOMINATOR_DESCRIPTION)
    public abstract String denominatorDescription();

    @Nullable
    @ColumnName(Columns.URL)
    public abstract String url();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {
        public abstract Builder annualized(Boolean annualized);

        public abstract Builder indicatorType(String indicatorType);

        public abstract Builder numerator(String numerator);

        public abstract Builder numeratorDescription(String numeratorDescription);

        public abstract Builder denominator(String denominator);

        public abstract Builder denominatorDescription(String denominatorDescription);

        public abstract Builder url(String url);

        public abstract IndicatorModel build();
    }
}
