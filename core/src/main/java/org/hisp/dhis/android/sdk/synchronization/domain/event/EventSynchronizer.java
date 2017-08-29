package org.hisp.dhis.android.sdk.synchronization.domain.event;

import static android.R.attr.id;

import static org.hisp.dhis.android.sdk.persistence.models.FailedItem.EVENT;

import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.common.Synchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

public class EventSynchronizer extends Synchronizer{
    IEventRepository mEventRepository;
    IFailedItemRepository mFailedItemRepository;

    public EventSynchronizer(IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        super(failedItemRepository);

        mEventRepository = eventRepository;
        mFailedItemRepository = failedItemRepository;
    }

    public void sync(Event event) {
        try {
            ImportSummary importSummary = mEventRepository.sync(event);

            if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                    ImportSummary.OK.equals(importSummary.getStatus())) {

                event.setFromServer(true);
                mEventRepository.save(event);
                super.clearFailedItem(EVENT, event.getLocalId());

            } else if (ImportSummary.ERROR.equals(importSummary.getStatus())) {
                super.handleImportSummaryError(importSummary, EVENT, 200, id);
            }
        } catch (APIException api) {
            super.handleSerializableItemException(api, FailedItem.EVENT,
                    event.getLocalId());
        }
    }
}
