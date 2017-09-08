package org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance;

import static android.R.attr.id;

import static org.hisp.dhis.android.sdk.persistence.models.FailedItem.TRACKEDENTITYINSTANCE;

import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.response.ImportSummary2;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
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

public class TrackedEntityInstanceSynchronizer extends Synchronizer {


    ITrackedEntityInstanceRepository mTrackedEntityInstanceRepository;
    IEnrollmentRepository mEnrollmentRepository;
    IEventRepository mEventRepository;
    IFailedItemRepository mFailedItemRepository;

    public TrackedEntityInstanceSynchronizer(
            ITrackedEntityInstanceRepository trackedEntityInstanceRepository,
            IEnrollmentRepository enrollmentRepository, IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        super(failedItemRepository);
        mTrackedEntityInstanceRepository = trackedEntityInstanceRepository;
        mEnrollmentRepository = enrollmentRepository;
        mEventRepository = eventRepository;
        mFailedItemRepository = failedItemRepository;
    }

    public void sync(TrackedEntityInstance trackedEntityInstance) {

        if (existsRelationships(trackedEntityInstance)) {
            syncAllTeisInTwoSteps(mTrackedEntityInstanceRepository.getAllLocalTeis());
        } else {
            syncSingleTei(trackedEntityInstance);
        }
    }


    public void sync(List<TrackedEntityInstance> trackedEntityInstances) {
        if (trackedEntityInstances != null && trackedEntityInstances.size() >= 0) {
            if (existsRelationships(trackedEntityInstances)) {
                syncAllTeisInTwoSteps(trackedEntityInstances);
            } else if (trackedEntityInstances.size() == 1) {
                syncSingleTei(trackedEntityInstances.get(0));
            } else {
                syncTeis(trackedEntityInstances);
            }
        }
    }

    private void syncAllTeisInTwoSteps(List<TrackedEntityInstance> trackedEntityInstances) {
        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            trackedEntityInstance.setRelationships(new ArrayList<Relationship>());
        }
        syncTeis(trackedEntityInstances);

        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            trackedEntityInstance.setFromServer(false);
            trackedEntityInstance.setRelationships(null);
            trackedEntityInstance.getRelationships();
        }
        syncTeis(trackedEntityInstances);
    }

    private void syncSingleTei(TrackedEntityInstance trackedEntityInstance) {
        try {
            ImportSummary importSummary = mTrackedEntityInstanceRepository.sync(
                    trackedEntityInstance);

            if (importSummary.isSuccessOrOK()) {
                syncEnrollments(trackedEntityInstance.getLocalId());
                changeTEIToSynced(trackedEntityInstance);
            } else if (importSummary.isError()) {
                super.handleImportSummaryError(importSummary, TRACKEDENTITYINSTANCE,
                        200, trackedEntityInstance.getLocalId());
            }
        } catch (APIException api) {
            super.handleSerializableItemException(api, TRACKEDENTITYINSTANCE,
                    trackedEntityInstance.getLocalId());
        }
    }

    private void syncTeis(List<TrackedEntityInstance> trackedEntityInstances) {
        try {
            Map<String, TrackedEntityInstance> trackedEntityInstanceMap =
                    TrackedEntityInstance.toMap(trackedEntityInstances);

            List<ImportSummary2> importSummaries = mTrackedEntityInstanceRepository.sync(
                    trackedEntityInstances);

            for (ImportSummary2 importSummary : importSummaries) {
                TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceMap.get(
                        importSummary.getReference());
                if (trackedEntityInstance != null) {
                    if (importSummary.isSuccessOrOK()) {
                        syncEnrollments(trackedEntityInstance.getLocalId());
                        changeTEIToSynced(trackedEntityInstance);
                    } else if (importSummary.isError()) {
                        super.handleImportSummaryError(null, TRACKEDENTITYINSTANCE, 200, id);
                    }
                }
            }
        } catch (Exception e) {
            syncOneByOne(trackedEntityInstances);
        }
    }

    private void syncOneByOne(List<TrackedEntityInstance> trackedEntityInstances) {
        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            syncSingleTei(trackedEntityInstance);
        }
    }

    private void changeTEIToSynced(TrackedEntityInstance trackedEntityInstance) {
        trackedEntityInstance.setFromServer(true);
        mTrackedEntityInstanceRepository.save(trackedEntityInstance);
        super.clearFailedItem(TRACKEDENTITYINSTANCE,
                trackedEntityInstance.getLocalId());
    }

    private void syncEnrollments(long localId) {
        EnrollmentSynchronizer eventSynchronizer = new EnrollmentSynchronizer(mEnrollmentRepository,
                mEventRepository, mFailedItemRepository);
        List<Enrollment> enrollmentList =
                mEnrollmentRepository.getEnrollmentsByTrackedEntityInstanceId(localId);
        eventSynchronizer.sync(enrollmentList);
    }

    private boolean existsRelationships(TrackedEntityInstance trackedEntityInstance) {
        return trackedEntityInstance.getRelationships() != null &&
                trackedEntityInstance.getRelationships().size() > 0;
    }

    private boolean existsRelationships(List<TrackedEntityInstance> trackedEntityInstances) {
        boolean existsRelationships = false;

        for (TrackedEntityInstance tei : trackedEntityInstances) {
            if (existsRelationships(tei)) {
                existsRelationships = true;
                break;
            }
        }

        return existsRelationships;
    }
}
