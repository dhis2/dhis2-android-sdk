package org.hisp.dhis.android.core.constant;

import android.database.Cursor;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class ConstantStoreIntegrationTest extends AbsStoreTestCase {

    private ConstantStore constantStore;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Date CREATED = new java.util.Date();
    private static final Date LAST_UPDATED = new java.util.Date();
    private static final String VALUE = "0.18";

    private static final String[] CONSTANT_PROJECTION = {
            ConstantModel.Columns.UID, ConstantModel.Columns.CODE, ConstantModel.Columns.NAME, ConstantModel.Columns.DISPLAY_NAME,
            ConstantModel.Columns.CREATED, ConstantModel.Columns.LAST_UPDATED, ConstantModel.Columns.VALUE
    };

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        constantStore = new ConstantStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        long rowId = constantStore.insert(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, VALUE);
        Cursor cursor = database().query(DbOpenHelper.Tables.CONSTANT, CONSTANT_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isNotEqualTo(-1L); // Checks that the insert was successful (row ID would otherwise be -1)

        assertThatCursor(cursor).hasRow(UID, CODE, NAME, DISPLAY_NAME, BaseIdentifiableObject.DATE_FORMAT.format(CREATED), BaseIdentifiableObject.DATE_FORMAT.format(LAST_UPDATED), VALUE);
        assertThatCursor(cursor).isExhausted();
    }
}
