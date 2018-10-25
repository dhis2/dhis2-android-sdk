package org.hisp.dhis.android.core.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public abstract class MockIntegrationShould {

    private static SQLiteDatabase sqLiteDatabase;
    private static String dbName = null;

    private static Dhis2MockServer dhis2MockServer;
    protected static D2 d2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(
                InstrumentationRegistry.getTargetContext().getApplicationContext(), dbName);
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        DatabaseAdapter databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());
        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        dhis2MockServer.shutdown();
        sqLiteDatabase.close();
    }

    protected static void downloadMetadata() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
        d2.syncMetaData().call();
    }
}