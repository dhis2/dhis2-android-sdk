package org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance;

import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.response.ImportSummary2;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.domain.common.Synchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.EnrollmentSynchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.IEnrollmentRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

import java.util.ArrayList;
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
        try {
            ImportSummary importSummary = mTrackedEntityInstanceRepository.sync(
                    trackedEntityInstance);

            if (importSummary.isSuccessOrOK()) {
                manageSyncResult(trackedEntityInstance, importSummary.isSuccessOrOK(), importSummary.isError());
                syncEnrollments(trackedEntityInstance.getLocalId());
            } else if (importSummary.isError()) {
                super.handleImportSummaryError(importSummary, FailedItem.TRACKEDENTITYINSTANCE,
                        200, trackedEntityInstance.getLocalId());
            }
        } catch (APIException api) {
            super.handleSerializableItemException(api, FailedItem.TRACKEDENTITYINSTANCE,
                    trackedEntityInstance.getLocalId());
        }
    }

    private void manageSyncResult(TrackedEntityInstance trackedEntityInstance, boolean  isSuccess, boolean isError) {
        if(isSuccess) {
            trackedEntityInstance.setFromServer(true);
            mTrackedEntityInstanceRepository.save(trackedEntityInstance);
        }else if(isError) {
            super.clearFailedItem(FailedItem.TRACKEDENTITYINSTANCE,
                    trackedEntityInstance.getLocalId());
        }
    }


    public void sync(List<TrackedEntityInstance> trackedEntityInstances) {
        try{
            if (trackedEntityInstances == null || trackedEntityInstances.size() == 0) {
                return;
            }
            Map<String, TrackedEntityInstance> trackedEntityInstanceMap = mTrackedEntityInstanceRepository.toMap(trackedEntityInstances);
            List<ImportSummary2> importSummaries = mTrackedEntityInstanceRepository.sync(trackedEntityInstances);

            for (ImportSummary2 importSummary : importSummaries) {
                TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceMap.get(importSummary.getReference());
                if (trackedEntityInstance != null) {
                    manageSyncResult(trackedEntityInstance, importSummary.isSuccessOrOK(), importSummary.isError());
                }
            }
        } catch (Exception e){
            syncOneByOne(trackedEntityInstances);
        }
    }

    private void syncOneByOne(List<TrackedEntityInstance> trackedEntityInstances){
        for(TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            sync(trackedEntityInstance);
        }
    }

    private void syncEnrollments(long localId) {
        EnrollmentSynchronizer eventSynchronizer = new EnrollmentSynchronizer(mEnrollmentRepository, mEventRepository, mFailedItemRepository);
        List<Enrollment> enrollmentList = mEnrollmentRepository.getEnrollments(localId);
        eventSynchronizer.sync(enrollmentList);
    }

    public void syncAllTeisInTwoSteps(List<TrackedEntityInstance> trackedEntityInstances) {
        for(TrackedEntityInstance trackedEntityInstance: trackedEntityInstances){
            trackedEntityInstance.setRelationships(new ArrayList<Relationship>());
        }
        sync(trackedEntityInstances);
        for(TrackedEntityInstance trackedEntityInstance: trackedEntityInstances){
            trackedEntityInstance.setFromServer(false);
            trackedEntityInstance.setRelationships(null);
            trackedEntityInstance.getRelationships();
        }
        sync(trackedEntityInstances);
    }
}
