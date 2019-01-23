package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.utils.Utils;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Call;

@Reusable
public final class TrackedEntityInstancesEndpointCallFactory {

    private final TrackedEntityInstanceService trackedEntityInstanceService;

    @Inject
    TrackedEntityInstancesEndpointCallFactory(
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService) {
        this.trackedEntityInstanceService = trackedEntityInstanceService;
    }

    public Call<Payload<TrackedEntityInstance>> getCall(final TeiQuery trackerQuery) {
        return trackedEntityInstanceService.getTrackedEntityInstances(
                Utils.joinCollectionWithSeparator(trackerQuery.orgUnits(), ";"),
                trackerQuery.ouMode().name(), TrackedEntityInstanceFields.allFields, Boolean.TRUE,
                trackerQuery.page(), trackerQuery.pageSize(), trackerQuery.lastUpdatedStartDate(), true);
    }
}