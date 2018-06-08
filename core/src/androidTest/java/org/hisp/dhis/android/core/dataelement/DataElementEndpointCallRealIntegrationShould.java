package org.hisp.dhis.android.core.dataelement;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class DataElementEndpointCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private Call<List<DataElement>> dataElementCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create("https://play.dhis2.org/android-current/api/", databaseAdapter());
        dataElementCall = createCall();
    }

    private Call<List<DataElement>> createCall() {
        GenericCallData data = GenericCallData.create(databaseAdapter(), d2.retrofit(), new Date());

        Set<String> uids = new HashSet<>();

        uids.add("FTRrcoaog83");
        uids.add("P3jJH5Tu5VC");
        uids.add("FQ2o8UBlcrS");

        return DataElementEndpointCall.FACTORY.create(data, uids);
    }

    // @Test
    public void download_data_elements() throws Exception {
        d2.logIn("android", "Android123").call();

        /*  This test won't pass independently of DataElementEndpointCall and
            CategoryComboEndpointCall, as the foreign keys constraints won't be satisfied.
            To run the test, you will need to disable foreign key support in database in
            DbOpenHelper.java replacing 'foreign_keys = ON' with 'foreign_keys = OFF' and
            uncomment the @Test tag */

        dataElementCall.call();
    }

    @Test
    public void stub() {
    }
}
