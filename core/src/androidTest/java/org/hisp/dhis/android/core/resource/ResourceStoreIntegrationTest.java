package org.hisp.dhis.android.core.resource;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.resource.ResourceModel.Columns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ResourceStoreIntegrationTest extends AbsStoreTestCase {
    private static final Long ID = 2L;
    private static final String RESOURCE_TYPE = "OrganisationUnit";
    private static final String RESOURCE_UID = "test_organisation_unit_uid";

    // timestamp
    private static final String DATE = "2017-01-18T13:39:00.000";

    private static final String[] PROJECTION = {Columns.RESOURCE_TYPE, Columns.RESOURCE_UID, Columns.LAST_SYNCED};

    private ResourceStore resourceStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.resourceStore = new ResourceStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistResourceInDatabase() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        long rowId = resourceStore.insert(RESOURCE_TYPE, RESOURCE_UID, date);

        // checking that resource was successfully inserted into database
        assertThat(rowId).isEqualTo(1L);

        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, RESOURCE_UID, DATE).isExhausted();

    }

    @Test
    public void insert_shouldPersistResourceInDatabaseWithoutLastSynced() throws Exception {
        long rowId = resourceStore.insert(RESOURCE_TYPE, RESOURCE_UID, null);
        assertThat(rowId).isEqualTo(1L);
        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, RESOURCE_UID, null).isExhausted();

    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_shouldNotPersistResourceInDatabaseWithoutResourceUid() throws Exception {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        resourceStore.insert(RESOURCE_TYPE, null, date);

    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_shouldNotPersistResourceInDatabaseWithoutResourceType() throws Exception {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        resourceStore.insert(null, RESOURCE_UID, date);
    }

}
