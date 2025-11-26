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
package org.hisp.dhis.android.core.trackedentity.search

import androidx.paging.ItemKeyedDataSource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl.Companion.create
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityEndpointCallFactory
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TrackedEntityInstanceQueryDataSourceShould {
    private lateinit var singleEventFilterScope: TrackedEntityInstanceQueryRepositoryScope
    private lateinit var multipleEventFilterScope: TrackedEntityInstanceQueryRepositoryScope

    private val store: TrackedEntityInstanceStore = mock()
    private val trackerParentCallFactory: TrackerParentCallFactory = mock()
    private val onlineCallFactory: TrackedEntityEndpointCallFactory = mock()
    private val trackedEntity: TrackedEntityInstance = mock()

    private lateinit var offlineObjects: List<TrackedEntityInstance>
    private lateinit var onlineObjects1: List<TrackedEntityInstance>
    private lateinit var onlineObjects2: List<TrackedEntityInstance>

    var captureInstances: KArgumentCaptor<List<TrackedEntityInstance>> = argumentCaptor()

    private val childrenAppenders: ChildrenAppenderGetter<TrackedEntityInstance> = mock()

    private val initialCallback: ItemKeyedDataSource.LoadInitialCallback<TrackedEntityInstance> = mock()
    private val clockProvider = ClockProviderFactory.clockProvider
    private val periodHelper = DateFilterPeriodHelper(clockProvider, create(clockProvider))
    private val onlineHelper = TrackedEntityInstanceQueryOnlineHelper(periodHelper)
    private val localQueryHelper = TrackedEntityInstanceLocalQueryHelper(periodHelper)
    private val onlineCache: TrackedEntityInstanceOnlineCache =
        TrackedEntityInstanceOnlineCache(TimeUnit.MINUTES.toMillis(5))
    private val initialLoad = 30

    @Before
    fun setUp() = runTest {
        offlineObjects = listOf(
            TrackedEntityInstance.builder().uid("offline1").build(),
            TrackedEntityInstance.builder().uid("offline2").build(),
            TrackedEntityInstance.builder().uid("offline3").build(),
        )
        onlineObjects1 = listOf(
            TrackedEntityInstance.builder().uid("online1").build(),
            TrackedEntityInstance.builder().uid("offline2").build(),
            TrackedEntityInstance.builder().uid("online3").build(),
            TrackedEntityInstance.builder().uid("online4").build(),
            TrackedEntityInstance.builder().uid("online5").build(),
        )
        onlineObjects2 = listOf(
            TrackedEntityInstance.builder().uid("online5").build(),
            TrackedEntityInstance.builder().uid("online6").build(),
        )
        whenever(store.selectRawQuery(any())).doReturn(offlineObjects)

        singleEventFilterScope = emptyScopeWithModes(AssignedUserMode.ANY)
        multipleEventFilterScope = emptyScopeWithModes(AssignedUserMode.ANY, AssignedUserMode.CURRENT)

        whenever(trackerParentCallFactory.getTrackedEntityCall()).doReturn(onlineCallFactory)

        onlineCallFactory.stub {
            onBlocking { getQueryCall(argThat(QueryPageUserModeMatcher(1, initialLoad, AssignedUserMode.ANY))) }
                .doReturn(TrackerQueryResult(onlineObjects1, true))

            onBlocking { getQueryCall(argThat(QueryPageUserModeMatcher(2, initialLoad, AssignedUserMode.ANY))) }
                .doReturn(TrackerQueryResult(emptyList(), true))

            onBlocking { getQueryCall(argThat(QueryPageUserModeMatcher(4, 10, AssignedUserMode.ANY))) }
                .doReturn(TrackerQueryResult(emptyList(), true))

            onBlocking { getQueryCall(argThat(QueryPageUserModeMatcher(1, initialLoad, AssignedUserMode.CURRENT))) }
                .doReturn(TrackerQueryResult(onlineObjects2, true))

            onBlocking { getQueryCall(argThat(QueryPageUserModeMatcher(4, 10, AssignedUserMode.CURRENT))) }
                .doReturn(TrackerQueryResult(emptyList(), true))
        }

        whenever(childrenAppenders[anyString()]).doReturn { identityAppender() }
    }

    @Test
    fun get_initial_online_page() = runTest {
        val scope = singleEventFilterScope.toBuilder().mode(RepositoryMode.ONLINE_ONLY).build()
        val dataSource = TrackedEntityInstanceQueryDataSource(getDataFetcher(scope))
        dataSource.loadInitial(
            ItemKeyedDataSource.LoadInitialParams(null, initialLoad, false),
            initialCallback,
        )
        verify(onlineCallFactory)
            .getQueryCall(argThat(QueryPageUserModeMatcher(1, initialLoad, AssignedUserMode.ANY)))
        verify(initialCallback).onResult(onlineObjects1)
        verifyNoMoreInteractions(onlineCallFactory)
    }

    @Test
    fun get_initial_offline_page() = runTest {
        val scope = singleEventFilterScope.toBuilder().mode(RepositoryMode.OFFLINE_ONLY).build()
        val dataSource = TrackedEntityInstanceQueryDataSource(getDataFetcher(scope))
        dataSource.loadInitial(
            ItemKeyedDataSource.LoadInitialParams(null, initialLoad, false),
            initialCallback,
        )
        verify(store).selectRawQuery(anyString())
        verify(initialCallback).onResult(offlineObjects)
        verifyNoMoreInteractions(store)
    }

    @Test
    fun query_online_when_offline_exhausted() = runTest {
        val scope = singleEventFilterScope.toBuilder().mode(RepositoryMode.OFFLINE_FIRST).build()
        val dataSource = TrackedEntityInstanceQueryDataSource(getDataFetcher(scope))
        dataSource.loadInitial(
            ItemKeyedDataSource.LoadInitialParams(null, initialLoad, false),
            initialCallback,
        )
        verify(store).selectRawQuery(anyString())
        verifyNoMoreInteractions(store)
        verify(onlineCallFactory)
            .getQueryCall(argThat(QueryPageUserModeMatcher(1, initialLoad, AssignedUserMode.ANY)))
        verifyNoMoreInteractions(onlineCallFactory)
    }

    @Test
    fun query_online_again_if_not_exhausted_and_use_right_paging() = runTest {
        val scope = singleEventFilterScope.toBuilder().mode(RepositoryMode.OFFLINE_FIRST).build()
        val dataSource = TrackedEntityInstanceQueryDataSource(getDataFetcher(scope))

        onlineCallFactory.stub {
            onBlocking { getQueryCall(argThat(QueryPageUserModeMatcher(1, 4, AssignedUserMode.ANY))) }
                .doReturn(TrackerQueryResult(onlineObjects1, false))

            onBlocking { getQueryCall(argThat(QueryPageUserModeMatcher(3, 2, AssignedUserMode.ANY))) }
                .doReturn(TrackerQueryResult(emptyList(), true))
        }

        dataSource.loadInitial(
            ItemKeyedDataSource.LoadInitialParams(null, 4, false),
            initialCallback,
        )
        verify(store).selectRawQuery(anyString())
        verifyNoMoreInteractions(store)
        verify(onlineCallFactory)
            .getQueryCall(argThat(QueryPageUserModeMatcher(1, 4, AssignedUserMode.ANY)))
        dataSource.loadAfter(ItemKeyedDataSource.LoadParams(trackedEntity, 2), initialCallback)
        verify(onlineCallFactory)
            .getQueryCall(argThat(QueryPageUserModeMatcher(3, 2, AssignedUserMode.ANY)))
        verifyNoMoreInteractions(onlineCallFactory)
    }

    @Test
    fun get_initial_online_page_from_cache() = runTest {
        val scope = singleEventFilterScope.toBuilder().mode(RepositoryMode.ONLINE_ONLY).allowOnlineCache(true).build()
        val dataSource1 = TrackedEntityInstanceQueryDataSource(getDataFetcher(scope))
        dataSource1.loadInitial(
            ItemKeyedDataSource.LoadInitialParams(null, initialLoad, false),
            initialCallback,
        )
        verify(onlineCallFactory).getQueryCall(any())

        val dataSource2 = TrackedEntityInstanceQueryDataSource(getDataFetcher(scope))
        dataSource2.loadInitial(
            ItemKeyedDataSource.LoadInitialParams(null, initialLoad, false),
            initialCallback,
        )
        verifyNoMoreInteractions(onlineCallFactory)
    }

    @Test
    fun get_multiple_event_filter_queries() = runTest {
        val scope = multipleEventFilterScope.toBuilder().mode(RepositoryMode.OFFLINE_FIRST).build()
        val dataSource = TrackedEntityInstanceQueryDataSource(getDataFetcher(scope))
        dataSource.loadInitial(
            ItemKeyedDataSource.LoadInitialParams(null, initialLoad, false),
            initialCallback,
        )
        verify(store).selectRawQuery(anyString())
        verifyNoMoreInteractions(store)
        verify(onlineCallFactory).getQueryCall(argThat(QueryUserModeMatcher(AssignedUserMode.ANY)))
        verify(onlineCallFactory).getQueryCall(argThat(QueryUserModeMatcher(AssignedUserMode.CURRENT)))
        verifyNoMoreInteractions(onlineCallFactory)
        verify(initialCallback).onResult(
            captureInstances.capture(),
        )
        assertThat(captureInstances.firstValue.size).isEqualTo(8)
    }

    /*
     This test makes sense when the online call returns the number of values requested, but the response contains
     values already returned offline (duplicates), so it is needed to do a second query.
     */
    @Test
    fun get_second_online_page_if_needed_in_initial_load() = runTest {
        val scope = singleEventFilterScope.toBuilder().mode(RepositoryMode.OFFLINE_FIRST).build()
        val dataSource = TrackedEntityInstanceQueryDataSource(getDataFetcher(scope))

        onlineCallFactory.stub {
            onBlocking { getQueryCall(argThat(QueryPageUserModeMatcher(1, 5, AssignedUserMode.ANY))) }
                .doReturn(TrackerQueryResult(onlineObjects1, false))

            onBlocking { getQueryCall(argThat(QueryPageUserModeMatcher(2, 5, AssignedUserMode.ANY))) }
                .doReturn(TrackerQueryResult(emptyList(), true))
        }

        dataSource.loadInitial(
            ItemKeyedDataSource.LoadInitialParams(null, 5, false),
            initialCallback,
        )
        verify(store).selectRawQuery(anyString())
        verifyNoMoreInteractions(store)
        verify(onlineCallFactory)
            .getQueryCall(argThat(QueryPageUserModeMatcher(1, 5, AssignedUserMode.ANY)))
        verify(onlineCallFactory)
            .getQueryCall(argThat(QueryPageUserModeMatcher(2, 5, AssignedUserMode.ANY)))
        verifyNoMoreInteractions(onlineCallFactory)
    }

    private fun getDataFetcher(
        scope: TrackedEntityInstanceQueryRepositoryScope,
    ): TrackedEntityInstanceQueryDataFetcher {
        return TrackedEntityInstanceQueryDataFetcher(
            store,
            trackerParentCallFactory,
            scope,
            childrenAppenders,
            onlineCache,
            onlineHelper,
            localQueryHelper,
        )
    }

    private fun identityAppender(): ChildrenAppender<TrackedEntityInstance> {
        return object : ChildrenAppender<TrackedEntityInstance>() {
            override suspend fun appendChildren(m: TrackedEntityInstance): TrackedEntityInstance {
                return m
            }
        }
    }

    private fun emptyScopeWithModes(
        vararg assignedUserModes: AssignedUserMode,
    ): TrackedEntityInstanceQueryRepositoryScope {
        val eventFilters = assignedUserModes.map {
            TrackedEntityInstanceQueryEventFilter.builder().assignedUserMode(it).build()
        }
        return TrackedEntityInstanceQueryRepositoryScope.builder().eventFilters(eventFilters).build()
    }
}
