package org.hisp.dhis.android.sdk.synchronization.domain.event;

import static android.R.attr.id;

import static org.hisp.dhis.android.sdk.persistence.models.FailedItem.EVENT;

import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.common.Synchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSynchronizer extends Synchronizer {
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
            if (isDeletedAndOnlyLocalEvent(event)) {
                mEventRepository.delete(event);
                return;
            }

            ImportSummary importSummary = mEventRepository.sync(event);

            manageSyncResult(event, importSummary);

        } catch (APIException api) {
            super.handleSerializableItemException(api, FailedItem.EVENT,
                    event.getLocalId());
        }
    }

    public void sync(List<Event> events) {
        try {
            Map<String, Event> eventsMapCheck = removeDeletedAndOnlyLocalEvents(events);

            List<ImportSummary> importSummaries = mEventRepository.sync(
                    new ArrayList<>(eventsMapCheck.values()));

            for (ImportSummary importSummary : importSummaries) {
                Event event = eventsMapCheck.get(importSummary.getReference());

                manageSyncResult(event, importSummary);
            }
        } catch (APIException api) {
            syncOneByOne(events);
        }
    }

    private void manageSyncResult(Event event, ImportSummary importSummary) {
        if (importSummary.isSuccessOrOK()) {
            updateSyncedEventLocally(event);
        } else if (ImportSummary.ERROR.equals(importSummary.getStatus())) {
            super.handleImportSummaryError(importSummary, EVENT, 200, id);
        }
    }

    private void updateSyncedEventLocally(Event event) {
        if (event.getStatus().equals(Event.STATUS_DELETED)) {
            mEventRepository.delete(event);
        } else {
            event.setFromServer(true);
            mEventRepository.save(event);
        }

        super.clearFailedItem(EVENT, event.getLocalId());
    }

    private void syncOneByOne(List<Event> events) {
        for (Event event : events) {
            sync(event);
        }
    }

    public boolean isDeletedAndOnlyLocalEvent(Event event) {
        return (event.getStatus().equals(Event.STATUS_DELETED) && (event.getCreated() == null));
    }


    private Map<String, Event> removeDeletedAndOnlyLocalEvents (List<Event> events) {
        Map<String, Event> eventsMapCheck = new HashMap<>();

        for (Event event : events) {
            if (isDeletedAndOnlyLocalEvent(event)) {
                mEventRepository.delete(event);
            } else {
                eventsMapCheck.put(event.getUid(), event);
            }
        }

        return eventsMapCheck;
    }
}
