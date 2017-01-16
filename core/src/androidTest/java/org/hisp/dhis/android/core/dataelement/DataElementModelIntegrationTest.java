package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class DataElementModelIntegrationTest {
    private static final long ID = 2L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final ValueType VALUE_TYPE = ValueType.TEXT;
    private static final Integer ZERO_IS_SIGNIFICANT = 0;
    private static final String AGGREGATION_OPERATOR = "test_aggregationOperator";
    private static final String FORM_NAME = "test_formName";
    private static final String NUMBER_TYPE = "test_numberType";
    private static final String DOMAIN_TYPE = "test_domainType";
    private static final String DIMENSION = "test_dimension";
    private static final String DISPLAY_FORM_NAME = "test_displayFormName";
    private static final String OPTION_SET = "test_optionSet";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    @Test
    public void create_shouldConvertToDataElementModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                DataElementModel.Columns.ID,
                DataElementModel.Columns.UID,
                DataElementModel.Columns.CODE,
                DataElementModel.Columns.NAME,
                DataElementModel.Columns.DISPLAY_NAME,
                DataElementModel.Columns.CREATED,
                DataElementModel.Columns.LAST_UPDATED,
                DataElementModel.Columns.SHORT_NAME,
                DataElementModel.Columns.DISPLAY_SHORT_NAME,
                DataElementModel.Columns.DESCRIPTION,
                DataElementModel.Columns.DISPLAY_DESCRIPTION,
                DataElementModel.Columns.VALUE_TYPE,
                DataElementModel.Columns.ZERO_IS_SIGNIFICANT,
                DataElementModel.Columns.AGGREGATION_OPERATOR,
                DataElementModel.Columns.FORM_NAME,
                DataElementModel.Columns.NUMBER_TYPE,
                DataElementModel.Columns.DOMAIN_TYPE,
                DataElementModel.Columns.DIMENSION,
                DataElementModel.Columns.DISPLAY_FORM_NAME,
                DataElementModel.Columns.OPTION_SET
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME,
                DATE, DATE,
                SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION,
                VALUE_TYPE, ZERO_IS_SIGNIFICANT, AGGREGATION_OPERATOR,
                FORM_NAME, NUMBER_TYPE, DOMAIN_TYPE, DIMENSION,
                DISPLAY_FORM_NAME, OPTION_SET
        });

        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        DataElementModel dataElement = DataElementModel.create(matrixCursor);

        assertThat(dataElement.id()).isEqualTo(ID);
        assertThat(dataElement.uid()).isEqualTo(UID);
        assertThat(dataElement.code()).isEqualTo(CODE);
        assertThat(dataElement.name()).isEqualTo(NAME);
        assertThat(dataElement.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(dataElement.created()).isEqualTo(timeStamp);
        assertThat(dataElement.lastUpdated()).isEqualTo(timeStamp);
        assertThat(dataElement.shortName()).isEqualTo(SHORT_NAME);
        assertThat(dataElement.displayShortName()).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(dataElement.description()).isEqualTo(DESCRIPTION);
        assertThat(dataElement.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(dataElement.valueType()).isEqualTo(VALUE_TYPE);
        assertThat(dataElement.zeroIsSignificant()).isFalse();
        assertThat(dataElement.aggregationOperator()).isEqualTo(AGGREGATION_OPERATOR);
        assertThat(dataElement.formName()).isEqualTo(FORM_NAME);
        assertThat(dataElement.numberType()).isEqualTo(NUMBER_TYPE);
        assertThat(dataElement.domainType()).isEqualTo(DOMAIN_TYPE);
        assertThat(dataElement.dimension()).isEqualTo(DIMENSION);
        assertThat(dataElement.displayFormName()).isEqualTo(DISPLAY_FORM_NAME);
        assertThat(dataElement.optionSet()).isEqualTo(OPTION_SET);
    }

    @Test
    public void create_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        DataElementModel dataElementModel = DataElementModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .shortName(SHORT_NAME)
                .displayShortName(DISPLAY_SHORT_NAME)
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .valueType(VALUE_TYPE)
                .zeroIsSignificant(ZERO_IS_SIGNIFICANT != 0 ? Boolean.TRUE : Boolean.FALSE)
                .aggregationOperator(AGGREGATION_OPERATOR)
                .formName(FORM_NAME)
                .numberType(NUMBER_TYPE)
                .domainType(DOMAIN_TYPE)
                .dimension(DIMENSION)
                .displayFormName(DISPLAY_FORM_NAME)
                .optionSet(OPTION_SET)
                .build();

        ContentValues contentValues = dataElementModel.toContentValues();

        assertThat(contentValues.getAsLong(DataElementModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(DataElementModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(DataElementModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(DataElementModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(DataElementModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(DataElementModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(DataElementModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(DataElementModel.Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(DataElementModel.Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(DataElementModel.Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(DataElementModel.Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsString(DataElementModel.Columns.VALUE_TYPE)).isEqualTo(VALUE_TYPE.name());
        assertThat(contentValues.getAsBoolean(DataElementModel.Columns.ZERO_IS_SIGNIFICANT)).isFalse();
        assertThat(contentValues.getAsString(DataElementModel.Columns.AGGREGATION_OPERATOR)).isEqualTo(AGGREGATION_OPERATOR);
        assertThat(contentValues.getAsString(DataElementModel.Columns.FORM_NAME)).isEqualTo(FORM_NAME);
        assertThat(contentValues.getAsString(DataElementModel.Columns.DOMAIN_TYPE)).isEqualTo(DOMAIN_TYPE);
        assertThat(contentValues.getAsString(DataElementModel.Columns.DIMENSION)).isEqualTo(DIMENSION);
        assertThat(contentValues.getAsString(DataElementModel.Columns.DISPLAY_FORM_NAME)).isEqualTo(DISPLAY_FORM_NAME);
        assertThat(contentValues.getAsString(DataElementModel.Columns.OPTION_SET)).isEqualTo(OPTION_SET);
    }
}
