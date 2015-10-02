/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.core.controllers;

import android.net.Uri;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.core.controllers.common.PushableDataController;
import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.core.network.IDhisApi;
import org.hisp.dhis.android.sdk.core.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.core.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.core.providers.ObjectMapperProvider;
import org.hisp.dhis.android.sdk.core.utils.DbUtils;
import org.hisp.dhis.android.sdk.core.utils.NetworkUtils;
import org.hisp.dhis.android.sdk.models.common.base.IStore;
import org.hisp.dhis.android.sdk.models.common.faileditem.FailedItemType;
import org.hisp.dhis.android.sdk.models.common.faileditem.IFailedItemStore;
import org.hisp.dhis.android.sdk.models.common.importsummary.ImportSummary;
import org.hisp.dhis.android.sdk.models.common.meta.DbOperation;
import org.hisp.dhis.android.sdk.models.common.meta.IDbOperation;
import org.hisp.dhis.android.sdk.models.common.state.Action;
import org.hisp.dhis.android.sdk.models.common.state.IStateStore;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.event.Event;
import org.hisp.dhis.android.sdk.models.event.IEventStore;
import org.hisp.dhis.android.sdk.models.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.android.sdk.models.program.IProgramStore;
import org.hisp.dhis.android.sdk.models.program.Program;
import org.hisp.dhis.android.sdk.models.trackedentity.ITrackedEntityDataValueStore;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityDataValue;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import retrofit.client.Header;
import retrofit.client.Response;

public final class EventController extends PushableDataController implements IEventController {
    private final IDhisApi mDhisApi;
    private final IStateStore stateStore;
    private final IEventStore eventStore;
    private final ITrackedEntityDataValueStore trackedEntityDataValueStore;
    private final IOrganisationUnitStore organisationUnitStore;
    private final IProgramStore programStore;
    private final IFailedItemStore failedItemStore;

    public EventController(IDhisApi dhisApi, IStateStore stateStore, IEventStore eventStore, ITrackedEntityDataValueStore trackedEntityDataValueStore, IOrganisationUnitStore organisationUnitStore, IProgramStore programStore, IFailedItemStore failedItemStore) {
        mDhisApi = dhisApi;
        this.stateStore = stateStore;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.organisationUnitStore = organisationUnitStore;
        this.programStore = programStore;
        this.failedItemStore = failedItemStore;
    }

    /**
     * This method loads the last 200 events for a program and org unit and stores on device.
     * Events that are not included in the result of this query are deleted from the device
     * (old events previously saved) unless they are modified locally.
     *
     * @param organisationUnitUid
     * @param programUid
     * @param serverDateTime
     * @throws APIException
     */
    private void getEventsDataFromServer(String organisationUnitUid, String programUid, DateTime serverDateTime) throws APIException {
        ResourceType resourceType = ResourceType.EVENTS;
        String extraIdentifier = organisationUnitUid + programUid;
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(resourceType, extraIdentifier);
        JsonNode updatedEventsResponse = mDhisApi.getEvents(programUid, organisationUnitUid, 200,
                getAllFieldsQueryMap(lastUpdated));
        List<Event> updatedEvents = getEvents(updatedEventsResponse);
        saveResourceDataFromServer(resourceType, extraIdentifier, updatedEvents,
                eventStore.query(organisationUnitStore.queryByUid(organisationUnitUid),
                        programStore.queryByUid(programUid)), serverDateTime);
    }

    /**
     * loads all events for a given enrollment from the server and stores locally on device
     *
     * @param enrollment
     */
    @Override
    public void getEventsDataFromServer(Enrollment enrollment) throws APIException {
        if (enrollment == null) {
            return;
        }
        ResourceType resourceType = ResourceType.EVENTS;
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.EVENTS, enrollment.getEnrollmentUid());
        DateTime serverDateTime = mDhisApi.getSystemInfo()
                .getServerDate();
        Program program = programStore.queryByUid(enrollment.getProgram());
        if (program == null || program.getUId() == null) {
            return;
        }
        JsonNode existingEventsResponse = mDhisApi
                .getEventsForEnrollment(program.getUId(), enrollment.getStatus(),
                        enrollment.getTrackedEntityInstanceUid(),
                        getBasicQueryMap());
        List<Event> existingEvents = getEvents(existingEventsResponse);
        JsonNode updatedEventsResponse = mDhisApi
                .getEventsForEnrollment(program.getUId(), enrollment.getStatus(),
                        enrollment.getTrackedEntityInstanceUid(),
                        getAllFieldsQueryMap(lastUpdated));
        List<Event> updatedEvents = getEvents(updatedEventsResponse);
        List<Event> existingPersistedAndUpdatedEvents = merge(existingEvents, updatedEvents, eventStore.query(enrollment));
        for (Event event : updatedEvents) {
            event.setEnrollment(enrollment);
        }

