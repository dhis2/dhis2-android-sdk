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

package org.hisp.dhis.android.core.indicator;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.common.ModelFactory;
import org.hisp.dhis.android.core.common.StatementBinder;
import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
@SuppressWarnings("PMD")
public abstract class IndicatorTypeModel extends BaseIdentifiableObjectModel implements StatementBinder {

    public static final String TABLE = "IndicatorType";

    public static class Columns extends BaseNameableObjectModel.Columns {
        public final static String NUMBER = "number";
        public final static String FACTOR = "factor";

        public static String[] all() {
            return Utils.appendInNewArray(BaseIdentifiableObjectModel.Columns.all(),
                    NUMBER, FACTOR);
        }
    }

    static IndicatorTypeModel create(Cursor cursor) {
        return AutoValue_IndicatorTypeModel.createFromCursor(cursor);
    }

    public static final ModelFactory<IndicatorTypeModel, IndicatorType> factory
            = new ModelFactory<IndicatorTypeModel, IndicatorType>() {
        @Override
        public IndicatorTypeModel fromCursor(Cursor cursor) {
            return create(cursor);
        }

        @Override
        public IndicatorTypeModel fromPojo(IndicatorType type) {
            return IndicatorTypeModel.builder()
                    .uid(type.uid())
                    .code(type.code())
                    .name(type.name())
                    .displayName(type.displayName())
                    .created(type.created())
                    .lastUpdated(type.lastUpdated())
                    .number(type.number())
                    .factor(type.factor())
                    .build();
        }
    };

    public static Builder builder() {
        return new $AutoValue_IndicatorTypeModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.NUMBER)
    public abstract Boolean number();

    @Nullable
    @ColumnName(Columns.FACTOR)
    public abstract Integer factor();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        super.bindToStatement(sqLiteStatement);
        sqLiteBind(sqLiteStatement, 7, number());
        sqLiteBind(sqLiteStatement, 8, factor());
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {
        public abstract Builder number(Boolean number);

        public abstract Builder factor(Integer factor);

        public abstract IndicatorTypeModel build();
    }
}
