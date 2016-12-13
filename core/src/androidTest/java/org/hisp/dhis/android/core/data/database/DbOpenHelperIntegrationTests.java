package org.hisp.dhis.android.core.data.database;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DbOpenHelperIntegrationTests {
    private static final int DATABASE_TESTS_VERSION = 0;

    @Test
    public void databaseVersion_shouldHaveCorrespondingTests() {
        // ToDo: tests for schema migration (requires some research to be done)
        // make sure that whenever database version is bumped,
        // corresponding schema migration tests are implemented
        // assertThat(DATABASE_TESTS_VERSION).isEqualTo(DbOpenHelper.VERSION);
    }
}
