/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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
package org.hisp.dhis.android.core.event.search;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.PeriodFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventCollectionRepository;
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

    private final EventFilterCollectionRepository filtersRepository;

    private final EventQueryRepositoryScope scope;


    @Inject
    EventQueryCollectionRepository(final EventCollectionRepositoryAdapter eventCollectionRepositoryAdapter,
                                   final EventFilterCollectionRepository filtersRepository,
                                   final EventQueryRepositoryScope scope) {
        this.eventCollectionRepositoryAdapter = eventCollectionRepositoryAdapter;
        this.filtersRepository = filtersRepository;
        this.scope = scope;
        this.connectorFactory = new ScopedFilterConnectorFactory<>(s ->
                new EventQueryCollectionRepository(eventCollectionRepositoryAdapter, filtersRepository, s));
    }

    public ListFilterConnector<EventQueryCollectionRepository, String> byUid() {
        return connectorFactory.listConnector(uidList -> scope.toBuilder().events(uidList).build());
    }

    public PeriodFilterConnector<EventQueryCollectionRepository> byLastUpdated() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod mergedFilter = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.lastUpdatedDate(), filter);
            return scope.toBuilder().lastUpdatedDate(mergedFilter).build();
        });
    }

    public EqFilterConnector<EventQueryCollectionRepository, EventStatus> byStatus() {
        return connectorFactory.eqConnector(status -> scope.toBuilder().eventStatus(status).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, String> byProgramUid() {
        return connectorFactory.eqConnector(programUid -> scope.toBuilder().program(programUid).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, String> byProgramStageUid() {
        return connectorFactory.eqConnector(programStageUid -> scope.toBuilder().programStage(programStageUid).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, String> byOrganisationUnitUid() {
        return connectorFactory.eqConnector(orgunitUid -> scope.toBuilder().organisationUnit(orgunitUid).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, OrganisationUnitMode> byOrganisationUnitMode() {
        return connectorFactory.eqConnector(mode -> scope.toBuilder().organisationUnitMode(mode).build());
    }

    public PeriodFilterConnector<EventQueryCollectionRepository> byEventDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod mergedFilter = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.eventDate(), filter);
            return scope.toBuilder().eventDate(mergedFilter).build();
        });
    }

    public PeriodFilterConnector<EventQueryCollectionRepository> byCompleteDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod mergedFilter = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.completedDate(), filter);
            return scope.toBuilder().completedDate(mergedFilter).build();
        });
    }

    public PeriodFilterConnector<EventQueryCollectionRepository> byDueDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod mergedFilter = DateFilterPeriodHelper.mergeDateFilterPeriods(scope.dueDate(), filter);
            return scope.toBuilder().dueDate(mergedFilter).build();
        });
    }

    public EqFilterConnector<EventQueryCollectionRepository, Boolean> byIncludeDeleted() {
        return connectorFactory.eqConnector(includeDeleted -> scope.toBuilder().includeDeleted(includeDeleted).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, String> byTrackedEntityInstanceUid() {
        return connectorFactory.eqConnector(teiUid -> scope.toBuilder().trackedEntityInstance(teiUid).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository, AssignedUserMode> byAssignedUser() {
        return connectorFactory.eqConnector(userMode -> scope.toBuilder().assignedUserMode(userMode).build());
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByEventDate(RepositoryScope.OrderByDirection direction) {
        return orderConnector(EventQueryScopeOrderColumn.EVENT_DATE);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByDueDate(RepositoryScope.OrderByDirection direction) {
        return orderConnector(EventQueryScopeOrderColumn.DUE_DATE);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByCompleteDate(RepositoryScope.OrderByDirection direction) {
        return orderConnector(EventQueryScopeOrderColumn.COMPLETED_DATE);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByCreated(RepositoryScope.OrderByDirection direction) {
        return orderConnector(EventQueryScopeOrderColumn.CREATED);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByLastUpdated(RepositoryScope.OrderByDirection direction) {
        return orderConnector(EventQueryScopeOrderColumn.LAST_UPDATED);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByOrganisationUnitName(RepositoryScope.OrderByDirection direction) {
        return orderConnector(EventQueryScopeOrderColumn.ORGUNIT_NAME);
    }

    public EqFilterConnector<EventQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByTimeline() {
        return orderConnector(EventQueryScopeOrderColumn.TIMELINE);
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