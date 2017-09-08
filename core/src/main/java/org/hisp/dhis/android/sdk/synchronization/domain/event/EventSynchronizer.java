package org.hisp.dhis.android.sdk.synchronization.domain.event;

import static android.R.attr.id;

import static org.hisp.dhis.android.sdk.persistence.models.FailedItem.EVENT;

import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.ApiResponse;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.common.Synchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;
import org.hisp.dhis.android.sdk.utils.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.client.Response;
import retrofit.converter.ConversionException;

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
            System.out.println(
                    "Synchronizing single event " + event.getUid() + " " + event.getStatus());

            ImportSummary importSummary = mEventRepository.sync(event);

            manageSyncResult(event, importSummary);

        } catch (APIException api) {
            super.handleSerializableItemException(api, FailedItem.EVENT,
                    event.getLocalId());
        }
    }

    public void sync(List<Event> events) {
        if (events == null || events.size() == 0) {
            return;
        }

        Map<String, Event> eventsMapCheck = removeDeletedAndOnlyLocalEvents(events);

        if (eventsMapCheck.values().size() != 0) {
            System.out.println("Synchronizing list of events ");

            List<ImportSummary> importSummaries = null;
            try {
                importSummaries = mEventRepository.sync(
                        new ArrayList<>(eventsMapCheck.values()));
                for (ImportSummary importSummary : importSummaries) {
                    Event event = eventsMapCheck.get(importSummary.getReference());
                    if (event != null) {
                        manageSyncResult(event, importSummary);
                    }
                }
            } catch (APIException api) {
                if (api != null && api.getResponse() != null) {
                    importSummaries = getImportSummary(api.getResponse());
                    if (importSummaries != null) {
                        for (ImportSummary importSummary : importSummaries) {
                            Event event = eventsMapCheck.get(importSummary.getReference());
                            if (event != null) {
                                mEventRepository.updateEventTimestampIfIsPushed(event, importSummary);
                                manageSyncResult(event, importSummary);
                                events.remove(event);
                            }
                        }
                    }
                }
                //Send only missing events
                syncOneByOne(events);
            }
        }
    }


    public void syncRemovedEvents(List<Event> events) {
        try {

            if (events == null || events.size() == 0) {
                return;
            }

            Map<String, Event> eventsMapCheck = removeDeletedAndOnlyLocalEvents(events);

            if (eventsMapCheck.values().size() != 0) {

                System.out.println("Synchronizing removed events");
                List<ImportSummary> importSummaries = mEventRepository.syncRemovedEvents(
                        new ArrayList<>(eventsMapCheck.values()));

                for (ImportSummary importSummary : importSummaries) {
                    Event event = eventsMapCheck.get(importSummary.getReference());
                    if (event == null && importSummary.getDescription() != null) {
                        event = eventsMapCheck.get(
                                importSummary.getDescription().replace("Deletion of event ",
                                        "").replace(" was successful", ""));
                    }
                    if (event != null) {
                        manageSyncResult(event, importSummary);
                    }
                }
            }
        } catch (APIException api) {
            syncOneByOne(events);
        }
    }


    private void manageSyncResult(Event event, ImportSummary importSummary) {
        if (importSummary.isSuccessOrOK()) {
            updateSyncedEventLocally(event);
        } else if (importSummary.isError()) {
            super.handleImportSummaryError(importSummary, EVENT, 200, event.getLocalId());
        }
    }

    private void updateSyncedEventLocally(Event event) {
        if (event.isDeleted()) {
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
        return (event.isDeleted() && (event.getCreated() == null));
    }


    private Map<String, Event> removeDeletedAndOnlyLocalEvents(List<Event> events) {
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
