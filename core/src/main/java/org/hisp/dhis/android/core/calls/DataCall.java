package org.hisp.dhis.android.core.calls;

import retrofit2.Response;
//TODO REIMPLEMENT
public class DataCall implements Call<Response> {
    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public Response call() throws Exception {
        return null;
    }


//
//    // stores
//    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
//    private final EnrollmentStore enrollmentStore;
//    private final EventStore eventStore;
//    private final ResourceStore resourceStore;
//    private final DatabaseAdapter databaseAdapter;
//
//    // service
//    private final TrackedEntityInstanceService trackedEntityInstanceService;
//
//    private final OuMode ouMode;
//    private final ResourceModel.Type resourceType;
//    private final Date serverDate;
//
//    public DataCall(@NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
//                    @NonNull EnrollmentStore enrollmentStore,
//                    @NonNull EventStore eventStore,
//                    @NonNull ResourceStore resourceStore,
//                    @NonNull DatabaseAdapter databaseAdapter,
//                    @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
//                    @NonNull OuMode ouMode,
//                    @NonNull ResourceModel.Type resourceType,
//                    @NonNull Date serverDate) {
//        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
//        this.enrollmentStore = enrollmentStore;
//        this.eventStore = eventStore;
//        this.resourceStore = resourceStore;
//        this.databaseAdapter = databaseAdapter;
//        this.trackedEntityInstanceService = trackedEntityInstanceService;
//        this.ouMode = ouMode;
//        this.resourceType = resourceType;
//        this.serverDate = serverDate;
//    }
//
//    private boolean isExecuted;
//
//    @Override
//    public boolean isExecuted() {
//        return isExecuted;
//    }
//
//
//    @Override
//    public Response call() throws Exception {
//        synchronized (this) {
//            if (isExecuted) {
//                throw new IllegalStateException("Already executed");
//            }
//
//            isExecuted = true;
//        }
//
//        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);
//        String lastUpdated = resourceHandler.getLastUpdated(ResourceModel.Type.TRACKED_ENTITY_INSTANCE);
//        EventHandler eventHandler = new EventHandler(eventStore);
//        EnrollmentHandler enrollmentHandler = new EnrollmentHandler(enrollmentStore, eventHandler);
//
//        TrackedEntityInstanceHandler handler = new TrackedEntityInstanceHandler(
//                trackedEntityInstanceStore, enrollmentHandler
//        );
//        Response<List<TrackedEntityInstance>> response =
//                trackedEntityInstanceService.trackedEntityInstances(
//                        fields(), TrackedEntityInstance.lastUpdated.gt(lastUpdated),
//                        ouMode,
//                        Boolean.TRUE // includeDeleted == true
//                ).execute();
//
//        Transaction transaction = databaseAdapter.beginNewTransaction();
//        try {
//            if (response != null && response.isSuccessful()) {
//                List<TrackedEntityInstance> trackedEntityInstances = response.body();
//
//                int size = trackedEntityInstances.size();
//                for (int i = 0; i < size; i++) {
//                    TrackedEntityInstance trackedEntityInstance = trackedEntityInstances.get(i);
//                    handler.handle(trackedEntityInstance);
//                }
//
//                resourceHandler.handleResource(resourceType, serverDate);
//                transaction.setSuccessful();
//            }
//        } finally {
//            transaction.end();
//        }
//
//        return response;
//    }
//
//    private Fields<TrackedEntityInstance> fields() {
//        return Fields.<TrackedEntityInstance>builder().fields(
//                TrackedEntityInstance.uid, TrackedEntityInstance.created, TrackedEntityInstance.lastUpdated,
//                TrackedEntityInstance.organisationUnit, TrackedEntityInstance.deleted,
//                TrackedEntityInstance.relationships.with(
//                        Relationship.relationshipType.with(
//                                RelationshipType.uid, RelationshipType.code, RelationshipType.created,
//                                RelationshipType.lastUpdated, RelationshipType.name, RelationshipType.displayName,
//                                RelationshipType.aIsToB, RelationshipType.bIsToA, RelationshipType.deleted
//                        )
//
//                ),
//                TrackedEntityInstance.trackedEntityAttributeValues.with(
//                        TrackedEntityAttributeValue.trackedEntityAttribute, TrackedEntityAttributeValue.value
//                ),
//                TrackedEntityInstance.enrollment.with(
//                        Enrollment.uid, Enrollment.created, Enrollment.lastUpdated, Enrollment.coordinate,
//                        Enrollment.dateOfEnrollment, Enrollment.dateOfIncident, Enrollment.enrollmentStatus,
//                        Enrollment.followUp, Enrollment.program, Enrollment.trackedEntityInstance, Enrollment.deleted,
//                        Enrollment.events.with(
//                                Event.uid, Event.created, Event.lastUpdated, Event.completeDate, Event.coordinates,
//                                Event.dueDate, Event.enrollment, Event.eventDate, Event.eventStatus,
//                                Event.organisationUnit, Event.program, Event.programStage,
//                                Event.deleted,
//                                Event.trackedEntityDataValues.with(
//                                        TrackedEntityDataValue.created, TrackedEntityDataValue.lastUpdated,
//                                        TrackedEntityDataValue.dataElement, TrackedEntityDataValue.providedElsewhere,
//                                        TrackedEntityDataValue.storedBy, TrackedEntityDataValue.value
//                                )
//                        )
//                )
//        ).build();
//    }
}
