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
    private ConfigurationModel configurationModel;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        configurationStore = new ConfigurationStoreImpl(database());
        configurationModel = ConfigurationModel.builder()
                .id(10L)
                .serverUrl("test_server_url")
                .build();
    }

    @Test
    public void save_shouldPersistRowInDatabase() {
        long rowId = configurationStore.save(configurationModel);

        Cursor cursor = database().query(DbOpenHelper.Tables.CONFIGURATION,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(10L);
        assertThatCursor(cursor)
                .hasRow(10L, "test_server_url")
                .isExhausted();
    }

    @Test
    public void save_shouldNotThrowOnConflict() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "test_server_url");

        database().insert(DbOpenHelper.Tables.CONFIGURATION, null, contentValues);

        // trying to save configuration with server url (which is set to be unique in the table)
        long rowId = configurationStore.save(configurationModel);

        Cursor cursor = database().query(DbOpenHelper.Tables.CONFIGURATION,
                PROJECTION, null, null, null, null, null);
        assertThat(rowId).isEqualTo(10L);
        assertThatCursor(cursor)
                .hasRow(10L, "test_server_url")
                .isExhausted();
    }

    @Test
    public void query_shouldReturnPersistedRows() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationModel.Columns.SERVER_URL, "test_server_url");

        database().insert(DbOpenHelper.Tables.CONFIGURATION, null, contentValues);

        ConfigurationModel configuration = ConfigurationModel.builder()
                .id(1L).serverUrl("test_server_url")
                .build();

        assertThat(configurationStore.query().size()).isEqualTo(1);
        assertThat(configurationStore.query()).contains(configuration);
    }
}
