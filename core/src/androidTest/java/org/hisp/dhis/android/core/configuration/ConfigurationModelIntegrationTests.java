package org.hisp.dhis.android.core.configuration;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.configuration.ConfigurationContract.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ConfigurationModelIntegrationTests {
    private static final long ID = 11L;
    private static final String SERVER_URL = "https://testurl.org";

    @Test
    public void create_shouldConvertToModel() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.SERVER_URL
        });

        matrixCursor.addRow(new Object[]{
                ID, SERVER_URL
        });

        matrixCursor.moveToFirst();

        ConfigurationModel configuration = ConfigurationModel.create(matrixCursor);

        assertThat(configuration.id()).isEqualTo(ID);
        assertThat(configuration.serverUrl()).isEqualTo(SERVER_URL);
    }

    @Test
    public void toContentValues_shouldConvertToContentValuesCorrectly() {
        ConfigurationModel configurationModel = ConfigurationModel.builder()
                .id(ID).serverUrl(SERVER_URL).build();

        ContentValues contentValues = configurationModel.toContentValues();
        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.SERVER_URL)).isEqualTo(SERVER_URL);
    }
}
