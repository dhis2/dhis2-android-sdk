package org.hisp.dhis.client.sdk.android.enrollment;

import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentApiClient;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportSummary;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.joda.time.DateTime;

import java.util.List;

public class EnrollmentApiClientImpl implements EnrollmentApiClient {
    private final EnrollmentApiClientRetrofit apiClientRetrofit;

    public EnrollmentApiClientImpl(EnrollmentApiClientRetrofit apiClientRetrofit) {
        this.apiClientRetrofit = apiClientRetrofit;
    }

    @Override
    public List<Enrollment> getBasicEnrollments(String trackedEntityInstanceUid, DateTime lastUpdated) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Enrollment> getFullEnrollments(String trackedEntityInstanceUid, DateTime lastUpdated) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Enrollment getFullEnrollment(String uid, DateTime lastUpdated) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Enrollment getBasicEnrollment(String uid, DateTime lastUpdated) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ImportSummary postEnrollment(Enrollment enrollment) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ImportSummary putEnrollment(Enrollment enrollment) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
