/*
 *  Copyright (c) 2004-2026, University of Oslo
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
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyOneObjectRepositoryFinalImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingList
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListCollectionRepository
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterCollectionRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class TrackedEntitySearchOperatorsShould {

    private val scopeHelper: TrackedEntityInstanceQueryRepositoryScopeHelper = mock()
    private val versionManager: DHISVersionManager = mock()
    private val filtersRepository: TrackedEntityInstanceFilterCollectionRepository = mock()
    private val workingListRepository: ProgramStageWorkingListCollectionRepository = mock()

    private val emptyScope = TrackedEntityInstanceQueryRepositoryScope.empty()
    private val helperScope = TrackedEntityInstanceQueryRepositoryScope.builder().program("helperProgram").build()

    private val startDate = DateUtils.DATE_FORMAT.parse("2021-01-10T00:00:00.000")
    private val endDate = DateUtils.DATE_FORMAT.parse("2021-01-20T00:00:00.000")

    private lateinit var repository: TrackedEntitySearchCollectionRepository

    @Before
    fun setUp() {
        repository = TrackedEntitySearchCollectionRepository(
            mock(),
            mock(),
            emptyScope,
            scopeHelper,
            versionManager,
            filtersRepository,
            workingListRepository,
            mock(),
            mock(),
            mock(),
            mock(),
        )
    }

    @Test
    fun `Should set repository modes`() {
        assertThat(repository.onlineOnly().scope.mode()).isEqualTo(RepositoryMode.ONLINE_ONLY)
        assertThat(repository.offlineOnly().scope.mode()).isEqualTo(RepositoryMode.OFFLINE_ONLY)
        assertThat(repository.onlineFirst().scope.mode()).isEqualTo(RepositoryMode.ONLINE_FIRST)
        assertThat(repository.offlineFirst().scope.mode()).isEqualTo(RepositoryMode.OFFLINE_FIRST)
    }

    @Test
    fun `Should add attribute filters through scope helper`() {
        whenever(scopeHelper.addFilter(any(), any())) doReturn helperScope

        val scope = repository.byFilter("attribute").eq("value").scope

        assertThat(scope).isEqualTo(helperScope)
        verify(scopeHelper).addFilter(emptyScope, filterItem("attribute", FilterItemOperator.EQ, "value"))
    }

    @Test
    fun `Should add attribute filters through deprecated byAttribute`() {
        whenever(scopeHelper.addFilter(any(), any())) doReturn helperScope

        @Suppress("DEPRECATION")
        val scope = repository.byAttribute("attribute").like("value").scope

        assertThat(scope).isEqualTo(helperScope)
        verify(scopeHelper).addFilter(emptyScope, filterItem("attribute", FilterItemOperator.LIKE, "value"))
    }

    @Test
    fun `Should set query`() {
        @Suppress("DEPRECATION")
        val scope = repository.byQuery().eq("value").scope

        assertThat(scope.query()).isEqualTo(filterItem("", FilterItemOperator.EQ, "value"))
    }

    @Test
    fun `Should append data value filters`() {
        val scope = repository
            .byDataValue("de1").eq("5")
            .byDataValue("de2").like("abc")
            .scope

        assertThat(scope.dataValue()).containsExactly(
            filterItem("de1", FilterItemOperator.EQ, "5"),
            filterItem("de2", FilterItemOperator.LIKE, "abc"),
        ).inOrder()
    }

    @Test
    fun `Should set filter connectors`() {
        val scope = repository
            .byProgram().eq("program")
            .byProgramStage().eq("programStage")
            .byOrgUnits().`in`("ou1", "ou2")
            .byOrgUnitMode().eq(OrganisationUnitMode.DESCENDANTS)
            .byEnrollmentStatus().`in`(EnrollmentStatus.ACTIVE, EnrollmentStatus.COMPLETED)
            .byEventStatus().`in`(EventStatus.ACTIVE)
            .byTrackedEntityType().eq("tet")
            .byIncludeDeleted().eq(true)
            .byFollowUp().isTrue
            .byTrackedEntities().`in`("tei1", "tei2")
            .allowOnlineCache().eq(true)
            .excludeUids().`in`("tei3", "tei3", "tei4")
            .scope

        assertThat(scope.program()).isEqualTo("program")
        assertThat(scope.programStage()).isEqualTo("programStage")
        assertThat(scope.orgUnits()).containsExactly("ou1", "ou2").inOrder()
        assertThat(scope.orgUnitMode()).isEqualTo(OrganisationUnitMode.DESCENDANTS)
        assertThat(scope.enrollmentStatus())
            .containsExactly(EnrollmentStatus.ACTIVE, EnrollmentStatus.COMPLETED).inOrder()
        assertThat(scope.eventStatus()).containsExactly(EventStatus.ACTIVE)
        assertThat(scope.trackedEntityType()).isEqualTo("tet")
        assertThat(scope.includeDeleted()).isTrue()
        assertThat(scope.followUp()).isTrue()
        assertThat(scope.uids()).containsExactly("tei1", "tei2").inOrder()
        assertThat(scope.allowOnlineCache()).isTrue()
        assertThat(scope.excludedUids()).containsExactly("tei3", "tei4")
    }

    @Test
    fun `Should force offline only mode when filtering by states`() {
        val scope = repository
            .onlineOnly()
            .byStates().`in`(State.TO_POST, State.TO_UPDATE)
            .scope

        assertThat(scope.states()).containsExactly(State.TO_POST, State.TO_UPDATE).inOrder()
        assertThat(scope.mode()).isEqualTo(RepositoryMode.OFFLINE_ONLY)
    }

    @Test
    fun `Should keep scope when includeDeleted or allowOnlineCache are null`() {
        val scope = repository
            .byIncludeDeleted().eq(null)
            .allowOnlineCache().eq(null)
            .scope

        assertThat(scope).isEqualTo(emptyScope)
    }

    @Test
    fun `Should merge programDate, incidentDate, eventDate and lastUpdatedDate periods`() {
        val scope = repository
            .byProgramDate().afterOrEqual(startDate)
            .byProgramDate().beforeOrEqual(endDate)
            .byIncidentDate().afterOrEqual(startDate)
            .byIncidentDate().beforeOrEqual(endDate)
            .byEventDate().afterOrEqual(startDate)
            .byEventDate().beforeOrEqual(endDate)
            .byLastUpdatedDate().afterOrEqual(startDate)
            .byLastUpdatedDate().beforeOrEqual(endDate)
            .scope

        listOf(scope.programDate(), scope.incidentDate(), scope.eventDate(), scope.lastUpdatedDate()).forEach {
            assertThat(it?.startDate()).isEqualTo(startDate)
            assertThat(it?.endDate()).isEqualTo(endDate)
        }
    }

    @Test
    fun `Should set relative period`() {
        val scope = repository.byEventDate().inPeriod(RelativePeriod.LAST_7_DAYS).scope

        assertThat(scope.eventDate()?.period()).isEqualTo(RelativePeriod.LAST_7_DAYS)
    }

    @Test
    fun `Should set assignedUserMode if version is greater than 2_31`() {
        whenever(versionManager.isGreaterThan(DHISVersion.V2_31)) doReturn true

        val scope = repository.byAssignedUserMode().eq(AssignedUserMode.CURRENT).scope

        assertThat(scope.assignedUserMode()).isEqualTo(AssignedUserMode.CURRENT)
    }

    @Test
    fun `Should ignore assignedUserMode if version is not greater than 2_31`() {
        whenever(versionManager.isGreaterThan(DHISVersion.V2_31)) doReturn false

        val scope = repository.byAssignedUserMode().eq(AssignedUserMode.CURRENT).scope

        assertThat(scope.assignedUserMode()).isNull()
    }

    @Test
    fun `Should apply tracked entity instance filter by uid`() {
        val filter: TrackedEntityInstanceFilter = mock()
        val withEventFilters: TrackedEntityInstanceFilterCollectionRepository = mock()
        val withAttributeFilters: TrackedEntityInstanceFilterCollectionRepository = mock()
        val filterObjectRepository: ReadOnlyOneObjectRepositoryFinalImpl<TrackedEntityInstanceFilter> = mock {
            on { blockingGet() } doReturn filter
        }
        whenever(filtersRepository.withTrackedEntityInstanceEventFilters()) doReturn withEventFilters
        whenever(withEventFilters.withAttributeValueFilters()) doReturn withAttributeFilters
        whenever(withAttributeFilters.uid("filter")) doReturn filterObjectRepository
        whenever(scopeHelper.addTrackedEntityInstanceFilter(emptyScope, filter)) doReturn helperScope

        val scope = repository.byTrackedEntityInstanceFilter().eq("filter").scope

        assertThat(scope).isEqualTo(helperScope)
    }

    @Test
    fun `Should apply tracked entity instance filter object`() {
        val filter: TrackedEntityInstanceFilter = mock()
        whenever(scopeHelper.addTrackedEntityInstanceFilter(emptyScope, filter)) doReturn helperScope

        val scope = repository.byTrackedEntityInstanceFilterObject().eq(filter).scope

        assertThat(scope).isEqualTo(helperScope)
    }

    @Test
    fun `Should apply program stage working list by uid`() {
        val workingList: ProgramStageWorkingList = mock()
        val withDataFilters: ProgramStageWorkingListCollectionRepository = mock()
        val withAttributeFilters: ProgramStageWorkingListCollectionRepository = mock()
        val workingListObjectRepository: ReadOnlyOneObjectRepositoryFinalImpl<ProgramStageWorkingList> = mock {
            on { blockingGet() } doReturn workingList
        }
        whenever(workingListRepository.withDataFilters()) doReturn withDataFilters
        whenever(withDataFilters.withAttributeValueFilters()) doReturn withAttributeFilters
        whenever(withAttributeFilters.uid("workingList")) doReturn workingListObjectRepository
        whenever(scopeHelper.addProgramStageWorkingList(emptyScope, workingList)) doReturn helperScope

        val scope = repository.byProgramStageWorkingList().eq("workingList").scope

        assertThat(scope).isEqualTo(helperScope)
    }

    @Test
    fun `Should apply program stage working list object`() {
        val workingList: ProgramStageWorkingList = mock()
        whenever(scopeHelper.addProgramStageWorkingList(emptyScope, workingList)) doReturn helperScope

        val scope = repository.byProgramStageWorkingListObject().eq(workingList).scope

        assertThat(scope).isEqualTo(helperScope)
    }

    @Test
    fun `Should concat every order column`() {
        val asc = RepositoryScope.OrderByDirection.ASC
        val desc = RepositoryScope.OrderByDirection.DESC
        val scope = repository
            .orderByCreated().eq(asc)
            .orderByLastUpdated().eq(desc)
            .orderByAttribute("attribute").eq(asc)
            .orderByOrganisationUnitName().eq(desc)
            .orderByEnrollmentDate().eq(asc)
            .orderByIncidentDate().eq(desc)
            .orderByEventDate().eq(asc)
            .orderByCompletedDate().eq(desc)
            .orderByEnrollmentStatus().eq(asc)
            .scope

        assertThat(scope.order().map { it.column().type() }).containsExactly(
            TrackedEntityInstanceQueryScopeOrderColumn.Type.CREATED,
            TrackedEntityInstanceQueryScopeOrderColumn.Type.LAST_UPDATED,
            TrackedEntityInstanceQueryScopeOrderColumn.Type.ATTRIBUTE,
            TrackedEntityInstanceQueryScopeOrderColumn.Type.ORGUNIT_NAME,
            TrackedEntityInstanceQueryScopeOrderColumn.Type.ENROLLMENT_DATE,
            TrackedEntityInstanceQueryScopeOrderColumn.Type.INCIDENT_DATE,
            TrackedEntityInstanceQueryScopeOrderColumn.Type.EVENT_DATE,
            TrackedEntityInstanceQueryScopeOrderColumn.Type.COMPLETION_DATE,
            TrackedEntityInstanceQueryScopeOrderColumn.Type.ENROLLMENT_STATUS,
        ).inOrder()
        assertThat(scope.order().map { it.direction() })
            .containsExactly(asc, desc, asc, desc, asc, desc, asc, desc, asc).inOrder()
        assertThat(scope.order()[2].column().value()).isEqualTo("attribute")
    }

    @Test
    fun `Should ignore null order direction`() {
        val scope = repository.orderByCreated().eq(null).scope

        assertThat(scope.order()).isEmpty()
    }

    private fun filterItem(key: String, operator: FilterItemOperator, value: String): RepositoryScopeFilterItem =
        RepositoryScopeFilterItem.builder().key(key).operator(operator).value(value).build()
}
