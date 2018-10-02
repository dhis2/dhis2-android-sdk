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

package org.hisp.dhis.android.core.datavalue;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.Date;

@AutoValue
@Deprecated
public abstract class DataValueModel extends BaseDataModel {

    public static final String TABLE = "DataValue";

    public static class Columns extends BaseDataModel.Columns {
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
        public static final String FOLLOW_UP = "followup";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    DATA_ELEMENT, PERIOD, ORGANISATION_UNIT, CATEGORY_OPTION_COMBO,
                    ATTRIBUTE_OPTION_COMBO, VALUE, STORED_BY, CREATED, LAST_UPDATED,
                    COMMENT, FOLLOW_UP, STATE);
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{DATA_ELEMENT, PERIOD, ORGANISATION_UNIT, CATEGORY_OPTION_COMBO,
                    ATTRIBUTE_OPTION_COMBO};
        }
    }

    static DataValueModel create(Cursor cursor) {
        return AutoValue_DataValueModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_DataValueModel.Builder();
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

    @AutoValue.Builder
    public static abstract class Builder extends BaseDataModel.Builder<Builder> {
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