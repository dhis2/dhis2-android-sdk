package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.option.OptionSetModelIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class DataElementStoreIntegrationTest extends AbsStoreTestCase {

    private static final long ID = 21L;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";

    private static final ValueType VALUE_TYPE = ValueType.TEXT;
    private static final Boolean ZERO_IS_SIGNIFICANT = Boolean.FALSE;
    private static final String AGGREGATION_OPERATOR = "test_aggregationOperator";
    private static final String FORM_NAME = "test_formName";
    private static final String NUMBER_TYPE = "test_numberType";
    private static final String DOMAIN_TYPE = "test_domainType";
    private static final String DIMENSION = "test_dimension";
    private static final String DISPLAY_FORM_NAME = "test_displayFormName";
    private static final String OPTION_SET = "test_optionSet";

    // timestamp
    private static final String DATE = "2016-12-20T16:26:00.007";

    private static final String[] DATA_ELEMENT_PROJECTION = {
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
    };

    private DataElementStore dataElementStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.dataElementStore = new DataElementStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistDataElementInDatabase() throws ParseException {
        ContentValues optionSet = OptionSetModelIntegrationTest.create(ID, OPTION_SET);


        database().insert(Tables.OPTION_SET, null, optionSet);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        long rowId = dataElementStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                ZERO_IS_SIGNIFICANT,
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                OPTION_SET
        );

        Cursor cursor = database().query(Tables.DATA_ELEMENT, DATA_ELEMENT_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                DATE,
                DATE,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                0, // ZERO_IS_SIGNIFICANT = Boolean.FALSE
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                OPTION_SET
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistDataElementInDatabaseWithoutOptionSet() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        long rowId = dataElementStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                ZERO_IS_SIGNIFICANT,
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                null
        );

        Cursor cursor = database().query(Tables.DATA_ELEMENT, DATA_ELEMENT_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                DATE,
                DATE,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                0, // ZERO_IS_SIGNIFICANT = Boolean.FALSE
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                null
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistDataElementWithInvalidForeignKey() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        String fakeOptionSetUid = "fake_option_set_uid";
        long rowId = dataElementStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                timeStamp,
                timeStamp,
                SHORT_NAME,
                DISPLAY_SHORT_NAME,
                DESCRIPTION,
                DISPLAY_DESCRIPTION,
                VALUE_TYPE,
                ZERO_IS_SIGNIFICANT,
                AGGREGATION_OPERATOR,
                FORM_NAME,
                NUMBER_TYPE,
                DOMAIN_TYPE,
                DIMENSION,
                DISPLAY_FORM_NAME,
                fakeOptionSetUid
        );

        assertThat(rowId).isEqualTo(-1);
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        dataElementStore.close();

        assertThat(database().isOpen()).isTrue();
    }

}
