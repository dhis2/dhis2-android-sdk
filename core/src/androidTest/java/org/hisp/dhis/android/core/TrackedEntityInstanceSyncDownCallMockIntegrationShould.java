package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentFields;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.period.FeatureType;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class TrackedEntityInstanceSyncDownCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;
    private TrackedEntityInstanceStore trackedEntityInstanceStore;
    private ResourceStore resourceStore;
    private EnrollmentStore enrollmentStore;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        trackedEntityInstanceStore = new TrackedEntityInstanceStoreImpl(databaseAdapter());
        resourceStore = new ResourceStoreImpl(databaseAdapter());
        enrollmentStore = EnrollmentStoreImpl.create(databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void not_download_tracked_entity_instances_if_does_not_exists_nothing_in_database()
            throws Exception {

        givenAMetadataInDatabase();

        d2.syncDownSyncedTrackedEntityInstances().call();

        verifyHaveNotSynchronized(Collections.EMPTY_LIST);
    }

    @Test
    public void not_download_tracked_entity_instances_if_does_not_exists_synced_in_database()
            throws Exception {

        givenAMetadataInDatabase();

        TrackedEntityInstance toPostTrackedEntityInstance =
                givenAToPostTrackedEntityInstanceInDatabase();

        d2.syncDownSyncedTrackedEntityInstances().call();

        verifyHaveNotSynchronized(Arrays.asList(toPostTrackedEntityInstance));
    }

    @Test
    public void only_download_synced_tracked_entity_instance_that_exists_in_database()
            throws Exception {
        givenAMetadataInDatabase();

        TrackedEntityInstance toPostTrackedEntityInstance =
                givenAToPostTrackedEntityInstanceInDatabase();

        TrackedEntityInstance syncedTrackedEntityInstance =
                givenASyncedTrackedEntityInstanceInDatabase();

        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance.json");

        d2.syncDownSyncedTrackedEntityInstances().call();

        verifyHaveSynchronized(Arrays.asList(syncedTrackedEntityInstance),
                Arrays.asList(toPostTrackedEntityInstance));
    }

    @Test
    public void download_all_synced_tracked_entity_instances_that_exists_in_database()
            throws Exception {
        givenAMetadataInDatabase();

        List<TrackedEntityInstance> trackedEntityInstances = givenASyncedTrackedEntityInstancesInDatabase();

        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance.json");
        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance_2.json");

        d2.syncDownSyncedTrackedEntityInstances().call();

        verifyHaveSynchronized(trackedEntityInstances, Collections.EMPTY_LIST);
    }

    private void verifyHaveSynchronized(List<TrackedEntityInstance> syncedExpected,
                                        List<TrackedEntityInstance> toPostExpected) {

        Map<String, TrackedEntityInstance> toPostInDatabase =
                trackedEntityInstanceStore.queryToPost();

        Map<String, TrackedEntityInstance> syncedInDatabase =
                trackedEntityInstanceStore.querySynced();

        String lastUpdated = resourceStore.getLastUpdated(
                ResourceModel.Type.TRACKED_ENTITY_INSTANCE);

        assertThat(syncedInDatabase.size(), is(syncedExpected.size()));
        assertThat(lastUpdated, is(nullValue()));
        assertThat(toPostInDatabase.size(), is(toPostExpected.size()));

        for (TrackedEntityInstance trackedEntityInstance : syncedExpected) {
            assertThat(syncedInDatabase.containsKey(trackedEntityInstance.uid()), is(true));
            List<Enrollment> enrollmentList = enrollmentStore.selectWhereClause(new WhereClauseBuilder()
                    .appendKeyStringValue(EnrollmentFields.TRACKED_ENTITY_INSTANCE, trackedEntityInstance.uid()).build());
            assertThat(enrollmentList, is(notNullValue()));
        }

        for (TrackedEntityInstance trackedEntityInstance : toPostExpected) {
            assertThat(toPostInDatabase.containsKey(trackedEntityInstance.uid()), is(true));
        }
    }

    private void verifyHaveNotSynchronized(
            List<TrackedEntityInstance> expectedToPost) {

        Map<String, TrackedEntityInstance> inDatabaseToPost =
                trackedEntityInstanceStore.queryToPost();

        Map<String, TrackedEntityInstance> inDatabaseSynced =
                trackedEntityInstanceStore.querySynced();

        String lastUpdated = resourceStore.getLastUpdated(
                ResourceModel.Type.TRACKED_ENTITY_INSTANCE);

        assertThat(inDatabaseSynced.size(), is(0));
        assertThat(lastUpdated, is(nullValue()));

        assertThat(inDatabaseToPost.size(), is(expectedToPost.size()));

        for (TrackedEntityInstance trackedEntityInstance : expectedToPost) {
            assertThat(inDatabaseToPost.containsKey(trackedEntityInstance.uid()), is(true));
        }
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
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
                "DiszpKrYNg8", "nEenWmSyUEp", "[9,9]", FeatureType.POINT,
                false, null, null, null);

        trackedEntityInstanceStore.insert(
                trackedEntityInstance.uid(), trackedEntityInstance.created(),
                trackedEntityInstance.lastUpdated(), trackedEntityInstance.createdAtClient(),
                trackedEntityInstance.lastUpdatedAtClient(), trackedEntityInstance.organisationUnit(),
                trackedEntityInstance.trackedEntityType(), trackedEntityInstance.coordinates(),
                trackedEntityInstance.featureType(), state);

        return trackedEntityInstance;
    }
}
