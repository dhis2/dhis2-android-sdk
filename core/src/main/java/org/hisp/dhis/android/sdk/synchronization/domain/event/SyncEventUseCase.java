package org.hisp.dhis.android.sdk.synchronization.domain.event;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.EnrollmentSynchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.IEnrollmentRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance
        .ITrackedEntityInstanceRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance
        .TrackedEntityInstanceSynchronizer;

public class SyncEventUseCase {
    //coordinate items to sync

    ITrackedEntityInstanceRepository mTrackedEntityInstanceRepository;
    TrackedEntityInstanceSynchronizer mTrackedEntityInstanceSynchronizer;
    IEventRepository mEventRepository;
    IEnrollmentRepository mEnrollmentRepository;
    IFailedItemRepository mFailedItemRepository;
    EventSynchronizer mEventSynchronizer;


    public SyncEventUseCase(IEventRepository eventRepository,
            IEnrollmentRepository enrollmentRepository,
            ITrackedEntityInstanceRepository trackedEntityInstanceRepository,
            IFailedItemRepository failedItemRepository) {
        mEventRepository = eventRepository;
        mEnrollmentRepository = enrollmentRepository;
        mTrackedEntityInstanceRepository = trackedEntityInstanceRepository;
        mFailedItemRepository = failedItemRepository;
        mEventSynchronizer = new EventSynchronizer(mEventRepository, mFailedItemRepository);
    }

    public void execute(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("the Event to sync can not be null");
        }

        //if (Do you have to synchronize TEI?)
        //TrackedEntityInstanceSynchronizer.sync(TEI);

        //else if (Do you have to synchronize enrollment?)
        //EnrollmentSynchronizer.sync(enrollment);

        Enrollment enrollment = mEnrollmentRepository.getEnrollment(event.getEnrollment());
        TrackedEntityInstance tei = mEnrollmentRepository.getTrackedEntityInstance(
                enrollment.getTrackedEntityInstance());
        if (!tei.isFromServer()) {
            TrackedEntityInstanceSynchronizer trackedEntityInstanceSynchronizer =
                    new TrackedEntityInstanceSynchronizer(mTrackedEntityInstanceRepository, mEventRepository, mFailedItemRepository);
            trackedEntityInstanceSynchronizer.sync(tei);
        } else if (!enrollment.isFromServer()) {
            EnrollmentSynchronizer mEnrollmentSynchronizer = new EnrollmentSynchronizer(
                    mEnrollmentRepository, mEventRepository, mFailedItemRepository);
            mEnrollmentSynchronizer.sync(enrollment);
        } else {
            mEventSynchronizer.sync(event);
        }
    }

}
