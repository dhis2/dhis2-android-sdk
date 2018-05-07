package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQuery;
import org.hisp.dhis.android.core.user.User;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;

public class TrackedEntityInstanceQueryCallRealIntegrationShould extends AbsStoreTestCase {
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
                .orgUnits(orgUnits).orgUnitMode(OuMode.ACCESSIBLE).program("IpHINAT79UW");
    }

    //@Test
    public void query_tracked_entity_instances_no_filter() throws Exception {
        login();
        List<TrackedEntityInstance> queryResponse = d2.queryTrackedEntityInstances(queryBuilder.build()).call();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_filter_name() throws Exception {
        login();

        TrackedEntityInstanceQuery query = queryBuilder.query("jorge").build();
        List<TrackedEntityInstance> queryResponse = d2.queryTrackedEntityInstances(query).call();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_one_attribute() throws Exception {
        login();

        List<String> attributeList = new ArrayList<>(1);
        attributeList.add("w75KJ2mc4zz:like:jorge");

        TrackedEntityInstanceQuery query = queryBuilder.attribute(attributeList).build();
        List<TrackedEntityInstance> queryResponse = d2.queryTrackedEntityInstances(query).call();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_two_attributes() throws Exception {
        login();

        List<String> attributeList = new ArrayList<>(2);
        attributeList.add("w75KJ2mc4zz:like:Filona");
        attributeList.add("zDhUuAYrxNC:like:Ryder");

        TrackedEntityInstanceQuery query = queryBuilder.attribute(attributeList).build();
        List<TrackedEntityInstance> queryResponse = d2.queryTrackedEntityInstances(query).call();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void use_attribute_to_reduce_attributes_returned() throws Exception {
        login();

        List<String> attributeList = new ArrayList<>(1);
        attributeList.add("w75KJ2mc4zz");

        TrackedEntityInstanceQuery query = queryBuilder.attribute(attributeList).build();
        List<TrackedEntityInstance> queryResponse = d2.queryTrackedEntityInstances(query).call();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_one_filter() throws Exception {
        login();

        List<String> filterList = new ArrayList<>(1);
        filterList.add("w75KJ2mc4zz:like:jorge");

        TrackedEntityInstanceQuery query = queryBuilder.filter(filterList).build();
        List<TrackedEntityInstance> queryResponse = d2.queryTrackedEntityInstances(query).call();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_two_filters() throws Exception {
        login();

        List<String> filterList = new ArrayList<>(2);
        filterList.add("w75KJ2mc4zz:like:Filona");
        filterList.add("zDhUuAYrxNC:like:Ryder");

        TrackedEntityInstanceQuery query = queryBuilder.filter(filterList).build();
        List<TrackedEntityInstance> queryResponse = d2.queryTrackedEntityInstances(query).call();
        assertThat(queryResponse).isNotEmpty();
    }

    private void login() throws Exception {
        d2.logout().call();
        Response<User> loginResponse = d2.logIn("android", "Android123").call();
        assertThat(loginResponse.isSuccessful()).isTrue();
    }
}
