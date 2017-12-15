package org.hisp.dhis.android.core.enrollment;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventHandler;

import java.util.List;

public class EnrollmentHandler {
    private final EnrollmentStore enrollmentStore;
    private final EventHandler eventHandler;

    public EnrollmentHandler(@NonNull EnrollmentStore enrollmentStore,
            @NonNull EventHandler eventHandler) {
        this.enrollmentStore = enrollmentStore;
        this.eventHandler = eventHandler;
    }

    public void handle(@NonNull List<Enrollment> enrollments) {

        if (enrollments != null && !enrollments.isEmpty()) {
            int size = enrollments.size();

            for (int i = 0; i < size; i++) {
                Enrollment enrollment = enrollments.get(i);
                handle(enrollment);
            }
        }
    }

    private void handle(@NonNull Enrollment enrollment) {
        if (enrollment == null) {
            return;
        }

        if (isDeleted(enrollment)) {
            enrollmentStore.delete(enrollment.uid());
        } else {
            String latitude = null;
            String longitude = null;
            if (enrollment.coordinate() != null) {
                latitude = enrollment.coordinate().latitude();
                longitude = enrollment.coordinate().longitude();
            }

            int updatedRow = enrollmentStore.update(enrollment.uid(), enrollment.created(),
                    enrollment.lastUpdated(),
                    enrollment.createdAtClient(), enrollment.lastUpdatedAtClient(),
                    enrollment.organisationUnit(),
                    enrollment.program(), enrollment.dateOfEnrollment(),
                    enrollment.dateOfIncident(),
                    enrollment.followUp(), enrollment.enrollmentStatus(),
                    enrollment.trackedEntityInstance(),
                    latitude, longitude,
                    State.SYNCED, enrollment.uid());

            if (updatedRow <= 0) {
                enrollmentStore.insert(enrollment.uid(), enrollment.created(),
                        enrollment.lastUpdated(),
                        enrollment.createdAtClient(), enrollment.lastUpdatedAtClient(),
                        enrollment.organisationUnit(), enrollment.program(),
                        enrollment.dateOfEnrollment(),
                        enrollment.dateOfIncident(), enrollment.followUp(),
                        enrollment.enrollmentStatus(),
                        enrollment.trackedEntityInstance(), latitude, longitude,
                        State.SYNCED);
            }

            eventHandler.handle(enrollment.events());
        }
    }
}
