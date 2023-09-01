/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.search

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import dagger.Reusable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import org.hisp.dhis.android.core.arch.cache.internal.D2Cache
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BoolFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EqLikeItemFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ListFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.PeriodFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.ScopedFilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper.Companion.mergeDateFilterPeriods
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListCollectionRepository
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterCollectionRepository
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import java.util.Date
import javax.inject.Inject

@Reusable
@Suppress("TooManyFunctions", "LongParameterList")
class TrackedEntityInstanceQueryCollectionRepository @Inject internal constructor(
    private val store: TrackedEntityInstanceStore,
    private val trackerParentCallFactory: TrackerParentCallFactory,
    private val childrenAppenders: MutableMap<String, ChildrenAppender<TrackedEntityInstance>>,
    val scope: TrackedEntityInstanceQueryRepositoryScope,
    private val scopeHelper: TrackedEntityInstanceQueryRepositoryScopeHelper,
    private val versionManager: DHISVersionManager,
    private val filtersRepository: TrackedEntityInstanceFilterCollectionRepository,
    private val workingListRepository: ProgramStageWorkingListCollectionRepository,
    private val onlineCache: D2Cache<TrackedEntityInstanceQueryOnline, TrackedEntityInstanceOnlineResult>,
    private val onlineHelper: TrackedEntityInstanceQueryOnlineHelper,
    private val localQueryHelper: TrackedEntityInstanceLocalQueryHelper,
) : ReadOnlyWithUidCollectionRepository<TrackedEntityInstance> {
    private val connectorFactory:
        ScopedFilterConnectorFactory<
            TrackedEntityInstanceQueryCollectionRepository,
            TrackedEntityInstanceQueryRepositoryScope,
            > =
        ScopedFilterConnectorFactory { s: TrackedEntityInstanceQueryRepositoryScope ->
            TrackedEntityInstanceQueryCollectionRepository(
                store, trackerParentCallFactory, childrenAppenders,
                s, scopeHelper, versionManager, filtersRepository, workingListRepository, onlineCache,
                onlineHelper, localQueryHelper,
            )
        }

    /**
     * Only TrackedEntityInstances coming from the server are shown in the list.
     * <br></br>**Important:** Internet connection is required to use this mode.
     *
     * @return
     */
    fun onlineOnly(): TrackedEntityInstanceQueryCollectionRepository {
        return connectorFactory.eqConnector<Any> {
            scope.toBuilder().mode(RepositoryMode.ONLINE_ONLY).build()
        }.eq(null)
    }

    /**
     * Only TrackedEntityInstances coming from local database are shown in the list.
     *
     * @return
     */
    fun offlineOnly(): TrackedEntityInstanceQueryCollectionRepository {
        return connectorFactory.eqConnector<Any> {
            scope.toBuilder().mode(RepositoryMode.OFFLINE_ONLY).build()
        }.eq(null)
    }

    /**
     * TrackedEntityInstances coming from the server are shown in first place. Once there are no more results online,
     * it continues with TrackedEntityInstances in local database.
     * <br></br>**Important:** Internet connection is required to use this mode.
     *
     * @return
     */
    fun onlineFirst(): TrackedEntityInstanceQueryCollectionRepository {
        return connectorFactory.eqConnector<Any> {
            scope.toBuilder().mode(RepositoryMode.ONLINE_FIRST).build()
        }.eq(null)
    }

    /**
     * TrackedEntityInstances coming from local database are shown in first place. Once there are no more results, it
     * continues with TrackedEntityInstances coming from the server. This method may speed up the initial load.
     * <br></br>**Important:** Internet connection is required to use this mode.
     *
     * @return
     */
    fun offlineFirst(): TrackedEntityInstanceQueryCollectionRepository {
        return connectorFactory.eqConnector<Any> {
            scope.toBuilder().mode(RepositoryMode.OFFLINE_FIRST).build()
        }.eq(null)
    }

    /**
     * Add an "attribute" filter to the query. If this method is called several times, conditions are appended with
     * AND connector.
     *
     *
     * For example,
     * <pre><br></br>.byAttribute("uid1").eq("value1")<br></br>.byAttribute("uid2").eq("value2")<br></br></pre>
     * means that the instance must have attribute "uid1" with value "value1" **AND** attribute "uid2" with
     * value "value2".
     *
     * @param attributeId Attribute uid to use in the filter
     * @return Repository connector
     */
    fun byAttribute(attributeId: String): EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository> {
        return byFilter(attributeId)
    }

    /**
     * Add a "filter" to the query. If this method is called several times, conditions are appended with
     * AND connector.
     *
     *
     * For example,
     * <pre><br></br>.byFilter("uid1").eq("value1")<br></br>.byFilter("uid2").eq("value2")<br></br></pre>
     * means that the instance must have attribute "uid1" with value "value1" **AND** attribute "uid2" with
     * value "value2".
     *
     * @param attributeId Attribute uid to use in the filter
     * @return Repository connector
     */
    fun byFilter(attributeId: String): EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository> {
        return connectorFactory.eqLikeItemC(attributeId) { filterItem: RepositoryScopeFilterItem ->
            scopeHelper.addFilter(scope, filterItem)
        }
    }

    /**
     * Search tracked entity instances with **any** attribute matching the query.
     *
     * @return Repository connector
     */
    fun byQuery(): EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository> {
        return connectorFactory.eqLikeItemC("") { filterItem: RepositoryScopeFilterItem ->
            scope.toBuilder().query(filterItem).build()
        }
    }

    /**
     * Filter the tracked entity for those matching this filter. If this method is called several times, conditions
     * are appended with AND connector.
     *
     * @param dataElement DataElement uid to use in the filter
     * @return Repository connector
     */
    fun byDataValue(dataElement: String): EqLikeItemFilterConnector<TrackedEntityInstanceQueryCollectionRepository> {
        return connectorFactory.eqLikeItemC(dataElement) { filterItem: RepositoryScopeFilterItem ->
            scope.toBuilder().dataValue(scope.dataValue() + filterItem).build()
        }
    }

    /**
     * Filter by enrollment program. Only one program can be specified.
     *
     * @return Repository connector
     */
    fun byProgram(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> {
        return connectorFactory.eqConnector { programUid: String? -> scope.toBuilder().program(programUid).build() }
    }

    /**
     * Filter by event program stage. Only one program can be specified.
     *
     * @return Repository connector
     */
    fun byProgramStage(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> {
        return connectorFactory.eqConnector { uid: String? -> scope.toBuilder().programStage(uid).build() }
    }

    /**
     * Filter by tracked entity instance organisation unit.
     *
     * @return Repository connector
     */
    fun byOrgUnits(): ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> {
        return connectorFactory.listConnector { scope.toBuilder().orgUnits(it).build() }
    }

    /**
     * Define the organisation unit mode. See [OrganisationUnitMode] for more details on the modes.
     *
     * @return Repository connector
     */
    fun byOrgUnitMode(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrganisationUnitMode> {
        return connectorFactory.eqConnector { scope.toBuilder().orgUnitMode(it).build() }
    }

    @Deprecated("use {@link #byProgramDate()} instead.")
    fun byProgramStartDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> {
        return connectorFactory.eqConnector { byProgramDate().afterOrEqual(it!!).scope }
    }

    @Deprecated("use {@link #byProgramDate()} instead.")
    fun byProgramEndDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> {
        return connectorFactory.eqConnector { byProgramDate().beforeOrEqual(it!!).scope }
    }

    /**
     * Define an enrollment date filter. It only applies if a program has been specified in [.byProgram].
     *
     * @return Repository connector
     */
    fun byProgramDate(): PeriodFilterConnector<TrackedEntityInstanceQueryCollectionRepository> {
        return connectorFactory.periodConnector { filter: DateFilterPeriod ->
            val mergedFilter = mergeDateFilterPeriods(
                scope.programDate(),
                filter,
            )
            scope.toBuilder().programDate(mergedFilter).build()
        }
    }

    /**
     * Define an incident date filter. It only applies if a program has been specified in [.byProgram].
     *
     * @return Repository connector
     */
    fun byIncidentDate(): PeriodFilterConnector<TrackedEntityInstanceQueryCollectionRepository> {
        return connectorFactory.periodConnector { filter: DateFilterPeriod ->
            val mergedFilter = mergeDateFilterPeriods(
                scope.incidentDate(),
                filter,
            )
            scope.toBuilder().incidentDate(mergedFilter).build()
        }
    }

    @Deprecated("use {@link #byEnrollmentStatus()} instead.")
    fun byProgramStatus(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, EnrollmentStatus> {
        return connectorFactory.eqConnector { status: EnrollmentStatus? ->
            scope.toBuilder().enrollmentStatus(listOf(status)).build()
        }
    }

    /**
     * Filter by enrollment status. It only applies if a program has been specified in [.byProgram].
     * <br></br>**IMPORTANT:** this filter accepts a list of status, but only the first one will be used for the online
     * query because the web API does not support querying by multiple status.
     *
     * @return Repository connector
     */
    fun byEnrollmentStatus(): ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, EnrollmentStatus> {
        return connectorFactory.listConnector {
            scope.toBuilder().enrollmentStatus(it).build()
        }
    }

    @Deprecated("use {@link #byEventDate()} instead.")
    fun byEventStartDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> {
        return connectorFactory.eqConnector {
            byEventDate().afterOrEqual(it!!).scope
        }
    }

    @Deprecated("use {@link #byEventDate()} instead.")
    fun byEventEndDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Date> {
        return connectorFactory.eqConnector {
            byEventDate().beforeOrEqual(it!!).scope
        }
    }

    /**
     * Define an event date filter. It only applies if a program has been specified in [.byProgram].
     *
     * @return Repository connector
     */
    fun byEventDate(): PeriodFilterConnector<TrackedEntityInstanceQueryCollectionRepository> {
        return connectorFactory.periodConnector { filter: DateFilterPeriod ->
            val mergedFilter = mergeDateFilterPeriods(
                scope.eventDate(),
                filter,
            )
            scope.toBuilder().eventDate(mergedFilter).build()
        }
    }

    /**
     * Filter by event status. It only applies if a program has been specified in [.byProgram].
     * <br></br>**IMPORTANT:** this filter requires that eventStartDate [.byEventStartDate] and eventEndDate
     * [.byEventEndDate] are defined.
     *
     * @return Repository connector
     */
    fun byEventStatus(): ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, EventStatus> {
        return connectorFactory.listConnector { scope.toBuilder().eventStatus(it).build() }
    }

    /**
     * Filter by TrackedEntityType. Only one type can be specified.
     *
     * @return Repository connector
     */
    fun byTrackedEntityType(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> {
        return connectorFactory.eqConnector { scope.toBuilder().trackedEntityType(it).build() }
    }

    /**
     * Whether to include or not deleted tracked entity instances.
     * <br></br>**IMPORTANT:** currently this filter only applies to **offline** instances.
     *
     * @return Repository connector
     */
    fun byIncludeDeleted(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Boolean> {
        return connectorFactory.eqConnector { scope.toBuilder().includeDeleted(it).build() }
    }

    /**
     * Filter by sync status.
     * <br></br>**IMPORTANT:** using this filter forces **offlineOnly** mode.
     *
     * @return Repository connector
     */
    fun byStates(): ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, State> {
        return connectorFactory.listConnector { scope.toBuilder().states(it).build() }
    }

    /**
     * Filter by follow up status. It only applies if a program has been specified in [.byProgram].
     *
     * @return Repository connector
     */
    fun byFollowUp(): BoolFilterConnector<TrackedEntityInstanceQueryCollectionRepository> {
        return connectorFactory.booleanConnector { scope.toBuilder().followUp(it).build() }
    }

    /**
     * Filter by assigned user mode.
     * <br></br>**IMPORTANT:** this filter has effect if DHIS2 version is 2.32 or later. Otherwise, it is ignored.
     *
     * @return Repository connector
     */
    fun byAssignedUserMode(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, AssignedUserMode> {
        return connectorFactory.eqConnector { mode: AssignedUserMode? ->
            return@eqConnector if (versionManager.isGreaterThan(DHISVersion.V2_31)) {
                scope.toBuilder().assignedUserMode(mode).build()
            } else {
                scope
            }
        }
    }

    /**
     * Define an lastUpdated date filter.
     *
     * @return Repository connector
     */
    fun byLastUpdatedDate(): PeriodFilterConnector<TrackedEntityInstanceQueryCollectionRepository> {
        return connectorFactory.periodConnector { filter: DateFilterPeriod ->
            val mergedFilter = mergeDateFilterPeriods(
                scope.lastUpdatedDate(),
                filter,
            )
            scope.toBuilder().lastUpdatedDate(mergedFilter).build()
        }
    }

    /**
     * Whether to allow or not cached results for online queries. Its value is 'false' by default.
     *
     * @return Repository connector
     */
    fun allowOnlineCache(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, Boolean> {
        return connectorFactory.eqConnector { scope.toBuilder().allowOnlineCache(it).build() }
    }

    fun excludeUids(): ListFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> {
        return connectorFactory.listConnector { list: List<String> ->
            scope.toBuilder().excludedUids(HashSet(list)).build()
        }
    }

    /**
     * Apply the filters defined in a [TrackedEntityInstanceFilter]. It will overwrite previous filters in case
     * they overlap. In the same way, they could be overwritten by subsequent filters.
     *
     * @return Repository connector
     */
    fun byTrackedEntityInstanceFilter(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> {
        return connectorFactory.eqConnector { id: String? ->
            val filter = filtersRepository
                .withTrackedEntityInstanceEventFilters()
                .withAttributeValueFilters()
                .uid(id).blockingGet()
            scopeHelper.addTrackedEntityInstanceFilter(scope, filter!!)
        }
    }

    /**
     * Apply the filters defined in a [ProgramStageWorkingList]. It will overwrite previous filters in case
     * they overlap. In the same way, they could be overwritten by subsequent filters.
     *
     * @return Repository connector
     */
    fun byProgramStageWorkingList(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, String> {
        return connectorFactory.eqConnector { id: String? ->
            val workingList = workingListRepository
                .withDataFilters()
                .withAttributeValueFilters()
                .uid(id).blockingGet()
            scopeHelper.addProgramStageWorkingList(scope, workingList!!)
        }
    }

    /**
     * Order by created date. If a program is provided, it takes the created of most recent enrollment.
     * Otherwise it takes the value of the tracked entity instance.
     *
     * @return Repository connector
     */
    fun orderByCreated(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrderByDirection> {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.CREATED)
    }

    /**
     * Order by last updated date. If a program is provided, it takes the last updated of most recent enrollment.
     * Otherwise it takes the value of the tracked entity instance.
     *
     * @return Repository connector
     */
    fun orderByLastUpdated(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrderByDirection> {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.LAST_UPDATED)
    }

    /**
     * Order by tracked entity instance attribute value.
     *
     * @return Repository connector
     */
    fun orderByAttribute(
        attr: String,
    ): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrderByDirection> {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.attribute(attr))
    }

    /**
     * Order by organisation unit name of the tracked entity instance.
     *
     * @return Repository connector
     */
    fun orderByOrganisationUnitName(): EqFilterConnector<
        TrackedEntityInstanceQueryCollectionRepository,
        OrderByDirection,
        > {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.ORGUNIT_NAME)
    }

    /**
     * Order by enrollment date of most recent enrollment. This order only applies to local results.
     *
     * @return Repository connector
     */
    fun orderByEnrollmentDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrderByDirection> {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.ENROLLMENT_DATE)
    }

    /**
     * Order by incident date of most recent enrollment. This order only applies to local results.
     *
     * @return Repository connector
     */
    fun orderByIncidentDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrderByDirection> {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.INCIDENT_DATE)
    }

    /**
     * Order by most recent event. It takes the event date and, if it is null, it fallbacks to due date. This order
     * only applies to local results.
     *
     * @return Repository connector
     */
    fun orderByEventDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrderByDirection> {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.EVENT_DATE)
    }

    /**
     * Order by completion date of the most recent event. This order only applies to local results.
     *
     * @return Repository connector
     */
    fun orderByCompletedDate(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrderByDirection> {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.COMPLETION_DATE)
    }

    /**
     * Order by enrollment status.
     *
     * @return Repository connector
     */
    fun orderByEnrollmentStatus(): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrderByDirection> {
        return orderConnector(TrackedEntityInstanceQueryScopeOrderColumn.ENROLLMENT_STATUS)
    }

    @Deprecated("Use {@link #getPagingData()} instead}", replaceWith = ReplaceWith("getPagingData()"))
    override fun getPaged(pageSize: Int): LiveData<PagedList<TrackedEntityInstance>> {
        val factory: DataSource.Factory<TrackedEntityInstance, TrackedEntityInstance> =
            object : DataSource.Factory<TrackedEntityInstance, TrackedEntityInstance>() {
                override fun create(): DataSource<TrackedEntityInstance, TrackedEntityInstance> {
                    return dataSource
                }
            }
        return LivePagedListBuilder(factory, pageSize).build()
    }

    override fun getPagingData(pageSize: Int): Flow<PagingData<TrackedEntityInstance>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize),
        ) {
            pagingSource
        }.flow
    }

    val dataSource: DataSource<TrackedEntityInstance, TrackedEntityInstance>
        get() = TrackedEntityInstanceQueryDataSource(getDataFetcher())

    val pagingSource: PagingSource<TrackedEntityInstance, TrackedEntityInstance>
        get() = TrackeEntityInstanceQueryPagingSource(
            store,
            trackerParentCallFactory,
            scope,
            childrenAppenders,
            onlineCache,
            onlineHelper,
            localQueryHelper,
        )

    @Deprecated("use getPagingdata")
    val resultDataSource: DataSource<TrackedEntityInstance, Result<TrackedEntityInstance, D2Error>>
        get() = TrackedEntityInstanceQueryDataSourceResult(getDataFetcher())

    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
    override fun blockingGet(): List<TrackedEntityInstance> {
        val dataFetcher = getDataFetcher()
        val searchResult =
            if (scope.mode() == RepositoryMode.OFFLINE_ONLY || scope.mode() == RepositoryMode.OFFLINE_FIRST) {
                dataFetcher.queryAllOffline()
            } else {
                dataFetcher.queryAllOnline()
            }

        return searchResult.map {
            when (it) {
                is Result.Success -> it.value
                is Result.Failure -> throw it.failure
            }
        }
    }

    override fun get(): Single<List<TrackedEntityInstance>> {
        return Single.fromCallable { blockingGet() }
    }

    override fun count(): Single<Int> {
        return Single.fromCallable { blockingCount() }
    }

    override fun blockingCount(): Int {
        return blockingGet().size
    }

    override fun isEmpty(): Single<Boolean> {
        return Single.fromCallable { blockingIsEmpty() }
    }

    override fun blockingIsEmpty(): Boolean {
        return blockingCount() == 0
    }

    override fun one(): ReadOnlyObjectRepository<TrackedEntityInstance> {
        return objectRepository(
            object : Transformer<List<TrackedEntityInstance>, TrackedEntityInstance?> {
                override fun transform(o: List<TrackedEntityInstance>): TrackedEntityInstance? {
                    return o.firstOrNull()
                }
            },
        )
    }

    private fun orderConnector(
        col: TrackedEntityInstanceQueryScopeOrderColumn,
    ): EqFilterConnector<TrackedEntityInstanceQueryCollectionRepository, OrderByDirection> {
        return connectorFactory.eqConnector { direction: OrderByDirection? ->
            val order = TrackedEntityInstanceQueryScopeOrderByItem.builder().column(col).direction(direction).build()
            scope.toBuilder().order(scope.order() + order).build()
        }
    }

    private fun getDataFetcher(): TrackedEntityInstanceQueryDataFetcher {
        return TrackedEntityInstanceQueryDataFetcher(
            store,
            trackerParentCallFactory,
            scope,
            childrenAppenders,
            onlineCache,
            onlineHelper,
            localQueryHelper
        )
    }

    override fun uid(uid: String?): ReadOnlyObjectRepository<TrackedEntityInstance> {
        return objectRepository(
            object : Transformer<List<TrackedEntityInstance>, TrackedEntityInstance?> {
                override fun transform(o: List<TrackedEntityInstance>): TrackedEntityInstance? {
                    return o.find { uid == it.uid() }
                }
            },
        )
    }

    override fun getUids(): Single<List<String>> {
        return Single.fromCallable { blockingGetUids() }
    }

    override fun blockingGetUids(): List<String> {
        return if (scope.mode() == RepositoryMode.OFFLINE_ONLY || scope.mode() == RepositoryMode.OFFLINE_FIRST) {
            getDataFetcher().queryAllOfflineUids()
        } else {
            getUids(blockingGet()).toList()
        }
    }

    private fun objectRepository(
        transformer: Transformer<List<TrackedEntityInstance>, TrackedEntityInstance?>,
    ): ReadOnlyObjectRepository<TrackedEntityInstance> {
        return object : ReadOnlyObjectRepository<TrackedEntityInstance> {
            override fun get(): Single<TrackedEntityInstance?> {
                return Single.fromCallable { this.blockingGet() }
            }

            override fun blockingGet(): TrackedEntityInstance? {
                val list = this@TrackedEntityInstanceQueryCollectionRepository.blockingGet()
                return transformer.transform(list)
            }

            override fun exists(): Single<Boolean> {
                return Single.fromCallable { blockingExists() }
            }

            override fun blockingExists(): Boolean {
                return blockingGet() != null
            }
        }
    }
}
