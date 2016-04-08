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

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.Logger;
import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.controllers.AbsDataController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.network.ApiResponse;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportSummary;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.core.common.utils.CollectionUtils.isEmpty;

public final class EventController extends AbsDataController<Event> implements IEventController {

    /* Controllers */
    private final ISystemInfoController systemInfoController;

    /* Api clients */
    private final IEventApiClient eventApiClient;

    /* Preferences */
    private final LastUpdatedPreferences lastUpdatedPreferences;

    /* Persistence */
    private final IEventStore eventStore;
    private final StateStore stateStore;

    /* Utilities */
    private final TransactionManager transactionManager;

    public EventController(ISystemInfoController systemInfoController,
                           IEventApiClient eventApiClient,
                           LastUpdatedPreferences lastUpdatedPreferences,
                           IEventStore eventStore, StateStore stateStore,
                           TransactionManager transactionManager,
                           Logger logger) {
        super(logger, eventStore);

        this.systemInfoController = systemInfoController;
        this.eventApiClient = eventApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.eventStore = eventStore;
        this.stateStore = stateStore;
        this.transactionManager = transactionManager;
    }


    @Override
    public void sync(SyncStrategy strategy) {
        // get list of local uids
        Set<String> uids = ModelUtils.toUidSet(
                eventStore.queryAll());

        if (!uids.isEmpty()) {
            pull(strategy, uids);
            push(uids);
        }
    }

    @Override
    public void sync(SyncStrategy strategy, Set<String> uids) {
        isEmpty(uids, "Set of event uids must not be null");

        /* first we need to get information about new events from server */
        pull(strategy, uids);

        /* then we should try to push data to server */
        push(uids);
    }

    @Override
    public void pull(SyncStrategy strategy) throws ApiException {
        // get list of local uids
        Set<String> uids = ModelUtils.toUidSet(
                eventStore.queryAll());

        if (!uids.isEmpty()) {
            pull(strategy, uids);
        }
    }

    @Override
    public void pull(SyncStrategy strategy, Set<String> uids) throws ApiException {
        isEmpty(uids, "Set of event uids must not be null");

        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.EVENTS, DateType.SERVER);

        // we need models which we got from server (not those which we created locally)
        List<Event> persistedEvents = stateStore.queryModelsWithActions(Event.class, uids,
                Action.SYNCED, Action.TO_UPDATE, Action.TO_DELETE);

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<Event> allExistingEvents = eventApiClient.getEvents(Fields.BASIC, null, uids);

        Set<String> uidSet = ModelUtils.toUidSet(persistedEvents);
        uidSet.addAll(uids);

        List<Event> updatedEvents = eventApiClient.getEvents(
                Fields.ALL, lastUpdated, uidSet);

        // we will have to perform something similar to what happens in AbsController
        List<DbOperation> dbOperations = DbUtils.createOperations(allExistingEvents,
                updatedEvents, persistedEvents, eventStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.EVENTS, DateType.SERVER, serverTime);
    }

    @Override
    public void push(Set<String> uids) throws ApiException {
        isEmpty(uids, "Set of event uids must not be null");

        sendEvents(uids);
        deleteEvents(uids);
    }

    private void sendEvents(Set<String> uids) throws ApiException {
        List<Event> events = stateStore.queryModelsWithActions(
                Event.class, uids, Action.TO_POST, Action.TO_UPDATE);

        if (events == null || events.isEmpty()) {
            return;
        }

        try {
            ApiResponse apiResponse = eventApiClient.postEvents(events);

            List<ImportSummary> importSummaries = apiResponse.getImportSummaries();
            Map<String, Event> eventMap = ModelUtils.toMap(events);

            // check if all items were synced successfully
            for (ImportSummary importSummary : importSummaries) {
                Event event = eventMap.get(importSummary.getReference());
                if (ImportSummary.Status.SUCCESS.equals(importSummary.getStatus()) ||
                        ImportSummary.Status.OK.equals(importSummary.getStatus())) {
                    stateStore.saveActionForModel(event, Action.SYNCED);
                } else {
                    stateStore.saveActionForModel(event, Action.ERROR);
                }
            }
        } catch (ApiException apiException) {
            handleApiException(apiException, null);
        }
    }

    private void deleteEvents(Set<String> uids) throws ApiException {
        List<Event> events = stateStore.queryModelsWithActions(
                Event.class, uids, Action.TO_DELETE);

        if (events == null || events.isEmpty()) {
            return;
        }

        for (Event event : events) {
            try {
                ApiResponse apiResponse = eventApiClient.deleteEvent(event);
                if (ImportSummary.Status.SUCCESS.equals(apiResponse.getStatus()) ||
                        ImportSummary.Status.OK.equals(apiResponse.getStatus())) {
                    stateStore.saveActionForModel(event, Action.SYNCED);
                } else {
                    stateStore.saveActionForModel(event, Action.ERROR);
                }
            } catch (ApiException apiException) {
                handleApiException(apiException, event);
            }
        }
    }
}
