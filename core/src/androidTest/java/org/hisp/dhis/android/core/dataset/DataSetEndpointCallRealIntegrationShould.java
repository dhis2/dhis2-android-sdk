package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

@RunWith(AndroidJUnit4.class)
public class DataSetEndpointCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private Callable<List<DataSet>> dataSetCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create("https://play.dhis2.org/android-current/api/", databaseAdapter());
        dataSetCall = createCall();
    }

    private Callable<List<DataSet>> createCall() {
        return getD2DIComponent(d2).dataSetCallFactory().create();
    }

    // @Test
    public void download_data_sets() throws Exception {
        if (!d2.userModule().isLogged().call()) {
            d2.userModule().logIn("android", "Android123").call();
        }

        /*  This test won't pass independently of DataElementEndpointCallFactory and
            CategoryComboEndpointCallFactory, as the foreign keys constraints won't be satisfied.
            To run the test, you will need to disable foreign key support in database in
            DbOpenHelper.java replacing 'foreign_keys = ON' with 'foreign_keys = OFF' and
            uncomment the @Test tag */

        dataSetCall.call();
    }

    @Test
    public void stub() {
    }
}
