package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentHandler;
import org.hisp.dhis.android.core.relationship.RelationshipHandler;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class TrackedEntityInstanceHandler {
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler;
    private final EnrollmentHandler enrollmentHandler;
    private final RelationshipHandler relationshipHandler;


    public TrackedEntityInstanceHandler(
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler,
            @NonNull EnrollmentHandler enrollmentHandler,
            @NonNull RelationshipHandler relationshipHandler) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.trackedEntityAttributeValueHandler = trackedEntityAttributeValueHandler;
        this.enrollmentHandler = enrollmentHandler;
        this.relationshipHandler = relationshipHandler;
    }

    public void handle(@NonNull TrackedEntityInstance trackedEntityInstance) {
        if (trackedEntityInstance == null) {
            return;
        }

        if (isDeleted(trackedEntityInstance)) {
            trackedEntityInstanceStore.delete(trackedEntityInstance.uid());
        } else {
            int updatedRow = trackedEntityInstanceStore.update(
                    trackedEntityInstance.uid(), trackedEntityInstance.created(), trackedEntityInstance.lastUpdated(),
                    trackedEntityInstance.createdAtClient(), trackedEntityInstance.lastUpdatedAtClient(),
                    trackedEntityInstance.organisationUnit(), trackedEntityInstance.trackedEntity(),
                    State.SYNCED, trackedEntityInstance.uid());

            if (updatedRow <= 0) {
                trackedEntityInstanceStore.insert(
                        trackedEntityInstance.uid(), trackedEntityInstance.created(),
                        trackedEntityInstance.lastUpdated(), trackedEntityInstance.createdAtClient(),
                        trackedEntityInstance.lastUpdatedAtClient(), trackedEntityInstance.organisationUnit(),
                        trackedEntityInstance.trackedEntity(), State.SYNCED);
            }

            trackedEntityAttributeValueHandler.handle(
                    trackedEntityInstance.uid(),
                    trackedEntityInstance.trackedEntityAttributeValues());

            List<Enrollment> enrollments = trackedEntityInstance.enrollments();

            enrollmentHandler.handle(enrollments);

            relationshipHandler.handle(trackedEntityInstance);
        }
    }
}
