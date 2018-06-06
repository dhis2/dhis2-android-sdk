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

package org.hisp.dhis.android.core.systeminfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class SystemInfoModel extends BaseModel {

    public static final String TABLE = "SystemInfo";

    public static class Columns extends BaseModel.Columns {
        public static final String SERVER_DATE = "serverDate";
        public static final String DATE_FORMAT = "dateFormat";
        public static final String VERSION = "version";
        public static final String CONTEXT_PATH = "contextPath";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), SERVER_DATE, DATE_FORMAT, VERSION, CONTEXT_PATH);
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{CONTEXT_PATH};
        }
    }

    public static SystemInfoModel create(Cursor cursor) {
        return AutoValue_SystemInfoModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_SystemInfoModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.SERVER_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date serverDate();

    @Nullable
    @ColumnName(Columns.DATE_FORMAT)
    public abstract String dateFormat();

    @Nullable
    @ColumnName(Columns.VERSION)
    public abstract String version();

    @Nullable
    @ColumnName(Columns.CONTEXT_PATH)
    public abstract String contextPath();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 1, serverDate());
        sqLiteBind(sqLiteStatement, 2, dateFormat());
        sqLiteBind(sqLiteStatement, 3, version());
        sqLiteBind(sqLiteStatement, 4, contextPath());
    }

    @Override
    public void bindToUpdateWhereStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 5, contextPath());
    }

    public static final CursorModelFactory<SystemInfoModel> factory
            = new CursorModelFactory<SystemInfoModel>() {
        @Override
        public SystemInfoModel fromCursor(Cursor cursor) {
            return create(cursor);
        }
    };

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {

        public abstract Builder serverDate(@Nullable Date serverDate);

        public abstract Builder dateFormat(@Nullable String dateFormat);

        public abstract Builder version(@Nullable String version);

        public abstract Builder contextPath(@Nullable String contextPath);

        public abstract SystemInfoModel build();
    }
}
