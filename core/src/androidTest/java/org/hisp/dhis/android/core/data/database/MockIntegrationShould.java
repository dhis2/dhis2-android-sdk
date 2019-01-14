package org.hisp.dhis.android.core.data.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public abstract class MockIntegrationShould {

    private static SQLiteDatabase sqLiteDatabase;
    private static String dbName = null;
    protected static DatabaseAdapter databaseAdapter;

    private static Dhis2MockServer dhis2MockServer;
    protected static D2 d2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(
                InstrumentationRegistry.getTargetContext().getApplicationContext(), dbName);
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter);
        Stetho.initializeWithDefaults(InstrumentationRegistry.getTargetContext().getApplicationContext());
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        dhis2MockServer.shutdown();
        sqLiteDatabase.close();
    }

    protected static void login() throws Exception {
        dhis2MockServer.enqueueLoginResponses();
        d2.userModule().logIn("android", "Android123").call();
    }

    protected static void downloadMetadata() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
        d2.syncMetaData().call();
    }

    protected static void downloadEvents() throws Exception {
        dhis2MockServer.enqueueEventResponses();
        d2.downloadSingleEvents(1, false).call();
    }

    protected static void downloadTrackedEntityInstances() throws Exception {
        dhis2MockServer.enqueueTrackedEntityInstanceResponses();
        d2.downloadTrackedEntityInstances(1, false).call();
    }
}