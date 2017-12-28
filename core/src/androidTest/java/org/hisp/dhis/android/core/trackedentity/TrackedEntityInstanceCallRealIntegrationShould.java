package org.hisp.dhis.android.core.trackedentity;

import android.support.test.filters.LargeTest;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.TrackedEntityInstanceCallFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TrackedEntityInstanceCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    @Test
    @LargeTest
    public void download_tei_enrollments_and_events() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(response.isSuccessful()).isTrue();


        response = d2.syncMetaData().call();
        Truth.assertThat(response.isSuccessful()).isTrue();

        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                TrackedEntityInstanceCallFactory.create(
                        d2.retrofit(), databaseAdapter(), "IaxoagO9899");

        response = trackedEntityInstanceEndPointCall.call();

        Truth.assertThat(response.isSuccessful()).isTrue();
    }
}
