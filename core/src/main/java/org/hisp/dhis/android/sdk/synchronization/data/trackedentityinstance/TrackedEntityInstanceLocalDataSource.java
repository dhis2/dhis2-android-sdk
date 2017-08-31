package org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance;


import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance$Table;

import java.util.List;

public class TrackedEntityInstanceLocalDataSource {
    public void save(TrackedEntityInstance trackedEntityInstance) {
        trackedEntityInstance.save();
    }

    public List<TrackedEntityInstance> getAllLocalTeis() {
        return new Select().from(TrackedEntityInstance.class).where(Condition.column(TrackedEntityInstance$Table.FROMSERVER).
                is(false)).queryList();
    }

    public TrackedEntityInstance getTrackedEntityInstance(String trackedEntityInstanceUid) {
        return new Select().from(TrackedEntityInstance.class).where(Condition.column
                (TrackedEntityInstance$Table.TRACKEDENTITYINSTANCE).is(trackedEntityInstanceUid)).querySingle();
    }
}
