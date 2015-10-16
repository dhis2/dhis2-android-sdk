package org.hisp.dhis.java.sdk.enrollment;

import org.hisp.dhis.java.sdk.common.network.Response;
import org.hisp.dhis.java.sdk.models.enrollment.Enrollment;
import org.joda.time.DateTime;

import java.util.List;

public interface IEnrollmentApiClient {
    List<Enrollment> getBasicEnrollments(String trackedEntityInstanceUid, DateTime lastUpdated);

    List<Enrollment> getFullEnrollments(String trackedEntityInstanceUid, DateTime lastUpdated);

    Enrollment getFullEnrollment(String uid, DateTime lastUpdated);

    Enrollment getBasicEnrollment(String uid, DateTime lastUpdated);

    Response postEnrollment(Enrollment enrollment);

    Response putEnrollment(Enrollment enrollment);
}
