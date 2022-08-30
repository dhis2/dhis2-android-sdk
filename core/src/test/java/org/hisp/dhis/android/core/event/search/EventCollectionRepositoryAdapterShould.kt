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
import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.user.AuthenticatedUserObjectRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class EventCollectionRepositoryAdapterShould {

    private val eventRepository: EventCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val ouRepository: OrganisationUnitCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val userRepository: AuthenticatedUserObjectRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val dateFilterPeriodHelper: DateFilterPeriodHelper = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)

    private val orgunit = "ou"
    private val orgunitChildren = listOf("ou1", "ou2") + orgunit
    private val orgunitDescendants = orgunitChildren + listOf("ou3")

    private lateinit var adapter: EventCollectionRepositoryAdapter

    @Before
    fun setUp() {
        adapter = EventCollectionRepositoryAdapter(
            eventRepository,
            ouRepository,
            userRepository,
            dateFilterPeriodHelper
        )

        whenever(ouRepository.blockingGetUids()) doReturn orgunitDescendants
        whenever(ouRepository.byPath().like(orgunit).blockingGetUids()) doReturn orgunitDescendants
        whenever(ouRepository.byParentUid().like(orgunit).blockingGetUids()) doReturn orgunitChildren
    }

    @Test
    fun `Should get null if orgUnit is null and mode is SELECTED`() {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnitMode(OrganisationUnitMode.SELECTED)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isNull()
    }

    @Test
    fun `Should get selected orgunit`() {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnits(listOf(orgunit))
            .orgUnitMode(OrganisationUnitMode.SELECTED)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isEqualTo(listOf(orgunit))
    }

    @Test
    fun `Should get all if ALL mode`() {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnitMode(OrganisationUnitMode.ALL)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isEqualTo(orgunitDescendants)
    }

    @Test
    fun `Should get descendants if DESCENDANTS mode`() {
        val scope = EventQueryRepositoryScope.builder()
            .orgUnits(listOf(orgunit))
            .orgUnitMode(OrganisationUnitMode.DESCENDANTS)
            .build()

        val orgunitList = adapter.getOrganisationUnits(scope)
        assertThat(orgunitList).isEqualTo(orgunitDescendants)
    }

    @Test
    fun `Should apply and concat sort orders`() {
        val scope = EventQueryRepositoryScope.builder()
            .order(
                listOf(
                    EventQueryScopeOrderByItem.builder()
                        .column(EventQueryScopeOrderColumn.EVENT_DATE)
                        .direction(RepositoryScope.OrderByDirection.ASC)
                        .build(),
                    EventQueryScopeOrderByItem.builder()
                        .column(EventQueryScopeOrderColumn.COMPLETED_DATE)
                        .direction(RepositoryScope.OrderByDirection.DESC)
                        .build()
                )
            ).build()

        val intermediateRepository: EventCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
        whenever(eventRepository.orderByEventDate(any())) doReturn intermediateRepository

        adapter.getCollectionRepository(scope)

        verify(eventRepository).orderByEventDate(RepositoryScope.OrderByDirection.ASC)
        verify(intermediateRepository).orderByCompleteDate(RepositoryScope.OrderByDirection.DESC)
    }
}
