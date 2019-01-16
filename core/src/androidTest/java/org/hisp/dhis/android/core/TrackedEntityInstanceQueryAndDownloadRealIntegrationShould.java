package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQuery;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

public class TrackedEntityInstanceQueryAndDownloadRealIntegrationShould extends AbsStoreTestCase {
    private D2 d2;
    private TrackedEntityInstanceQuery.Builder queryBuilder;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());


        List<String> orgUnits = new ArrayList<>();
        orgUnits.add("O6uvpzGd5pu");

        queryBuilder = TrackedEntityInstanceQuery.builder()
                .paging(true).page(1).pageSize(50)
                .orgUnits(orgUnits).orgUnitMode(OuMode.ACCESSIBLE);
    }

    //@Test
    public void query_and_download_tracked_entity_instances() throws Exception {
        login();

        d2.syncMetaData().call();

        TrackedEntityInstanceQuery query = queryBuilder.build();
        List<TrackedEntityInstance> queriedTeis = d2.trackedEntityModule().queryTrackedEntityInstances(query).call();
        assertThat(queriedTeis).isNotEmpty();

        Set<String> uids = new HashSet<>(queriedTeis.size());

        for(TrackedEntityInstance tei: queriedTeis) {
            uids.add(tei.uid());
        }

        List<TrackedEntityInstance> downloadedTeis = d2.trackedEntityModule().downloadTrackedEntityInstancesByUid(uids).call();
        assertThat(queriedTeis.size()).isEqualTo(downloadedTeis.size());
    }

    private void login() throws Exception {
        d2.userModule().logIn("android", "Android123").call();
    }
}
