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
import org.hisp.dhis.client.sdk.core.common.ILogger;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.controllers.AbsDataController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.network.ApiResponse;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
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

public final class EventController extends AbsDataController<Event> implements IEventController {

    /* Controllers */
    private final ISystemInfoController systemInfoController;

    /* Api clients */
    private final IEventApiClient eventApiClient;

    /* Preferences */
    private final ILastUpdatedPreferences lastUpdatedPreferences;

    /* Persistence */
    private final IEventStore eventStore;
    private final IStateStore stateStore;

    /* Utilities */
    private final ITransactionManager transactionManager;

    public EventController(ISystemInfoController systemInfoController,
                           IEventApiClient eventApiClient,
                           ILastUpdatedPreferences lastUpdatedPreferences,
                           IEventStore eventStore, IStateStore stateStore,
                           ITransactionManager transactionManager,
                           ILogger logger) {
        super(logger, eventStore);

        this.systemInfoController = systemInfoController;
        this.eventApiClient = eventApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.eventStore = eventStore;
        this.stateStore = stateStore;
        this.transactionManager = transactionManager;
    }

    @Override
    public void sync(SyncStrategy strategy, Set<String> uids) throws ApiException {
        isEmpty(uids);

        /* first we need to get information about new events from server */
        pullUpdates(strategy, uids);

        /* then we should try to push data to server */
        pushUpdates(strategy, uids);
    }

    @Override
    public void pullUpdates(SyncStrategy strategy, Set<String> uids) throws ApiException {
        isEmpty(uids);

        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.EVENTS, DateType.SERVER);

        // we need models which we got from server (not those which we created locally)

        // TODO  get only items which are specified with uids
        List<Event> persistedEvents = stateStore.queryModelsWithActions(Event.class,
                Action.SYNCED, Action.TO_UPDATE, Action.TO_DELETE);

        // we have to download all ids from server in order to
        // find out what was removed on the server side

        // TODO fot events endpoint we cannot do this!
        // TODO Change to check based on uids
        List<Event> allExistingEvents = eventApiClient.getEvents(Fields.BASIC, null, null);

        // TODO we always will be providing uids! otherwise fail
        Set<String> uidSet = null;
        if (uids != null) {
            // here we want to get list of ids of events which are
            // stored locally and list of events which we want to download
            uidSet = ModelUtils.toUidSet(persistedEvents);
            uidSet.addAll(uids);
        }

        // TODO this is okay
        List<Event> updatedEvents = eventApiClient.getEvents(
                Fields.ALL, lastUpdated, uidSet);

        // we will have to perform something similar to what happens in AbsController
        List<IDbOperation> dbOperations = DbUtils.createOperations(allExistingEvents,
                updatedEvents, persistedEvents, eventStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.EVENTS, DateType.SERVER, serverTime);
    }

    @Override
    public void pushUpdates(SyncStrategy strategy, Set<String> uids) throws ApiException {
        isEmpty(uids);

        sendEvents(strategy, uids);
        deleteEvents(strategy, uids);
    }

    private void sendEvents(SyncStrategy strategy, Set<String> uids) throws ApiException {
        List<Event> events = stateStore.queryModelsWithActions(
                Event.class, Action.TO_POST, Action.TO_UPDATE);

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

    private void deleteEvents(SyncStrategy strategy, Set<String> uids) throws ApiException {
        List<Event> events = stateStore.queryModelsWithActions(
                Event.class, Action.TO_DELETE);

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

    private void isEmpty(Set<String> uids) {
        if (uids == null || uids.isEmpty()) {
            throw new IllegalArgumentException("Set of event uids must not be null");
        }
    }
}
