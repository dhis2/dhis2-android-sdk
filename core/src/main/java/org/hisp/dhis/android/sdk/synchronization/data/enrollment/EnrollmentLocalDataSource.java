package org.hisp.dhis.android.sdk.synchronization.data.enrollment;


import org.hisp.dhis.android.sdk.persistence.models.Enrollment;

public class EnrollmentLocalDataSource {
    public void save(Enrollment enrollment) {
        enrollment.save();
    }
}
