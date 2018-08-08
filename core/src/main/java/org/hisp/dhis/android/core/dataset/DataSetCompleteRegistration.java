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

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.utils.StoreUtils;

import java.util.Date;

@AutoValue
public abstract class DataSetCompleteRegistration extends BaseModel {

    public abstract String period();

    public abstract String dataSet();

    public abstract String organisationUnit();

    public abstract String attributeOptionCombo();

    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date date();


    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        StoreUtils.sqLiteBind(sqLiteStatement, 1, period());
        StoreUtils.sqLiteBind(sqLiteStatement, 2, dataSet());
        StoreUtils.sqLiteBind(sqLiteStatement, 3, organisationUnit());
        StoreUtils.sqLiteBind(sqLiteStatement, 4, attributeOptionCombo());
        StoreUtils.sqLiteBind(sqLiteStatement, 5, date());
    }

    @Override
    public void bindToUpdateWhereStatement(@NonNull SQLiteStatement sqLiteStatement) {
        StoreUtils.sqLiteBind(sqLiteStatement, 6, period());
        StoreUtils.sqLiteBind(sqLiteStatement, 7, dataSet());
        StoreUtils.sqLiteBind(sqLiteStatement, 8, organisationUnit());
        StoreUtils.sqLiteBind(sqLiteStatement, 9, attributeOptionCombo());
        StoreUtils.sqLiteBind(sqLiteStatement, 10, date());
    }

    @NonNull
    public static DataSetCompleteRegistration create(Cursor cursor) {
        return AutoValue_DataSetCompleteRegistration.createFromCursor(cursor);
    }

    public static final CursorModelFactory<DataSetCompleteRegistration> factory = new CursorModelFactory<DataSetCompleteRegistration>() {
        @Override
        public DataSetCompleteRegistration fromCursor(Cursor cursor) {
            return create(cursor);
        }
    };

    public static Builder builder() {
        return new $$AutoValue_DataSetCompleteRegistration.Builder();
    }


    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseModel.Builder<DataSetCompleteRegistration.Builder> {
        public abstract Builder period(String period);

        public abstract Builder dataSet(String dataSet);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder attributeOptionCombo(String attributeOptionCombo);

        public abstract Builder date(Date date);

        public abstract DataSetCompleteRegistration build();
    }
}
