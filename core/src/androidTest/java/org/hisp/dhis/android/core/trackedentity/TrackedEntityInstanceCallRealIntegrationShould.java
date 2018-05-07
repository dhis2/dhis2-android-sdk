package org.hisp.dhis.android.core.trackedentity;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;

public class TrackedEntityInstanceCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //This test is commented because technically it is flaky.
    //It depends on a live server to operate and the login is hardcoded here.
    //Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.

    //@Test
    public void download_tei_enrollments_and_events() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(response.isSuccessful()).isTrue();


        response = d2.syncMetaData().call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                TrackedEntityInstanceEndPointCall.create(
                        d2.databaseAdapter(), d2.retrofit(), "IaxoagO9899");

        response = trackedEntityInstanceEndPointCall.call();

        Truth.assertThat(response.isSuccessful()).isTrue();
    }
}
