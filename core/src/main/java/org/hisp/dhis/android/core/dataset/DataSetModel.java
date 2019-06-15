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

package org.hisp.dhis.android.core.dataset;

import android.database.Cursor;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.data.database.DbPeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.utils.Utils;

import androidx.annotation.Nullable;

@Deprecated
@AutoValue
public abstract class DataSetModel extends BaseNameableObjectModel {

    public static final String TABLE = "DataSet";

    public static class Columns extends BaseNameableObjectModel.Columns {
        public static final String PERIOD_TYPE = "periodType";
        public static final String CATEGORY_COMBO = "categoryCombo";
        public static final String MOBILE = "mobile";
        public static final String VERSION = "version";
        public static final String EXPIRY_DAYS = "expiryDays";
        public static final String TIMELY_DAYS = "timelyDays";
        public static final String NOTIFY_COMPLETING_USER = "notifyCompletingUser";
        public static final String OPEN_FUTURE_PERIODS = "openFuturePeriods";
        public static final String FIELD_COMBINATION_REQUIRED = "fieldCombinationRequired";
        public static final String VALID_COMPLETE_ONLY = "validCompleteOnly";
        public static final String NO_VALUE_REQUIRES_COMMENT = "noValueRequiresComment";
        public static final String SKIP_OFFLINE = "skipOffline";
        public static final String DATA_ELEMENT_DECORATION = "dataElementDecoration";
        public static final String RENDER_AS_TABS = "renderAsTabs";
        public static final String RENDER_HORIZONTALLY = "renderHorizontally";
        public static final String ACCESS_DATA_WRITE = "accessDataWrite";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    PERIOD_TYPE, CATEGORY_COMBO, MOBILE, VERSION, EXPIRY_DAYS, TIMELY_DAYS,
                    NOTIFY_COMPLETING_USER, OPEN_FUTURE_PERIODS, FIELD_COMBINATION_REQUIRED,
                    VALID_COMPLETE_ONLY, NO_VALUE_REQUIRES_COMMENT, SKIP_OFFLINE, DATA_ELEMENT_DECORATION,
                    RENDER_AS_TABS, RENDER_HORIZONTALLY, ACCESS_DATA_WRITE);
        }
    }

    public static DataSetModel create(Cursor cursor) {
        return AutoValue_DataSetModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_DataSetModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.PERIOD_TYPE)
    @ColumnAdapter(DbPeriodTypeColumnAdapter.class)
    public abstract PeriodType periodType();

    @Nullable
    @ColumnName(Columns.CATEGORY_COMBO)
    public abstract String categoryCombo();

    @Nullable
    @ColumnName(Columns.MOBILE)
    public abstract Boolean mobile();

    @Nullable
    @ColumnName(Columns.VERSION)
    public abstract Integer version();

    @Nullable
    @ColumnName(Columns.EXPIRY_DAYS)
    public abstract Integer expiryDays();

    @Nullable
    @ColumnName(Columns.TIMELY_DAYS)
    public abstract Integer timelyDays();

    @Nullable
    @ColumnName(Columns.NOTIFY_COMPLETING_USER)
    public abstract Boolean notifyCompletingUser();

    @Nullable
    @ColumnName(Columns.OPEN_FUTURE_PERIODS)
    public abstract Integer openFuturePeriods();

    @Nullable
    @ColumnName(Columns.FIELD_COMBINATION_REQUIRED)
    public abstract Boolean fieldCombinationRequired();

    @Nullable
    @ColumnName(Columns.VALID_COMPLETE_ONLY)
    public abstract Boolean validCompleteOnly();

    @Nullable
    @ColumnName(Columns.NO_VALUE_REQUIRES_COMMENT)
    public abstract Boolean noValueRequiresComment();

    @Nullable
    @ColumnName(Columns.SKIP_OFFLINE)
    public abstract Boolean skipOffline();

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT_DECORATION)
    public abstract Boolean dataElementDecoration();

    @Nullable
    @ColumnName(Columns.RENDER_AS_TABS)
    public abstract Boolean renderAsTabs();

    @Nullable
    @ColumnName(Columns.RENDER_HORIZONTALLY)
    public abstract Boolean renderHorizontally();

    @Nullable
    @ColumnName(Columns.ACCESS_DATA_WRITE)
    public abstract Boolean accessDataWrite();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {
        public abstract Builder periodType(PeriodType periodType);

        public abstract Builder categoryCombo(String categoryCombo);

        public abstract Builder mobile(Boolean mobile);

        public abstract Builder version(Integer version);

        public abstract Builder expiryDays(Integer expiryDays);

        public abstract Builder timelyDays(Integer timelyDays);

        public abstract Builder notifyCompletingUser(Boolean notifyCompletingUser);

        public abstract Builder openFuturePeriods(Integer openFuturePeriods);

        public abstract Builder fieldCombinationRequired(Boolean fieldCombinationRequired);

        public abstract Builder validCompleteOnly(Boolean validCompleteOnly);

        public abstract Builder noValueRequiresComment(Boolean noValueRequiresComment);

        public abstract Builder skipOffline(Boolean skipOffline);

        public abstract Builder dataElementDecoration(Boolean dataElementDecoration);

        public abstract Builder renderAsTabs(Boolean renderAsTabs);

        public abstract Builder renderHorizontally(Boolean renderHorizontally);

        public abstract Builder accessDataWrite(Boolean accessDataWrite);

        public abstract DataSetModel build();
    }
}
