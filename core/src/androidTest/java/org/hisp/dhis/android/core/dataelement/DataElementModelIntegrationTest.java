package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElementContract.Columns;
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

    public static ContentValues createWithOptionSet(long id, String uid, String optionSetId) {
        ContentValues dataElement = new ContentValues();
        dataElement.put(Columns.ID, id);
        dataElement.put(Columns.UID, uid);
        dataElement.put(Columns.CODE, CODE);
        dataElement.put(Columns.NAME, NAME);
        dataElement.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        dataElement.put(Columns.CREATED, DATE);
        dataElement.put(Columns.LAST_UPDATED, DATE);
        dataElement.put(Columns.SHORT_NAME, SHORT_NAME);
        dataElement.put(Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        dataElement.put(Columns.DESCRIPTION, DESCRIPTION);
        dataElement.put(Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        dataElement.put(Columns.VALUE_TYPE, VALUE_TYPE.name());
        dataElement.put(Columns.ZERO_IS_SIGNIFICANT, ZERO_IS_SIGNIFICANT);
        dataElement.put(Columns.AGGREGATION_OPERATOR, AGGREGATION_OPERATOR);
        dataElement.put(Columns.FORM_NAME, FORM_NAME);
        dataElement.put(Columns.NUMBER_TYPE, NUMBER_TYPE);
        dataElement.put(Columns.DOMAIN_TYPE, DOMAIN_TYPE);
        dataElement.put(Columns.DIMENSION, DIMENSION);
        dataElement.put(Columns.DISPLAY_FORM_NAME, DISPLAY_FORM_NAME);
        dataElement.put(Columns.OPTION_SET, optionSetId);

        return dataElement;
    }

    public static ContentValues createWithoutOptionSet(long id, String uid) {
        ContentValues dataElement = new ContentValues();
        dataElement.put(Columns.ID, id);
        dataElement.put(Columns.UID, uid);
        dataElement.put(Columns.CODE, CODE);
        dataElement.put(Columns.NAME, NAME);
        dataElement.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        dataElement.put(Columns.CREATED, DATE);
        dataElement.put(Columns.LAST_UPDATED, DATE);
        dataElement.put(Columns.SHORT_NAME, SHORT_NAME);
        dataElement.put(Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        dataElement.put(Columns.DESCRIPTION, DESCRIPTION);
        dataElement.put(Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        dataElement.put(Columns.VALUE_TYPE, VALUE_TYPE.name());
        dataElement.put(Columns.ZERO_IS_SIGNIFICANT, ZERO_IS_SIGNIFICANT);
        dataElement.put(Columns.AGGREGATION_OPERATOR, AGGREGATION_OPERATOR);
        dataElement.put(Columns.FORM_NAME, FORM_NAME);
        dataElement.put(Columns.NUMBER_TYPE, NUMBER_TYPE);
        dataElement.put(Columns.DOMAIN_TYPE, DOMAIN_TYPE);
        dataElement.put(Columns.DIMENSION, DIMENSION);
        dataElement.put(Columns.DISPLAY_FORM_NAME, DISPLAY_FORM_NAME);
        dataElement.putNull(Columns.OPTION_SET);

        return dataElement;
    }

    @Test
    public void create_shouldConvertToDataElementModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.UID,
                Columns.CODE,
                Columns.NAME,
                Columns.DISPLAY_NAME,
                Columns.CREATED,
                Columns.LAST_UPDATED,
                Columns.SHORT_NAME,
                Columns.DISPLAY_SHORT_NAME,
                Columns.DESCRIPTION,
                Columns.DISPLAY_DESCRIPTION,
                Columns.VALUE_TYPE,
                Columns.ZERO_IS_SIGNIFICANT,
                Columns.AGGREGATION_OPERATOR,
                Columns.FORM_NAME,
                Columns.NUMBER_TYPE,
                Columns.DOMAIN_TYPE,
                Columns.DIMENSION,
                Columns.DISPLAY_FORM_NAME,
                Columns.OPTION_SET
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

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.VALUE_TYPE)).isEqualTo(VALUE_TYPE.name());
        assertThat(contentValues.getAsBoolean(Columns.ZERO_IS_SIGNIFICANT)).isFalse();
        assertThat(contentValues.getAsString(Columns.AGGREGATION_OPERATOR)).isEqualTo(AGGREGATION_OPERATOR);
        assertThat(contentValues.getAsString(Columns.FORM_NAME)).isEqualTo(FORM_NAME);
        assertThat(contentValues.getAsString(Columns.DOMAIN_TYPE)).isEqualTo(DOMAIN_TYPE);
        assertThat(contentValues.getAsString(Columns.DIMENSION)).isEqualTo(DIMENSION);
        assertThat(contentValues.getAsString(Columns.DISPLAY_FORM_NAME)).isEqualTo(DISPLAY_FORM_NAME);
        assertThat(contentValues.getAsString(Columns.OPTION_SET)).isEqualTo(OPTION_SET);
    }
}
