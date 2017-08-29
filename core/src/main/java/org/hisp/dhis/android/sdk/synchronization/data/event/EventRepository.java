package org.hisp.dhis.android.sdk.synchronization.data.event;

import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRepository implements IEventRepository {
    EventLocalDataSource mLocalDataSource;
    EventRemoteDataSource mRemoteDataSource;

    public EventRepository(
            EventLocalDataSource localDataSource,
            EventRemoteDataSource remoteDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
    }

    @Override
    public void save(Event event) {
        mLocalDataSource.save(event);
    }

    @Override
    public void delete(Event event) {
        mLocalDataSource.delete(event);
    }

    @Override
    public ImportSummary sync(Event event) {
        ImportSummary importSummary = mRemoteDataSource.update(event);

        if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                ImportSummary.OK.equals(importSummary.getStatus())) {
            if(event.getStatus().equals(Event.STATUS_DELETED)){
                event.delete();
            }else {
                event.setFromServer(true);
                updateEventTimestamp(event);
            }
        }

        return importSummary;
    }

    @Override
    public List<ImportSummary> sync(List<Event> events) {
        Map<String, List<Event>> eventsMap = new HashMap<>();
        eventsMap.put("events", events);

        Map<String, Event> eventsMapCheck = new HashMap<>();
        for (Event event : events) {
            eventsMapCheck.put(event.getUid(), event);
        }
        List<ImportSummary> importSummaries = mRemoteDataSource.save(eventsMap);
        DateTime dateTime = mRemoteDataSource.getServerTime();
        if (importSummaries != null) {
            for (ImportSummary importSummary : importSummaries) {
                if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.OK.equals(importSummary.getStatus())) {
                    System.out.println("IMPORT SUMMARY: " + importSummary.getDescription());
                    Event event = eventsMapCheck.get(importSummary.getReference());
                    if (event != null) {
                        if(event.getStatus().equals(Event.STATUS_DELETED)){
                            event.delete();
                        }else {
                            updateEventTimestamp(event, dateTime.toString(), dateTime.toString());
                        }
                    }
                }
            }
        }
        return importSummaries;
    }

    private void updateEventTimestamp(Event event) {
        Event remoteEvent = mRemoteDataSource.getEvent(event.getEvent());
        updateEventTimestamp(event, remoteEvent.getCreated(), remoteEvent.getLastUpdated());
    }

    private void updateEventTimestamp(Event event, String createdDate, String lastUpdated) {
        // merging updated timestamp to local event model
        event.setFromServer(true);
        event.setCreated(createdDate);
        event.setLastUpdated(lastUpdated);

        mLocalDataSource.save(event);
    }
}
