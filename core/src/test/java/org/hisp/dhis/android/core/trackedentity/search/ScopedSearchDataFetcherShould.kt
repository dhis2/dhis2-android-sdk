/*
 *  Copyright (c) 2004-2025, University of Oslo
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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl.Companion.create
import org.hisp.dhis.android.core.period.internal.RelativePeriodHelperMock
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackerParentCallFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import java.util.concurrent.TimeUnit

/**
 * Regression tests for the interaction between a search grant and the online query resolution.
 *
 * A scoped search is forced offline, and `TrackedEntityInstanceQueryOnlineHelper.fromScope` refuses a
 * granted scope outright — an online search is answered by the server, where none of the grant's
 * local restrictions apply. Those two facts only compose if the online queries are resolved lazily:
 * resolving them in the constructor made the refusal fire for *every* scoped search, so a granted
 * caller could not search at all and the guard rejected precisely the case it was meant to allow.
 */
@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ScopedSearchDataFetcherShould {

    private val store: TrackedEntityInstanceStore = mock()
    private val trackerParentCallFactory: TrackerParentCallFactory = mock()
    private val childrenAppenders: ChildrenAppenderGetter<TrackedEntityInstance> = mock()

    private val clockProvider = ClockProviderFactory.clockProvider
    private val periodHelper = DateFilterPeriodHelper(clockProvider, create(clockProvider, RelativePeriodHelperMock()))
    private val onlineHelper = TrackedEntityInstanceQueryOnlineHelper(periodHelper)
    private val localQueryHelper = TrackedEntityInstanceLocalQueryHelper(periodHelper)
    private val onlineCache = TrackedEntityInstanceOnlineCache(TimeUnit.MINUTES.toMillis(5))

    private val grantedScope = TrackedEntityInstanceQueryRepositoryScope.empty()
        .toBuilder()
        .mandatory(
            TrackedEntityQueryGrant(
                programs = setOf("IpHINAT79UW"),
                orgUnits = null,
                trackedEntityTypes = null,
            ),
        )
        .build()
        .applyGrant()

    private fun fetcher(scope: TrackedEntityInstanceQueryRepositoryScope) =
        TrackedEntityInstanceQueryDataFetcher(
            store,
            trackerParentCallFactory,
            scope,
            childrenAppenders,
            onlineCache,
            onlineHelper,
            localQueryHelper,
        )

    @Test
    fun `force a granted scope offline`() {
        assertThat(grantedScope.mode()).isEqualTo(RepositoryMode.OFFLINE_ONLY)
    }

    @Test
    fun `answer an offline search on a granted scope without consulting the online helper`() = runTest {
        store.stub { onBlocking { selectRawQuery(any()) } doReturn emptyList() }

        // Constructing and querying must both stay clear of fromScope. Before this was lazy, merely
        // building the fetcher threw SCOPE_VIOLATION and no scoped search could run at all.
        val result = fetcher(grantedScope).queryAllOffline()

        assertThat(result).isEmpty()
    }

    @Test
    fun `refuse an online search on a granted scope`() = runTest {
        // The guard is still reachable, just no longer on the offline path: getting here means the
        // OFFLINE_ONLY invariant was bypassed rather than that the caller asked for something odd.
        val onlineScope = grantedScope.toBuilder().mode(RepositoryMode.ONLINE_ONLY).build()

        val error = runCatching { fetcher(onlineScope).queryAllOnline() }.exceptionOrNull()

        assertThat(error).isInstanceOf(D2Error::class.java)
        assertThat((error as D2Error).errorCode()).isEqualTo(D2ErrorCode.SCOPE_VIOLATION)
    }
}
