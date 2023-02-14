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

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.common.*
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.trackedentity.AttributeValueFilter
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TrackedEntityInstanceQueryRepositoryScopeHelperShould {

    private val filterUid = "filterUid"
    private val programId = "programId"
    private val programStage1 = "programStage1"
    private val programStage2 = "programStage2"
    private val enrollmentStatus = EnrollmentStatus.COMPLETED
    private val followUp = true

    private val dateFilterPeriodHelper: DateFilterPeriodHelper = mock()

    private lateinit var scopeHelper: TrackedEntityInstanceQueryRepositoryScopeHelper

    @Before
    fun setUp() {
        scopeHelper = TrackedEntityInstanceQueryRepositoryScopeHelper(dateFilterPeriodHelper)
    }

    @Test
    fun `Should parse first level properties`() {
        val scope = TrackedEntityInstanceQueryRepositoryScope.empty()

        val filter = TrackedEntityInstanceFilter.builder()
            .uid(filterUid)
            .program(ObjectWithUid.create(programId))
            .entityQueryCriteria(
                EntityQueryCriteria.builder()
                    .enrollmentStatus(enrollmentStatus)
                    .followUp(followUp)
                    .enrollmentCreatedDate(
                        DateFilterPeriod.builder()
                            .startBuffer(-2)
                            .endBuffer(5)
                            .build()
                    )
                    .build()
            )
            .build()

        val updatedScope = scopeHelper.addTrackedEntityInstanceFilter(scope, filter)

        assertThat(updatedScope.program()).isEqualTo(programId)
        assertThat(updatedScope.enrollmentStatus()).isEqualTo(listOf(enrollmentStatus))
        assertThat(updatedScope.followUp()).isEqualTo(followUp)

        assertThat(updatedScope.programDate()).isNotNull()
        val daysBetween = updatedScope.programDate()!!.endBuffer()!! - updatedScope.programDate()!!.startBuffer()!!
        assertThat(daysBetween).isEqualTo(7)
    }

    @Test
    fun `Should parse event filters`() {
        val scope = TrackedEntityInstanceQueryRepositoryScope.empty()

        val filter = TrackedEntityInstanceFilter.builder()
            .uid(filterUid)
            .eventFilters(
                listOf(
                    TrackedEntityInstanceEventFilter.builder()
                        .programStage(programStage1).eventStatus(EventStatus.ACTIVE).build(),
                    TrackedEntityInstanceEventFilter.builder()
                        .programStage(programStage2).assignedUserMode(AssignedUserMode.CURRENT)
                        .eventCreatedPeriod(FilterPeriod.create(-5, 2)).build()
                )
            )
            .entityQueryCriteria(EntityQueryCriteria.builder().build())
            .build()

        val updatedScope = scopeHelper.addTrackedEntityInstanceFilter(scope, filter)

        assertThat(updatedScope.eventFilters().size).isEqualTo(2)
        updatedScope.eventFilters().forEach {
            when (it.programStage()) {
                programStage1 ->
                    assertThat(it.eventStatus()).isEqualTo(listOf(EventStatus.ACTIVE))
                programStage2 -> {
                    assertThat(it.assignedUserMode()).isEqualTo(AssignedUserMode.CURRENT)
                    assertThat(it.eventDate()).isNotNull()
                    val daysBetween = it.eventDate()!!.endBuffer()!! - it.eventDate()!!.startBuffer()!!
                    assertThat(daysBetween).isEqualTo(7)
                }
                else -> fail("Unknown programStageId")
            }
        }
    }

    @Test
    fun `Should overwrite existing properties`() {
        val scope = TrackedEntityInstanceQueryRepositoryScope.builder()
            .program("existingProgramUid")
            .enrollmentStatus(listOf(EnrollmentStatus.ACTIVE))
            .eventFilters(
                listOf(
                    TrackedEntityInstanceQueryEventFilter.builder().assignedUserMode(AssignedUserMode.CURRENT).build()
                )
            )
            .build()

        val filter = TrackedEntityInstanceFilter.builder()
            .uid(filterUid)
            .program(ObjectWithUid.create(programId))
            .eventFilters(
                listOf(
                    TrackedEntityInstanceEventFilter.builder().assignedUserMode(AssignedUserMode.ANY).build()
                )
            )
            .entityQueryCriteria(EntityQueryCriteria.builder().build())
            .build()

        val updatedScope = scopeHelper.addTrackedEntityInstanceFilter(scope, filter)

        assertThat(updatedScope.program()).isEqualTo(programId)
        assertThat(updatedScope.enrollmentStatus()).isEqualTo(listOf(EnrollmentStatus.ACTIVE))
        assertThat(updatedScope.eventFilters().size).isEqualTo(1)
        assertThat(updatedScope.eventFilters()[0].assignedUserMode()).isEqualTo(AssignedUserMode.ANY)
    }

    @Test
    fun `Should parse attribute filter values`() {
        val scope = TrackedEntityInstanceQueryRepositoryScope.empty()

        val filter = TrackedEntityInstanceFilter.builder()
            .uid(filterUid)
            .program(ObjectWithUid.create(programId))
            .entityQueryCriteria(
                EntityQueryCriteria.builder()
                    .attributeValueFilters(
                        listOf(
                            AttributeValueFilter.builder()
                                .attribute("attribute1")
                                .like("like_str")
                                .build(),
                            AttributeValueFilter.builder()
                                .attribute("attribute2")
                                .eq("eq_str")
                                .build(),
                        )
                    )
                    .build()
            )
            .build()

        val updatedScope = scopeHelper.addTrackedEntityInstanceFilter(scope, filter)

        val filters = updatedScope.filter()
        assertThat(filters.size).isEqualTo(2)
        assertThat(
            filters.any {
                it.operator() == FilterItemOperator.LIKE && it.key() == "attribute1" && it.value() == "like_str"
            }
        ).isTrue()
        assertThat(
            filters.any {
                it.operator() == FilterItemOperator.EQ && it.key() == "attribute2" && it.value() == "eq_str"
            }
        ).isTrue()
    }
}
