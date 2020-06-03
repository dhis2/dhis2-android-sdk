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
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramType
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
    fun count_events_by_condition_on_tracked_entity_data_values_and_status() {
        val dataElements = d2.dataElementModule().dataElements()
                .byDomainType().eq("TRACKER")
                .blockingGet()
        val de0 = dataElements[0]
        val de1 = dataElements[1]
        val tedv0 = d2.trackedEntityModule().trackedEntityDataValues()
                .byDataElement().eq(de0.uid())
                .byValue().like("a")
                .blockingGet()
        val tedv1 = d2.trackedEntityModule().trackedEntityDataValues()
                .byDataElement().eq(de1.uid())
                .byValue().like("b")
                .blockingGet()
        val eventUidsCond0 = tedv0.map { it.event() }
        val eventUidsCond1 = tedv1.map { it.event() }
        val eventUids = eventUidsCond0.intersect(eventUidsCond1)
        val eventsCount = d2.eventModule().events()
                .byUid().`in`(eventUids)
                .byStatus().eq(EventStatus.ACTIVE)
                .blockingCount()
        assertThat(eventsCount).isAtLeast(1)
    }

    @Test
    fun count_teis_by_condition_on_tracked_entity_attribute_values() {
        val attributes = d2.trackedEntityModule().trackedEntityAttributes()
                .blockingGet()
        val at0 = attributes[0]
        val at1 = attributes[1]
        val atv0 = d2.trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityAttribute().eq(at0.uid())
                .byValue().like("a")
                .blockingGet()
        val atv1 = d2.trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityAttribute().eq(at1.uid())
                .byValue().like("b")
                .blockingGet()
        val teiUidsCond0 = atv0.map { it.trackedEntityInstance() }
        val teiUidsCond1 = atv1.map { it.trackedEntityInstance() }
        val teiUids = teiUidsCond0.intersect(teiUidsCond1)
        val teiCount = d2.trackedEntityModule().trackedEntityInstances()
                .byUid().`in`(teiUids)
                .blockingCount()
        assertThat(teiCount).isAtLeast(1)
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
}