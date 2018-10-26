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

package org.hisp.dhis.android.core.enrollment.note;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.Date;

@AutoValue
public abstract class NoteModel extends BaseModel {

    public static final String TABLE = "Note2229Compatible";

    public static class Columns extends BaseModel.Columns {
        public static final String ENROLLMENT = "enrollment";
        public static final String VALUE = "value";
        public static final String STORED_BY = "storedBy";
        public static final String STORED_DATE = "storedDate";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), ENROLLMENT, VALUE, STORED_BY, STORED_DATE);
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{ENROLLMENT, VALUE, STORED_BY, STORED_DATE};
        }
    }

    public static NoteModel create(Cursor cursor) {
        return AutoValue_NoteModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $AutoValue_NoteModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.ENROLLMENT)
    public abstract String enrollment();

    @Nullable
    @ColumnName(Columns.VALUE)
    public abstract String value();

    @Nullable
    @ColumnName(Columns.STORED_BY)
    public abstract String storedBy();

    @Nullable
    @ColumnName(Columns.STORED_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date storedDate();

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder enrollment(String enrollment);

        public abstract Builder value(String value);

        public abstract Builder storedBy(String storedBy);

        public abstract Builder storedDate(Date storedDate);

        public abstract NoteModel build();
    }
}
