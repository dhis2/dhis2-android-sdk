package org.hisp.dhis.android.core.database;

import android.content.ContentValues;
import android.net.Uri;

import static com.google.common.truth.Truth.assertThat;

// ToDo: tests for notifications emitted by insert / update methods in cp:
//       Might be challenging because of MockContentResolver which does not propagate updates to observers
public class DbContentProviderIntegrationTests extends AbsProviderTestCase {
    private static final String TEST_RESOURCE = "test_resource";

    public void testInsertOnUnknownUri_shouldThrowException() {
        try {
            getProvider().insert(Uri.withAppendedPath(
                    DbContract.AUTHORITY_URI, TEST_RESOURCE), new ContentValues());

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }

    public void testUpdateOnUnknownUri_shouldThrowException() {
        try {
            getProvider().update(Uri.withAppendedPath(DbContract.AUTHORITY_URI, TEST_RESOURCE),
                    new ContentValues(), null, null);

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }

    public void testDeleteOnUnknownUri_shouldThrowException() {
        try {
            getProvider().delete(Uri.withAppendedPath(
                    DbContract.AUTHORITY_URI, TEST_RESOURCE), null, null);

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }

    public void testQueryOnUnknownUri_shouldThrowException() {
        try {
            getProvider().query(Uri.withAppendedPath(DbContract.AUTHORITY_URI, TEST_RESOURCE),
                    null, null, null, null);

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }

    public void testGetTypeOnUnknownUri_shouldThrowException() {
        try {
            getProvider().getType(Uri.withAppendedPath(
                    DbContract.AUTHORITY_URI, TEST_RESOURCE));

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }
}
