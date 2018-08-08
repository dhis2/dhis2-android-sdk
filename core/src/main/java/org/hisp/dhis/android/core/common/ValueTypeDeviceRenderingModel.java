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

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.utils.Utils;

@AutoValue
public abstract class ValueTypeDeviceRenderingModel extends BaseModel {
    public static final String TABLE = "ValueTypeDeviceRendering";

    public static class Columns extends BaseModel.Columns {
        public static final String UID = BaseIdentifiableObjectModel.Columns.UID;
        public static final String OBJECT_TABLE = "objectTable";
        public static final String DEVICE_TYPE = "deviceType";
        public static final String TYPE = "type";
        public static final String MIN = "min";
        public static final String MAX = "max";
        public static final String STEP = "step";
        public static final String DECIMAL_POINTS = "decimalPoints";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    UID, OBJECT_TABLE, DEVICE_TYPE, TYPE, MIN, MAX, STEP, DECIMAL_POINTS);
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{UID, DEVICE_TYPE};
        }
    }

    public static ValueTypeDeviceRenderingModel create(Cursor cursor) {
        return AutoValue_ValueTypeDeviceRenderingModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ValueTypeDeviceRenderingModel.Builder();
    }

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
