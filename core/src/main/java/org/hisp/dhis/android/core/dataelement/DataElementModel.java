package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DbValueTypeColumnAdapter;

@AutoValue
public abstract class DataElementModel extends BaseNameableObjectModel {

    public interface Columns extends BaseNameableObjectModel.Columns {
        String VALUE_TYPE = "valueType";
        String ZERO_IS_SIGNIFICANT = "zeroIsSignificant";
        String AGGREGATION_OPERATOR = "aggregationOperator";
        String FORM_NAME = "formName";
        String NUMBER_TYPE = "numberType";
        String DOMAIN_TYPE = "domainType";
        String DIMENSION = "dimension";
        String DISPLAY_FORM_NAME = "displayFormName";
        String OPTION_SET = "optionSet";
    }

    public static DataElementModel create(Cursor cursor) {
        return AutoValue_DataElementModel.createFromCursor(cursor);
    }

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
    @ColumnName(Columns.AGGREGATION_OPERATOR)
    public abstract String aggregationOperator();

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

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {
        public abstract Builder valueType(ValueType valueType);

        public abstract Builder zeroIsSignificant(Boolean zeroIsSignificant);

        public abstract Builder aggregationOperator(String aggregationOperator);

        public abstract Builder formName(String formName);

        public abstract Builder numberType(String numberType);

        public abstract Builder domainType(String domainType);

        public abstract Builder dimension(String dimension);

        public abstract Builder displayFormName(String displayFormName);

        public abstract Builder optionSet(String optionSet);

        public abstract DataElementModel build();

    }
}
