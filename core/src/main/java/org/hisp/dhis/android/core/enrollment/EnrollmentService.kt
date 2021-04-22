/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.enrollment

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository

@Reusable
class EnrollmentService @Inject constructor(
    private val enrollmentRepository: EnrollmentCollectionRepository,
    private val trackedEntityInstanceRepository: TrackedEntityInstanceCollectionRepository,
    private val programRepository: ProgramCollectionRepository,
    private val organisationUnitRepository: OrganisationUnitCollectionRepository
) {

    /**
     * Blocking version of [isOpen].
     *
     * @see isOpen
     */
    fun blockingIsOpen(enrollmentUid: String): Boolean {
        val enrollment = enrollmentRepository.uid(enrollmentUid).blockingGet() ?: return true

        return enrollment.status()?.equals(EnrollmentStatus.ACTIVE) ?: false
    }

    /**
     * Checks if the enrollment status is ACTIVE.
     */
    fun isOpen(enrollmentUid: String): Single<Boolean> {
        return Single.fromCallable { blockingIsOpen(enrollmentUid) }
    }

    /**
     * Blocking version of [getEnrollmentAccess].
     *
     * @see getEnrollmentAccess
     */
    fun blockingGetEnrollmentAccess(trackedEntityInstanceUid: String, programUid: String): EnrollmentAccess {
        val program = programRepository.uid(programUid).blockingGet() ?: return EnrollmentAccess.NO_ACCESS

        val dataAccess =
            if (program.access()?.data()?.write() == true) EnrollmentAccess.WRITE_ACCESS
            else EnrollmentAccess.READ_ACCESS

        return when (program.accessLevel()) {
            AccessLevel.PROTECTED ->
                if (isTeiInCaptureScope(trackedEntityInstanceUid)) dataAccess
                else EnrollmentAccess.PROTECTED_PROGRAM_DENIED
            AccessLevel.CLOSED ->
                if (isTeiInCaptureScope(trackedEntityInstanceUid)) dataAccess
                else EnrollmentAccess.CLOSED_PROGRAM_DENIED
            else ->
                dataAccess
        }
    }

    /**
     * Evaluates the access level of the user to this program and trackedEntityInstance.
     *
     * It checks the data access level to the program, the program access level (OPEN, PROTECTED,...)
     * and the enrollment orgunit scope (SEARCH or CAPTURE).
     */
    fun getEnrollmentAccess(trackedEntityInstanceUid: String, programUid: String): Single<EnrollmentAccess> {
        return Single.fromCallable { blockingGetEnrollmentAccess(trackedEntityInstanceUid, programUid) }
    }

    private fun isTeiInCaptureScope(trackedEntityInstanceUid: String): Boolean {
        val tei = trackedEntityInstanceRepository.uid(trackedEntityInstanceUid).blockingGet()

        return organisationUnitRepository
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .uid(tei.organisationUnit())
            .blockingExists()
    }
}
