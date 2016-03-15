/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.core.event;

import org.hisp.dhis.client.sdk.core.common.IFailedItemStore;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.IStore;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStore;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportSummary;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public final class EventController implements IEventController {
    private final IEventApiClient eventApiClient;
    private final ISystemInfoApiClient systemInfoApiClient;
    private final ILastUpdatedPreferences lastUpdatedPreferences;
    private final ITransactionManager transactionManager;
    private final IStateStore stateStore;
    private final IEventStore eventStore;
    private final ITrackedEntityDataValueStore trackedEntityDataValueStore;
    private final IOrganisationUnitStore organisationUnitStore;
    private final IProgramStore programStore;
    private final IFailedItemStore failedItemStore;

    public EventController(IEventApiClient eventApiClient,
                           ISystemInfoApiClient systemInfoApiClient,
                           ILastUpdatedPreferences lastUpdatedPreferences,
                           ITransactionManager transactionManager,
                           IStateStore stateStore, IEventStore eventStore,
                           ITrackedEntityDataValueStore trackedEntityDataValueStore,
                           IOrganisationUnitStore organisationUnitStore, IProgramStore programStore,
                           IFailedItemStore failedItemStore) {
        this.eventApiClient = eventApiClient;
        this.systemInfoApiClient = systemInfoApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.transactionManager = transactionManager;
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
     * @param lastUpdated
     * @throws ApiException
     */
    private void getEventsDataFromServer(String organisationUnitUid, String programUid, int
            limit, DateTime lastUpdated) throws ApiException {
        ResourceType resourceType = ResourceType.EVENTS;
        String extraIdentifier = organisationUnitUid + programUid;
        DateTime serverDateTime = systemInfoApiClient.getSystemInfo().getServerDate();
        List<Event> updatedEvents = eventApiClient.getFullEvents(programUid, organisationUnitUid,
                limit, lastUpdated);
        saveResourceDataFromServer(resourceType, extraIdentifier, updatedEvents,
                eventStore.query(organisationUnitStore.queryByUid(organisationUnitUid),
                        programStore.queryByUid(programUid)), serverDateTime);
    }

    /**
     * This method loads the last events limited by the limit argument for a program and org unit
     * and stores on device.
     * Events that are not included in the result of this query are deleted from the device
     * (old events previously saved) unless they are modified locally.
     *
     * @param organisationUnitUid
     * @param programUid
     * @throws ApiException
     */
    private void getEventsDataFromServer(String organisationUnitUid, String programUid, int
            limit) throws ApiException {
        ResourceType resourceType = ResourceType.EVENTS;
        String extraIdentifier = organisationUnitUid + programUid;
        DateTime serverDateTime = systemInfoApiClient.getSystemInfo().getServerDate();
        List<Event> updatedEvents;
        updatedEvents = eventApiClient.getFullEvents(programUid, organisationUnitUid, limit);
        saveResourceDataFromServer(resourceType, extraIdentifier, updatedEvents,
                eventStore.query(organisationUnitStore.queryByUid(organisationUnitUid),
                        programStore.queryByUid(programUid)), serverDateTime);
    }

    /**
     * This method loads the last events limited by the limit argument for a program and org unit
     * and stores on device.
     * Events that are not included in the result of this query are deleted from the device
     * (old events previously saved) unless they are modified locally.
     *
     * @param organisationUnitUid
     * @param programUid
     * @throws ApiException
     */
    private void getEventsDataFromServer(String organisationUnitUid, String programUid) throws
            ApiException {
        ResourceType resourceType = ResourceType.EVENTS;
        String extraIdentifier = organisationUnitUid + programUid;
        DateTime serverDateTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.EVENTS, extraIdentifier);
        List<Event> updatedEvents = eventApiClient.getFullEvents(programUid, organisationUnitUid,
                lastUpdated);
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
    public void getEventsDataFromServer(Enrollment enrollment) throws ApiException {
        if (enrollment == null) {
            return;
        }
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.EVENTS, enrollment.getUId());
        DateTime serverDateTime = systemInfoApiClient.getSystemInfo().getServerDate();
        Program program = programStore.queryByUid(enrollment.getProgram());
        if (program == null || program.getUId() == null) {
            return;
        }
        List<Event> existingEvents = eventApiClient.getBasicEvents(program.getUId(),
                enrollment.getStatus(), enrollment.getTrackedEntityInstance()
                        .getTrackedEntityInstanceUid(), null);
        List<Event> updatedEvents = eventApiClient.getFullEvents(program.getUId(),
                enrollment.getStatus(), enrollment.getTrackedEntityInstance()
                        .getTrackedEntityInstanceUid(), lastUpdated);
        List<Event> existingPersistedAndUpdatedEvents = ModelUtils.merge(existingEvents,
                updatedEvents, eventStore.query(enrollment));
        for (Event event : updatedEvents) {
            event.setEnrollment(enrollment);
        }

        saveResourceDataFromServer(ResourceType.EVENTS, enrollment.getUId(),
                existingPersistedAndUpdatedEvents, eventStore.query(enrollment), serverDateTime);
    }

    /**
     * Fetches data for a single event by uid from server and saves it (or updates if already
     * existing)
     *
     * @param uid
     * @throws ApiException
     */
    private void getEventDataFromServer(String uid) throws ApiException {
        DateTime serverDateTime = systemInfoApiClient.getSystemInfo().getServerDate();

        Event updatedEvent = eventApiClient.getFullEvent(uid, null);//mDhisApi.getEvent(uid,
        // getAllFieldsQueryMap(null));
        //todo: delete the event if it has been deleted on server.
        //todo: be sure to check if the event has ever been on the server, or if it is still
        // pending first time registration sync

        Event persistedEvent = eventStore.queryByUid(uid);
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
                    eventStore.queryById(updatedEvent.getId()), updatedEvent,
                    trackedEntityDataValueStore.query(updatedEvent), updatedDataValues));
        }
        transactionManager.transact(operations);
        lastUpdatedPreferences.save(ResourceType.EVENT, serverDateTime, uid);
    }

    private void saveResourceDataFromServer(ResourceType resourceType, String extraIdentifier,
                                            List<Event> updatedItems,
                                            List<Event> persistedItems,
                                            DateTime serverDateTime) {
        Queue<IDbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(eventStore, persistedItems, updatedItems));
        transactionManager.transact(operations);
        operations.clear();

        for (Event event : updatedItems) {
            List<TrackedEntityDataValue> updatedDataValues = event.getTrackedEntityDataValues();
            if (updatedDataValues != null) {
                for (TrackedEntityDataValue dataValue : updatedDataValues) {
                    dataValue.setEvent(event);
                }
                operations.addAll(createOperations(trackedEntityDataValueStore,
                        eventStore.queryById(event.getId()), event,
                        trackedEntityDataValueStore.query(event), updatedDataValues));
            }
            lastUpdatedPreferences.save(ResourceType.EVENT, serverDateTime, event.getUId());
        }
        lastUpdatedPreferences.save(resourceType, serverDateTime, extraIdentifier);
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

    private static Map<String, TrackedEntityDataValue> toMap(Collection<TrackedEntityDataValue>
                                                                     objects) {
        Map<String, TrackedEntityDataValue> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (TrackedEntityDataValue object : objects) {
                if (object.getEvent() != null && object.getDataElement() != null) {
                    map.put(object.getEvent().getUId() + object.getDataElement(), object);
                }
            }
        }
        return map;
    }

    private void sendEventChanges() throws ApiException {
        List<Event> events = getLocallyChangedEvents();
        sendEventChanges(events);
    }

    private List<Event> getLocallyChangedEvents() {
        List<Event> toPost = stateStore.queryModelsWithActions(Event.class, Action.TO_POST);
        List<Event> toPut = stateStore.queryModelsWithActions(Event.class, Action.TO_UPDATE);
        List<Event> events = new ArrayList<>();
        events.addAll(toPost);
        events.addAll(toPut);
        return events;
    }

    @Override
    public void sendEventChanges(List<Event> events) throws ApiException {
        if (events == null || events.isEmpty()) {
            return;
        }

        Map<Long, Action> actionMap = stateStore
                .queryActionsForModel(Event.class);

        for (int i = 0; i < events.size(); i++) {/* removing events with local enrollment
        reference. In this case, the enrollment needs to be synced first*/
            Event event = events.get(i);
            Action enrollmentAction = null;
            Enrollment enrollment = event.getEnrollment();
            if (enrollment != null) {
                enrollmentAction = stateStore.queryActionForModel(enrollment);
            }
            //we avoid trying to send events whose enrollments that have not yet been posted to
            // server
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

    private void sendEventChanges(Event event, Action action) throws ApiException {
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

    private void postEvent(Event event) throws ApiException {
        //setting event to null to avoid sending temporary local reference
        event.setUId(null);
        try {
            ImportSummary importSummary = eventApiClient.postEvent(event);
//            handleImportSummaryWithError(importSummary, failedItemStore, FailedItemType.EVENT,
// event.getId());
            if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
                    ImportSummary.Status.OK.equals(importSummary.getStatus())) {
                stateStore.saveActionForModel(event, Action.SYNCED);
                List<IDbOperation> operations = new ArrayList<>();
                for (TrackedEntityDataValue dataValue : event.getTrackedEntityDataValues()) {
                    stateStore.saveActionForModel(dataValue, Action.SYNCED);
                    operations.add(DbOperation.with(trackedEntityDataValueStore).save(dataValue));
                }
                operations.add(DbOperation.with(eventStore).save(event));
                transactionManager.transact(operations);
                updateEventTimestamp(event);
                eventStore.save(event);
//                    clearFailedItem(FailedItemType.EVENT, failedItemStore, event.getId());
            }
        } catch (ApiException apiException) {
//            handleEventSendException(apiException, failedItemStore, event);
        }
    }

    private void putEvent(Event event) throws ApiException {
        try {
            ImportSummary importSummary = eventApiClient.putEvent(event);
//                handleImportSummaryWithError(importSummary, failedItemStore, FailedItemType
// .EVENT, event.getId());
            if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
                    ImportSummary.Status.OK.equals(importSummary.getStatus())) {

                stateStore.saveActionForModel(event, Action.SYNCED);
                List<IDbOperation> operations = new ArrayList<>();
                for (TrackedEntityDataValue dataValue : event.getTrackedEntityDataValues()) {
                    stateStore.saveActionForModel(dataValue, Action.SYNCED);
                    operations.add(DbOperation.with(trackedEntityDataValueStore).save(dataValue));
                }
                operations.add(DbOperation.with(eventStore).save(event));
                transactionManager.transact(operations);
//                    clearFailedItem(FailedItemType.EVENT, failedItemStore, event.getId());
                updateEventTimestamp(event);
                eventStore.save(event);
            }

        } catch (ApiException apiException) {
//            handleEventSendException(apiException, failedItemStore, event);
        }
    }

    private Event updateEventTimestamp(Event event) throws ApiException {
        try {
            Event updatedEvent = eventApiClient.getBasicEvent(event.getUId(), null);

            // merging updated timestamp to local event model
            event.setCreated(updatedEvent.getCreated());
            event.setLastUpdated(updatedEvent.getLastUpdated());
        } catch (ApiException ApiException) {
            //NetworkUtils.handleApiException(ApiException); todo
        }
        return event;
    }

    @Override
    public void sync() throws ApiException {
        sendEventChanges();
    }

    @Override
    public void sync(OrganisationUnit organisationUnit, Program program, int count, DateTime
            serverDateTime) throws ApiException {
        getEventsDataFromServer(organisationUnit.getUId(), program.getUId(), count, serverDateTime);
    }

    @Override
    public void sync(OrganisationUnit organisationUnit, Program program, int limit) throws
            ApiException {
        getEventsDataFromServer(organisationUnit.getUId(), program.getUId(), limit);
    }

    @Override
    public void sync(OrganisationUnit organisationUnit, Program program) throws ApiException {
        getEventsDataFromServer(organisationUnit.getUId(), program.getUId());
    }

    @Override
    public void sync(Enrollment enrollment) throws ApiException {
        getEventsDataFromServer(enrollment);
    }

    @Override
    public void sync(String UId) {
        getEventDataFromServer(UId);
    }
}
