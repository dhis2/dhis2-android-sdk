package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.DataOrphanCleanerImpl;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentFields;
import org.hisp.dhis.android.core.enrollment.EnrollmentHandler;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.RelationshipHandler;

import java.util.Collection;
import java.util.List;

class TrackedEntityInstanceHandler extends IdentifiableSyncHandlerImpl<TrackedEntityInstance> {
    private final RelationshipDHISVersionManager relationshipVersionManager;
    private final RelationshipHandler relationshipHandler;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final SyncHandlerWithTransformer<TrackedEntityAttributeValue> trackedEntityAttributeValueHandler;
    private final SyncHandlerWithTransformer<Enrollment> enrollmentHandler;
    private final OrphanCleaner<TrackedEntityInstance, Enrollment> enrollmentOrphanCleaner;

    TrackedEntityInstanceHandler(
            @NonNull RelationshipDHISVersionManager relationshipVersionManager,
            @NonNull RelationshipHandler relationshipHandler,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull SyncHandlerWithTransformer<TrackedEntityAttributeValue> trackedEntityAttributeValueHandler,
            @NonNull SyncHandlerWithTransformer<Enrollment> enrollmentHandler,
            @NonNull OrphanCleaner<TrackedEntityInstance, Enrollment> enrollmentOrphanCleaner) {
        super(trackedEntityInstanceStore);
        this.relationshipVersionManager = relationshipVersionManager;
        this.relationshipHandler = relationshipHandler;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.trackedEntityAttributeValueHandler = trackedEntityAttributeValueHandler;
        this.enrollmentHandler = enrollmentHandler;
        this.enrollmentOrphanCleaner = enrollmentOrphanCleaner;
    }

    @Override
    protected void afterObjectHandled(final TrackedEntityInstance trackedEntityInstance, HandleAction action) {
        if (action != HandleAction.Delete) {
            trackedEntityAttributeValueHandler.handleMany(
                    trackedEntityInstance.trackedEntityAttributeValues(),
                    new ModelBuilder<TrackedEntityAttributeValue, TrackedEntityAttributeValue>() {
                        @Override
                        public TrackedEntityAttributeValue buildModel(TrackedEntityAttributeValue value) {
                            return value.toBuilder().trackedEntityInstance(trackedEntityInstance.uid()).build();
                        }
                    });

            List<Enrollment> enrollments = trackedEntityInstance.enrollments();
            if (enrollments != null) {
                enrollmentHandler.handleMany(enrollments, new ModelBuilder<Enrollment, Enrollment>() {
                    @Override
                    public Enrollment buildModel(Enrollment enrollment) {
                        return enrollment.toBuilder()
                                .state(State.SYNCED)
                                .build();
                    }
                });
            }

            handleRelationships(trackedEntityInstance);
        }

        enrollmentOrphanCleaner.deleteOrphan(trackedEntityInstance, trackedEntityInstance.enrollments());
    }

    private void handleRelationships(TrackedEntityInstance trackedEntityInstance) {
        List<Relationship229Compatible> relationships = trackedEntityInstance.relationships();
        if (relationships != null) {
            for (Relationship229Compatible relationship229 : trackedEntityInstance.relationships()) {
                TrackedEntityInstance relativeTEI =
                        relationshipVersionManager.getRelativeTei(relationship229, trackedEntityInstance.uid());

                if (relativeTEI != null) {
                    handleRelationship(relativeTEI, relationship229);
                }
            }
        }
    }

    private void handleRelationship(TrackedEntityInstance relativeTEI, Relationship229Compatible relationship229) {
        if (!trackedEntityInstanceStore.exists(relativeTEI.uid())) {
            handle(relativeTEI, relationshipModelBuilder());
        }

        Relationship relationship = relationshipVersionManager.from229Compatible(relationship229);
        relationshipHandler.handle(relationship);
    }

    public final void handleMany(final Collection<TrackedEntityInstance> trackedEntityInstances,
                                 boolean asRelationship) {
        if (asRelationship) {
            handleMany(trackedEntityInstances, relationshipModelBuilder());
        } else {
            handleMany(trackedEntityInstances,
                    new ModelBuilder<TrackedEntityInstance, TrackedEntityInstance>() {
                        @Override
                        public TrackedEntityInstance buildModel(TrackedEntityInstance trackedEntityInstance) {
                            return trackedEntityInstance.toBuilder()
                                    .state(State.SYNCED)
                                    .build();
                        }
                    });
        }
    }

    private ModelBuilder<TrackedEntityInstance, TrackedEntityInstance> relationshipModelBuilder() {
        return new ModelBuilder<TrackedEntityInstance, TrackedEntityInstance>() {
            @Override
            public TrackedEntityInstance buildModel(TrackedEntityInstance trackedEntityInstance) {
                State currentState = trackedEntityInstanceStore.getState(trackedEntityInstance.uid());
                if (currentState == State.RELATIONSHIP || currentState == null) {
                    return trackedEntityInstance.toBuilder()
                            .state(State.RELATIONSHIP)
                            .build();
                } else {
                    return trackedEntityInstance;
                }
            }
        };
    }

    public static TrackedEntityInstanceHandler create(DatabaseAdapter databaseAdapter,
                                                      D2InternalModules internalModules) {
        return new TrackedEntityInstanceHandler(
                new RelationshipDHISVersionManager(internalModules.systemInfo.publicModule.versionManager),
                internalModules.relationship.relationshipHandler,
                TrackedEntityInstanceStoreImpl.create(databaseAdapter),
                TrackedEntityAttributeValueHandler.create(databaseAdapter),
                EnrollmentHandler.create(databaseAdapter, internalModules.systemInfo.publicModule.versionManager),
                new DataOrphanCleanerImpl<TrackedEntityInstance, Enrollment>(EnrollmentTableInfo.TABLE_INFO.name(),
                        EnrollmentFields.TRACKED_ENTITY_INSTANCE, BaseDataModel.Columns.STATE, databaseAdapter)
        );
    }
}