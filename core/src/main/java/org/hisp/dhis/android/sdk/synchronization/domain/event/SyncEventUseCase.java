package org.hisp.dhis.android.sdk.synchronization.domain.event;

import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance
        .TrackedEntityInstanceSynchronizer;

public class SyncEventUseCase {
    //coordinate items to sync

    IEventRepository mEventRepository;
    EventSynchronizer mEventSynchronizer;


    public SyncEventUseCase(IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        mEventRepository = eventRepository;

    }

    public void execute(Event event) {
        if (event == null) {
            return;
        }

        //if (Do you have to synchronize TEI?)
        //TrackedEntityInstanceSynchronizer.sync(TEI);

        //else if (Do you have to synchronize enrollment?)
        //EnrollmentSynchronizer.sync(enrollment);

        //else
        //mEventSynchronizer.sync(event);
    }
}
