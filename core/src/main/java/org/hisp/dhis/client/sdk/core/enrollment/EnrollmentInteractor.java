package org.hisp.dhis.client.sdk.core.enrollment;

public interface EnrollmentInteractor {
    EnrollmentStore store();

    EnrollmentApi api();
}
