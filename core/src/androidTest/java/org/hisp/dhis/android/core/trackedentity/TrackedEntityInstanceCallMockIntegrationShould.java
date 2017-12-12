package org.hisp.dhis.android.core.trackedentity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.TrackedEntityInstanceCallFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void download_tei_enrollments_and_events() throws Exception {
        String teiUid = "PgmUFEQYZdt";

        givenAMetadataInDatabase();

        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                TrackedEntityInstanceCallFactory.create(
                        d2.retrofit(), databaseAdapter(), teiUid);

        dhis2MockServer.enqueueMockResponse("TrackedEntityInstance.json");

        trackedEntityInstanceEndPointCall.call();

        verifyDownloadedTei("TrackedEntityInstance.json", teiUid);
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMockResponse("system_info.json");
        dhis2MockServer.enqueueMockResponse("user.json");
        dhis2MockServer.enqueueMockResponse("organisationUnits.json");
        dhis2MockServer.enqueueMockResponse("programs.json");
        dhis2MockServer.enqueueMockResponse("tracked_entities.json");
        dhis2MockServer.enqueueMockResponse("option_sets.json");

        d2.syncMetaData().call();
    }

    private void verifyDownloadedTei(String file, String teiUid) throws IOException {
        TrackedEntityInstance expectedEnrollmentResponse = parseEventResponse(file);

        TrackedEntityInstance downloadedTei = getDownloadedTei(teiUid);

        assertThat(downloadedTei, is(expectedEnrollmentResponse));
    }

    private TrackedEntityInstance parseEventResponse(String file) throws IOException {
        String expectedEventsResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(expectedEventsResponseJson,
                new TypeReference<TrackedEntityInstance>() {
                });
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
                    event.eventDate(), event.status(), event.coordinates(), event.completedDate(),
                    event.dueDate(), event.deleted(), downloadedValues.get(event.uid()));

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
                    enrollment.dateOfEnrollment(), enrollment.dateOfIncident(),
                    enrollment.followUp(),
                    enrollment.enrollmentStatus(), downloadedTei.uid(), enrollment.coordinate(),
                    enrollment.deleted(), downloadedEvents.get(enrollment.uid()));

            downloadedEnrollments.add(enrollment);
        }

        List<Relationship> relationships = new ArrayList<>();

        if (downloadedTei.relationships() != null) {
            relationships = downloadedTei.relationships();
        }

        downloadedTei = TrackedEntityInstance.create(
                downloadedTei.uid(), downloadedTei.created(), downloadedTei.lastUpdated(),
                downloadedTei.createdAtClient(), downloadedTei.lastUpdatedAtClient(),
                downloadedTei.organisationUnit(), downloadedTei.trackedEntity(),
                downloadedTei.deleted(), attValues.get(downloadedTei.uid()),
                relationships, downloadedEnrollments);


        return downloadedTei;
    }
}
