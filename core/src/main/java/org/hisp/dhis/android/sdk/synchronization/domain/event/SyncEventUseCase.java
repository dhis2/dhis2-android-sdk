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

    ITrackedEntityInstanceRepository mTrackedEntityInstanceRepository;
    TrackedEntityInstanceSynchronizer mTrackedEntityInstanceSynchronizer;
    EnrollmentSynchronizer mEnrollmentSynchronizer;
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
        mTrackedEntityInstanceSynchronizer =
                new TrackedEntityInstanceSynchronizer(mTrackedEntityInstanceRepository, mEnrollmentRepository, mEventRepository, mFailedItemRepository);
        mEnrollmentSynchronizer = new EnrollmentSynchronizer(
                mEnrollmentRepository, mEventRepository, mFailedItemRepository);
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
        TrackedEntityInstance tei = mTrackedEntityInstanceRepository.getTrackedEntityInstance(
                enrollment.getTrackedEntityInstance());
        if (!tei.isFromServer()) {
            mTrackedEntityInstanceSynchronizer.sync(tei);
        } else if (!enrollment.isFromServer()) {
            mEnrollmentSynchronizer.sync(enrollment);
        } else {
            mEventSynchronizer.sync(event);
        }
    }

}
