package org.hisp.dhis.android.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

import android.support.test.filters.MediumTest;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class TrackerDataCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;
    TrackedEntityInstanceStore trackedEntityInstanceStore;
    ResourceStore resourceStore;
    EnrollmentStore enrollmentStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        trackedEntityInstanceStore = new TrackedEntityInstanceStoreImpl(databaseAdapter());
        resourceStore = new ResourceStoreImpl(databaseAdapter());
        enrollmentStore = new EnrollmentStoreImpl(databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }


    @Test
    @MediumTest
    public void not_download_tracked_entity_instances_if_does_not_exists_nothing_in_database()
            throws Exception {

        givenAMetadataInDatabase();

        Response response = d2.syncTrackerData().call();

        verifyHaveNotSynchronized(response, Collections.EMPTY_LIST);
    }

    @Test
    @MediumTest
    public void not_download_tracked_entity_instances_if_does_not_exists_synced_in_database()
            throws Exception {

        givenAMetadataInDatabase();

        TrackedEntityInstance toPostTrackedEntityInstance =
                givenAToPostTrackedEntityInstanceInDatabase();

        Response response = d2.syncTrackerData().call();

        verifyHaveNotSynchronized(response, Arrays.asList(toPostTrackedEntityInstance));
    }

    @Test
    @MediumTest
    public void only_download_synced_tracked_entity_instance_that_exists_in_database()
            throws Exception {
        givenAMetadataInDatabase();

        TrackedEntityInstance toPostTrackedEntityInstance =
                givenAToPostTrackedEntityInstanceInDatabase();

        TrackedEntityInstance syncedTrackedEntityInstance =
                givenASyncedTrackedEntityInstanceInDatabase();

        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("tracked_entity_instance.json");

        Response response = d2.syncTrackerData().call();

        verifyHaveSynchronized(response,
                Arrays.asList(syncedTrackedEntityInstance),
                Arrays.asList(toPostTrackedEntityInstance));
    }

    @Test
    @MediumTest
    public void download_all_synced_tracked_entity_instances_that_exists_in_database()
            throws Exception {
        givenAMetadataInDatabase();

        List<TrackedEntityInstance> trackedEntityInstances =
                givenASyncedTrackedEntityInstancesInDatabase();

        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("tracked_entity_instance.json");
        dhis2MockServer.enqueueMockResponse("tracked_entity_instance_2.json");

        Response response = d2.syncTrackerData().call();

        verifyHaveSynchronized(response, trackedEntityInstances, Collections.EMPTY_LIST);
    }

    private void verifyHaveSynchronized(Response response,
            List<TrackedEntityInstance> syncedExpected,
            List<TrackedEntityInstance> toPostExpected) {

        Map<String, TrackedEntityInstance> toPostInDatabase =
                trackedEntityInstanceStore.queryToPost();

        Map<String, TrackedEntityInstance> syncedInDatabase =
                trackedEntityInstanceStore.querySynced();

        String lastUpdated = resourceStore.getLastUpdated(
                ResourceModel.Type.TRACKED_ENTITY_INSTANCE);

        Map<String, List<Enrollment>> enrollmentsMap = enrollmentStore.queryAll();

        assertThat(response.isSuccessful(), is(true));
        assertThat(syncedInDatabase.size(), is(syncedExpected.size()));
        assertThat(lastUpdated, is(notNullValue()));
        assertThat(toPostInDatabase.size(), is(toPostExpected.size()));

        for (TrackedEntityInstance trackedEntityInstance : syncedExpected) {
            assertThat(syncedInDatabase.containsKey(trackedEntityInstance.uid()), is(true));
            assertThat(enrollmentsMap.get(trackedEntityInstance.uid()), is(notNullValue()));

        }

        for (TrackedEntityInstance trackedEntityInstance : toPostExpected) {
            assertThat(toPostInDatabase.containsKey(trackedEntityInstance.uid()), is(true));
        }
    }

    private void verifyHaveNotSynchronized(Response response,
            List<TrackedEntityInstance> expectedToPost) {

        Map<String, TrackedEntityInstance> inDatabaseToPost =
                trackedEntityInstanceStore.queryToPost();

        Map<String, TrackedEntityInstance> inDatabaseSynced =
                trackedEntityInstanceStore.querySynced();

        String lastUpdated = resourceStore.getLastUpdated(
                ResourceModel.Type.TRACKED_ENTITY_INSTANCE);

        assertThat(response, is(nullValue()));
        assertThat(inDatabaseSynced.size(), is(0));
        assertThat(lastUpdated, is(nullValue()));

        assertThat(inDatabaseToPost.size(), is(expectedToPost.size()));

        for (TrackedEntityInstance trackedEntityInstance : expectedToPost) {
            assertThat(inDatabaseToPost.containsKey(trackedEntityInstance.uid()), is(true));
        }
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("user.json");
        dhis2MockServer.enqueueMockResponse("organisationUnits.json");
        dhis2MockServer.enqueueMockResponse("categories.json");
        dhis2MockServer.enqueueMockResponse("category_combos.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");
        d2.syncMetaData().call();
    }

    private TrackedEntityInstance givenAToPostTrackedEntityInstanceInDatabase() {
        TrackedEntityInstance mikeBensinger = givenATrackedEntityInstancesInDatabase(
                "IaxoagO9899", State.TO_POST);

        return mikeBensinger;
    }

    private TrackedEntityInstance givenASyncedTrackedEntityInstanceInDatabase() {
        TrackedEntityInstance ngelehunGunnarson = givenATrackedEntityInstancesInDatabase(
                "PgmUFEQYZdt", State.SYNCED);

        return ngelehunGunnarson;
    }

    private List<TrackedEntityInstance> givenASyncedTrackedEntityInstancesInDatabase() {
        TrackedEntityInstance ngelehunGunnarson = givenATrackedEntityInstancesInDatabase(
                "PgmUFEQYZdt", State.SYNCED);

        TrackedEntityInstance mikeBensinger = givenATrackedEntityInstancesInDatabase(
                "IaxoagO9899", State.SYNCED);

        return Arrays.asList(mikeBensinger, ngelehunGunnarson);
    }

    private TrackedEntityInstance givenATrackedEntityInstancesInDatabase(String uid, State state) {
        Date date = new Date();
        String dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);

        TrackedEntityInstance trackedEntityInstance = TrackedEntityInstance.create(
                uid, date, date, dateString, dateString,
                "DiszpKrYNg8", "nEenWmSyUEp",
                false, null, null, null);

        trackedEntityInstanceStore.insert(
                trackedEntityInstance.uid(), trackedEntityInstance.created(),
                trackedEntityInstance.lastUpdated(), trackedEntityInstance.createdAtClient(),
                trackedEntityInstance.lastUpdatedAtClient(),
                trackedEntityInstance.organisationUnit(),
                trackedEntityInstance.trackedEntity(), state);

        return trackedEntityInstance;
    }
}
