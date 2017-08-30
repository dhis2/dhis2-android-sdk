package org.hisp.dhis.android.sdk.synchronization.domain.enrollment;


import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.synchronization.domain.event.EventSynchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

public class SyncEnrollmentUseCase {
    IEnrollmentRepository mEnrollmentRepository;
    IEventRepository mEventRepository;
    IFailedItemRepository mFailedItemRepository;
    EnrollmentSynchronizer mEnrollmentSynchronizer;


    public SyncEnrollmentUseCase(IEnrollmentRepository enrollmentRepository, IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        mEnrollmentRepository = enrollmentRepository;
        mFailedItemRepository = failedItemRepository;
        mEventRepository = eventRepository;
        mEnrollmentSynchronizer = new EnrollmentSynchronizer(mEnrollmentRepository, eventRepository, mFailedItemRepository);
    }

    public void execute(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("the Enrollment to sync can not be null");
        }

        //if (Do you have to synchronize TEI?)
        //TrackedEntityInstanceSynchronizer.sync(TEI);

        //else
        mEnrollmentSynchronizer.sync(enrollment);
    }
}