package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentImportHandler;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventImportHandler;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.imports.WebResponseHandler;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceImportHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstancePayload;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class TrackedEntityInstancePostCall implements Call<Response<WebResponse>> {
    // service
    private final TrackedEntityInstanceService trackedEntityInstanceService;

    // stores
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    private boolean isExecuted;

    public TrackedEntityInstancePostCall(@NonNull TrackedEntityInstanceService trackedEntityInstanceService,
                                         @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                         @NonNull EnrollmentStore enrollmentStore,
                                         @NonNull EventStore eventStore,
                                         @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore,
                                         @NonNull TrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentStore = enrollmentStore;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<WebResponse> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Call is already executed");
            }

            isExecuted = true;

        }

        List<TrackedEntityInstance> trackedEntityInstancesToPost = queryDataToSync();

        // if size is 0, then no need to do network request
        if (trackedEntityInstancesToPost.isEmpty()) {
            return null;
        }

        TrackedEntityInstancePayload trackedEntityInstancePayload = new TrackedEntityInstancePayload();
        trackedEntityInstancePayload.trackedEntityInstances = trackedEntityInstancesToPost;

        Response<WebResponse> response = trackedEntityInstanceService.postTrackedEntityInstances(
                trackedEntityInstancePayload)
                .execute();

        if (response.isSuccessful()) {
            handleWebResponse(response);
        }

        return response;
    }

    @NonNull
    private List<TrackedEntityInstance> queryDataToSync() {
        Map<String, List<TrackedEntityDataValue>> dataValueMap =
                trackedEntityDataValueStore.queryTrackedEntityDataValues(Boolean.FALSE);
        Map<String, List<Event>> eventMap = eventStore.queryEventsAttachedToEnrollmentToPost();
        Map<String, List<Enrollment>> enrollmentMap = enrollmentStore.query();
        Map<String, List<TrackedEntityAttributeValue>> attributeValueMap = trackedEntityAttributeValueStore.query();
        Map<String, TrackedEntityInstance> trackedEntityInstances =
                trackedEntityInstanceStore.queryToPost();

        List<TrackedEntityInstance> trackedEntityInstancesRecreated = new ArrayList<>();



        List<Relationship> relationshipRecreated = new ArrayList<>();

        // EMPTY LISTS TO REPLACE NULL VALUES SO THAT API DOESN'T BREAK.
        List<TrackedEntityAttributeValue> emptyAttributeValueList = new ArrayList<>();

        for (Map.Entry<String, TrackedEntityInstance> teiUid : trackedEntityInstances.entrySet()) {
            List<Enrollment> enrollmentsRecreated = new ArrayList<>();
            List<Enrollment> enrollments = enrollmentMap.get(teiUid.getKey());

            // if enrollments is not null, then they exist for this tracked entity instance
            if (enrollments != null) {
                List<Event> eventRecreated = new ArrayList<>();
                // building enrollment
                int enrollmentSize = enrollments.size();
                for (int i = 0; i < enrollmentSize; i++) {
                    Enrollment enrollment = enrollments.get(i);

                    // building events for enrollment
                    List<Event> eventsForEnrollment = eventMap.get(enrollment.uid());

                    // if eventsForEnrollment is not null, then they exist for this enrollment
                    if (eventsForEnrollment != null) {
                        int eventSize = eventsForEnrollment.size();
                        for (int j = 0; j < eventSize; j++) {
                            Event event = eventsForEnrollment.get(j);
                            List<TrackedEntityDataValue> dataValuesForEvent = dataValueMap.get(event.uid());

                            eventRecreated.add(Event.create(event.uid(), event.enrollmentUid(), event.created(),
                                    event.lastUpdated(), event.createdAtClient(), event.lastUpdatedAtClient(),
                                    event.program(), event.programStage(), event.organisationUnit(), event.eventDate(),
                                    event.status(), event.coordinates(),
                                    event.completedDate(), event.dueDate(), event.deleted(), dataValuesForEvent,
                                    event.attributeCategoryOptions(), event.attributeOptionCombo(),
                                    event.trackedEntityInstance()));
                        }
                    }
                    enrollmentsRecreated.add(
                            Enrollment.builder().uid(enrollment.uid()).created(enrollment.created())
                            .lastUpdated(enrollment.lastUpdated())
                            .createdAtClient(enrollment.createdAtClient())
                            .lastUpdatedAtClient(enrollment.lastUpdatedAtClient())
                            .organisationUnit(enrollment.organisationUnit())
                            .program(enrollment.program())
                            .dateOfEnrollment(enrollment.dateOfEnrollment())
                            .dateOfIncident(enrollment.dateOfIncident())
                            .followUp(enrollment.followUp())
                            .enrollmentStatus(enrollment.enrollmentStatus())
                            .trackedEntityInstance(enrollment.trackedEntityInstance())
                            .coordinate(enrollment.coordinate())
                            .deleted(enrollment.deleted())
                            .events(eventRecreated).build());

                }
            }

            // Building TEI WITHOUT (new ArrayList) relationships
            List<TrackedEntityAttributeValue> attributeValues = attributeValueMap.get(teiUid.getKey());

            // if attributeValues is null, it means that they doesn't exist.
            // Then we need to set it to empty arrayList so that API doesn't break
            if (attributeValues == null) {
                attributeValues = emptyAttributeValueList;
            }
            TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(teiUid.getKey());

            trackedEntityInstancesRecreated.add(TrackedEntityInstance.builder()
            .uid(trackedEntityInstance.uid())
                    .created(trackedEntityInstance.created())
                    .lastUpdated(trackedEntityInstance.lastUpdated())
                    .createdAtClient(trackedEntityInstance.createdAtClient())
                    .lastUpdatedAtClient(trackedEntityInstance.lastUpdatedAtClient())
                    .organisationUnit(trackedEntityInstance.organisationUnit())
                    .trackedEntity(trackedEntityInstance.trackedEntity())
                    .deleted(trackedEntityInstance.deleted())
                    .trackedEntityAttributeValues(attributeValues)
                    .relationships(relationshipRecreated)
                    .enrollments(enrollmentsRecreated).build());

        }

        return trackedEntityInstancesRecreated;

    }

    private void handleWebResponse(Response<WebResponse> response) {
        WebResponse webResponse = response.body();
        EventImportHandler eventImportHandler = new EventImportHandler(eventStore);

        EnrollmentImportHandler enrollmentImportHandler = new EnrollmentImportHandler(
                enrollmentStore, eventImportHandler
        );

        TrackedEntityInstanceImportHandler trackedEntityInstanceImportHandler =
                new TrackedEntityInstanceImportHandler(
                        trackedEntityInstanceStore, enrollmentImportHandler, eventImportHandler
                );
        WebResponseHandler webResponseHandler = new WebResponseHandler(trackedEntityInstanceImportHandler);

        webResponseHandler.handleWebResponse(webResponse);

    }
}
