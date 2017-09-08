package org.hisp.dhis.android.sdk.synchronization.data.enrollment;


import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;

import java.util.List;

public class EnrollmentLocalDataSource {
    public void save(Enrollment enrollment) {
        enrollment.save();
    }

    public List<Enrollment> getEnrollmentsByTrackedEntityInstanceId(long localTEIId) {
        return new Select().from(Enrollment.class).where(
                Condition.column(Enrollment$Table.LOCALTRACKEDENTITYINSTANCEID).
                        is(localTEIId))
                .and(Condition.column(Enrollment$Table.FROMSERVER).
                                is(false)).queryList();
    }

    public Enrollment getEnrollment(String enrollmentUid) {
        return new Select().from(Enrollment.class).where(Condition.column
                (Enrollment$Table.ENROLLMENT).is(enrollmentUid)).querySingle();
    }
}
