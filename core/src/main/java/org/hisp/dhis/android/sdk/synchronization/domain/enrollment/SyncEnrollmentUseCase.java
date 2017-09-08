package org.hisp.dhis.android.sdk.synchronization.domain.enrollment;


import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.domain.event.EventSynchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance
        .ITrackedEntityInstanceRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance
        .TrackedEntityInstanceSynchronizer;

public class SyncEnrollmentUseCase {

    ITrackedEntityInstanceRepository mTrackedEntityInstanceRepository;
    IEnrollmentRepository mEnrollmentRepository;
    IFailedItemRepository mFailedItemRepository;
    EnrollmentSynchronizer mEnrollmentSynchronizer;
    EventSynchronizer mEventSynchronizer;
    TrackedEntityInstanceSynchronizer mTrackedEntityInstanceSynchronizer;


    public SyncEnrollmentUseCase(IEnrollmentRepository enrollmentRepository, IEventRepository eventRepository,
            ITrackedEntityInstanceRepository trackedEntityInstanceRepository, IFailedItemRepository failedItemRepository) {
        mTrackedEntityInstanceRepository = trackedEntityInstanceRepository;
        mEnrollmentRepository = enrollmentRepository;
        mFailedItemRepository = failedItemRepository;
        mEnrollmentSynchronizer = new EnrollmentSynchronizer(mEnrollmentRepository, eventRepository, mFailedItemRepository);
        mEventSynchronizer = new EventSynchronizer(eventRepository, mFailedItemRepository);
        mTrackedEntityInstanceSynchronizer = new TrackedEntityInstanceSynchronizer(trackedEntityInstanceRepository, mEnrollmentRepository, eventRepository, mFailedItemRepository);
    }

    public void execute(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("the Enrollment to sync can not be null");
        }

        TrackedEntityInstance tei = mTrackedEntityInstanceRepository.getTrackedEntityInstance(enrollment.getTrackedEntityInstance());
        if(!tei.isFromServer()){
            mTrackedEntityInstanceSynchronizer.sync(tei);
        }else {
            mEnrollmentSynchronizer.sync(enrollment);
        }
    }
}