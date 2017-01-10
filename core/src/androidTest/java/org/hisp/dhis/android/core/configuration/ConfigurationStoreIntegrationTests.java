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
            ConfigurationContract.Columns.ID,
            ConfigurationContract.Columns.SERVER_URL
    };

    private ConfigurationStore configurationStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        configurationStore = new ConfigurationStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        long rowId = configurationStore.insert("test_server_url");

        Cursor cursor = database().query(DbOpenHelper.Tables.CONFIGURATION,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(1L, "test_server_url")
                .isExhausted();
    }

    @Test
    public void query_shouldReturnPersistedRows() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConfigurationContract.Columns.SERVER_URL, "test_server_url");

        database().insert(DbOpenHelper.Tables.CONFIGURATION, null, contentValues);

        ConfigurationModel configuration = ConfigurationModel.builder()
                .id(1L).serverUrl("test_server_url")
                .build();

        assertThat(configurationStore.query().size()).isEqualTo(1);
        assertThat(configurationStore.query()).contains(configuration);
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        configurationStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
