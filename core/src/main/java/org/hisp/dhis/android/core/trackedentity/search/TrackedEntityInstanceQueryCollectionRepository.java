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
package org.hisp.dhis.android.core.trackedentity.search;

import static org.hisp.dhis.android.core.common.DateFilterPeriodHelper.mergeDateFilterPeriods;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import org.hisp.dhis.android.core.arch.cache.internal.D2Cache;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.arch.helpers.Result;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderExecutor;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenSelection;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BoolFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqLikeItemFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.PeriodFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.common.AssignedUserMode;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.systeminfo.DHISVersion;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
@SuppressWarnings({"PMD.GodClass", "PMD.ExcessiveImports", "PMD.ExcessivePublicCount"})
public final class TrackedEntityInstanceQueryCollectionRepository
        implements ReadOnlyWithUidCollectionRepository<TrackedEntityInstance> {

    private final TrackedEntityInstanceStore store;
    private final TrackedEntityInstanceQueryCallFactory onlineCallFactory;
    private final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders;
    private final ScopedFilterConnectorFactory<TrackedEntityInstanceQueryCollectionRepository,
            TrackedEntityInstanceQueryRepositoryScope> connectorFactory;
    private final DHISVersionManager versionManager;
    private final TrackedEntityInstanceFilterCollectionRepository filtersRepository;

    private final TrackedEntityInstanceQueryRepositoryScope scope;
    private final TrackedEntityInstanceQueryRepositoryScopeHelper scopeHelper;

    private final D2Cache<TrackedEntityInstanceQueryOnline, List<Result<TrackedEntityInstance, D2Error>>> onlineCache;
    private final TrackedEntityInstanceQueryOnlineHelper onlineHelper;
    private final TrackedEntityInstanceLocalQueryHelper localQueryHelper;

    @Inject
    TrackedEntityInstanceQueryCollectionRepository(
            final TrackedEntityInstanceStore store,
            final TrackedEntityInstanceQueryCallFactory onlineCallFactory,
            final Map<String, ChildrenAppender<TrackedEntityInstance>> childrenAppenders,
            final TrackedEntityInstanceQueryRepositoryScope scope,
            final TrackedEntityInstanceQueryRepositoryScopeHelper scopeHelper,
            final DHISVersionManager versionManager,
            final TrackedEntityInstanceFilterCollectionRepository filtersRepository,
            final D2Cache<TrackedEntityInstanceQueryOnline, List<Result<TrackedEntityInstance, D2Error>>> onlineCache,
            final TrackedEntityInstanceQueryOnlineHelper onlineHelper,
            final TrackedEntityInstanceLocalQueryHelper localQueryHelper) {
        this.store = store;
        this.onlineCallFactory = onlineCallFactory;
        this.childrenAppenders = childrenAppenders;
        this.scope = scope;
        this.scopeHelper = scopeHelper;
        this.versionManager = versionManager;
        this.filtersRepository = filtersRepository;
        this.onlineCache = onlineCache;
        this.onlineHelper = onlineHelper;
        this.localQueryHelper = localQueryHelper;
        this.connectorFactory = new ScopedFilterConnectorFactory<>(s ->
                new TrackedEntityInstanceQueryCollectionRepository(store, onlineCallFactory, childrenAppenders, s,
                        scopeHelper, versionManager, filtersRepository, onlineCache, onlineHelper, localQueryHelper));
    }

    /**
     * Only TrackedEntityInstances coming from the server are shown in the list.
     * <br><b>Important:</b> Internet connection is required to use this mode.
     *
     * @return
     */
    public TrackedEntityInstanceQueryCollectionRepository onlineOnly() {
        return connectorFactory.eqConnector(v -> scope.toBuilder().mode(RepositoryMode.ONLINE_ONLY).build()).eq(null);
    }

    /**
     * Only TrackedEntityInstances coming from local database are shown in the list.
     *
     * @return
     */
    public TrackedEntityInstanceQueryCollectionRepository offlineOnly() {
        return connectorFactory.eqConnector(v -> scope.toBuilder().mode(RepositoryMode.OFFLINE_ONLY).build()).eq(null);
    }

    /**
     * TrackedEntityInstances coming from the server are shown in first place. Once there are no more results online,
     * it continues with TrackedEntityInstances in local database.
     * <br><b>Important:</b> Internet connection is required to use this mode.
     *
     * @return
     */
    public TrackedEntityInstanceQueryCollectionRepository onlineFirst() {
        return connectorFactory.eqConnector(v -> scope.toBuilder().mode(RepositoryMode.ONLINE_FIRST).build()).eq(null);
    }

    /**
     * TrackedEntityInstances coming from local database are shown in first place. Once there are no more results, it
     * continues with TrackedEntityInstances coming from the server. This method may speed up the initial load.
     * <br><b>Important:</b> Internet connection is required to use this mode.
     *
     * @return
     */
    public TrackedEntityInstanceQueryCollectionRepository offlineFirst() {
        return connectorFactory.eqConnector(v -> scope.toBuilder().mode(RepositoryMode.OFFLINE_FIRST).build()).eq(null);
    }

    /**
     * Add an "attribute" filter to the query. If this method is call several times, conditions are appended with
     * AND connector.
     * <p>
     * For example,
     * <pre><br>.byAttribute("uid1").eq("value1")<br>.byAttribute("uid2").eq("value2")<br></pre>
     * means that the instance must have attribute "uid1" with value "value1" <b>AND</b> attribute "uid2" with
     * value "value2".
     *
     * @param attributeId Attribute uid to use in the filter
     * @return Repository connector
     */
    public EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository> byAttribute(String attributeId) {
        return connectorFactory.eqLikeItemC(attributeId, filterItem -> {
            List<RepositoryScopeFilterItem> attributes = new ArrayList<>(scope.attribute());
            attributes.add(filterItem);
            return scope.toBuilder().attribute(attributes).build();
        });
    }

    /**
     * Add an "filter" to the query. If this method is call several times, conditions are appended with
     * AND connector.
     * <p>
     * For example,
     * <pre><br>.byFilter("uid1").eq("value1")<br>.byFilter("uid2").eq("value2")<br></pre>
     * means that the instance must have attribute "uid1" with value "value1" <b>AND</b> attribute "uid2" with
     * value "value2".
     *
     * @param attributeId Attribute uid to use in the filter
     * @return Repository connector
     */
    public EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository> byFilter(String attributeId) {
        return connectorFactory.eqLikeItemC(attributeId, filterItem -> {
            List<RepositoryScopeFilterItem> filters = new ArrayList<>(scope.filter());
            filters.add(filterItem);
            return scope.toBuilder().filter(filters).build();
        });
    }

    /**
     * Search tracked entity instances with <b>any</b> attribute matching the query.
     *
     * @return Repository connector
     */
    public EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository> byQuery() {
        return connectorFactory.eqLikeItemC("", filterItem -> scope.toBuilder().query(filterItem).build());
    }

    /**
     * Filter by enrollment program. Only one program can be specified.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> byProgram() {
        return connectorFactory.eqConnector(programUid -> scope.toBuilder().program(programUid).build());
    }

    /**
     * Filter by event program stage. Only one program can be specified.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> byProgramStage() {
        return connectorFactory.eqConnector(uid -> scope.toBuilder().programStage(uid).build());
    }

    /**
     * Filter by tracked entity instance organisation unit.
     *
     * @return Repository connector
     */
    public ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> byOrgUnits() {
        return connectorFactory.listConnector(orgunitUids -> scope.toBuilder().orgUnits(orgunitUids).build());
    }

    /**
     * Define the organisation unit mode. See {@link OrganisationUnitMode} for more details on the modes.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrganisationUnitMode> byOrgUnitMode() {
        return connectorFactory.eqConnector(mode -> scope.toBuilder().orgUnitMode(mode).build());
    }

    /**
     * @deprecated use {@link #byProgramDate()} instead.
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> byProgramStartDate() {
        return connectorFactory.eqConnector(date -> byProgramDate().afterOrEqual(date).getScope());
    }

    /**
     * @deprecated use {@link #byProgramDate()} instead.
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> byProgramEndDate() {
        return connectorFactory.eqConnector(date -> byProgramDate().beforeOrEqual(date).getScope());
    }

    /**
     * Define an enrollment date filter. It only applies if a program has been specified in {@link #byProgram()}.
     *
     * @return Repository connector
     */
    public PeriodFilterConnector<TrackedEntityInstanceQueryCollectionRepository> byProgramDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod mergedFilter = mergeDateFilterPeriods(scope.programDate(), filter);
            return scope.toBuilder().programDate(mergedFilter).build();
        });
    }

    /**
     * Define an incident date filter. It only applies if a program has been specified in {@link #byProgram()}.
     *
     * @return Repository connector
     */
    public PeriodFilterConnector<TrackedEntityInstanceQueryCollectionRepository> byIncidentDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod mergedFilter = mergeDateFilterPeriods(scope.incidentDate(), filter);
            return scope.toBuilder().incidentDate(mergedFilter).build();
        });
    }

    /**
     * @deprecated use {@link #byEnrollmentStatus()} instead.
     */
    @Deprecated
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, EnrollmentStatus> byProgramStatus() {
        return connectorFactory.eqConnector(status ->
                scope.toBuilder().enrollmentStatus(Collections.singletonList(status)).build());
    }

    /**
     * Filter by enrollment status. It only applies if a program has been specified in {@link #byProgram()}.
     * <br><b>IMPORTANT:</b> this filter accepts a list of status, but only the first one will be used for the online
     * query because the web API does not support querying by multiple status.
     *
     * @return Repository connector
     */
    public ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, EnrollmentStatus> byEnrollmentStatus() {
        return connectorFactory.listConnector(statusList -> scope.toBuilder().enrollmentStatus(statusList).build());
    }

    /**
     * @deprecated use {@link #byEventDate()} instead.
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> byEventStartDate() {
        return connectorFactory.eqConnector(date -> byEventDate().afterOrEqual(date).getScope());
    }

    /**
     * @deprecated use {@link #byEventDate()} instead.
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> byEventEndDate() {
        return connectorFactory.eqConnector(date -> byEventDate().beforeOrEqual(date).getScope());
    }

    /**
     * Define an event date filter. It only applies if a program has been specified in {@link #byProgram()}.
     *
     * @return Repository connector
     */
    public PeriodFilterConnector<TrackedEntityInstanceQueryCollectionRepository> byEventDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod mergedFilter = mergeDateFilterPeriods(scope.eventDate(), filter);
            return scope.toBuilder().eventDate(mergedFilter).build();
        });
    }

    /**
     * Filter by event status. It only applies if a program has been specified in {@link #byProgram()}.
     * <br><b>IMPORTANT:</b> this filter requires that eventStartDate {@link #byEventStartDate()} and eventEndDate
     * {@link #byEventEndDate()} are defined.
     *
     * @return Repository connector
     */
    public ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, EventStatus> byEventStatus() {
        return connectorFactory.listConnector(statusList -> scope.toBuilder().eventStatus(statusList).build());
    }

    /**
     * Filter by TrackedEntityType. Only one type can be specified.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> byTrackedEntityType() {
        return connectorFactory.eqConnector(type -> scope.toBuilder().trackedEntityType(type).build());
    }

    /**
     * Whether to include or not deleted tracked entity instances.
     * <br><b>IMPORTANT:</b> currently this filter only applies to <b>offline</b> instances.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Boolean> byIncludeDeleted() {
        return connectorFactory.eqConnector(bool -> scope.toBuilder().includeDeleted(bool).build());
    }

    /**
     * Filter by sync status.
     * <br><b>IMPORTANT:</b> using this filter forces <b>offlineOnly</b> mode.
     *
     * @return Repository connector
     */
    public ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, State> byStates() {
        return connectorFactory.listConnector(states -> scope.toBuilder().states(states).build());
    }

    /**
     * Filter by follow up status. It only applies if a program has been specified in {@link #byProgram()}.
     *
     * @return Repository connector
     */
    public BoolFilterConnector<TrackedEntityInstanceQueryCollectionRepository> byFollowUp() {
        return connectorFactory.booleanConnector(followUp -> scope.toBuilder().followUp(followUp).build());
    }

    /**
     * Filter by assigned user mode.
     * <br><b>IMPORTANT:</b> this filter has effect if DHIS2 version is 2.32 or later. Otherwise, it is ignored.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, AssignedUserMode> byAssignedUserMode() {
        return connectorFactory.eqConnector(mode -> {
            if (versionManager.isGreaterThan(DHISVersion.V2_31)) {
                return scope.toBuilder().assignedUserMode(mode).build();
            } else {
                return scope;
            }
        });
    }

    /**
     * Define an lastUpdated date filter.
     *
     * @return Repository connector
     */
    public PeriodFilterConnector<TrackedEntityInstanceQueryCollectionRepository> byLastUpdatedDate() {
        return connectorFactory.periodConnector(filter -> {
            DateFilterPeriod mergedFilter = mergeDateFilterPeriods(scope.lastUpdatedDate(), filter);
            return scope.toBuilder().lastUpdatedDate(mergedFilter).build();
        });
    }

    /**
     * Whether to allow or not cached results for online queries. Its value is 'false' by default.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Boolean> allowOnlineCache() {
        return connectorFactory.eqConnector(bool -> scope.toBuilder().allowOnlineCache(bool).build());
    }

    public ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> excludeUids() {
        return connectorFactory.listConnector(list -> scope.toBuilder().excludedUids(new HashSet<>(list)).build());
    }

    /**
     * Apply the filters defined in a {@link TrackedEntityInstanceFilter}. It will overwrite previous filters in case
     * they overlap. In the same way, they could be overwritten by subsequent filters.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> byTrackedEntityInstanceFilter() {
        return connectorFactory.eqConnector(id -> {
            TrackedEntityInstanceFilter filter =
                    filtersRepository
                            .withTrackedEntityInstanceEventFilters()
                            .withAttributeValueFilters()
                            .uid(id).blockingGet();
            return scopeHelper.addTrackedEntityInstanceFilter(scope, filter);
        });
    }

    /**
     * Order by created date. If a program is provided, it takes the created of most recent enrollment.
     * Otherwise it takes the value of the tracked entity instance.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByCreated() {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.CREATED);
    }

    /**
     * Order by last updated date. If a program is provided, it takes the last updated of most recent enrollment.
     * Otherwise it takes the value of the tracked entity instance.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByLastUpdated() {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.LAST_UPDATED);
    }

    /**
     * Order by tracked entity instance attribute value.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByAttribute(String attr) {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.attribute(attr));
    }

    /**
     * Order by organisation unit name of the tracked entity instance.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByOrganisationUnitName() {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.ORGUNIT_NAME);
    }

    /**
     * Order by enrollment date of most recent enrollment. This order only applies to local results.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByEnrollmentDate() {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.ENROLLMENT_DATE);
    }

    /**
     * Order by incident date of most recent enrollment. This order only applies to local results.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByIncidentDate() {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.INCIDENT_DATE);
    }

    /**
     * Order by most recent event. It takes the event date and, if it is null, it fallbacks to due date. This order
     * only applies to local results.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByEventDate() {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.EVENT_DATE);
    }

    /**
     * Order by completion date of the most recent event. This order only applies to local results.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByCompletedDate() {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.COMPLETION_DATE);
    }

    /**
     * Order by enrollment status.
     *
     * @return Repository connector
     */
    public EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderByEnrollmentStatus() {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.ENROLLMENT_STATUS);
    }


    @Override
    public LiveData<PagedList<TrackedEntityInstance>> getPaged(int pageSize) {
        DataSource.Factory<TrackedEntityInstance, TrackedEntityInstance> factory =
                new DataSource.Factory<TrackedEntityInstance, TrackedEntityInstance>() {
                    @Override
                    public DataSource<TrackedEntityInstance, TrackedEntityInstance> create() {
                        return getDataSource();
                    }
                };

        return new LivePagedListBuilder<>(factory, pageSize).build();
    }

    public DataSource<TrackedEntityInstance, TrackedEntityInstance> getDataSource() {
        return new TrackedEntityInstanceQueryDataSource(store, onlineCallFactory, scope,
                childrenAppenders, onlineCache, onlineHelper, localQueryHelper);
    }

    public DataSource<TrackedEntityInstance, Result<TrackedEntityInstance, D2Error>> getResultDataSource() {
        return new TrackedEntityInstanceQueryDataSourceResult(store, onlineCallFactory, scope,
                childrenAppenders, onlineCache, onlineHelper, localQueryHelper);
    }

    public TrackedEntityInstanceQueryRepositoryScope getScope() {
        return scope;
    }

    @Override
    public List<TrackedEntityInstance> blockingGet() {
        if (scope.mode().equals(RepositoryMode.OFFLINE_ONLY) || scope.mode().equals(RepositoryMode.OFFLINE_FIRST)) {
            String sqlQuery = localQueryHelper.getSqlQuery(scope, scope.excludedUids(), -1);
            List<TrackedEntityInstance> instances = store.selectRawQuery(sqlQuery);
            return ChildrenAppenderExecutor.appendInObjectCollection(instances, childrenAppenders,
                    new ChildrenSelection(Collections.singleton(
                            TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES)));
        } else {
            try {
                return onlineHelper.queryOnlineBlocking(onlineCallFactory, scope);
            } catch (D2Error e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public Single<List<TrackedEntityInstance>> get() {
        return Single.fromCallable(this::blockingGet);
    }

    @Override
    public Single<Integer> count() {
        return Single.fromCallable(this::blockingCount);
    }

    @Override
    public int blockingCount() {
        return blockingGet().size();
    }

    @Override
    public Single<Boolean> isEmpty() {
        return Single.fromCallable(this::blockingIsEmpty);
    }

    @Override
    public boolean blockingIsEmpty() {
        return blockingCount() == 0;
    }

    @Override
    public ReadOnlyObjectRepository<TrackedEntityInstance> one() {
        return objectRepository(list -> list.isEmpty() ? null : list.get(0));
    }

    private EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository,
            RepositoryScope.OrderByDirection> orderConnector(
            TrackedEntityInstanceQueryScopeOrderColumn col) {
        return connectorFactory.eqConnector(direction -> {
            List<TrackedEntityInstanceQueryScopeOrderByItem> order = new ArrayList<>(scope.order());
            order.add(TrackedEntityInstanceQueryScopeOrderByItem.builder().column(col).direction(direction).build());
            return scope.toBuilder().order(order).build();
        });
    }

    @Override
    public ReadOnlyObjectRepository<TrackedEntityInstance> uid(String uid) {
        return objectRepository(list -> {
            for (TrackedEntityInstance instance : list) {
                if (uid.equals(instance.uid())) {
                    return instance;
                }
            }
            return null;
        });
    }

    @Override
    public Single<List<String>> getUids() {
        return Single.fromCallable(this::blockingGetUids);
    }

    @Override
    public List<String> blockingGetUids() {
        if (scope.mode().equals(RepositoryMode.OFFLINE_ONLY) || scope.mode().equals(RepositoryMode.OFFLINE_FIRST)) {
            String sqlQuery = localQueryHelper.getUidsWhereClause(scope, scope.excludedUids(), -1);
            return store.selectUidsWhere(sqlQuery);
        } else {
            List<TrackedEntityInstance> instances = blockingGet();
            return new ArrayList<>(UidsHelper.getUids(instances));
        }
    }

    private ReadOnlyObjectRepository<TrackedEntityInstance> objectRepository(
            Transformer<List<TrackedEntityInstance>, TrackedEntityInstance> transformer) {
        return new ReadOnlyObjectRepository<TrackedEntityInstance>() {

            @Override
            public Single<TrackedEntityInstance> get() {
                return Single.fromCallable(this::blockingGet);
            }

            @Override
            public TrackedEntityInstance blockingGet() {
                List<TrackedEntityInstance> list = TrackedEntityInstanceQueryCollectionRepository.this.blockingGet();
                return transformer.transform(list);
            }

            @Override
            public Single<Boolean> exists() {
                return Single.fromCallable(this::blockingExists);
            }

            @Override
            public boolean blockingExists() {
                return blockingGet() != null;
            }
        };
    }
}
