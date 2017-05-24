package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentHandler;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class TrackedEntityInstanceHandler {
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentHandler enrollmentHandler;


    public TrackedEntityInstanceHandler(@NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                        @NonNull EnrollmentHandler enrollmentHandler) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentHandler = enrollmentHandler;
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

            List<Enrollment> enrollments = trackedEntityInstance.enrollments();

            if (enrollments != null && !enrollments.isEmpty()) {
                int size = enrollments.size();

                for (int i = 0; i < size; i++) {
                    Enrollment enrollment = enrollments.get(i);
                    enrollmentHandler.handle(enrollment);
                }
            }
        }
    }
}
