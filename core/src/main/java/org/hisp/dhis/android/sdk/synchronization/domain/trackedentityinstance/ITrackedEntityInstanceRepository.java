package org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance;


import org.hisp.dhis.android.sdk.network.response.ImportSummary2;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;

import java.util.List;
import java.util.Map;

public interface ITrackedEntityInstanceRepository {
    void save (TrackedEntityInstance trackedEntityInstance);

    ImportSummary sync (TrackedEntityInstance trackedEntityInstance);

    List<ImportSummary2> sync(List<TrackedEntityInstance> trackedEntityInstances);

    Map<String,TrackedEntityInstance> getRecursiveRelationatedTeis(TrackedEntityInstance trackedEntityInstance,
            Map<String, TrackedEntityInstance> relatedTeis);

    List<Enrollment> getEnrollments(long localId);
}


