package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentImportHandler;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.enrollment.note.Note229Compatible;
import org.hisp.dhis.android.core.enrollment.note.NoteBuilder;
import org.hisp.dhis.android.core.enrollment.note.NoteModel;
import org.hisp.dhis.android.core.enrollment.note.NoteStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventImportHandler;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.imports.WebResponseHandler;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceImportHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstancePayload;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Retrofit;

@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.ExcessiveImports"})
public final class TrackedEntityInstancePostCall extends SyncCall<WebResponse> {
    // internal modules
    private final DHISVersionManager versionManager;
    private final RelationshipDHISVersionManager relationshipDHISVersionManager;
    private final RelationshipCollectionRepository relationshipRepository;

    // service
    private final TrackedEntityInstanceService trackedEntityInstanceService;

    // stores
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private final ObjectWithoutUidStore<NoteModel> noteStore;

    private TrackedEntityInstancePostCall(@NonNull DHISVersionManager versionManager,
                                          @NonNull RelationshipDHISVersionManager relationshipDHISVersionManager,
                                          @NonNull RelationshipCollectionRepository relationshipRepository,
                                          @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
                                          @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                          @NonNull EnrollmentStore enrollmentStore,
                                          @NonNull EventStore eventStore,
                                          @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore,
                                          @NonNull TrackedEntityAttributeValueStore trackedEntityAttributeValueStore,
                                          @NonNull ObjectWithoutUidStore<NoteModel> noteStore) {
        this.versionManager = versionManager;
        this.relationshipDHISVersionManager = relationshipDHISVersionManager;
        this.relationshipRepository = relationshipRepository;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentStore = enrollmentStore;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
        this.noteStore = noteStore;
    }

    @Override
    public WebResponse call() throws D2CallException {
        setExecuted();

        List<TrackedEntityInstance> trackedEntityInstancesToPost = queryDataToSync();

        // if size is 0, then no need to do network request
        if (trackedEntityInstancesToPost.isEmpty()) {
            return WebResponse.EMPTY;
        }

        TrackedEntityInstancePayload trackedEntityInstancePayload = new TrackedEntityInstancePayload();
        trackedEntityInstancePayload.trackedEntityInstances = trackedEntityInstancesToPost;

        String strategy;
        if (versionManager.is2_29()) {
            strategy = "CREATE_AND_UPDATE";
        } else {
            strategy = "SYNC";
        }

        WebResponse webResponse = new APICallExecutor().executeObjectCallWithAcceptedErrorCodes(
                trackedEntityInstanceService.postTrackedEntityInstances(trackedEntityInstancePayload, strategy),
                Collections.singletonList(409), WebResponse.class);
        handleWebResponse(webResponse);
        return webResponse;
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
        Set<NoteModel> noteModels = noteStore.selectAll();

        List<TrackedEntityInstance> trackedEntityInstancesRecreated = new ArrayList<>();

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
                                    event.attributeOptionCombo(),
                                    event.trackedEntityInstance()));
                        }
                    }

                    List<Note229Compatible> notesForEnrollment = new ArrayList<>();
                    NoteBuilder noteBuilder = new NoteBuilder(versionManager);
                    for (NoteModel noteModel : noteModels) {
                        if (enrollment.uid().equals(noteModel.enrollment())) {
                            notesForEnrollment.add(noteBuilder.buildPojo(noteModel));
                        }
                    }

                    enrollmentsRecreated.add(
                            Enrollment.create(enrollment.uid(), enrollment.created(), enrollment.lastUpdated(),
                                    enrollment.createdAtClient(), enrollment.lastUpdatedAtClient(),
                                    enrollment.organisationUnit(), enrollment.program(), enrollment.enrollmentDate(),
                                    enrollment.incidentDate(), enrollment.followUp(), enrollment.enrollmentStatus(),
                                    enrollment.trackedEntityInstance(), enrollment.coordinate(), enrollment.deleted(),
                                    eventRecreated, notesForEnrollment));
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

            // Building relationships for TEI
            List<Relationship> dbRelationships =
                    relationshipRepository.getByItem(RelationshipHelper.teiItem(trackedEntityInstance.uid()));
            List<Relationship229Compatible> versionAwareRelationships =
                    relationshipDHISVersionManager.to229Compatible(dbRelationships, trackedEntityInstance.uid());

            trackedEntityInstancesRecreated.add(TrackedEntityInstance.create(trackedEntityInstance.uid(),
                    trackedEntityInstance.created(), trackedEntityInstance.lastUpdated(),
                    trackedEntityInstance.createdAtClient(), trackedEntityInstance.lastUpdatedAtClient(),
                    trackedEntityInstance.organisationUnit(), trackedEntityInstance.trackedEntityType(),
                    trackedEntityInstance.coordinates(), trackedEntityInstance.featureType(),
                    trackedEntityInstance.deleted(), attributeValues,
                    versionAwareRelationships, enrollmentsRecreated));

        }

        return trackedEntityInstancesRecreated;

    }

    private void handleWebResponse(WebResponse webResponse) {
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

    public static TrackedEntityInstancePostCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                                       D2InternalModules internalModules) {
        return new TrackedEntityInstancePostCall(
                internalModules.systemInfo.publicModule.versionManager,
                new RelationshipDHISVersionManager(internalModules.systemInfo.publicModule.versionManager),
                internalModules.relationshipModule.publicModule.relationships,
                retrofit.create(TrackedEntityInstanceService.class),
                new TrackedEntityInstanceStoreImpl(databaseAdapter),
                new EnrollmentStoreImpl(databaseAdapter),
                new EventStoreImpl(databaseAdapter),
                new TrackedEntityDataValueStoreImpl(databaseAdapter),
                new TrackedEntityAttributeValueStoreImpl(databaseAdapter),
                NoteStore.create(databaseAdapter)
        );
    }
}
