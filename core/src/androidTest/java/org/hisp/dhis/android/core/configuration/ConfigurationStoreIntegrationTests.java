package org.hisp.dhis.android.core.configuration;

import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class ConfigurationStoreIntegrationTests extends AbsStoreTestCase {
    private static final String[] PROJECTION = {
            ConfigurationModel.Columns.ID,
            ConfigurationModel.Columns.SERVER_URL
    };

    private ConfigurationStore configurationStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        configurationStore = new ConfigurationStoreImpl(database());
    }

    @Test
    public void save_shouldPersistRowInDatabase() {
        long rowId = configurationStore.save("test_server_url");

        Cursor cursor = database().query(DbOpenHelper.Tables.CONFIGURATION,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(1L, "test_server_url")
                .isExhausted();
    }

    @Test
    public void save_shouldNotThrowOnConflict() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "test_server_url");

        database().insert(DbOpenHelper.Tables.CONFIGURATION, null, contentValues);

        // trying to save configuration with server url (which is set to be unique in the table)
        long rowId = configurationStore.save("test_server_url");

        Cursor cursor = database().query(DbOpenHelper.Tables.CONFIGURATION,
                PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(1L, "test_server_url")
                .isExhausted();
    }

    @Test
    public void save_shouldNotPersistMoreThatOneUrl() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "test_server_url");

        database().insert(DbOpenHelper.Tables.CONFIGURATION, null, contentValues);

        long rowId = configurationStore.save("test_another_url");

        Cursor cursor = database().query(DbOpenHelper.Tables.CONFIGURATION,
                PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(1L, "test_another_url")
                .isExhausted();
    }

    @Test
    public void delete_shouldDeletePersistedRows() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "test_server_url");

        database().insert(DbOpenHelper.Tables.CONFIGURATION, null, contentValues);

        long deleted = configurationStore.delete();

        Cursor cursor = database().query(DbOpenHelper.Tables.CONFIGURATION,
                PROJECTION, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_shouldNotFail_ifNoRowsArePersisted() {
        long deleted = configurationStore.delete();
        assertThat(deleted).isEqualTo(0);
    }

    @Test
    public void query_shouldReturnPersistedRow() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "test_server_url");

        database().insert(DbOpenHelper.Tables.CONFIGURATION, null, contentValues);

        ConfigurationModel persistedConfiguration = configurationStore.query();
        assertThat(persistedConfiguration.id()).isEqualTo(1L);
        assertThat(persistedConfiguration.serverUrl()).isEqualTo("test_server_url");
    }

    @Test
    public void query_shouldReturnNull_ifNoRowsArePersisted() {
        ConfigurationModel persistedConfiguration = configurationStore.query();
        assertThat(persistedConfiguration).isNull();
    }
}
