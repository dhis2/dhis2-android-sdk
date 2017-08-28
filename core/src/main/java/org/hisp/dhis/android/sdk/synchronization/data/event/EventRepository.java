package org.hisp.dhis.android.sdk.synchronization.data.event;

import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;

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
    public ImportSummary sync(Event event) {
        ImportSummary importSummary = mRemoteDataSource.Save(event);

        if (ImportSummary.SUCCESS.equals(importSummary.getStatus()) ||
                ImportSummary.OK.equals(importSummary.getStatus())) {
            updateEventTimestamp(event);
        }

        return importSummary;
    }

    private void updateEventTimestamp(Event event) {
        Event remoteEvent = mRemoteDataSource.getEvent(event.getEvent());

        // merging updated timestamp to local event model
        event.setCreated(remoteEvent.getCreated());
        event.setLastUpdated(remoteEvent.getLastUpdated());

        mLocalDataSource.save(event);
    }
}
