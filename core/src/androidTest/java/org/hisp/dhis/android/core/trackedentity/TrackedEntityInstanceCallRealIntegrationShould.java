package org.hisp.dhis.android.core.trackedentity;

import com.google.common.collect.Lists;
import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

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
        d2.logIn(RealServerMother.user, RealServerMother.password).call();

        d2.syncMetaData().call();

        Call<List<TrackedEntityInstance>> trackedEntityInstanceByUidEndPointCall =
                d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList("IaxoagO9899"));

        List<TrackedEntityInstance> teiResponse = trackedEntityInstanceByUidEndPointCall.call();

        Truth.assertThat(teiResponse.isEmpty()).isFalse();
    }
}
