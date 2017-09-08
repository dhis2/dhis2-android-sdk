package org.hisp.dhis.android.sdk.synchronization.data.event;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Event$Table;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.joda.time.DateTime;

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
    public List<Event> getEventsByEnrollment(long enrollmentId) {
        return new Select().from(Event.class).where(
                Condition.column(Event$Table.LOCALENROLLMENTID).is(enrollmentId))
                .and(Condition.column(Event$Table.FROMSERVER).is(false))
                .and(Condition.column(Event$Table.STATUS).isNot(Event.STATUS_DELETED))
                .queryList();
    }

    @Override
    public List<Event> getEventsByEnrollmentToBeRemoved(long enrollmentId) {
        List<Event> events = new Select().from(Event.class).where(
                Condition.column(Event$Table.LOCALENROLLMENTID).is(enrollmentId))
                .and(Condition.column(Event$Table.FROMSERVER).is(false))
                .and(Condition.column(Event$Table.STATUS).is(Event.STATUS_DELETED))
                .queryList();
        return events;
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

        updateEventTimestampIfIsPushed(event, importSummary);

        return importSummary;
    }

    public void updateEventTimestampIfIsPushed(Event event, ImportSummary importSummary) {
        if (importSummary.isSuccessOrOK() || importSummary.isConflictOnBatchPush()) {
            if(!event.isDeleted()) {
                updateEventTimestamp(event);
            }
        }
    }

    @Override
    public List<ImportSummary> sync(List<Event> events) {

        List<ImportSummary> importSummaries = mRemoteDataSource.save(events);

        Map<String, Event> eventsMap = Event.toMap(events);

        return updateEventsIfIsPushed(importSummaries, eventsMap);
    }

    @Nullable
    private List<ImportSummary> updateEventsIfIsPushed(List<ImportSummary> importSummaries,
            Map<String, Event> eventsMap) {
        DateTime dateTime = mRemoteDataSource.getServerTime();
        if (importSummaries != null) {
            for (ImportSummary importSummary : importSummaries) {
                if (importSummary.isSuccessOrOK()) {
                    System.out.println("IMPORT SUMMARY(event): " + importSummary.getDescription() + importSummary.getHref());
                    Event event = eventsMap.get(importSummary.getReference());
                    if (event != null) {
                        updateEventTimestamp(event, dateTime.toString(), dateTime.toString());
                    }
                }
            }
        }
        return importSummaries;
    }

    @Override
    public List<ImportSummary> syncRemovedEvents(List<Event> events) {

        List<ImportSummary> importSummaries = mRemoteDataSource.delete(events);

        return importSummaries;
    }

    private void updateEventTimestamp(Event event) {
        Event remoteEvent = mRemoteDataSource.getEvent(event.getEvent());
        updateEventTimestamp(event, remoteEvent.getCreated(), remoteEvent.getLastUpdated());
    }

    private void updateEventTimestamp(Event event, String createdDate, String lastUpdated) {
        event.setCreated(createdDate);
        event.setLastUpdated(lastUpdated);

        mLocalDataSource.save(event);
    }
}
