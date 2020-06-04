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
package org.hisp.dhis.android.localanalytics

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.enrollment.EnrollmentCollectionRepository
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestLocalAnalyticsDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

//@Ignore("Tests for local analytics. Only to be executed on demand")
@RunWith(D2JunitRunner::class)
internal class LocalAnalyticsTrackerEventsMockIntegrationShould : BaseMockIntegrationTestLocalAnalyticsDispatcher() {

    @Test
    fun count_events() {
        val eventsCount = d2.eventModule().events()
                .blockingCount()
        assertThat(eventsCount).isEqualTo(12000)
    }

    @Test
    fun count_events_for_program_with_registration() {
        val eventsCount = d2.eventModule().events()
                .byProgramUid().eq(getProgramWithRegistration().uid())
                .blockingCount()
        assertThat(eventsCount).isEqualTo(9000)
    }

    @Test
    fun count_events_for_program_without_registration() {
        val eventsCount = d2.eventModule().events()
                .byProgramUid().eq(getProgramWithoutRegistration().uid())
                .blockingCount()
        assertThat(eventsCount).isEqualTo(3000)
    }

    @Test
    fun count_enrollments_by_program() {
        val enrollmentsCount = getProgramEnrollmentsRepository()
                .blockingCount()
        assertThat(enrollmentsCount).isEqualTo(3000)
    }

    @Test
    fun count_enrollments_by_program_and_status() {
        val enrollmentsCount = getProgramEnrollmentsRepository()
                .byStatus().eq(EnrollmentStatus.ACTIVE)
                .blockingCount()
        assertThat(enrollmentsCount).isEqualTo(3000)
    }

    @Test
    fun count_teis() {
        val teisCount = d2.trackedEntityModule().trackedEntityInstances().blockingCount()
        assertThat(teisCount).isEqualTo(3000)
    }

    @Test
    fun count_events_by_condition_on_1_tracked_entity_data_value_and_status() {
        val eventsCount = getEventRepositoryByTEDV(1).byStatus().eq(EventStatus.ACTIVE)
                .blockingCount()
        assertThat(eventsCount).isAtLeast(1)
    }

    @Test
    fun count_events_by_condition_on_2_tracked_entity_data_values_and_status() {
        val eventsCount = getEventRepositoryByTEDV(2).byStatus().eq(EventStatus.ACTIVE)
                .blockingCount()
        assertThat(eventsCount).isAtLeast(1)
    }

    @Test
    fun count_events_by_condition_on_3_tracked_entity_data_values_and_status() {
        val eventsCount = getEventRepositoryByTEDV(3).byStatus().eq(EventStatus.ACTIVE)
                .blockingCount()
        assertThat(eventsCount).isAtLeast(1)
    }

    @Test
    fun count_teis_by_condition_on_1_tracked_entity_attribute_value() {
        assertThat(getTeiRepositoryByTEAV(1).blockingCount()).isAtLeast(1)
    }

    @Test
    fun count_teis_by_condition_on_2_tracked_entity_attribute_values() {
        assertThat(getTeiRepositoryByTEAV(2).blockingCount()).isAtLeast(1)
    }

    @Test
    fun count_teis_by_condition_on_3_tracked_entity_attribute_values() {
        assertThat(getTeiRepositoryByTEAV(3).blockingCount()).isAtLeast(1)
    }

    @Test
    fun count_teis_by_condition_on_1_tracked_entity_data_value() {
        assertThat(countTeisByConditionOnTEDV(1)).isAtLeast(1)
    }

    @Test
    fun count_teis_by_condition_on_2_tracked_entity_data_values() {
        assertThat(countTeisByConditionOnTEDV(2)).isAtLeast(1)
    }

    @Test
    fun count_teis_by_condition_on_3_tracked_entity_data_values() {
        assertThat(countTeisByConditionOnTEDV(3)).isAtLeast(1)
    }

    @Test
    fun count_tedv_for_a_data_element() {
        val firstTedv = d2.trackedEntityModule().trackedEntityDataValues()
                .one().blockingGet()
        val tedvCount = d2.trackedEntityModule().trackedEntityDataValues()
                .byDataElement().eq(firstTedv.dataElement())
                .blockingCount()
        assertThat(tedvCount).isEqualTo(12000)
    }

    @Test
    fun aggregate_tedv_for_a_data_element() {
        val firstTedv = d2.trackedEntityModule().trackedEntityDataValues()
                .one().blockingGet()
        val tedv = d2.trackedEntityModule().trackedEntityDataValues()
                .byDataElement().eq(firstTedv.dataElement())
                .blockingGet()
        val aggregatedTedv = tedv.sumBy { v -> v.value()!!.count { it == 'a' } }
        assertThat(aggregatedTedv).isAtLeast(1)
    }

    private fun countTeisByConditionOnTEDV(tedvCount: Int): Int {
        val events = getEventRepositoryByTEDV(tedvCount)
                .byEnrollmentUid().isNotNull
                .blockingGet()
        val enrollmentUids = events.groupBy { it.enrollment() }.keys

        val enrollments = d2.enrollmentModule().enrollments()
                .byUid().`in`(enrollmentUids)
                .blockingGet()

        return enrollments.groupBy { it.trackedEntityInstance() }.size
    }

    private fun getProgramEnrollmentsRepository(): EnrollmentCollectionRepository {
        return d2.enrollmentModule().enrollments()
                .byProgram().eq(getProgramWithRegistration().uid())
    }

    private fun getProgramWithRegistration(): Program {
        return d2.programModule().programs()
                .byProgramType().eq(ProgramType.WITH_REGISTRATION)
                .one()
                .blockingGet()
    }

    private fun getProgramWithoutRegistration(): Program {
        return d2.programModule().programs()
                .byProgramType().eq(ProgramType.WITHOUT_REGISTRATION)
                .one()
                .blockingGet()
    }

    private fun getEventRepositoryByTEDV(tedvCount: Int): EventCollectionRepository {
        val dataElements = d2.dataElementModule().dataElements()
                .byDomainType().eq("TRACKER")
                .blockingGet()
        val eventsList = (0 until tedvCount).flatMap { i ->
            val de = dataElements[i]
            val tedv = d2.trackedEntityModule().trackedEntityDataValues()
                    .byDataElement().eq(de.uid())
                    .byValue().like("a")
                    .blockingGet()
            tedv.map { it.event() }
        }

        return d2.eventModule().events()
                .byUid().`in`(eventsList.toSet())
                .byStatus().eq(EventStatus.ACTIVE)
    }

    private fun getTeiRepositoryByTEAV(teavCount: Int): TrackedEntityInstanceCollectionRepository {
        val attributes = d2.trackedEntityModule().trackedEntityAttributes()
                .blockingGet()
        val teisList = (0 until teavCount).flatMap {  i ->
            val tea = attributes[i]
            val teav = d2.trackedEntityModule().trackedEntityAttributeValues()
                    .byTrackedEntityAttribute().eq(tea.uid())
                    .byValue().like("a")
                    .blockingGet()
            teav.map { it.trackedEntityInstance() }
        }

        return d2.trackedEntityModule().trackedEntityInstances()
                .byUid().`in`(teisList.toSet())
    }
}