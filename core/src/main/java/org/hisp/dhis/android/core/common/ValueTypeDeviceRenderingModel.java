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
package org.hisp.dhis.android.core.common;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class ValueTypeDeviceRenderingModel extends BaseModel implements UpdateWhereStatementBinder {
    public static final String TABLE = "ValueTypeDeviceRendering";

    public abstract static class Columns extends BaseModel.Columns {
        private static final String UID = BaseIdentifiableObjectModel.Columns.UID;
        private static final String OBJECT_TABLE = "objectTable";
        private static final String DEVICE_TYPE = "deviceType";
        private static final String TYPE = "type";
        private static final String MIN = "min";
        private static final String MAX = "max";
        private static final String STEP = "step";
        private static final String DECIMAL_POINTS = "decimalPoints";

        public static String[] all() {
            return Utils.appendInNewArray(BaseModel.Columns.all(),
                    UID, OBJECT_TABLE, DEVICE_TYPE, TYPE, MIN, MAX, STEP, DECIMAL_POINTS);
        }

        static String[] whereUpdate() {
            return new String[]{UID, DEVICE_TYPE};
        }
    }

    public static ValueTypeDeviceRenderingModel create(Cursor cursor) {
        return AutoValue_ValueTypeDeviceRenderingModel.createFromCursor(cursor);
    }

    public static final LinkModelFactory<ValueTypeDeviceRenderingModel> factory
            = new LinkModelFactory<ValueTypeDeviceRenderingModel>() {
        @Override
        public ValueTypeDeviceRenderingModel fromCursor(Cursor cursor) {
            return create(cursor);
        }
    };

    public static ValueTypeDeviceRenderingModel fromPojo(ValueTypeDeviceRendering deviceRendering,
                                                         String uid, String objectTable,
                                                         String deviceType) {
        return ValueTypeDeviceRenderingModel.builder()
                .uid(uid)
                .objectTable(objectTable)
                .deviceType(deviceType)
                .type(deviceRendering.type())
                .min(deviceRendering.min())
                .max(deviceRendering.max())
                .step(deviceRendering.step())
                .decimalPoints(deviceRendering.decimalPoints())
                .build();
    }

    public static Builder builder() {
        return new $$AutoValue_ValueTypeDeviceRenderingModel.Builder();
    }


    private static final String UID = BaseIdentifiableObjectModel.Columns.UID;
    private static final String OBJECT_TABLE = "objectTable";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String TYPE = "type";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String STEP = "step";
    private static final String DECIMAL_POINTS = "decimalPoints";

    @Nullable
    @ColumnName(Columns.UID)
    public abstract String uid();

    @Nullable
    @ColumnName(Columns.OBJECT_TABLE)
    public abstract String objectTable();

    @Nullable
    @ColumnName(Columns.DEVICE_TYPE)
    public abstract String deviceType();

    @Nullable
    @ColumnName(Columns.TYPE)
    @ColumnAdapter(ValueTypeRenderingTypeColumnAdapter.class)
    public abstract ValueTypeRenderingType type();

    @Nullable
    @ColumnName(Columns.MIN)
    public abstract Integer min();

    @Nullable
    @ColumnName(Columns.MAX)
    public abstract Integer max();

    @Nullable
    @ColumnName(Columns.STEP)
    public abstract Integer step();

    @Nullable
    @ColumnName(Columns.DECIMAL_POINTS)
    public abstract Integer decimalPoints();

    @NonNull
    public abstract ContentValues toContentValues();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 1, uid());
        sqLiteBind(sqLiteStatement, 2, objectTable());
        sqLiteBind(sqLiteStatement, 3, deviceType());
        sqLiteBind(sqLiteStatement, 4, type());
        sqLiteBind(sqLiteStatement, 5, min());
        sqLiteBind(sqLiteStatement, 6, max());
        sqLiteBind(sqLiteStatement, 7, step());
        sqLiteBind(sqLiteStatement, 8, decimalPoints());
    }

    @Override
    public void bindToUpdateWhereStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 9, uid());
        sqLiteBind(sqLiteStatement, 10, deviceType());
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder uid(String uid);

        public abstract Builder objectTable(String objectTable);

        public abstract Builder deviceType(String deviceType);

        public abstract Builder min(Integer min);

        public abstract Builder type(ValueTypeRenderingType type);

        public abstract Builder max(Integer max);

        public abstract Builder step(Integer step);

        public abstract Builder decimalPoints(Integer decimalPoints);

        public abstract ValueTypeDeviceRenderingModel build();
    }
}
