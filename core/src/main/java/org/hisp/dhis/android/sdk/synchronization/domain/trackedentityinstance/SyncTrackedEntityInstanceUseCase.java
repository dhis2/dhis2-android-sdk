package org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance;


import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.IEnrollmentRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

import java.util.HashMap;
import java.util.Map;

public class SyncTrackedEntityInstanceUseCase {
    ITrackedEntityInstanceRepository mTrackedEntityInstanceRepository;
    IFailedItemRepository mFailedItemRepository;
    TrackedEntityInstanceSynchronizer mTrackedEntityInstanceSynchronizer;


    public SyncTrackedEntityInstanceUseCase(ITrackedEntityInstanceRepository trackedEntityInstanceRepository, IEnrollmentRepository enrollmentRepository, IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        mTrackedEntityInstanceRepository = trackedEntityInstanceRepository;
        mFailedItemRepository = failedItemRepository;
        mTrackedEntityInstanceSynchronizer = new TrackedEntityInstanceSynchronizer(mTrackedEntityInstanceRepository, enrollmentRepository, eventRepository, mFailedItemRepository);
    }

    public void execute(TrackedEntityInstance trackedEntityInstance) {
        if (trackedEntityInstance == null) {
            return;
        }

        if(trackedEntityInstance.getRelationships()!=null){
            Map<String, TrackedEntityInstance> relatedTeiList = new HashMap<>();
            mTrackedEntityInstanceRepository.getRecursiveRelationatedTeis(trackedEntityInstance, relatedTeiList);
            if(relatedTeiList.size()>0) {
                mTrackedEntityInstanceSynchronizer.syncAllTeisInTwoSteps(mTrackedEntityInstanceRepository.getAllLocalTeis());
                return;
            }
        }

        mTrackedEntityInstanceSynchronizer.sync(trackedEntityInstance);
    }
}
