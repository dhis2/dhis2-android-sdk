package org.hisp.dhis.android.core.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class DataSetEndpointCallRealIntegrationShould extends AbsStoreTestCase {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private D2 d2;
    private Call<Response<Payload<DataSet>>> dataSetCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create("https://play.dhis2.org/android-current/api/", databaseAdapter());
        dataSetCall = createCall();
    }

    private Call<Response<Payload<DataSet>>> createCall() {
        GenericCallData data = GenericCallData.create(databaseAdapter(), d2.retrofit());

        Set<String> uids = new HashSet<>();

        uids.add("BfMAe6Itzgt");
        uids.add("Lpw6GcnTrmS");
        uids.add("TuL8IOPzpHh");

        return DataSetEndpointCall.FACTORY.create(data, uids);
    }

    // @Test
    public void download_data_sets() throws Exception {
        if (!d2.isUserLoggedIn().call()) {
            retrofit2.Response loginResponse = d2.logIn("android", "Android123").call();
            assertThat(loginResponse.isSuccessful()).isTrue();
        }

        /*  This test won't pass independently of DataElementEndpointCall and
            CategoryComboEndpointCall, as the foreign keys constraints won't be satisfied.
            To run the test, you will need to disable foreign key support in database in
            DbOpenHelper.java replacing 'foreign_keys = ON' with 'foreign_keys = OFF' and
            uncomment the @Test tag */

        retrofit2.Response dataSetResponse = dataSetCall.call();
        assertThat(dataSetResponse.isSuccessful()).isTrue();
    }

    @Test
    public void stub() {
    }
}
