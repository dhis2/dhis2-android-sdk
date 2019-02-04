package org.hisp.dhis.android.core.dataelement;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@RunWith(AndroidJUnit4.class)
public class DataElementEndpointCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private Callable<List<DataElement>> dataElementCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create("https://play.dhis2.org/android-current/api/", databaseAdapter());
        dataElementCall = createCall();
    }

    private Callable<List<DataElement>> createCall() {
        Set<String> uids = new HashSet<>();

        uids.add("FTRrcoaog83");
        uids.add("P3jJH5Tu5VC");
        uids.add("FQ2o8UBlcrS");

        APICallExecutor apiCallExecutor = APICallExecutorImpl.create(d2.databaseAdapter());
        DataElementService service = d2.retrofit().create(DataElementService.class);

        return new DataElementEndpointCallFactory(getGenericCallData(d2), apiCallExecutor, service,
                DataElementHandler.create(databaseAdapter())).create(uids);
    }

    // @Test
    public void download_data_elements() throws Exception {
        d2.userModule().logIn("android", "Android123").call();

        /*  This test won't pass independently of DataElementEndpointCallFactory and
            CategoryComboEndpointCallFactory, as the foreign keys constraints won't be satisfied.
            To run the test, you will need to disable foreign key support in database in
            DbOpenHelper.java replacing 'foreign_keys = ON' with 'foreign_keys = OFF' and
            uncomment the @Test tag */

        dataElementCall.call();
    }

    @Test
    public void stub() {
    }
}
