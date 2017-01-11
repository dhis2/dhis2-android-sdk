package org.hisp.dhis.android.core.systeminfo;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class SystemInfoStoreIntegrationTests extends AbsStoreTestCase {

    private static final long ID = 1L;
    private static final String DATE_FORMAT = "testDateFormat";

    private static final String[] SYSTEM_INFO_PROJECTION = {
            Columns.SERVER_DATE,
            Columns.DATE_FORMAT
    };

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    private SystemInfoStore systemInfoStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.systemInfoStore = new SystemInfoStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistSystemInfoInDatabase() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = systemInfoStore.insert(date, DATE_FORMAT);
        Cursor cursor = database().query(
                DbOpenHelper.Tables.SYSTEM_INFO,
                SYSTEM_INFO_PROJECTION,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                DATE_FORMAT
        ).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        systemInfoStore.close();
        assertThat(database().isOpen()).isTrue();
    }
}
