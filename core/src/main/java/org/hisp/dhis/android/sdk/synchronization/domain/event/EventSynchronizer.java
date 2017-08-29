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
            if(event.getStatus().equals(Event.STATUS_DELETED)){
                if(event.getCreated()==null){
                    mEventRepository.delete(event);
                    return;
                }
            }
            ImportSummary importSummary = mEventRepository.sync(event);

            if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                    ImportSummary.OK.equals(importSummary.getStatus())) {

                if(event.getStatus().equals(Event.STATUS_DELETED)){
                    mEventRepository.delete(event);
                }else {
                    event.setFromServer(true);
                    mEventRepository.save(event);
                }
                super.clearFailedItem(EVENT, event.getLocalId());
            } else if (ImportSummary.ERROR.equals(importSummary.getStatus())) {
                super.handleImportSummaryError(importSummary, EVENT, 200, id);
            }
        } catch (APIException api) {
            super.handleSerializableItemException(api, FailedItem.EVENT,
                    event.getLocalId());
        }
    }

    public void sync(List<Event> events) {
        try {
            Map<String, Event> eventsMapCheck = new HashMap<>();
            List<Event> eventsToBePushed = new ArrayList<>();
            for (Event event : events) {
                if(event.getStatus().equals(Event.STATUS_DELETED)){
                    if(event.getCreated()==null){
                        mEventRepository.delete(event);
                        continue;
                    }
                }
                eventsToBePushed.add(event);
                eventsMapCheck.put(event.getUid(), event);
            }
            List<ImportSummary> importSummaries = mEventRepository.sync(events);

            for (ImportSummary importSummary : importSummaries) {
                Event event = eventsMapCheck.get(importSummary.getReference());
                if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.OK.equals(importSummary.getStatus())) {
                    super.clearFailedItem(EVENT, event.getLocalId());
                } else if (ImportSummary.ERROR.equals(importSummary.getStatus())) {
                    super.handleImportSummaryError(importSummary, EVENT, 200,
                            event.getLocalId());
                }
            }
        } catch (APIException api) {
            for (Event event : events) {
                sync(event);
            }
        }
    }
}
