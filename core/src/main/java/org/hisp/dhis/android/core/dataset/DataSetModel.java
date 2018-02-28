/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.dataset;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.common.ModelFactory;
import org.hisp.dhis.android.core.common.StatementBinder;
import org.hisp.dhis.android.core.data.database.DbPeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class DataSetModel extends BaseNameableObjectModel implements StatementBinder {

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
        public static final String COLOR = "color";
        public static final String ICON = "icon";

        private Columns() {}

        public static String[] all() {
            return Utils.appendInNewArray(BaseNameableObjectModel.Columns.all(),
                    PERIOD_TYPE, CATEGORY_COMBO, MOBILE, VERSION, EXPIRY_DAYS, TIMELY_DAYS,
                    NOTIFY_COMPLETING_USER, OPEN_FUTURE_PERIODS, FIELD_COMBINATION_REQUIRED,
                    VALID_COMPLETE_ONLY, NO_VALUE_REQUIRES_COMMENT, SKIP_OFFLINE, DATA_ELEMENT_DECORATION,
                    RENDER_AS_TABS, RENDER_HORIZONTALLY, ACCESS_DATA_WRITE, COLOR, ICON);
        }
    }

    static DataSetModel create(Cursor cursor) {
        return AutoValue_DataSetModel.createFromCursor(cursor);
    }

    public static final ModelFactory<DataSetModel, DataSet> factory
            = new ModelFactory<DataSetModel, DataSet>() {
        @Override
        public DataSetModel fromCursor(Cursor cursor) {
            return create(cursor);
        }

        @Override
        public DataSetModel fromPojo(DataSet dataSet) {
            return DataSetModel.builder()
                    .uid(dataSet.uid())
                    .code(dataSet.code())
                    .name(dataSet.name())
                    .displayName(dataSet.displayName())
                    .created(dataSet.created())
                    .lastUpdated(dataSet.lastUpdated())
                    .shortName(dataSet.shortName())
                    .displayShortName(dataSet.displayShortName())
                    .description(dataSet.description())
                    .displayDescription(dataSet.displayDescription())
                    .periodType(dataSet.periodType())
                    .categoryCombo(dataSet.categoryComboUid())
                    .mobile(dataSet.mobile())
                    .version(dataSet.version())
                    .expiryDays(dataSet.expiryDays())
                    .timelyDays(dataSet.timelyDays())
                    .notifyCompletingUser(dataSet.notifyCompletingUser())
                    .openFuturePeriods(dataSet.openFuturePeriods())
                    .fieldCombinationRequired(dataSet.fieldCombinationRequired())
                    .validCompleteOnly(dataSet.validCompleteOnly())
                    .noValueRequiresComment(dataSet.noValueRequiresComment())
                    .skipOffline(dataSet.skipOffline())
                    .dataElementDecoration(dataSet.dataElementDecoration())
                    .renderAsTabs(dataSet.renderAsTabs())
                    .renderHorizontally(dataSet.renderHorizontally())
                    .accessDataWrite(dataSet.access().data().write())
                    .color(dataSet.style().color())
                    .icon(dataSet.style().icon())
                    .build();
        }
    };

    public static Builder builder() {
        return new $AutoValue_DataSetModel.Builder();
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

    @Nullable
    @ColumnName(Columns.COLOR)
    public abstract String color();

    @Nullable
    @ColumnName(Columns.ICON)
    public abstract String icon();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        super.bindToStatement(sqLiteStatement);
        sqLiteBind(sqLiteStatement, 11, periodType());
        sqLiteBind(sqLiteStatement, 12, categoryCombo());
        sqLiteBind(sqLiteStatement, 13, mobile());
        sqLiteBind(sqLiteStatement, 14, version());
        sqLiteBind(sqLiteStatement, 15, expiryDays());
        sqLiteBind(sqLiteStatement, 16, timelyDays());
        sqLiteBind(sqLiteStatement, 17, notifyCompletingUser());
        sqLiteBind(sqLiteStatement, 18, openFuturePeriods());
        sqLiteBind(sqLiteStatement, 19, fieldCombinationRequired());
        sqLiteBind(sqLiteStatement, 20, validCompleteOnly());
        sqLiteBind(sqLiteStatement, 21, noValueRequiresComment());
        sqLiteBind(sqLiteStatement, 22, skipOffline());
        sqLiteBind(sqLiteStatement, 23, dataElementDecoration());
        sqLiteBind(sqLiteStatement, 24, renderAsTabs());
        sqLiteBind(sqLiteStatement, 25, renderHorizontally());
        sqLiteBind(sqLiteStatement, 26, accessDataWrite());
        sqLiteBind(sqLiteStatement, 27, color());
        sqLiteBind(sqLiteStatement, 28, icon());
    }

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

        public abstract Builder color(String color);

        public abstract Builder icon(String icon);

        public abstract DataSetModel build();
    }
}
