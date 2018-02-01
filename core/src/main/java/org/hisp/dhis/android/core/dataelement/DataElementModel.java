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

package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.common.ModelFactory;
import org.hisp.dhis.android.core.common.StatementBinder;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DbValueTypeColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class DataElementModel extends BaseNameableObjectModel implements StatementBinder {

    public static final String TABLE = "DataElement";

    public static class Columns extends BaseNameableObjectModel.Columns {
        public static final String VALUE_TYPE = "valueType";
        public static final String ZERO_IS_SIGNIFICANT = "zeroIsSignificant";
        public static final String AGGREGATION_TYPE = "aggregationType";
        public static final String FORM_NAME = "formName";
        public static final String NUMBER_TYPE = "numberType";
        public static final String DOMAIN_TYPE = "domainType";
        public static final String DIMENSION = "dimension";
        public static final String DISPLAY_FORM_NAME = "displayFormName";
        public static final String OPTION_SET = "optionSet";
        public static final String CATEGORY_COMBO = "categoryCombo";

        private Columns() {}

        public static String[] all() {
            return Utils.appendInNewArray(BaseNameableObjectModel.Columns.all(),
                    VALUE_TYPE, ZERO_IS_SIGNIFICANT, AGGREGATION_TYPE, FORM_NAME, NUMBER_TYPE,
                    DOMAIN_TYPE, DIMENSION, DISPLAY_FORM_NAME, OPTION_SET, CATEGORY_COMBO);
        }
    }

    public static DataElementModel create(Cursor cursor) {
        return AutoValue_DataElementModel.createFromCursor(cursor);
    }

    public static final ModelFactory<DataElementModel, DataElement> factory
            = new ModelFactory<DataElementModel, DataElement>() {

        @Override
        public DataElementModel fromCursor(Cursor cursor) {
            return create(cursor);
        }

        @Override
        public DataElementModel fromPojo(DataElement dataElement) {
            return DataElementModel.builder()
                    .uid(dataElement.uid())
                    .code(dataElement.code())
                    .name(dataElement.name())
                    .displayName(dataElement.displayName())
                    .created(dataElement.created())
                    .lastUpdated(dataElement.lastUpdated())
                    .shortName(dataElement.shortName())
                    .displayShortName(dataElement.displayShortName())
                    .description(dataElement.description())
                    .displayDescription(dataElement.displayDescription())
                    .valueType(dataElement.valueType())
                    .zeroIsSignificant(dataElement.zeroIsSignificant())
                    .aggregationType(dataElement.aggregationType())
                    .formName(dataElement.formName())
                    .numberType(dataElement.numberType())
                    .domainType(dataElement.domainType())
                    .dimension(dataElement.dimension())
                    .displayFormName(dataElement.displayFormName())
                    .optionSet(dataElement.optionSetUid())
                    .categoryCombo(dataElement.categoryComboUid())
                    .build();
        }
    };

    public static Builder builder() {
        return new $$AutoValue_DataElementModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.VALUE_TYPE)
    @ColumnAdapter(DbValueTypeColumnAdapter.class)
    public abstract ValueType valueType();

    @Nullable
    @ColumnName(Columns.ZERO_IS_SIGNIFICANT)
    public abstract Boolean zeroIsSignificant();

    @Nullable
    @ColumnName(Columns.AGGREGATION_TYPE)
    public abstract String aggregationType();

    @Nullable
    @ColumnName(Columns.FORM_NAME)
    public abstract String formName();

    @Nullable
    @ColumnName(Columns.NUMBER_TYPE)
    public abstract String numberType();

    @Nullable
    @ColumnName(Columns.DOMAIN_TYPE)
    public abstract String domainType();

    @Nullable
    @ColumnName(Columns.DIMENSION)
    public abstract String dimension();

    @Nullable
    @ColumnName(Columns.DISPLAY_FORM_NAME)
    public abstract String displayFormName();

    @Nullable
    @ColumnName(Columns.OPTION_SET)
    public abstract String optionSet();

    @Nullable
    @ColumnName(Columns.CATEGORY_COMBO)
    public abstract String categoryCombo();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {
        public abstract Builder valueType(ValueType valueType);

        public abstract Builder zeroIsSignificant(Boolean zeroIsSignificant);

        public abstract Builder aggregationType(String aggregationType);

        public abstract Builder formName(String formName);

        public abstract Builder numberType(String numberType);

        public abstract Builder domainType(String domainType);

        public abstract Builder dimension(String dimension);

        public abstract Builder displayFormName(String displayFormName);

        public abstract Builder optionSet(String optionSet);

        public abstract Builder categoryCombo(@Nullable String categoryCombo);

        public abstract DataElementModel build();

    }

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        super.bindToStatement(sqLiteStatement);
        sqLiteBind(sqLiteStatement, 11, valueType());
        sqLiteBind(sqLiteStatement, 12, zeroIsSignificant());
        sqLiteBind(sqLiteStatement, 13, aggregationType());
        sqLiteBind(sqLiteStatement, 14, formName());
        sqLiteBind(sqLiteStatement, 15, numberType());
        sqLiteBind(sqLiteStatement, 16, domainType());
        sqLiteBind(sqLiteStatement, 17, dimension());
        sqLiteBind(sqLiteStatement, 18, displayFormName());
        sqLiteBind(sqLiteStatement, 19, optionSet());
        sqLiteBind(sqLiteStatement, 20, categoryCombo());
    }
}
