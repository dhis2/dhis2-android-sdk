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
import org.hisp.dhis.client.sdk.core.common.IFailedItemStore;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStore;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

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

    @Override
    public void sync(SyncStrategy syncStrategy) throws ApiException {
        sync(syncStrategy, null);
    }

    @Override
    public void sync(SyncStrategy syncStrategy, Set<String> uids) throws ApiException {
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.EVENTS);

        List<Event> persistedEvents = eventStore.queryAll();

        // we have to download all ids from server in order to
        // find out what was removed on the server side
        List<Event> allExistingEvents = eventApiClient.getEvents(Fields.BASIC, null);

        String[] uidArray = null;
        if (uids != null) {
            // here we want to get list of ids of programs which are
            // stored locally and list of programs which we want to download
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

        lastUpdatedPreferences.save(ResourceType.EVENTS, serverTime);
    }
}
