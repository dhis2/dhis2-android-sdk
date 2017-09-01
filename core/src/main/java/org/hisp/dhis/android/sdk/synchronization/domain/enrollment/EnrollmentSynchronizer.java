package org.hisp.dhis.android.sdk.synchronization.domain.enrollment;


import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
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
        boolean existsOnServerPreviously = enrollment.getCreated() != null;

        if (existsOnServerPreviously) {
            syncEvents(enrollment.getLocalId());
            if (syncEnrollment(enrollment))
                changeEnrollmentToSynced(enrollment);
        } else {
            if (syncEnrollment(enrollment))
            {
                syncEvents(enrollment.getLocalId());

                if ((enrollment.getStatus().equals(Enrollment.CANCELLED) ||
                        enrollment.getStatus().equals(Enrollment.COMPLETED))) {
                    //Send again because new enrollment is create as Active on server then
                    // Its necessary to change status from Active to Cancelled or Completed
                    if (syncEnrollment(enrollment));
                        changeEnrollmentToSynced(enrollment);
                }
                else{
                    changeEnrollmentToSynced(enrollment);
                }
            }
        }
    }

    public void sync(List<Enrollment> enrollments) {
        Collections.sort(enrollments, new Enrollment.EnrollmentComparator());

        for (Enrollment enrollment : enrollments) {
            sync(enrollment);
        }
    }

    private boolean syncEnrollment(Enrollment enrollment) {
        boolean isSyncSuccess = true;

        try {
            ImportSummary importSummary = mEnrollmentRepository.sync(enrollment);

            if (importSummary.isSuccessOrOK()) {
                isSyncSuccess = true;

            } else if (importSummary.isError()) {
                super.handleImportSummaryError(importSummary, FailedItem.ENROLLMENT, 200,
                        enrollment.getLocalId());
                isSyncSuccess = false;
            }
        } catch (APIException api) {
            super.handleSerializableItemException(api, FailedItem.ENROLLMENT,
                    enrollment.getLocalId());
            isSyncSuccess = false;
        }

        return isSyncSuccess;
    }

    private void changeEnrollmentToSynced(Enrollment enrollment) {
        enrollment.setFromServer(true);
        mEnrollmentRepository.save(enrollment);
        super.clearFailedItem(FailedItem.ENROLLMENT, enrollment.getLocalId());
    }


    private void syncEvents(long enrollmentId) {
        EventSynchronizer eventSynchronizer = new EventSynchronizer(mEventRepository,
                mFailedItemRepository);

        List<Event> events = mEventRepository.getEventsByEnrollment(enrollmentId);
        List<Event> eventsToBeRemoved = mEventRepository.getEventsByEnrollmentToBeRemoved(
                enrollmentId);

        if (eventsToBeRemoved != null && eventsToBeRemoved.size() > 0) {
            eventSynchronizer.syncRemovedEvents(eventsToBeRemoved);
        }

        if (events != null && events.size() > 0) {
            eventSynchronizer.sync(events);
        }
    }
}
