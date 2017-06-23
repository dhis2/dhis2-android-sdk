package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
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
import java.util.Set;

import retrofit2.Response;

public class TrackedEntityInstancePostCall implements Call<Response<WebResponse>> {
    // service
    private TrackedEntityInstanceService trackedEntityInstanceService;

    // stores
    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    private boolean isExecuted;

    public TrackedEntityInstancePostCall(@NonNull DatabaseAdapter databaseAdapter,
                                         @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
                                         @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                         @NonNull EnrollmentStore enrollmentStore,
                                         @NonNull EventStore eventStore,
                                         @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore,
                                         @NonNull TrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        this.databaseAdapter = databaseAdapter;
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


    public List<TrackedEntityInstance> dataToSync() {
        Map<String, List<TrackedEntityDataValue>> dataValueMap =
                trackedEntityDataValueStore.queryTrackedEntityDataValues(Boolean.FALSE);
        Map<String, List<Event>> eventMap = eventStore.queryEventsAttachedToEnrollmentToPost();
        Map<String, List<Enrollment>> enrollmentMap = enrollmentStore.query();
        Map<String, List<TrackedEntityAttributeValue>> attributeValueMap = trackedEntityAttributeValueStore.query();
        Map<String, TrackedEntityInstance> trackedEntityInstances = trackedEntityInstanceStore.query();

        List<TrackedEntityInstance> trackedEntityInstancesRecreated = new ArrayList<>();
        Set<String> trackedEntityInstanceUids = trackedEntityInstances.keySet();
        for (String teiUid : trackedEntityInstanceUids) {

            List<Enrollment> enrollments = enrollmentMap.get(teiUid);
            List<Enrollment> enrollmentsRecreated = new ArrayList<>();

            // building enrollment
            int enrollmentSize = enrollments.size();
            for (int i = 0; i < enrollmentSize; i++) {
                Enrollment enrollment = enrollments.get(i);

                // building events for enrollment
                List<Event> eventsForEnrollment = eventMap.get(enrollment.uid());
                List<Event> eventRecreated = new ArrayList<>();

                int eventSize = eventsForEnrollment.size();
                for (int j = 0; j < eventSize; j++) {
                    Event event = eventsForEnrollment.get(j);
                    List<TrackedEntityDataValue> dataValuesForEvent = dataValueMap.get(event.uid());

                    eventRecreated.add(Event.create(event.uid(), event.enrollmentUid(), event.created(), event.lastUpdated(),
                            event.createdAtClient(), event.lastUpdatedAtClient(), event.program(), event.programStage(),
                            event.organisationUnit(), event.eventDate(), event.status(), event.coordinates(),
                            event.completedDate(), event.dueDate(), event.deleted(), dataValuesForEvent));
                }
                enrollmentsRecreated.add(Enrollment.create(enrollment.uid(), enrollment.created(), enrollment.lastUpdated(),
                        enrollment.createdAtClient(), enrollment.lastUpdatedAtClient(),
                        enrollment.organisationUnit(), enrollment.program(), enrollment.dateOfEnrollment(),
                        enrollment.dateOfIncident(), enrollment.followUp(), enrollment.enrollmentStatus(),
                        enrollment.trackedEntityInstance(), enrollment.coordinate(), enrollment.deleted(),
                        eventRecreated));

            }

            // Building TEI WITHOUT (new ArrayList) relationships
            List<TrackedEntityAttributeValue> attributeValues = attributeValueMap.get(teiUid);
            TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(teiUid);

            trackedEntityInstancesRecreated.add(TrackedEntityInstance.create(trackedEntityInstance.uid(),
                    trackedEntityInstance.created(), trackedEntityInstance.lastUpdated(),
                    trackedEntityInstance.createdAtClient(), trackedEntityInstance.lastUpdatedAtClient(),
                    trackedEntityInstance.organisationUnit(), trackedEntityInstance.trackedEntity(),
                    trackedEntityInstance.deleted(), attributeValues, new ArrayList<Relationship>(), enrollmentsRecreated));

        }

        return trackedEntityInstancesRecreated;

    }

    @NonNull
    private List<TrackedEntityInstance> queryDataToSync() {
        return dataToSync();
    }

    private void handleWebResponse(Response<WebResponse> response) {
        WebResponse webResponse = response.body();
        EventImportHandler eventImportHandler = new EventImportHandler(eventStore);

        EnrollmentImportHandler enrollmentImportHandler = new EnrollmentImportHandler(
                enrollmentStore, eventImportHandler
        );

        TrackedEntityInstanceImportHandler trackedEntityInstanceImportHandler = new TrackedEntityInstanceImportHandler(
                trackedEntityInstanceStore, enrollmentImportHandler, eventImportHandler
        );
        WebResponseHandler webResponseHandler = new WebResponseHandler(trackedEntityInstanceImportHandler);

        webResponseHandler.handleWebResponse(webResponse);

    }
}
