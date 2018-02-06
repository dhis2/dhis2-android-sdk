package org.hisp.dhis.android.core.relationship;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;

import java.util.List;

public class RelationshipHandler {
    private final RelationshipStore relationshipStore;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;

    public RelationshipHandler(@NonNull RelationshipStore relationshipStore,
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore) {
        this.relationshipStore = relationshipStore;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
    }

    public void handle(@NonNull TrackedEntityInstance trackedEntityInstance) {
        //remove old relations
        relationshipStore.removeOldRelations(trackedEntityInstance.uid());
        List<Relationship> relationships = trackedEntityInstance.relationships();
        if (relationships != null && !relationships.isEmpty()) {
            for (Relationship relationship : relationships) {
                //insert relations
                String newTrackedEntityInstance;
                if (trackedEntityInstance.uid().equals(relationship.trackedEntityInstanceA())) {
                    newTrackedEntityInstance = relationship.trackedEntityInstanceB();
                } else {
                    newTrackedEntityInstance = relationship.trackedEntityInstanceA();
                }
                handle(relationship, newTrackedEntityInstance,
                        trackedEntityInstance.organisationUnit(),
                        trackedEntityInstance.trackedEntity());
            }
        }
    }

    private void handle(@NonNull Relationship relationship, String trackedEntityInstanceUId,
            String organisationUnit, String trackedEntity) {
        if (relationship == null) {
            return;
        }
        handleReference(trackedEntityInstanceUId, organisationUnit, trackedEntity);
        relationshipStore.insert(relationship.trackedEntityInstanceA(),
                relationship.trackedEntityInstanceB(), relationship.displayName());
    }

    private void handleReference(String uid, String organisationUnit, String trackedEntity) {
        if (trackedEntityInstanceStore.exists(uid)) {
            return;
        } else {
            trackedEntityInstanceStore.insert(uid, null, null, null, null,
                    organisationUnit, trackedEntity, State.TO_UPDATE);
        }

    }
}
