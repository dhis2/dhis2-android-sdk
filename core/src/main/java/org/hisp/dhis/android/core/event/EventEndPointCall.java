package org.hisp.dhis.android.core.event;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.util.Log;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.List;

import retrofit2.Response;

public class EventEndPointCall extends SyncCall<Payload<Event>> {

    private final GenericCallData genericCallData;
    private final EventService eventService;
    private final EventQuery eventQuery;
    private final EventHandler eventHandler;

    EventEndPointCall(@NonNull GenericCallData genericCallData,
                      @NonNull EventService eventService,
                      @NonNull EventHandler eventHandler,
                      @NonNull EventQuery eventQuery) {

        this.genericCallData = genericCallData;
        this.eventService = eventService;
        this.eventHandler = eventHandler;
        this.eventQuery = eventQuery;

        if (eventQuery != null && eventQuery.getUIds() != null &&
                eventQuery.getUIds().size() > MAX_UIDS) {
            throw new IllegalArgumentException(
                    "Can't handle the amount of events: " + eventQuery.getUIds().size() + ". " +
                            "Max size is: " + MAX_UIDS);
        }
    }

    @Override
    public Response<Payload<Event>> call() throws Exception {
        super.setExecuted();

        String lastSyncedEvents = genericCallData.resourceHandler().getLastUpdated(ResourceModel.Type.EVENT);

        Response<Payload<Event>> eventsByLastUpdated;

        if (eventQuery.getCategoryCombo() == null ||
                eventQuery.getCategoryOption() == null) {

            eventsByLastUpdated = eventService.getEvents(
                    eventQuery.getOrgUnit(), eventQuery.getProgram(),
                    eventQuery.getTrackedEntityInstance(), getSingleFields(),
                    Event.lastUpdated.gt(lastSyncedEvents), Event.uid.in(eventQuery.getUIds()),
                    Boolean.TRUE, eventQuery.getPage(), eventQuery.getPageSize()).execute();
        } else {
            CategoryCombo categoryCombo =  eventQuery.getCategoryCombo();
            CategoryOption categoryOption =  eventQuery.getCategoryOption();

            eventsByLastUpdated = eventService.getEvents(
                    eventQuery.getOrgUnit(), eventQuery.getProgram(),
                    eventQuery.getTrackedEntityInstance(), getSingleFields(),
                    Event.lastUpdated.gt(lastSyncedEvents), Event.uid.in(eventQuery.getUIds()),
                    Boolean.TRUE, eventQuery.getPage(), eventQuery.getPageSize(),
                    categoryCombo.uid(), categoryOption.uid()).execute();
        }

        if (eventsByLastUpdated.isSuccessful() && eventsByLastUpdated.body().items() != null) {
            List<Event> events = eventsByLastUpdated.body().items();
            int size = events.size();
            if (eventQuery.getPageLimit() > 0) {
                size = eventQuery.getPageLimit();
            }
            for (int i = 0; i < size; i++) {
                Transaction transaction = genericCallData.databaseAdapter().beginNewTransaction();
                Event event = events.get(i);
                try {
                    eventHandler.handle(event);
                    transaction.setSuccessful();
                } catch (SQLiteConstraintException sql) {
                    // This catch is necessary to ignore events with bad foreign keys exception
                    // More info: If the foreign key have the flag
                    // DEFERRABLE INITIALLY DEFERRED this exception will be throw in transaction
                    // .end()
                    // And the rollback will be executed only when the database is closed.
                    // It is a reported as unfixed bug: https://issuetracker.google
                    // .com/issues/37001653
                    Log.d(this.getClass().getSimpleName(), sql.getMessage());
                } finally {
                    transaction.end();
                }
            }
            genericCallData.resourceHandler().handleResource(ResourceModel.Type.EVENT,
                    genericCallData.serverDate());

        }
        return eventsByLastUpdated;
    }

    private Fields<Event> getSingleFields() {
        return Fields.<Event>builder().fields(
                Event.attributeCategoryOptions, Event.attributeOptionCombo,
                Event.uid, Event.created, Event.lastUpdated,
                Event.eventStatus, Event.coordinates, Event.program, Event.programStage,
                Event.organisationUnit, Event.eventDate, Event.completeDate,
                Event.dueDate, Event.deleted, Event.trackedEntityDataValues
        ).build();
    }

    public static EventEndPointCall create(GenericCallData genericCallData,
                                           EventQuery eventQuery) {
        return new EventEndPointCall(
                genericCallData,
                genericCallData.retrofit().create(EventService.class),
                EventHandler.create(genericCallData.databaseAdapter()),
                eventQuery
    );
}
}
