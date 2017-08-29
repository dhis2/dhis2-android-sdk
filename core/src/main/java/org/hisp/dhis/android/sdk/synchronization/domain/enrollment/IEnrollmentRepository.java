package org.hisp.dhis.android.sdk.synchronization.domain.enrollment;


import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;

import java.util.List;

public interface IEnrollmentRepository {
    void save (Enrollment enrollment);

    ImportSummary sync (Enrollment enrollment);

    List<Event> getEvents(long enrollmentId);

    Enrollment getEnrollment(String enrollmentUid);
}
