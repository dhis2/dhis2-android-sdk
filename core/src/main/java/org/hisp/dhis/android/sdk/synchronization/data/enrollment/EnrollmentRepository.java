package org.hisp.dhis.android.sdk.synchronization.data.enrollment;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.IEnrollmentRepository;

import java.util.List;

public class EnrollmentRepository implements IEnrollmentRepository {
    EnrollmentLocalDataSource mEnrollmentLocalDataSource;
    EnrollmentRemoteDataSource mEnrollmentRemoteDataSource;

    public EnrollmentRepository(EnrollmentLocalDataSource enrollmentLocalDataSource,
            EnrollmentRemoteDataSource enrollmentRemoteDataSource) {
        mEnrollmentLocalDataSource = enrollmentLocalDataSource;
        mEnrollmentRemoteDataSource = enrollmentRemoteDataSource;
    }

    @Override
    public void save(Enrollment enrollment) {
        mEnrollmentLocalDataSource.save(enrollment);
    }

    @Override
    public ImportSummary sync(Enrollment enrollment) {
        ImportSummary importSummary = mEnrollmentRemoteDataSource.save(enrollment);

        if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                ImportSummary.OK.equals(importSummary.getStatus())) {
            updateEnrollmentTimestamp(enrollment);
        }

        return importSummary;
    }

    private void updateEnrollmentTimestamp(Enrollment enrollment) {
        Enrollment remoteEvent = mEnrollmentRemoteDataSource.getEnrollment(
                enrollment.getEnrollment());

        // merging updated timestamp to local event model
        enrollment.setCreated(remoteEvent.getCreated());
        enrollment.setLastUpdated(remoteEvent.getLastUpdated());

        mEnrollmentLocalDataSource.save(enrollment);
    }

    @Override
    public Enrollment getEnrollment(String enrollmentUid) {
        return  mEnrollmentLocalDataSource.getEnrollment(enrollmentUid);
    }

    @Override
    public List<Enrollment> getEnrollmentsByTrackedEntityInstanceId(
            long trackedEntityInstancelocalId) {
        return mEnrollmentLocalDataSource
                .getEnrollmentsByTrackedEntityInstanceId(trackedEntityInstancelocalId);
    }
}
