package org.hisp.dhis.android.core.option;

import android.database.Cursor;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.option.OptionSetContract.Columns;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class OptionSetModelStoreIntegrationTest extends AbsStoreTestCase {

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final int VERSION = 51;

    // timestamp
    private static final String DATE = "2016-12-20T16:26:00.007";

    private static final String[] OPTION_SET_PROJECTION = {
            Columns.UID, Columns.CODE, Columns.NAME,
            Columns.DISPLAY_NAME, Columns.CREATED,
            Columns.LAST_UPDATED, Columns.VERSION, Columns.VALUE_TYPE
    };

    private OptionSetStore optionSetStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.optionSetStore = new OptionSetStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistOptionSetInDatabase() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        long rowId = optionSetStore.insert(
                UID, CODE, NAME, DISPLAY_NAME, date, date, VERSION, VALUE_TYPE);

        Cursor cursor = database().query(Tables.OPTION_SET, OPTION_SET_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID, CODE, NAME,
                DISPLAY_NAME, BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                VERSION, VALUE_TYPE).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        optionSetStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
