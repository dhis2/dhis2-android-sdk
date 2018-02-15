package org.hisp.dhis.android.core.trackedentity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import android.support.annotation.NonNull;
import android.support.test.filters.MediumTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.TrackedEntityInstanceCallFactory;
import org.hisp.dhis.android.core.common.responses.BasicMetadataMockResponseList;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipStore;
import org.hisp.dhis.android.core.relationship.RelationshipStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class TrackedEntityInstanceCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());
        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    @MediumTest
    public void download_tracked_entity_instance_enrollments_and_events() throws Exception {
        String teiUid = "PgmUFEQYZdt";

        givenAMetadataInDatabase();

        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                TrackedEntityInstanceCallFactory.create(
                        d2.retrofit(), databaseAdapter(), teiUid);

        dhis2MockServer.enqueueMockResponse("tracked_entity_instance.json");

        trackedEntityInstanceEndPointCall.call();

        verifyDownloadedTrackedEntityInstance("tracked_entity_instance.json", teiUid);
    }

    @Test
    @MediumTest
    public void download_tracked_entity_instance_enrollments_and_events_with_relationships() throws Exception {
        String teiUid = "Tm9Lh2J2n1M";

        givenAMetadataInDatabase();

        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                TrackedEntityInstanceCallFactory.create(
                        d2.retrofit(), databaseAdapter(), teiUid);

        dhis2MockServer.enqueueMockResponse("tracked_entity_instance_with_relationships.json");

        trackedEntityInstanceEndPointCall.call();

        verifyDownloadedTrackedEntityInstance("tracked_entity_instance_with_relationships.json", teiUid);
    }

    @Test
    @MediumTest
    public void remove_data_removed_in_server_after_second_download()
            throws Exception {
        String teiUid = "PgmUFEQYZdt";

        givenAMetadataInDatabase();

        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                TrackedEntityInstanceCallFactory.create(
                        d2.retrofit(), databaseAdapter(), teiUid);

        dhis2MockServer.enqueueMockResponse("tracked_entity_instance.json");

        trackedEntityInstanceEndPointCall.call();

        trackedEntityInstanceEndPointCall =
                TrackedEntityInstanceCallFactory.create(
                        d2.retrofit(), databaseAdapter(), teiUid);


        dhis2MockServer.enqueueMockResponse("tracked_entity_instance_with_removed_data.json");

        trackedEntityInstanceEndPointCall.call();

        verifyDownloadedTrackedEntityInstance("tracked_entity_instance_with_removed_data.json",
                teiUid);
    }

    @Test
    @MediumTest
    public void remove_relationship_removed_in_server_after_second_download()
            throws Exception {
        String teiUid = "Tm9Lh2J2n1M";

        givenAMetadataInDatabase();

        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                TrackedEntityInstanceCallFactory.create(
                        d2.retrofit(), databaseAdapter(), teiUid);

        dhis2MockServer.enqueueMockResponse("tracked_entity_instance_with_relationships.json");

        trackedEntityInstanceEndPointCall.call();

        trackedEntityInstanceEndPointCall =
                TrackedEntityInstanceCallFactory.create(
                        d2.retrofit(), databaseAdapter(), teiUid);


        dhis2MockServer.enqueueMockResponse(
                "tracked_entity_instance_with_relationships_with_removed_relationships.json");

        trackedEntityInstanceEndPointCall.call();

        verifyDownloadedTrackedEntityInstance(
                "tracked_entity_instance_with_relationships_with_removed_relationships.json",
                teiUid);
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponses(new BasicMetadataMockResponseList());
        Response response = d2.syncMetaData().call();
    }

    private void verifyDownloadedTrackedEntityInstance(String file, String teiUid)
            throws IOException {
        TrackedEntityInstance expectedEnrollmentResponse = parseTrackedEntityInstanceResponse(file);

        TrackedEntityInstance downloadedTei = addRelationships(getDownloadedTei(teiUid));

        assertThat(downloadedTei, is(expectedEnrollmentResponse));
    }

    private TrackedEntityInstance addRelationships(TrackedEntityInstance trackedEntityInstance) {
        RelationshipStore relationshipStore = new RelationshipStoreImpl(databaseAdapter());
        List<Relationship> relationships = relationshipStore.queryByTrackedEntityInstanceUid(trackedEntityInstance.uid());

        if (relationships != null && relationships.size() > 0) {
            trackedEntityInstance = TrackedEntityInstance.builder().uid(trackedEntityInstance.uid())
                    .created(trackedEntityInstance.created()).lastUpdated(
                            trackedEntityInstance.lastUpdated())
                    .createdAtClient(trackedEntityInstance.createdAtClient())
                    .lastUpdatedAtClient(trackedEntityInstance.lastUpdatedAtClient())
                    .organisationUnit(trackedEntityInstance.organisationUnit())
                    .trackedEntity(trackedEntityInstance.trackedEntity())
                    .deleted(trackedEntityInstance.deleted())
                    .trackedEntityAttributeValues(
                            trackedEntityInstance.trackedEntityAttributeValues())
                    .relationships(relationships)
                    .enrollments(trackedEntityInstance.enrollments()).build();
        }

        return trackedEntityInstance;
    }

    private TrackedEntityInstance parseTrackedEntityInstanceResponse(String file)
            throws IOException {
        String expectedEventsResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        TrackedEntityInstance trackedEntityInstance = objectMapper.readValue(
                expectedEventsResponseJson,
                new TypeReference<TrackedEntityInstance>() {
                });

        trackedEntityInstance = removeDeletedData(trackedEntityInstance);


        return trackedEntityInstance;
    }

    @NonNull
    private TrackedEntityInstance removeDeletedData(TrackedEntityInstance trackedEntityInstance) {
        Map<String, List<Event>> expectedEvents = new HashMap<>();
        List<Enrollment> expectedEnrollments = new ArrayList<>();


        for (Enrollment enrollment : trackedEntityInstance.enrollments()) {
            for (Event event : enrollment.events()) {
                if (!event.deleted()) {
                    if (expectedEvents.get(event.enrollmentUid()) == null) {
                        expectedEvents.put(event.enrollmentUid(), new ArrayList<Event>());
                    }

                    expectedEvents.get(event.enrollmentUid()).add(event);

                }
            }
            if (enrollment.deleted()!=null && !enrollment.deleted()) {
                enrollment = enrollment.toBuilder()
                        .events(expectedEvents.get(enrollment.uid()))
                        .build();

                expectedEnrollments.add(enrollment);
            }
        }
        if(expectedEnrollments.size()>0){
            trackedEntityInstance = trackedEntityInstance.toBuilder()
                    .enrollments(expectedEnrollments).build();
        }
        return trackedEntityInstance;
    }

    private TrackedEntityInstance getDownloadedTei(String teiUid) {
        TrackedEntityInstance downloadedTei;

        TrackedEntityAttributeValueStore teiAttributeValuesStore =
                new TrackedEntityAttributeValueStoreImpl(databaseAdapter());

        Map<String, List<TrackedEntityAttributeValue>> attValues =
                teiAttributeValuesStore.queryAll();

        TrackedEntityInstanceStoreImpl teiStore =
                new TrackedEntityInstanceStoreImpl(databaseAdapter());

        downloadedTei = teiStore.queryAll().get(teiUid);

        EnrollmentStoreImpl enrollmentStore = new EnrollmentStoreImpl(databaseAdapter());

        Map<String, List<Enrollment>> downloadedEnrollments = enrollmentStore.queryAll();

        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter());

        List<Event> downloadedEventsWithoutValues = eventStore.queryAll();

        TrackedEntityDataValueStoreImpl trackedEntityDataValue =
                new TrackedEntityDataValueStoreImpl(databaseAdapter());

        Map<String, List<TrackedEntityDataValue>> downloadedValues =
                trackedEntityDataValue.queryTrackedEntityDataValues();

        return createTei(downloadedTei, attValues, downloadedEnrollments.get(teiUid),
                downloadedEventsWithoutValues, downloadedValues);
    }

    private TrackedEntityInstance createTei(TrackedEntityInstance downloadedTei,
            Map<String, List<TrackedEntityAttributeValue>> attValues,
            List<Enrollment> downloadedEnrollmentsWithoutEvents,
            List<Event> downloadedEventsWithoutValues,
            Map<String, List<TrackedEntityDataValue>> downloadedValues) {


        Map<String, List<Event>> downloadedEvents = new HashMap<>();

        List<Enrollment> downloadedEnrollments = new ArrayList<>();

        for (Event event : downloadedEventsWithoutValues) {
            event = event.toBuilder()
                    .trackedEntityDataValues(downloadedValues.get(event.uid()))
                    .build();

            if (downloadedEvents.get(event.enrollmentUid()) == null) {
                downloadedEvents.put(event.enrollmentUid(), new ArrayList<Event>());
            }

            downloadedEvents.get(event.enrollmentUid()).add(event);
        }

        for (Enrollment enrollment : downloadedEnrollmentsWithoutEvents) {
            enrollment = enrollment.toBuilder()
                    .trackedEntityInstance(downloadedTei.uid())
                    .events(downloadedEvents.get(enrollment.uid())).build();

            downloadedEnrollments.add(enrollment);
        }

        List<Relationship> relationships = new ArrayList<>();

        if (downloadedTei.relationships() != null) {
            relationships = downloadedTei.relationships();
        }

        downloadedTei = downloadedTei.toBuilder()
                .trackedEntityAttributeValues(attValues.get(downloadedTei.uid()))
                .relationships(relationships).enrollments(downloadedEnrollments).build();

        return downloadedTei;
    }
}
