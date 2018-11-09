package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TrackedEntityInstanceCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void download_tracked_entity_instance_enrollments_and_events() throws Exception {
        String teiUid = "PgmUFEQYZdt";

        givenAMetadataInDatabase();

        Callable<List<TrackedEntityInstance>> trackedEntityInstanceByUidEndPointCall =
                d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiUid));

        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance.json");

        trackedEntityInstanceByUidEndPointCall.call();

        verifyDownloadedTrackedEntityInstance("trackedentity/tracked_entity_instance.json", teiUid);
    }

    @Test
    public void remove_data_removed_in_server_after_second_download()
            throws Exception {
        String teiUid = "PgmUFEQYZdt";

        givenAMetadataInDatabase();

        Callable<List<TrackedEntityInstance>> trackedEntityInstanceByUidEndPointCall =
                d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiUid));

        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance.json");

        trackedEntityInstanceByUidEndPointCall.call();

        trackedEntityInstanceByUidEndPointCall = d2.downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiUid));


        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance_with_removed_data.json");

        trackedEntityInstanceByUidEndPointCall.call();

        verifyDownloadedTrackedEntityInstance("trackedentity/tracked_entity_instance_with_removed_data.json",
                teiUid);
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
        d2.syncMetaData().call();
    }

    private void verifyDownloadedTrackedEntityInstance(String file, String teiUid)
            throws IOException {
        TrackedEntityInstance expectedEnrollmentResponse = parseTrackedEntityInstanceResponse(file);

        TrackedEntityInstance downloadedTei = getDownloadedTei(teiUid);

        assertThat(downloadedTei, is(expectedEnrollmentResponse));
    }

    private TrackedEntityInstance parseTrackedEntityInstanceResponse(String file)
            throws IOException {
        String expectedEventsResponseJson = new ResourcesFileReader().getStringFromFile(file);

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
            if (!enrollment.deleted()) {
                enrollment = Enrollment.create(
                        enrollment.uid(), enrollment.created(), enrollment.lastUpdated(),
                        enrollment.createdAtClient(), enrollment.lastUpdatedAtClient(),
                        enrollment.organisationUnit(), enrollment.program(),
                        enrollment.enrollmentDate(), enrollment.incidentDate(),
                        enrollment.followUp(),
                        enrollment.enrollmentStatus(), trackedEntityInstance.uid(),
                        enrollment.coordinate(),
                        enrollment.deleted(), expectedEvents.get(enrollment.uid()), enrollment.notes());

                expectedEnrollments.add(enrollment);
            }
        }

        trackedEntityInstance = TrackedEntityInstance.create(
                trackedEntityInstance.uid(), trackedEntityInstance.created(),
                trackedEntityInstance.lastUpdated(),
                trackedEntityInstance.createdAtClient(),
                trackedEntityInstance.lastUpdatedAtClient(),
                trackedEntityInstance.organisationUnit(),
                trackedEntityInstance.trackedEntityType(),
                trackedEntityInstance.coordinates(),
                trackedEntityInstance.featureType(),
                trackedEntityInstance.deleted(),
                trackedEntityInstance.trackedEntityAttributeValues(),
                trackedEntityInstance.relationships(), expectedEnrollments);

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
            event = Event.create(
                    event.uid(), event.enrollmentUid(), event.created(), event.lastUpdated(),
                    event.createdAtClient(), event.lastUpdatedAtClient(),
                    event.program(), event.programStage(), event.organisationUnit(),
                    event.eventDate(), event.status(), event.coordinates(),
                    event.completedDate(),
                    event.dueDate(), event.deleted(), downloadedValues.get(event.uid()),
                    event.attributeOptionCombo(),
                    event.trackedEntityInstance());

            if (downloadedEvents.get(event.enrollmentUid()) == null) {
                downloadedEvents.put(event.enrollmentUid(), new ArrayList<Event>());
            }

            downloadedEvents.get(event.enrollmentUid()).add(event);
        }

        for (Enrollment enrollment : downloadedEnrollmentsWithoutEvents) {
            enrollment = Enrollment.create(
                    enrollment.uid(), enrollment.created(), enrollment.lastUpdated(),
                    enrollment.createdAtClient(), enrollment.lastUpdatedAtClient(),
                    enrollment.organisationUnit(), enrollment.program(),
                    enrollment.enrollmentDate(), enrollment.incidentDate(),
                    enrollment.followUp(),
                    enrollment.enrollmentStatus(), downloadedTei.uid(), enrollment.coordinate(),
                    enrollment.deleted(), downloadedEvents.get(enrollment.uid()), enrollment.notes());

            downloadedEnrollments.add(enrollment);
        }

        List<Relationship229Compatible> relationships = new ArrayList<>();

        if (downloadedTei.relationships() != null) {
            relationships = downloadedTei.relationships();
        }

        downloadedTei = TrackedEntityInstance.create(
                downloadedTei.uid(), downloadedTei.created(), downloadedTei.lastUpdated(),
                downloadedTei.createdAtClient(), downloadedTei.lastUpdatedAtClient(),
                downloadedTei.organisationUnit(), downloadedTei.trackedEntityType(), downloadedTei.coordinates(),
                downloadedTei.featureType(), downloadedTei.deleted(), attValues.get(downloadedTei.uid()),
                relationships, downloadedEnrollments);

        return downloadedTei;
    }
}
