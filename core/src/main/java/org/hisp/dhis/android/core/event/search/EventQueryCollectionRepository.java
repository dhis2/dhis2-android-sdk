/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.event.search;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.PagedList;

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EventDataFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.PeriodFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventCollectionRepository;
import org.hisp.dhis.android.core.event.EventDataFilter;
import org.hisp.dhis.android.core.event.EventFilter;
import org.hisp.dhis.android.core.event.EventFilterCollectionRepository;
import org.hisp.dhis.android.core.event.EventObjectRepository;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public final class EventQueryCollectionRepository implements ReadOnlyWithUidCollectionRepository<Event> {

    private final EventCollectionRepositoryAdapter eventCollectionRepositoryAdapter;
    private final ScopedFilterConnectorFactory<EventQueryCollectionRepository,
            EventQueryRepositoryScope> connectorFactory;
    private final EventFilterCollectionRepository eventFilterRepository;

    private final EventQueryRepositoryScope scope;

    @Inject
    EventQueryCollectionRepository(final EventCollectionRepositoryAdapter eventCollectionRepositoryAdapter,
                                   final EventFilterCollectionRepository eventFilterRepository,
                                   final EventQueryRepositoryScope scope) {
        this.eventCollectionRepositoryAdapter = eventCollectionRepositoryAdapter;
        this.eventFilterRepository = eventFilterRepository;
        this.scope = scope;
        this.connectorFactory = new ScopedFilterConnectorFactory<>(s ->
                new EventQueryCollectionRepository(eventCollectionRepositoryAdapter, eventFilterRepository, s));
    }

    public ListFilterConnector<EventQueryCollectionRepository, String> byUid() {
        return connectorFactory.listConnector(uidList -> scope.toBuilder().events(uidList).build());
    }

    public PeriodFilterConnector<EventQueryCollectionRepository> byLastUpdated() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod merged = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.lastUpdatedDate(), filter);
            return scope.toBuilder().lastUpdatedDate(merged).build();
        });
    }

    /**
     * Filter by event status.
     * <br><b>IMPORTANT:</b> this filter accepts a list of event status, but only the first one will be used for
     * the online query because the web API does not support querying by multiple status.
     *
     * @return Repository connector
     */
    public ListFilterConnector<EventQueryCollectionRepository, EventStatus> byStatus() {
        return connectorFactory.listConnector(status -> scope.toBuilder().eventStatus(status).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, String> byProgram() {
        return connectorFactory.eqConnector(program -> scope.toBuilder().program(program).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, String> byProgramStage() {
        return connectorFactory.eqConnector(programStage -> scope.toBuilder().programStage(programStage).build());
    }

    /**
     * Filter by Event organisation units.
     * <br><b>IMPORTANT:</b> this filter accepts a list of organisation units, but only the first one will be used for
     * the online query because the web API does not support querying by multiple organisation units.
     *
     * @return Repository connector
     */
    public ListFilterConnector<EventQueryCollectionRepository, String> byOrgUnits() {
        return connectorFactory.listConnector(orgunits -> scope.toBuilder().orgUnits(orgunits).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, OrganisationUnitMode> byOrgUnitMode() {
        return connectorFactory.eqConnector(mode -> scope.toBuilder().orgUnitMode(mode).build());
    }

    public PeriodFilterConnector<EventQueryCollectionRepository> byEventDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod merged = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.eventDate(), filter);
            return scope.toBuilder().eventDate(merged).build();
        });
    }

    public PeriodFilterConnector<EventQueryCollectionRepository> byCompleteDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod merged = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.completedDate(), filter);
            return scope.toBuilder().completedDate(merged).build();
        });
    }

    public PeriodFilterConnector<EventQueryCollectionRepository> byDueDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod merged = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.dueDate(), filter);
            return scope.toBuilder().dueDate(merged).build();
        });
    }

    public EqFilterConnector<EventQueryCollectionRepository, Boolean> byIncludeDeleted() {
        return connectorFactory.eqConnector(includeDeleted -> scope.toBuilder().includeDeleted(includeDeleted).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, String> byTrackedEntityInstance() {
        return connectorFactory.eqConnector(tei -> scope.toBuilder().trackedEntityInstance(tei).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, AssignedUserMode> byAssignedUser() {
        return connectorFactory.eqConnector(userMode -> scope.toBuilder().assignedUserMode(userMode).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, String> byEventFilter() {
        return connectorFactory.eqConnector(id -> {
            EventFilter filter = eventFilterRepository.withEventDataFilters().uid(id).blockingGet();
            return EventQueryRepositoryScopeHelper.addEventFilter(scope, filter);
        });
    }

    /**
     * Filter by sync status.
     * <br><b>IMPORTANT:</b> using this filter forces <b>offlineOnly</b> mode.
     *
     * @return Repository connector
     */
    public ListFilterConnector<EventQueryCollectionRepository, State> byStates() {
        return connectorFactory.listConnector(states -> scope.toBuilder().states(states).build());
    }

    public ListFilterConnector<EventQueryCollectionRepository, String> byAttributeOptionCombo() {
        return connectorFactory.listConnector(aoc -> scope.toBuilder().attributeOptionCombos(aoc).build());
    }

    public EventDataFilterConnector<EventQueryCollectionRepository> byDataValue(String dataElementId) {
        return connectorFactory.eventDataFilterConnector(dataElementId, filter -> {
            List<EventDataFilter> filters = DateFilterPeriodHelper.mergeEventDataFilters(scope.dataFilters(), filter);
            return scope.toBuilder().dataFilters(filters).build();
        });
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByEventDate() {
        return orderConnector(EventQueryScopeOrderColumn.EVENT_DATE);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByDueDate() {
        return orderConnector(EventQueryScopeOrderColumn.DUE_DATE);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByCompleteDate() {
        return orderConnector(EventQueryScopeOrderColumn.COMPLETED_DATE);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByCreated() {
        return orderConnector(EventQueryScopeOrderColumn.CREATED);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByLastUpdated() {
        return orderConnector(EventQueryScopeOrderColumn.LAST_UPDATED);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByOrganisationUnitName() {
        return orderConnector(EventQueryScopeOrderColumn.ORGUNIT_NAME);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByTimeline() {
        return orderConnector(EventQueryScopeOrderColumn.TIMELINE);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByDataElement(String dataElement) {
        return orderConnector(EventQueryScopeOrderColumn.dataElement(dataElement));
    }

    private EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderConnector(
            EventQueryScopeOrderColumn col) {
        return connectorFactory.eqConnector(direction -> {
            List<EventQueryScopeOrderByItem> order = new ArrayList<>(scope.order());
            order.add(EventQueryScopeOrderByItem.builder().column(col).direction(direction).build());
            return scope.toBuilder().order(order).build();
        });
    }

    public EventQueryRepositoryScope getScope() {
        return scope;
    }

    @Override
    public EventObjectRepository uid(String uid) {
        return getEventCollectionRepository().uid(uid);
    }

    @Override
    public Single<List<String>> getUids() {
        return getEventCollectionRepository().getUids();
    }

    @Override
    public List<String> blockingGetUids() {
        return getEventCollectionRepository().blockingGetUids();
    }

    @Override
    public Single<List<Event>> get() {
        return getEventCollectionRepository().get();
    }

    @Override
    public List<Event> blockingGet() {
        return getEventCollectionRepository().blockingGet();
    }

    @Override
    public LiveData<PagedList<Event>> getPaged(int pageSize) {
        return getEventCollectionRepository().getPaged(pageSize);
    }

    public DataSource<Event, Event> getDataSource() {
        return getEventCollectionRepository().getDataSource();
    }

    @Override
    public Single<Integer> count() {
        return getEventCollectionRepository().count();
    }

    @Override
    public int blockingCount() {
        return getEventCollectionRepository().blockingCount();
    }

    @Override
    public Single<Boolean> isEmpty() {
        return getEventCollectionRepository().isEmpty();
    }

    @Override
    public boolean blockingIsEmpty() {
        return getEventCollectionRepository().blockingIsEmpty();
    }

    @Override
    public ReadOnlyObjectRepository<Event> one() {
        return getEventCollectionRepository().one();
    }

    private EventCollectionRepository getEventCollectionRepository() {
        return eventCollectionRepositoryAdapter
                .getCollectionRepository(scope)
                .withTrackedEntityDataValues();
    }
}