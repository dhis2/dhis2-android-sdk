package org.hisp.dhis.android.core.common;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.user.UserCredentialsModel;
import org.hisp.dhis.android.core.user.UserCredentialsStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ForeignKeyCleanerShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;
    private final String[] PROJECTION = {
            UserCredentialsModel.Columns.UID,
            UserCredentialsModel.Columns.USER
    };

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void remove_rows_that_produce_foreign_key_errors() throws Exception {
        final D2CallExecutor executor = new D2CallExecutor();

        executor.executeD2CallTransactionally(d2.databaseAdapter(), new Callable<Void>() {
            @Override
            public Void call() throws D2CallException {
                givenAMetadataInDatabase();
                new UserCredentialsStoreImpl(d2.databaseAdapter()).insert("user_credential_uid1", null,
                        null, null, null, null, null, "no_user_uid");
                assertThatCursorHasRowCount(2);
                new ForeignKeyCleaner(d2.databaseAdapter()).cleanForeignKeyErrors();
                return null;
            }
        });

        Cursor cursor = getCursor();
        assertThatCursorHasRowCount(1);
        cursor.moveToFirst();

        int uidColumnIndex = cursor.getColumnIndex(UserCredentialsModel.Columns.UID);
        Truth.assertThat(cursor.getString(uidColumnIndex)).isEqualTo("M0fCOxtkURr");
        int userColumnIndex = cursor.getColumnIndex(UserCredentialsModel.Columns.USER);
        Truth.assertThat(cursor.getString(userColumnIndex)).isEqualTo("DXyJmlo9rge");

        assertThatCursor(cursor).isExhausted();
    }

    private void givenAMetadataInDatabase() {
        try {
            dhis2MockServer.enqueueMetadataResponses();
            d2.syncMetaData().call();
        } catch (Exception ignore) {
        }
    }

    private Cursor getCursor() {
        return database().query(UserCredentialsModel.TABLE, PROJECTION, null, null,
                null, null, null);
    }

    private void assertThatCursorHasRowCount(int rowCount) {
        Truth.assertThat(getCursor().getCount()).isEqualTo(rowCount);
    }
}