/*
 * Copyright (c) 2004-2018, University of Oslo
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
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class DataInputPeriodModel extends BaseModel {

    public final static String TABLE = "DataInputPeriod";

    public static class Columns extends BaseModel.Columns {

        public static final String DATA_SET = "dataSet";
        public static final String PERIOD = "period";
        public static final String OPENING_DATE = "openingDate";
        public static final String CLOSING_DATE = "closingDate";

        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    DATA_SET,
                    PERIOD,
                    OPENING_DATE,
                    CLOSING_DATE);
        }
    }

    @ColumnName(Columns.DATA_SET)
    public abstract String dataSet();

    @ColumnName(Columns.PERIOD)
    public abstract String period();

    @Nullable
    @ColumnName(Columns.OPENING_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date openingDate();

    @Nullable
    @ColumnName(Columns.CLOSING_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date closingDate();

    public static DataInputPeriodModel.Builder builder() {
        return new $$AutoValue_DataInputPeriodModel.Builder();
    }

    public static DataInputPeriodModel create(Cursor cursor) {
        return AutoValue_DataInputPeriodModel.createFromCursor(cursor);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<DataInputPeriodModel.Builder> {

        public abstract Builder dataSet(String dataSet);

        public abstract Builder period(String period);

        public abstract Builder openingDate(Date openingDate);

        public abstract Builder closingDate(Date closingDate);

        public abstract DataInputPeriodModel build();
    }

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 1, dataSet());
        sqLiteBind(sqLiteStatement, 2, period());
        sqLiteBind(sqLiteStatement, 3, openingDate());
        sqLiteBind(sqLiteStatement, 4, closingDate());
    }

}
