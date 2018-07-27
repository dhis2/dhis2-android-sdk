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

package org.hisp.dhis.android.core.dataelement;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;

import javax.annotation.Nullable;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class DataElementOperandModel extends BaseNameableObjectModel {

    public final static String TABLE = "DataElementOperand";

    public static class Columns {

        public static final String UID = "uid";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String SHORT_NAME = "shortName";
        public static final String DISPLAY_SHORT_NAME = "displayShortName";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String CATEGORY_OPTION_COMBO = "categoryOptionCombo";


        public String[] all() {
            return new String[] {UID, NAME, DISPLAY_NAME, CREATED,
                    LAST_UPDATED, SHORT_NAME, DISPLAY_SHORT_NAME,
                    DATA_ELEMENT, CATEGORY_OPTION_COMBO};
        }
    }

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(Columns.CATEGORY_OPTION_COMBO)
    public abstract String categoryOptionCombo();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<DataElementOperandModel.Builder> {

        public abstract Builder dataElement(String dataElement);
        public abstract Builder categoryOptionCombo(String categoryOptionCombo);

        public abstract DataElementOperandModel build();
    }

    public static Builder builder() {
        return new $$AutoValue_DataElementOperandModel.Builder();
    }

    public static DataElementOperandModel create(Cursor cursor) {
        return AutoValue_DataElementOperandModel.createFromCursor(cursor);
    }


    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {

        sqLiteBind(sqLiteStatement, 1, uid());
        sqLiteBind(sqLiteStatement, 2, name());
        sqLiteBind(sqLiteStatement, 3, displayName());
        sqLiteBind(sqLiteStatement, 4, created());
        sqLiteBind(sqLiteStatement, 5, lastUpdated());
        sqLiteBind(sqLiteStatement, 6, shortName());
        sqLiteBind(sqLiteStatement, 7, displayShortName());
        sqLiteBind(sqLiteStatement, 8, dataElement());
        sqLiteBind(sqLiteStatement, 9, categoryOptionCombo());
    }

}
