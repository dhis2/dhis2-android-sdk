package org.hisp.dhis.android.core.trackedentity;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeValueStoreIntegrationTests extends AbsStoreTestCase {
    //BaseDataModel:
    private static final State STATE = State.SYNCED;
    //TrackedEntityAttributeValueModel:
    private static final String ATTRIBUTE = "TestAttribute";
    private static final String VALUE = "TestValue";

    private static final String[] TRACKED_ENTITY_ATTRIBUTE_VALUE_PROJECTION = {
            TrackedEntityAttributeValueModel.Columns.STATE,
            TrackedEntityAttributeValueModel.Columns.ATTRIBUTE,
            TrackedEntityAttributeValueModel.Columns.VALUE
    };

    private TrackedEntityAttributeValueStore store;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.store = new TrackedEntityAttributeValueStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistTrackedEntityAttributeValueInDatabase() {
        long rowId = store.insert(STATE, ATTRIBUTE, VALUE);

        Cursor cursor = database().query(Tables.TRACKED_ENTITY_ATTRIBUTE_VALUE,
                TRACKED_ENTITY_ATTRIBUTE_VALUE_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(STATE, ATTRIBUTE, VALUE).isExhausted();
    }

    @Test
    public void insert_shouldPersistTrackedEntityAttributeValueNullableInDatabase() {
        long rowId = store.insert(STATE, ATTRIBUTE, null);

        Cursor cursor = database().query(Tables.TRACKED_ENTITY_ATTRIBUTE_VALUE,
                TRACKED_ENTITY_ATTRIBUTE_VALUE_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(STATE, ATTRIBUTE, null).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        store.close();
        assertThat(database().isOpen()).isTrue();
    }
}
