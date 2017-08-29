package org.hisp.dhis.android.sdk.synchronization.domain.event;

import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

public class SyncEventUseCase {
    //coordinate items to sync

    IEventRepository mEventRepository;
    IFailedItemRepository mFailedItemRepository;
    EventSynchronizer mEventSynchronizer;


    public SyncEventUseCase(IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        mEventRepository = eventRepository;
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

        //else
        mEventSynchronizer.sync(event);
    }
}
