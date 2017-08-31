package org.hisp.dhis.android.sdk.synchronization.domain.enrollment;


import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.data.enrollment.EnrollmentRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.common.Synchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.event.EventSynchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

import java.util.Collections;
import java.util.List;

public class EnrollmentSynchronizer extends Synchronizer {
    IEnrollmentRepository mEnrollmentRepository;
    IEventRepository mEventRepository;
    IFailedItemRepository mFailedItemRepository;

    public EnrollmentSynchronizer(IEnrollmentRepository enrollmentRepository,
            IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        super(failedItemRepository);

        mEnrollmentRepository = enrollmentRepository;
        mEventRepository = eventRepository;
        mFailedItemRepository = failedItemRepository;
    }

    public void sync(Enrollment enrollment) {
        try {
            boolean isFirstTime = enrollment.getCreated()==null;

            if(!isFirstTime){
                syncEvents(enrollment.getLocalId());
            }

            ImportSummary importSummary = mEnrollmentRepository.sync(enrollment);

            if (importSummary.isSuccessOrOK()) {

                if(isFirstTime) {
                    syncEvents(enrollment.getLocalId());
                }


                if(isFirstTime && enrollment.getStatus().equals(Enrollment.CANCELLED) || enrollment.getStatus().equals(
                        Enrollment.COMPLETED)){
                    sync(enrollment);
                }

                enrollment.setFromServer(true);
                mEnrollmentRepository.save(enrollment);
                super.clearFailedItem(FailedItem.ENROLLMENT, enrollment.getLocalId());
            } else if (importSummary.isError()) {
                super.handleImportSummaryError(importSummary, FailedItem.ENROLLMENT, 200,
                        enrollment.getLocalId());
            }
        } catch (APIException api) {
            super.handleSerializableItemException(api, FailedItem.ENROLLMENT,
                    enrollment.getLocalId());
        }
    }

    public void sync(List<Enrollment> enrollments) {
        Collections.sort(enrollments, new Enrollment.EnrollmentComparator());

        for (Enrollment enrollment : enrollments) {
            if(enrollment.isFromServer()){
                continue;
            }
            sync(enrollment);
        }
    }

    private void syncEvents(long enrollmentId) {
        EventSynchronizer eventSynchronizer = new EventSynchronizer(mEventRepository,
                mFailedItemRepository);

        List<Event> events = mEventRepository.getEventsByEnrollment(enrollmentId);
        List<Event> eventsToBeRemoved = mEventRepository.getEventsByEnrollmentToBeRemoved(enrollmentId);

        if(eventsToBeRemoved!=null && eventsToBeRemoved.size()>0){
            eventSynchronizer.syncRemovedEvents(eventsToBeRemoved);
        }

        if(events!=null && events.size()>0) {
            eventSynchronizer.sync(events);
        }
    }
}
