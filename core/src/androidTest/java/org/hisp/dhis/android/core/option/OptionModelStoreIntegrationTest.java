package org.hisp.dhis.android.core.option;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.option.OptionContract.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class OptionModelStoreIntegrationTest extends AbsStoreTestCase {

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final long OPTION_SET_ID = 53L;
    private static final String OPTION_SET_UID = "test_option_set_uid";

    private static final String[] OPTION_PROJECTION = {
            Columns.UID, Columns.CODE, Columns.NAME,
            Columns.DISPLAY_NAME, Columns.CREATED, Columns.LAST_UPDATED,
            Columns.OPTION_SET
    };

    // timestamp
    private static final String DATE = "2016-12-20T16:26:00.007";

    private OptionStore optionStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.optionStore = new OptionStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistOptionInDatabase() throws ParseException {
        // INSERT OPTION SETS
        ContentValues optionSet =
                OptionSetModelIntegrationTest.create(OPTION_SET_ID, OPTION_SET_UID);

        database().insert(Tables.OPTION_SET, null, optionSet);

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        long rowId = optionStore.insert(
                UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID
        );

        Cursor cursor = database().query(Tables.OPTION, OPTION_PROJECTION,
                null, null, null, null, null);

        // Checking if rowId == 1.
        // If it is 1, then it means it is first successful insert into db
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID, CODE, NAME,
                DISPLAY_NAME, BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date), OPTION_SET_UID)
                .isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_persistOptionWithoutForeignKey() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        long rowId = optionStore.insert(
                UID, CODE, NAME, DISPLAY_NAME, date, date, OPTION_SET_UID
        );

        assertThat(rowId).isEqualTo(-1);
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        optionStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
