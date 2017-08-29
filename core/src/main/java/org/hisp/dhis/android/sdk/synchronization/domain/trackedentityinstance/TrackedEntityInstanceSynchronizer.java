package org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance;

import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.domain.common.Synchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.EnrollmentSynchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.IEnrollmentRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackedEntityInstanceSynchronizer extends Synchronizer{


    ITrackedEntityInstanceRepository mTrackedEntityInstanceRepository;
    IEnrollmentRepository mEnrollmentRepository;
    IEventRepository mEventRepository;
    IFailedItemRepository mFailedItemRepository;

    public TrackedEntityInstanceSynchronizer(ITrackedEntityInstanceRepository trackedEntityInstanceRepository, IEnrollmentRepository enrollmentRepository, IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        super(failedItemRepository);
        mTrackedEntityInstanceRepository = trackedEntityInstanceRepository;
        mEnrollmentRepository = enrollmentRepository;
        mEventRepository = eventRepository;
        mFailedItemRepository = failedItemRepository;
    }

    public void sync(TrackedEntityInstance trackedEntityInstance) {
        Map<String, TrackedEntityInstance> relatedTeis = new HashMap<String,
                TrackedEntityInstance>();
        try {
            ImportSummary importSummary = mTrackedEntityInstanceRepository.sync(trackedEntityInstance);

            if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                    ImportSummary.OK.equals(importSummary.getStatus())) {

                trackedEntityInstance.setFromServer(true);
                mTrackedEntityInstanceRepository.save(trackedEntityInstance);
                super.clearFailedItem(FailedItem.TRACKEDENTITYINSTANCE, trackedEntityInstance.getLocalId());
                syncEnrollments(trackedEntityInstance.getLocalId());
            } else if (ImportSummary.ERROR.equals(importSummary.getStatus())) {
                super.handleImportSummaryError(importSummary, FailedItem.TRACKEDENTITYINSTANCE, 200, trackedEntityInstance.getLocalId());
            }
        } catch (APIException api) {
            super.handleSerializableItemException(api, FailedItem.ENROLLMENT,
                    trackedEntityInstance.getLocalId());
        }
    }


    private void syncEnrollments(long localId) {
        EnrollmentSynchronizer eventSynchronizer = new EnrollmentSynchronizer(mEnrollmentRepository, mEventRepository, mFailedItemRepository);
        List<Enrollment> enrollmentList = mTrackedEntityInstanceRepository.getEnrollments(localId);
        eventSynchronizer.sync(enrollmentList);
    }
}
