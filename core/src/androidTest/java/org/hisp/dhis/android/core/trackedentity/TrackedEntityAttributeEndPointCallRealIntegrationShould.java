package org.hisp.dhis.android.core.trackedentity;

import android.support.test.filters.LargeTest;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.HandlerFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class TrackedEntityAttributeEndPointCallRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;

    private TrackedEntityAttributeFactory trackedEntityAttributeFactory;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());

        trackedEntityAttributeFactory =
                new TrackedEntityAttributeFactory(d2.retrofit(), d2.databaseAdapter(),
                        HandlerFactory.createResourceHandler(databaseAdapter()));
    }

    @Test
    @LargeTest
    public void download_TrackedEntityAttributes_according_to_default_query() throws Exception {
        retrofit2.Response response = null;

        response = d2.logIn(RealServerMother.user, RealServerMother.password).call();

        Truth.assertThat(response.isSuccessful()).isTrue();


        Set<String> uIds =
                new HashSet<>(Arrays.asList("VqEFza8wbwA", "spFvx9FndA4", "gHGyrwKPzej"));

        TrackedEntityAttributeQuery trackedEntityAttributeQuery =
                new TrackedEntityAttributeQuery(uIds);

        response = trackedEntityAttributeFactory.newEndPointCall(trackedEntityAttributeQuery,
                new Date()).call();

        Truth.assertThat(response.isSuccessful()).isTrue();
    }


}
