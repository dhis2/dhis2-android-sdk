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
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

// TODO re-consider name methods in controllers (replace sync
// TODO in meta-data controllers with something similar to pullModels()
public final class EventController implements IEventController {

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
                           ITransactionManager transactionManager) {
        this.systemInfoController = systemInfoController;
        this.eventApiClient = eventApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.eventStore = eventStore;
        this.stateStore = stateStore;
        this.transactionManager = transactionManager;
    }

    @Override
    public void sync(SyncStrategy strategy) throws ApiException {
        sync(strategy, null);
    }

    @Override
    public void sync(SyncStrategy strategy, Set<String> uids) throws ApiException {
        /* first we need to get information about new events from server */
        pullEvents(strategy, uids);

        /* then we should try to push data to server */
        pushEvents(strategy, uids);
    }

    @Override
    public void pullEvents(SyncStrategy strategy, Set<String> uids) throws ApiException {
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.EVENTS, DateType.SERVER);

        // we need models which we got from server (not those which we created locally)
        List<Event> persistedEvents = stateStore.queryModelsWithActions(Event.class,
                Action.SYNCED, Action.TO_UPDATE, Action.TO_DELETE);

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<Event> allExistingEvents = eventApiClient.getEvents(Fields.BASIC, null);

        String[] uidArray = null;
        if (uids != null) {
            // here we want to get list of ids of events which are
            // stored locally and list of events which we want to download
            Set<String> persistedEventIds = ModelUtils.toUidSet(persistedEvents);
            persistedEventIds.addAll(uids);

            uidArray = persistedEventIds.toArray(new String[persistedEventIds.size()]);
        }

        List<Event> updatedEvents = eventApiClient.getEvents(
                Fields.ALL, lastUpdated, uidArray);

        // we will have to perform something similar to what happens in AbsController
        List<IDbOperation> dbOperations = DbUtils.createOperations(allExistingEvents,
                updatedEvents, persistedEvents, eventStore);
        transactionManager.transact(dbOperations);

        lastUpdatedPreferences.save(ResourceType.EVENTS, DateType.SERVER, serverTime);
    }

    @Override
    public void pushEvents(SyncStrategy strategy, Set<String> uids) throws ApiException {
        // 2) Get all e
    }
}
