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
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestLocalAnalyticsDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

//@Ignore("Tests for local analytics. Only to be executed on demand")
@RunWith(D2JunitRunner::class)
internal class LocalAnalyticsTrackerEventsMockIntegrationShould : BaseMockIntegrationTestLocalAnalyticsDispatcher() {

    @Test
    fun count_teis() {
        val teisCount = d2.trackedEntityModule().trackedEntityInstances().blockingCount()
        assertThat(teisCount).isEqualTo(3000)
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

    private fun getProgramEnrollmentsRepository(): EnrollmentCollectionRepository {
        val program = d2.programModule().programs()
                .byProgramType().eq(ProgramType.WITH_REGISTRATION)
                .one()
                .blockingGet()
        return d2.enrollmentModule().enrollments()
                .byProgram().eq(program.uid())
    }
}