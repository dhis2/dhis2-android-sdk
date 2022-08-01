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

package org.hisp.dhis.android.core.event.search

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.DatePeriodType
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.event.EventFilterCollectionRepository
import org.junit.Before
import org.junit.Test

class EventQueryCollectionRepositoryShould {

    private val adapter: EventCollectionRepositoryAdapter = mock()
    private val filterRepository: EventFilterCollectionRepository = mock()

    private lateinit var queryRepository: EventQueryCollectionRepository

    @Before
    fun setUp() {
        val emptyScope = EventQueryRepositoryScope.empty()
        queryRepository = EventQueryCollectionRepository(adapter, filterRepository, emptyScope)
    }

    @Test
    fun `Should create scope with event date`() {
        val startDate = DateUtils.DATE_FORMAT.parse("2021-01-20T00:00:00.000")
        val endDate = DateUtils.DATE_FORMAT.parse("2021-01-29T00:00:00.000")

        val scope = queryRepository
            .byEventDate().afterOrEqual(startDate)
            .byEventDate().beforeOrEqual(endDate)
            .scope

        assertThat(scope.eventDate()?.startDate()).isEqualTo(startDate)
        assertThat(scope.eventDate()?.endDate()).isEqualTo(endDate)
    }

    @Test
    fun `Should create scope with relative event date`() {
        val scope = queryRepository
            .byEventDate().inPeriod(RelativePeriod.LAST_3_DAYS)
            .scope

        assertThat(scope.eventDate()?.period()).isEqualTo(RelativePeriod.LAST_3_DAYS)
        assertThat(scope.eventDate()?.type()).isEqualTo(DatePeriodType.RELATIVE)
    }

    @Test
    fun `Should concat sort orders`() {
        val scope = queryRepository
            .orderByEventDate().eq(RepositoryScope.OrderByDirection.ASC)
            .orderByLastUpdated().eq(RepositoryScope.OrderByDirection.DESC)
            .scope

        assertThat(scope.order().size).isEqualTo(2)
        assertThat(scope.order().first().column()).isEqualTo(EventQueryScopeOrderColumn.EVENT_DATE)
        assertThat(scope.order().first().direction()).isEqualTo(RepositoryScope.OrderByDirection.ASC)
        assertThat(scope.order().last().column()).isEqualTo(EventQueryScopeOrderColumn.LAST_UPDATED)
        assertThat(scope.order().last().direction()).isEqualTo(RepositoryScope.OrderByDirection.DESC)
    }
}
