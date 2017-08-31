package org.hisp.dhis.android.sdk.synchronization.domain.enrollment;


import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;

import java.util.List;

public interface IEnrollmentRepository {
    void save (Enrollment enrollment);

    ImportSummary sync (Enrollment enrollment);

    Enrollment getEnrollment(String enrollmentUid);

    List<Enrollment> getEnrollmentsByTrackedEntityInstanceId(long trackedEntityInstanceLocalId);
}
