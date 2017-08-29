package org.hisp.dhis.android.sdk.synchronization.data.enrollment;


import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.Event;
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

    @Override
    public List<Event> getEvents(Enrollment enrollment) {
        return TrackerController.getEventsByEnrollment(enrollment.getLocalId());
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
    public Enrollment getEnrollment(String enrollment) {
        return new Select().from(Enrollment.class).where(Condition.column
                (Enrollment$Table.ENROLLMENT).is(enrollment)).querySingle();
    }
}
