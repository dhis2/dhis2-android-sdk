package org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance;


import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment$Table;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;

import java.util.List;

public class TrackedEntityInstanceLocalDataSource {
    public void save(TrackedEntityInstance trackedEntityInstance) {
        trackedEntityInstance.save();
    }

    public List<Enrollment> getEnrollments(long localId) {
        return new Select().from(Enrollment.class).where(Condition.column(Enrollment$Table.LOCALTRACKEDENTITYINSTANCEID).
                is(localId)).queryList();
    }
}
