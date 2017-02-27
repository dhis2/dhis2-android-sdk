package org.hisp.dhis.android.core.resource;

import android.content.ContentValues;
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
public class ResourceStoreTests extends AbsStoreTestCase {
    private static final String RESOURCE_TYPE = "TestClassName";

    private static final String[] PROJECTION = {Columns.RESOURCE_TYPE, Columns.LAST_SYNCED};

    private ResourceStore resourceStore;

    private final Date date;
    //    private final Date date2;
    private final String dateString;
    private static final String dateString2 = "2001-01-18T13:39:00.000";

    public ResourceStoreTests() throws ParseException {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
//        this.date2 = BaseIdentifiableObject.DATE_FORMAT.parse(dateString2);
    }

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.resourceStore = new ResourceStoreImpl(databaseAdapter());
    }

    @Test
    public void insert_shouldPersistResourceInDatabase() {
        long rowId = resourceStore.insert(RESOURCE_TYPE, date);
        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, dateString).isExhausted();
    }

    @Test
    public void insert_shouldPersistResourceInDatabaseWithoutLastSynced() {
        long rowId = resourceStore.insert(RESOURCE_TYPE, null);
        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, null).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_insert_shouldNotPersistResourceInDatabaseWithoutResourceType() throws Exception {
        resourceStore.insert(null, date);
    }

    @Test
    public void update_shouldUpdateExisting() {
        database().insert(ResourceModel.TABLE, null, createResource(RESOURCE_TYPE, dateString2));

        int returnValue = resourceStore.update(RESOURCE_TYPE, date, RESOURCE_TYPE);

        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(returnValue).isNotNull();
        assertThat(returnValue).isEqualTo(1);
        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, dateString).isExhausted();
    }

    @Test
    public void update_shouldNotInsert() {
        int returnValue = resourceStore.update(RESOURCE_TYPE, date, RESOURCE_TYPE);

        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(returnValue).isEqualTo(0);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void update_shouldNotUpdate_WithoutWhere() {
        database().insert(ResourceModel.TABLE, null, createResource(RESOURCE_TYPE, dateString));

        int returnValue = resourceStore.update(RESOURCE_TYPE, date, null);

        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);
        assertThat(returnValue).isNotNull();
        assertThat(returnValue).isEqualTo(0);
        assertThatCursor(cursor).hasRow(RESOURCE_TYPE, dateString).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_update_ShouldNotPersist_WithoutResourceType() {
        database().insert(ResourceModel.TABLE, null, createResource(RESOURCE_TYPE, dateString));
        resourceStore.update(null, date, RESOURCE_TYPE);
    }

    @Test
    public void delete_shouldDeleteRow() {
        database().insert(ResourceModel.TABLE, null, createResource(RESOURCE_TYPE, dateString));
        int returnValue = resourceStore.delete(RESOURCE_TYPE);

        Cursor cursor = database().query(ResourceModel.TABLE, PROJECTION, null, null, null, null, null);

        assertThat(returnValue).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void getLastUpdated_shouldReturnCorrectLastUpdated() {
        database().insert(ResourceModel.TABLE, null, createResource(RESOURCE_TYPE, dateString));

        String lastUpdated = resourceStore.getLastUpdated(RESOURCE_TYPE);

        assertThat(lastUpdated).isNotNull();
        assertThat(lastUpdated).isEqualTo(dateString);
    }

    @Test
    public void getLastUpdated_shouldReturnNull_IfNotExisting() {
        String lastUpdated = resourceStore.getLastUpdated(RESOURCE_TYPE);
        assertThat(lastUpdated).isNull();
    }

    private ContentValues createResource(String resourceName, String dateString) {
        ContentValues resource = new ContentValues();
        resource.put(Columns.RESOURCE_TYPE, resourceName);
        resource.put(Columns.LAST_SYNCED, dateString);
        return resource;
    }
}
