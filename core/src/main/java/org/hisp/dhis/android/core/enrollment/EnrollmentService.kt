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
package org.hisp.dhis.android.core.enrollment

import dagger.Reusable
import io.reactivex.Single
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository
import javax.inject.Inject

@Reusable
class EnrollmentService @Inject constructor(
        private val enrollmentRepository: EnrollmentCollectionRepository,
        private val trackedEntityInstanceRepository: TrackedEntityInstanceCollectionRepository,
        private val programRepository: ProgramCollectionRepository,
        private val organisationUnitRepository: OrganisationUnitCollectionRepository
) {

    fun blockingIsOpen(enrollmentUid: String): Boolean {
        val enrollment = enrollmentRepository.uid(enrollmentUid).blockingGet() ?: return true

        return enrollment.status()?.equals(EnrollmentStatus.ACTIVE) ?: false
    }

    fun isOpen(enrollmentUid: String): Single<Boolean> {
        return Single.fromCallable { blockingIsOpen(enrollmentUid) }
    }

    fun blockingGetEnrollmentAccess(trackedEntityInstanceUid: String, programUid: String): EnrollmentAccessLevel {
        val program = programRepository.uid(programUid).blockingGet() ?: return EnrollmentAccessLevel.PROGRAM_ACCESS_DENIED

        return when(program.accessLevel()) {
            AccessLevel.OPEN, AccessLevel.AUDITED ->
                if (program.access()?.data()?.write() == true) EnrollmentAccessLevel.OPEN_PROGRAM_OK
                else EnrollmentAccessLevel.PROGRAM_ACCESS_DENIED
            AccessLevel.PROTECTED ->
                if (isTeiInCaptureScope(trackedEntityInstanceUid)) EnrollmentAccessLevel.PROTECTED_PROGRAM_OK
                else EnrollmentAccessLevel.PROGRAM_ACCESS_DENIED
            AccessLevel.CLOSED ->
                if (isTeiInCaptureScope(trackedEntityInstanceUid)) EnrollmentAccessLevel.CLOSED_PROGRAM_OK
                else EnrollmentAccessLevel.CLOSED_PROGRAM_DENIED
            else ->
                EnrollmentAccessLevel.PROGRAM_ACCESS_DENIED
        }
    }

    fun getEnrollmentAccess(trackedEntityInstanceUid: String, programUid: String): Single<EnrollmentAccessLevel> {
        return Single.fromCallable{ blockingGetEnrollmentAccess(trackedEntityInstanceUid, programUid) }
    }

    private fun isTeiInCaptureScope(trackedEntityInstanceUid: String): Boolean {
        val tei = trackedEntityInstanceRepository.uid(trackedEntityInstanceUid).blockingGet()

        return organisationUnitRepository
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .byUid().eq(tei.organisationUnit())
                .blockingGet()
                .isNotEmpty()
    }
}
