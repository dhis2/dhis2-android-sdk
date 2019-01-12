package org.hisp.dhis.android.core.calls;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentImportHandler;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteStore;
import org.hisp.dhis.android.core.enrollment.note.NoteToPostTransformer;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventImportHandler;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.imports.WebResponseHandler;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.relationship.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
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
    private final ObjectWithoutUidStore<Note> noteStore;

    private final APICallExecutor apiCallExecutor;

    private TrackedEntityInstancePostCall(@NonNull DHISVersionManager versionManager,
                                          @NonNull RelationshipDHISVersionManager relationshipDHISVersionManager,
                                          @NonNull RelationshipCollectionRepository relationshipRepository,
                                          @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
                                          @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                          @NonNull EnrollmentStore enrollmentStore,
                                          @NonNull EventStore eventStore,
                                          @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore,
                                          @NonNull TrackedEntityAttributeValueStore trackedEntityAttributeValueStore,
                                          @NonNull ObjectWithoutUidStore<Note> noteStore,
                                          @NonNull APICallExecutor apiCallExecutor) {
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
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public WebResponse call() throws D2Error {
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

        WebResponse webResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                trackedEntityInstanceService.postTrackedEntityInstances(trackedEntityInstancePayload, strategy),
                Collections.singletonList(409), WebResponse.class);
        handleWebResponse(webResponse);
        return webResponse;
    }

    @NonNull
    private List<TrackedEntityInstance> queryDataToSync() {
        Map<String, List<TrackedEntityDataValue>> dataValueMap =
                trackedEntityDataValueStore.queryTrackerTrackedEntityDataValues();
        Map<String, List<Event>> eventMap = eventStore.queryEventsAttachedToEnrollmentToPost();
        Map<String, List<Enrollment>> enrollmentMap = enrollmentStore.queryEnrollmentsToPost();
        Map<String, List<TrackedEntityAttributeValue>> attributeValueMap =
                trackedEntityAttributeValueStore.queryTrackedEntityAttributeValueToPost();
        List<TrackedEntityInstance> trackedEntityInstances =
                trackedEntityInstanceStore.queryTrackedEntityInstancesToPost();

        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(BaseDataModel.Columns.STATE, State.TO_POST).build();
        List<Note> notes = noteStore.selectWhereClause(whereClause);

        List<TrackedEntityInstance> trackedEntityInstancesRecreated = new ArrayList<>();
        List<TrackedEntityAttributeValue> emptyAttributeValueList = new ArrayList<>();

        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            String trackedEntityInstanceUid = trackedEntityInstance.uid();
            List<Enrollment> enrollmentsRecreated = new ArrayList<>();
            List<Enrollment> enrollments = enrollmentMap.get(trackedEntityInstanceUid);

            if (enrollments != null) {
                List<Event> eventRecreated = new ArrayList<>();
                for (Enrollment enrollment : enrollments) {
                    List<Event> eventsForEnrollment = eventMap.get(enrollment.uid());
                    if (eventsForEnrollment != null) {
                        for (Event event : eventsForEnrollment) {
                            List<TrackedEntityDataValue> dataValuesForEvent = dataValueMap.get(event.uid());
                            eventRecreated.add(event.toBuilder().trackedEntityDataValues(dataValuesForEvent).build());
                        }
                    }

                    List<Note> notesForEnrollment = new ArrayList<>();
                    NoteToPostTransformer transformer = new NoteToPostTransformer(versionManager);
                    for (Note note : notes) {
                        if (enrollment.uid().equals(note.enrollment())) {
                            notesForEnrollment.add(transformer.transform(note));
                        }
                    }

                    enrollmentsRecreated.add(enrollment.toBuilder()
                            .events(eventRecreated)
                            .notes(notesForEnrollment)
                            .build());
                }
            }

            List<TrackedEntityAttributeValue> attributeValues = attributeValueMap.get(trackedEntityInstanceUid);

            List<Relationship> dbRelationships =
                    relationshipRepository.getByItem(RelationshipHelper.teiItem(trackedEntityInstance.uid()));
            List<Relationship229Compatible> versionAwareRelationships =
                    relationshipDHISVersionManager.to229Compatible(dbRelationships, trackedEntityInstance.uid());

            TrackedEntityInstance recreatedTrackedEntityInstance = trackedEntityInstance.toBuilder()
                    .trackedEntityAttributeValues(attributeValues == null ? emptyAttributeValueList : attributeValues)
                    .relationships(versionAwareRelationships)
                    .enrollments(enrollmentsRecreated)
                    .build();

            trackedEntityInstancesRecreated.add(recreatedTrackedEntityInstance);
        }

        return trackedEntityInstancesRecreated;

    }

    private void handleWebResponse(WebResponse webResponse) {
        EventImportHandler eventImportHandler = new EventImportHandler(eventStore);

        EnrollmentImportHandler enrollmentImportHandler = new EnrollmentImportHandler(
                enrollmentStore, noteStore, eventImportHandler
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
                internalModules.relationship.publicModule.relationships,
                retrofit.create(TrackedEntityInstanceService.class),
                TrackedEntityInstanceStoreImpl.create(databaseAdapter),
                EnrollmentStoreImpl.create(databaseAdapter),
                EventStoreImpl.create(databaseAdapter),
                TrackedEntityDataValueStoreImpl.create(databaseAdapter),
                TrackedEntityAttributeValueStoreImpl.create(databaseAdapter),
                NoteStore.create(databaseAdapter),
                APICallExecutorImpl.create(databaseAdapter)
        );
    }
}