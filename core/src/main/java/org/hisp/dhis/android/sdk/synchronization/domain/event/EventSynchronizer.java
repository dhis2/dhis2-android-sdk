package org.hisp.dhis.android.sdk.synchronization.domain.event;

import static android.R.attr.id;

import static org.hisp.dhis.android.sdk.persistence.models.FailedItem.EVENT;

import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.utils.NetworkUtils;

public class EventSynchronizer {
    //coordinate one type of item

    IEventRepository mEventRepository;

    public EventSynchronizer(IEventRepository eventRepository) {
        mEventRepository = eventRepository;
    }

    public void sync(Event event) {
        try {
            ImportSummary importSummary = mEventRepository.sync(event);

            if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                    ImportSummary.OK.equals(importSummary.getStatus())) {

                event.setFromServer(true);
                mEventRepository.save(event);

                clearFailedItem(EVENT, event.getLocalId());
            }else if (ImportSummary.ERROR.equals(importSummary.getStatus())) {
                //Log.d(CLASS_TAG, "failed.. ");
                NetworkUtils.handleImportSummaryError(importSummary, EVENT, 200, id);
            }

        } catch (APIException apiException) {
            NetworkUtils.handleEventSendException(apiException, event);
        }
    }

    private void clearFailedItem(String type, long id) {
      /*  FailedItem item = mFailedItemRepository.getFailedItem(type, id);
        if (item != null) {
            item.async().delete();
        }*/
    }
}
