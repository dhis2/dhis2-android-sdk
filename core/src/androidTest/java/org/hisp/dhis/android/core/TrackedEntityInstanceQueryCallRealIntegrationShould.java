package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQuery;
import org.hisp.dhis.android.core.user.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;

public class TrackedEntityInstanceQueryCallRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void query_tracked_entity_instances() throws Exception {
        d2.logout().call();
        Response<User> loginResponse = d2.logIn("android", "Android123").call();
        assertThat(loginResponse.isSuccessful()).isTrue();

        List<String> orgUnits = new ArrayList<>();
        orgUnits.add("O6uvpzGd5pu");

        TrackedEntityInstanceQuery query = TrackedEntityInstanceQuery.builder()
                .paging(true).page(1).pageSize(50)
                .orgUnits(orgUnits).orgUnitMode(OuMode.ACCESSIBLE).program("IpHINAT79UW").build();

        List<TrackedEntityInstance> queryResponse = d2.queryTrackedEntityInstances(query).call();
        assertThat(queryResponse).isNotEmpty();
    }
}
