package org.hisp.dhis.android.sdk.synchronization.domain.app;


import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.IEnrollmentRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance
        .ITrackedEntityInstanceRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance
        .TrackedEntityInstanceSynchronizer;

import java.util.ArrayList;
import java.util.List;

public class SyncAppUseCase {
    ITrackedEntityInstanceRepository mTrackedEntityInstanceRepository;
    IFailedItemRepository mFailedItemRepository;
    TrackedEntityInstanceSynchronizer mTrackedEntityInstanceSynchronizer;


    public SyncAppUseCase(ITrackedEntityInstanceRepository trackedEntityInstanceRepository, IEnrollmentRepository enrollmentRepository, IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        mTrackedEntityInstanceRepository = trackedEntityInstanceRepository;
        mFailedItemRepository = failedItemRepository;
        mTrackedEntityInstanceSynchronizer = new TrackedEntityInstanceSynchronizer(mTrackedEntityInstanceRepository, enrollmentRepository, eventRepository, mFailedItemRepository);
    }

    public void execute(){
        List<TrackedEntityInstance> trackedEntityInstanceList = mTrackedEntityInstanceRepository.getAllLocalTeis();

        mTrackedEntityInstanceSynchronizer.sync(trackedEntityInstanceList);
    }
}