        saveResourceDataFromServer(ResourceType.EVENTS, enrollment.getEnrollmentUid(),
                existingPersistedAndUpdatedEvents, eventStore.query(enrollment), serverDateTime);
    }

    /**
     * Fetches data for a single event by uid from server and saves it (or updates if already existing)
     *
     * @param uid
     * @throws APIException
     */
    private void getEventDataFromServer(String uid) throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.EVENTS, uid);
        DateTime serverDateTime = mDhisApi.getSystemInfo()
                .getServerDate();

        Event updatedEvent = mDhisApi.getEvent(uid, getAllFieldsQueryMap(null));
        //todo: delete the event if it has been deleted on server.
        //todo: be sure to check if the event has ever been on the server, or if it is still pending first time registration sync

        Event persistedEvent = eventStore.query(uid);
        if (persistedEvent != null) {
            updatedEvent.setId(persistedEvent.getId());
            if (updatedEvent.getLastUpdated().isAfter(persistedEvent.getLastUpdated())) {
                DbOperation.with(eventStore).update(updatedEvent).execute();
            }
        } else {
            DbOperation.with(eventStore).insert(updatedEvent).execute();
        }
        List<IDbOperation> operations = new ArrayList<>();
        List<TrackedEntityDataValue> updatedDataValues = updatedEvent.getTrackedEntityDataValues();
        if (updatedDataValues != null) {
            for (TrackedEntityDataValue dataValue : updatedDataValues) {
                dataValue.setEvent(updatedEvent);
            }
            operations.addAll(createOperations(trackedEntityDataValueStore,
                    eventStore.query(updatedEvent.getId()), updatedEvent,
                    trackedEntityDataValueStore.query(updatedEvent), updatedDataValues));
        }
        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.EVENT, uid, serverDateTime);
    }

    private void saveResourceDataFromServer(ResourceType resourceType, String extraIdentifier,
                                            List<Event> updatedItems,
                                            List<Event> persistedItems,
                                            DateTime serverDateTime) {
        Queue<IDbOperation> operations = new LinkedList<>();
        operations.addAll(createOperations(eventStore, persistedItems, updatedItems));
        DbUtils.applyBatch(operations);
        operations.clear();

        for (Event event : updatedItems) {
            List<TrackedEntityDataValue> updatedDataValues = event.getTrackedEntityDataValues();
            if (updatedDataValues != null) {
                for (TrackedEntityDataValue dataValue : updatedDataValues) {
                    dataValue.setEvent(event);
                }
                operations.addAll(createOperations(trackedEntityDataValueStore,
                        eventStore.query(event.getId()), event,
                        trackedEntityDataValueStore.query(event), updatedDataValues));
            }
            DateTimeManager.getInstance()
                    .setLastUpdated(ResourceType.EVENT, event.getEventUid(), serverDateTime);
        }
        DateTimeManager.getInstance()
                .setLastUpdated(resourceType, extraIdentifier, serverDateTime);
    }

    private static List<Event> getEvents(JsonNode jsonNode) {
        TypeReference<List<Event>> typeRef =
                new TypeReference<List<Event>>() {
                };
        List<Event> events;
        try {
            if (jsonNode.has("events")) {
                events = ObjectMapperProvider.getInstance().
                        readValue(jsonNode.get("events").traverse(), typeRef);
            } else {
                events = new ArrayList<>();
            }
        } catch (IOException e) {
            events = new ArrayList<>();
            e.printStackTrace();
        }
        return events;
    }

    private Map<String, String> getBasicQueryMap() {
        final Map<String, String> map = new HashMap<>();
        map.put("fields", "event");
        return map;
    }

    private Map<String, String> getAllFieldsQueryMap(DateTime lastUpdated) {
        final Map<String, String> map = new HashMap<>();
        map.put("fields", "[:all]");
        if (lastUpdated != null) {
            map.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }
        return map;
    }

    /**
     * This utility method allows to determine which type of operation to apply to
     * each BaseIdentifiableObject$Flow depending on TimeStamp.
     *
     * @param oldModels List of models from local storage.
     * @param newModels List of models of distance instance of DHIS.
     */
    private List<DbOperation> createOperations(IStore<Event> modelStore,
                                               List<Event> oldModels,
                                               List<Event> newModels) {
        List<DbOperation> ops = new ArrayList<>();

        Map<String, Event> newModelsMap = toMap(newModels);
        Map<String, Event> oldModelsMap = toMap(oldModels);

        // As we will go through map of persisted items, we will try to update existing data.
        // Also, during each iteration we will remove old model key from list of new models.
        // As the result, the list of remaining items in newModelsMap,
        // will contain only those items which were not inserted before.
        for (String oldModelKey : oldModelsMap.keySet()) {
            Event newModel = newModelsMap.get(oldModelKey);
            Event oldModel = oldModelsMap.get(oldModelKey);

            // if there is no particular model with given uid in list of
            // actual (up to date) items, it means it was removed on the server side,
            // or the item was created locally and has not yet been posted.
            if (newModel == null) {
                Action action = stateStore.queryActionForModel(oldModel);
                if (!Action.TO_UPDATE.equals(action) && !Action.TO_POST.equals(action)) {
                    ops.add(DbOperation.with(modelStore)
                            .delete(oldModel));
                }

                // in case if there is no new model object,
                // we can jump to next iteration.
                continue;
            }

            // if the last updated field in up to date model is after the same
            // field in persisted model, it means we need to update it.
            if (newModel.getLastUpdated().isAfter(oldModel.getLastUpdated())) {
                // note, we need to pass database primary id to updated model
                // in order to avoid creation of new object.
                newModel.setId(oldModel.getId());
                ops.add(DbOperation.with(modelStore)
                        .update(newModel));
                //todo: the data values related also depend on this..
                //todo see createOperations for TrackedEntityDataValue
            }

            // as we have processed given old (persisted) model,
            // we can remove it from map of new models.
            newModelsMap.remove(oldModelKey);
        }

        // Inserting new items.
        for (String newModelKey : newModelsMap.keySet()) {
            Event item = newModelsMap.get(newModelKey);
            ops.add(DbOperation.with(modelStore)
                    .insert(item));
        }

        return ops;
    }

    /**
     * This utility method allows to determine which type of operation to apply to
     * each BaseIdentifiableObject$Flow depending on TimeStamp.
     *
     * @param oldModels List of models from local storage.
     * @param newModels List of models of distance instance of DHIS.
     */
    private List<DbOperation> createOperations(IStore<TrackedEntityDataValue> modelStore,
                                               Event oldEvent,
                                               Event newEvent,
                                               List<TrackedEntityDataValue> oldModels,
                                               List<TrackedEntityDataValue> newModels) {
        List<DbOperation> ops = new ArrayList<>();

        Map<String, TrackedEntityDataValue> newModelsMap = toMap(newModels);
        Map<String, TrackedEntityDataValue> oldModelsMap = toMap(oldModels);

        // As we will go through map of persisted items, we will try to update existing data.
        // Also, during each iteration we will remove old model key from list of new models.
        // As the result, the list of remaining items in newModelsMap,
        // will contain only those items which were not inserted before.
        for (String oldModelKey : oldModelsMap.keySet()) {
            TrackedEntityDataValue newModel = newModelsMap.get(oldModelKey);
            TrackedEntityDataValue oldModel = oldModelsMap.get(oldModelKey);

            // if there is no particular model with given uid in list of
            // actual (up to date) items, it means it was removed on the server side
            if (newModel == null) {
                //todo: check the state here in case the data value has changed locally before
                //todo:  deleting local value.
                //todo: this may happen if an event on the server was missing the datavalue for the
                //todo:  dataelement of the current "model" (aka TrackedEntityDataValue)
                //todo:  and it was created locally, but has not yet been synced to the server.
                //todo: this may be problematic in cases where a data element has been removed
                //todo:  from a program on the server, but the user of the this code has changed
                //todo:  the datavalue for the dataelement that has been removed on the server.
                Action action = stateStore.queryActionForModel(oldModel);
                if (!Action.TO_UPDATE.equals(action) && !Action.TO_POST.equals(action)) {
                    ops.add(DbOperation.with(modelStore)
                            .delete(oldModel));
                }

                // in case if there is no new model object,
                // we can jump to next iteration.
                continue;
            }

            // if the last updated field in up to date model is after the same
            // field in persisted model, it means we need to update it.
            if (oldEvent == null || newEvent.getLastUpdated().isAfter(oldEvent.getLastUpdated())) {
                ops.add(DbOperation.with(modelStore)
                        .update(newModel));
            }

            // as we have processed given old (persisted) model,
            // we can remove it from map of new models.
            newModelsMap.remove(oldModelKey);
        }

        // Inserting new items.
        for (String newModelKey : newModelsMap.keySet()) {
            TrackedEntityDataValue item = newModelsMap.get(newModelKey);
            ops.add(DbOperation.with(modelStore)
                    .insert(item));
        }

        return ops;
    }

    /**
     * Returns a list of items taken from updatedItems and persistedItems, based on the items in
     * the passed existingItems List. Items that are not present in existingItems will not be
     * included.
     *
     * @param existingItems
     * @param updatedItems
     * @param persistedItems
     * @return
     */
    private static List<Event> merge(List<Event> existingItems,
                                     List<Event> updatedItems,
                                     List<Event> persistedItems) {
        Map<String, Event> updatedItemsMap = toMap(updatedItems);
        Map<String, Event> persistedItemsMap = toMap(persistedItems);
        Map<String, Event> existingItemsMap = new HashMap<>();

        if (existingItems == null || existingItems.isEmpty()) {
            return new ArrayList<>(existingItemsMap.values());
        }

        for (Event existingItem : existingItems) {
            String id = existingItem.getEventUid();
            Event updatedItem = updatedItemsMap.get(id);
            Event persistedItem = persistedItemsMap.get(id);

            if (updatedItem != null) {
                if (persistedItem != null) {
                    updatedItem.setId(persistedItem.getId());
                }
                existingItemsMap.put(id, updatedItem);
                continue;
            }

            if (persistedItem != null) {
                existingItemsMap.put(id, persistedItem);
            }
        }

        return new ArrayList<>(existingItemsMap.values());
    }

    private static Map<String, Event> toMap(List<Event> objects) {
        Map<String, Event> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (Event object : objects) {
                if (object.getEventUid() != null) {
                    map.put(object.getEventUid(), object);
                }
            }
        }
        return map;
    }

    private static Map<String, TrackedEntityDataValue> toMap(Collection<TrackedEntityDataValue> objects) {
        Map<String, TrackedEntityDataValue> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (TrackedEntityDataValue object : objects) {
                if (object.getEvent() != null && object.getDataElement() != null) {
                    map.put(object.getEvent().getEventUid() + object.getDataElement(), object);
                }
            }
        }
        return map;
    }

    private void sendEventChanges() throws APIException {
        List<Event> events = getLocallyChangedEvents();
        sendEventChanges(events);
    }

    private List<Event> getLocallyChangedEvents() {
        List<Event> toPost = stateStore.filterModelsByAction(Event.class, Action.TO_POST);
        List<Event> toPut = stateStore.filterModelsByAction(Event.class, Action.TO_UPDATE);
        List<Event> events = new ArrayList<>();
        events.addAll(toPost);
        events.addAll(toPut);
        return events;
    }

    @Override
    public void sendEventChanges(List<Event> events) throws APIException {
        if (events == null || events.isEmpty()) {
            return;
        }

        Map<Long, Action> actionMap = stateStore
                .queryActionsForModel(Event.class);

        for (int i = 0; i < events.size(); i++) {/* removing events with local enrollment reference. In this case, the enrollment needs to be synced first*/
            Event event = events.get(i);
            Action enrollmentAction = null;
            Enrollment enrollment = event.getEnrollment();
            if (enrollment != null) {
                enrollmentAction = stateStore.queryActionForModel(enrollment);
            }
            //we avoid trying to send events whose enrollments that have not yet been posted to server
            if (Action.TO_POST.equals(enrollmentAction)) {
                events.remove(i);
                i--;
                continue;
            }
        }

        for (Event event : events) {
            sendEventChanges(event, actionMap.get(event.getId()));
        }
    }

    private void sendEventChanges(Event event, Action action) throws APIException {
        if (event == null) {
            return;
        }

        Action enrollmentAction = null;
        Enrollment enrollment = event.getEnrollment();
        if (enrollment != null) {
            enrollmentAction = stateStore.queryActionForModel(enrollment);
        }
        //we avoid trying to send events whose enrollments that have not yet been posted to server
        if (Action.TO_POST.equals(enrollmentAction)) {
            return;
        }

        if (Action.TO_POST.equals(action)) {
            postEvent(event);
        } else if (Action.TO_UPDATE.equals(action)) {
            putEvent(event);
        }
    }

    private void postEvent(Event event) throws APIException {
        //setting event to null to avoid sending temporary local reference
        event.setEventUid(null);
        try {
            Response response = mDhisApi.postEvent(event);
            if (response.getStatus() == 200) {
                ImportSummary importSummary = getImportSummary(response);
                handleImportSummary(importSummary, failedItemStore, FailedItemType.EVENT, event.getId());
                if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.Status.OK.equals(importSummary.getStatus())) {
                    // also, we will need to find UUID of newly created event,
                    // which is contained inside of HTTP Location header
                    Header header = NetworkUtils.findLocationHeader(response.getHeaders());
                    // parse the value of header as URI and extract the id
                    String eventUid = Uri.parse(header.getValue()).getLastPathSegment();
                    // set UUID, change state and save event
                    event.setEventUid(eventUid);
                    stateStore.saveActionForModel(event, Action.SYNCED);
                    List<IDbOperation> operations = new ArrayList<>();
                    for (TrackedEntityDataValue dataValue : event.getTrackedEntityDataValues()) {
                        stateStore.saveActionForModel(dataValue, Action.SYNCED);
                        operations.add(DbOperation.with(trackedEntityDataValueStore).save(dataValue));
                    }
                    operations.add(DbOperation.with(eventStore).save(event));
                    DbUtils.applyBatch(operations);
                    updateEventTimestamp(event);
                    eventStore.save(event);
                    clearFailedItem(FailedItemType.EVENT, failedItemStore, event.getId());
                }
            }
        } catch (APIException apiException) {
            handleEventSendException(apiException, failedItemStore, event);
        }
    }

    private void putEvent(Event event) throws APIException {
        try {
            Response response = mDhisApi.putEvent(event.getEventUid(), event);
            if (response.getStatus() == 200) {
                ImportSummary importSummary = getImportSummary(response);
                handleImportSummary(importSummary, failedItemStore, FailedItemType.EVENT, event.getId());
                if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.Status.OK.equals(importSummary.getStatus())) {

                    stateStore.saveActionForModel(event, Action.SYNCED);
                    List<IDbOperation> operations = new ArrayList<>();
                    for (TrackedEntityDataValue dataValue : event.getTrackedEntityDataValues()) {
                        stateStore.saveActionForModel(dataValue, Action.SYNCED);
                        operations.add(DbOperation.with(trackedEntityDataValueStore).save(dataValue));
                    }
                    operations.add(DbOperation.with(eventStore).save(event));
                    DbUtils.applyBatch(operations);
                    clearFailedItem(FailedItemType.EVENT, failedItemStore, event.getId());
                    updateEventTimestamp(event);
                    eventStore.save(event);
                }
            }
        } catch (APIException apiException) {
            handleEventSendException(apiException, failedItemStore, event);
        }
    }

    private Event updateEventTimestamp(Event event) throws APIException {
        try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "created,lastUpdated");
            Event updatedEvent = mDhisApi
                    .getEvent(event.getEventUid(), QUERY_PARAMS);

            // merging updated timestamp to local event model
            event.setCreated(updatedEvent.getCreated());
            event.setLastUpdated(updatedEvent.getLastUpdated());
            //Models.events().save(event);
        } catch (APIException apiException) {
            NetworkUtils.handleApiException(apiException);
        }
        return event;
    }

    @Override
    public void sync() throws APIException {
    }

    @Override
    public void sync(String organisationUnitUid, String programUid, DateTime serverDateTime) throws APIException {
        getEventsDataFromServer(organisationUnitUid, programUid, serverDateTime);
    }

    @Override
    public void sync(Enrollment enrollment) throws APIException {
        getEventsDataFromServer(enrollment);
    }
}
