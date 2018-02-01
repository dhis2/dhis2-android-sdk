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

package org.hisp.dhis.android.core.datavalue;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.ModelFactory;
import org.hisp.dhis.android.core.common.UpdateWhereStatementBinder;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
@SuppressWarnings("PMD")
public abstract class DataValueModel extends BaseModel implements UpdateWhereStatementBinder {

    public static final String TABLE = "DataValue";

    public static class Columns extends BaseModel.Columns {
        public static final String DATA_ELEMENT = "dataElement";
        public static final String PERIOD = "period";
        public static final String ORGANISATION_UNIT = "organisationUnit";
        public static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";
        public static final String ATTRIBUTE_OPTION_COMBO = "attributeOptionCombo";
        public static final String VALUE = "value";
        public static final String STORED_BY = "storedBy";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String COMMENT = "comment";
        public static final String FOLLOW_UP = "followUp";

        public static String[] all() {
            return Utils.appendInNewArray(BaseModel.Columns.all(),
                    DATA_ELEMENT, PERIOD, ORGANISATION_UNIT, CATEGORY_OPTION_COMBO,
                    ATTRIBUTE_OPTION_COMBO, VALUE, STORED_BY, CREATED, LAST_UPDATED, COMMENT, FOLLOW_UP);
        }

        public static String[] whereUpdate() {
            return new String[]{DATA_ELEMENT, PERIOD, ORGANISATION_UNIT};
        }
    }

    static DataValueModel create(Cursor cursor) {
        return AutoValue_DataValueModel.createFromCursor(cursor);
    }

    public static final ModelFactory<DataValueModel, DataValue> factory
            = new ModelFactory<DataValueModel, DataValue>() {
        @Override
        public DataValueModel fromCursor(Cursor cursor) {
            return create(cursor);
        }

        @Override
        public DataValueModel fromPojo(DataValue dataValue) {
            return DataValueModel.builder()
                    .dataElement(dataValue.dataElement())
                    .period(dataValue.period())
                    .organisationUnit(dataValue.organisationUnit())
                    .categoryOptionCombo(dataValue.categoryOptionCombo())
                    .attributeOptionCombo(dataValue.attributeOptionCombo())
                    .value(dataValue.value())
                    .storedBy(dataValue.storedBy())
                    .created(dataValue.created())
                    .lastUpdated(dataValue.lastUpdated())
                    .comment(dataValue.comment())
                    .followUp(dataValue.followUp())
                    .build();
        }
    };

    public static Builder builder() {
        return new $AutoValue_DataValueModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(Columns.PERIOD)
    public abstract String period();

    @Nullable
    @ColumnName(Columns.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @ColumnName(Columns.CATEGORY_OPTION_COMBO)
    public abstract String categoryOptionCombo();

    @Nullable
    @ColumnName(Columns.ATTRIBUTE_OPTION_COMBO)
    public abstract String attributeOptionCombo();

    @Nullable
    @ColumnName(Columns.VALUE)
    public abstract String value();

    @Nullable
    @ColumnName(Columns.STORED_BY)
    public abstract String storedBy();

    @Nullable
    @ColumnName(Columns.CREATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @ColumnName(Columns.LAST_UPDATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    @ColumnName(Columns.COMMENT)
    public abstract String comment();

    @Nullable
    @ColumnName(Columns.FOLLOW_UP)
    public abstract Boolean followUp();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 1, dataElement());
        sqLiteBind(sqLiteStatement, 2, period());
        sqLiteBind(sqLiteStatement, 3, organisationUnit());
        sqLiteBind(sqLiteStatement, 4, categoryOptionCombo());
        sqLiteBind(sqLiteStatement, 5, attributeOptionCombo());
        sqLiteBind(sqLiteStatement, 6, value());
        sqLiteBind(sqLiteStatement, 7, storedBy());
        sqLiteBind(sqLiteStatement, 8, created());
        sqLiteBind(sqLiteStatement, 9, lastUpdated());
        sqLiteBind(sqLiteStatement, 10, comment());
        sqLiteBind(sqLiteStatement, 11, followUp());
    }

    @Override
    public void bindToUpdateWhereStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 12, dataElement());
        sqLiteBind(sqLiteStatement, 13, period());
        sqLiteBind(sqLiteStatement, 14, organisationUnit());
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder dataElement(String dataElement);

        public abstract Builder period(String period);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder categoryOptionCombo(String categoryOptionCombo);

        public abstract Builder attributeOptionCombo(String attributeOptionCombo);

        public abstract Builder value(String value);

        public abstract Builder storedBy(String storedBy);

        public abstract Builder created(Date created);

        public abstract Builder lastUpdated(Date lastUpdated);

        public abstract Builder comment(String comment);

        public abstract Builder followUp(Boolean followUp);

        public abstract DataValueModel build();
    }
}