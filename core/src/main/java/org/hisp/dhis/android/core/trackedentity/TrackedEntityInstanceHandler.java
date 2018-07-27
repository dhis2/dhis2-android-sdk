package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2InternalModules;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentHandler;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipRepositoryInterface;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

@SuppressWarnings({
        "PMD.CyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.NPathComplexity"
})
public class TrackedEntityInstanceHandler {
    private final DHISVersionManager versionManager;
    private final RelationshipRepositoryInterface relationshipRepository;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler;
    private final EnrollmentHandler enrollmentHandler;

    public TrackedEntityInstanceHandler(
            @NonNull DHISVersionManager versionManager,
            @NonNull RelationshipRepositoryInterface relationshipRepository,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler,
            @NonNull EnrollmentHandler enrollmentHandler) {
        this.versionManager = versionManager;
        this.relationshipRepository = relationshipRepository;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.trackedEntityAttributeValueHandler = trackedEntityAttributeValueHandler;
        this.enrollmentHandler = enrollmentHandler;
    }

    public void handle(@NonNull TrackedEntityInstance trackedEntityInstance, boolean asRelationship) {
        if (trackedEntityInstance == null) {
            return;
        }

        if (isDeleted(trackedEntityInstance)) {
            trackedEntityInstanceStore.delete(trackedEntityInstance.uid());
        } else {

            if (asRelationship) {
                State currentState = trackedEntityInstanceStore.getState(trackedEntityInstance.uid());

                if (currentState == State.RELATIONSHIP) {
                    updateOrInsert(trackedEntityInstance, State.RELATIONSHIP);
                } else if (currentState == null) {
                    insert(trackedEntityInstance, State.RELATIONSHIP);
                }

            } else {
                updateOrInsert(trackedEntityInstance, State.SYNCED);
            }

            trackedEntityAttributeValueHandler.handle(
                    trackedEntityInstance.uid(),
                    trackedEntityInstance.trackedEntityAttributeValues());

            List<Enrollment> enrollments = trackedEntityInstance.enrollments();

            enrollmentHandler.handle(enrollments);

            for (Relationship relationship : trackedEntityInstance.relationships()) {

                String relationshipType;
                String fromTEIUid;
                String toTEIUid;
                String teiUid = trackedEntityInstance.uid();
                TrackedEntityInstance relatedTEI;

                if (versionManager.is2_29()) {
                    relationshipType = relationship.relationship();
                    fromTEIUid = relationship.trackedEntityInstanceA();
                    toTEIUid = relationship.trackedEntityInstanceB();
                    relatedTEI = relationship.relative();
                } else {
                    relationshipType = relationship.relationshipType();

                    fromTEIUid = getTEIUidFromRelationshipItem(relationship.from());
                    toTEIUid = getTEIUidFromRelationshipItem(relationship.to());

                    if (fromTEIUid == null || toTEIUid == null) {
                        continue;
                    }

                    String relatedTEIUid = teiUid.equals(fromTEIUid) ? toTEIUid : fromTEIUid;

                    relatedTEI = TrackedEntityInstance.create(relatedTEIUid, null, null,
                            null, null, null, null, null,
                            null, false, null, Collections.<Relationship>emptyList(), null);
                }

                if (relatedTEI != null && fromTEIUid != null && toTEIUid != null) {
                    this.handle(relatedTEI, true);
                    relationshipRepository.createTEIRelationship(relationshipType, fromTEIUid, toTEIUid);
                }
            }
        }
    }

    private String getTEIUidFromRelationshipItem(RelationshipItem item) {
        if (item != null && item.trackedEntityInstance() != null) {
            return item.trackedEntityInstance().trackedEntityInstance();
        }
        return null;
    }

    private void updateOrInsert(@NonNull TrackedEntityInstance trackedEntityInstance, State state) {
        int affectedRows = trackedEntityInstanceStore.update(
                trackedEntityInstance.uid(), trackedEntityInstance.created(),
                trackedEntityInstance.lastUpdated(), trackedEntityInstance.createdAtClient(),
                trackedEntityInstance.lastUpdatedAtClient(), trackedEntityInstance.organisationUnit(),
                trackedEntityInstance.trackedEntityType(), trackedEntityInstance.coordinates(),
                trackedEntityInstance.featureType(), state, trackedEntityInstance.uid());
        if (affectedRows <= 0) {
            insert(trackedEntityInstance, state);
        }
    }

    private void insert(@NonNull TrackedEntityInstance trackedEntityInstance, State state) {
        trackedEntityInstanceStore.insert(
                trackedEntityInstance.uid(), trackedEntityInstance.created(),
                trackedEntityInstance.lastUpdated(), trackedEntityInstance.createdAtClient(),
                trackedEntityInstance.lastUpdatedAtClient(), trackedEntityInstance.organisationUnit(),
                trackedEntityInstance.trackedEntityType(), trackedEntityInstance.coordinates(),
                trackedEntityInstance.featureType(), state);
    }

    public void handleMany(@NonNull Collection<TrackedEntityInstance> trackedEntityInstances) {
        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            handle(trackedEntityInstance, false);
        }
    }

    public static TrackedEntityInstanceHandler create(DatabaseAdapter databaseAdapter,
                                                      D2InternalModules internalModules) {
        return new TrackedEntityInstanceHandler(
                internalModules.systemInfo.publicModule.versionManager,
                internalModules.relationshipModule.publicModule.relationship,
                new TrackedEntityInstanceStoreImpl(databaseAdapter),
                TrackedEntityAttributeValueHandler.create(databaseAdapter),
                EnrollmentHandler.create(databaseAdapter)
        );
    }
}
