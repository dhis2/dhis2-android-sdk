package org.hisp.dhis.android.sdk.synchronization.domain.event;

import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.EnrollmentSynchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.enrollment.IEnrollmentRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

import java.util.List;

public class SyncEventUseCase {
    //coordinate items to sync

    IEventRepository mEventRepository;
    IEnrollmentRepository mEnrollmentRepository;
    IFailedItemRepository mFailedItemRepository;
    EventSynchronizer mEventSynchronizer;


    public SyncEventUseCase(IEventRepository eventRepository,
            IEnrollmentRepository enrollmentRepository, IFailedItemRepository failedItemRepository) {
        mEventRepository = eventRepository;
        mEnrollmentRepository = enrollmentRepository;
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
        if(!enrollment.isFromServer()){
            EnrollmentSynchronizer mEnrollmentSynchronizer = new EnrollmentSynchronizer(mEnrollmentRepository, mFailedItemRepository);
            boolean success = mEnrollmentSynchronizer.sync(enrollment);
            if(success){
                List<Event> events = mEnrollmentRepository.getEvents(enrollment);
                mEventSynchronizer.sync(events);
            }
        }else{
            mEventSynchronizer.sync(event);
        }
    }

}
