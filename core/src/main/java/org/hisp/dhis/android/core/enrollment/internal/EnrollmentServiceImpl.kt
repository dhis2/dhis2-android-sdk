/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.enrollment.internal

import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.enrollment.EnrollmentAccess
import org.hisp.dhis.android.core.enrollment.EnrollmentCollectionRepository
import org.hisp.dhis.android.core.enrollment.EnrollmentService
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerStore
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramTempOwnerStore
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.trackedentity.ProgramOwnerTableInfo
import org.hisp.dhis.android.persistence.trackedentity.ProgramTempOwnerTableInfo
import org.koin.core.annotation.Singleton
import java.util.Date

@Suppress("TooManyFunctions")
@Singleton
internal class EnrollmentServiceImpl(
    private val enrollmentRepository: EnrollmentCollectionRepository,
    private val trackedEntityInstanceRepository: TrackedEntityInstanceCollectionRepository,
    private val programRepository: ProgramCollectionRepository,
    private val organisationUnitRepository: OrganisationUnitCollectionRepository,
    private val eventCollectionRepository: EventCollectionRepository,
    private val programStagesCollectionRepository: ProgramStageCollectionRepository,
    private val programTempOwnerStore: ProgramTempOwnerStore,
    private val programOwnerStore: ProgramOwnerStore,
) : EnrollmentService {

    override fun blockingIsOpen(enrollmentUid: String): Boolean {
        return runBlocking { suspendIsOpen(enrollmentUid) }
    }

    @Deprecated(message = "Use rxIsOpen instead", ReplaceWith("rxIsOpen(enrollmentUid)"))
    override fun isOpen(enrollmentUid: String): Single<Boolean> {
        return rxSingle { suspendIsOpen(enrollmentUid) }
    }

    override fun rxIsOpen(enrollmentUid: String): Single<Boolean> {
        return rxSingle { suspendIsOpen(enrollmentUid) }
    }

    override suspend fun suspendIsOpen(enrollmentUid: String): Boolean {
        val enrollment = enrollmentRepository.uid(enrollmentUid).getInternal() ?: return true

        return enrollment.status()?.equals(EnrollmentStatus.ACTIVE) ?: false
    }

    override fun blockingGetEnrollmentAccess(trackedEntityInstanceUid: String, programUid: String): EnrollmentAccess {
        return runBlocking { suspendGetEnrollmentAccess(trackedEntityInstanceUid, programUid) }
    }

    @Deprecated(message = "Use rxGetEnrollmentAccess instead", ReplaceWith("rxGetEnrollmentAccess(trackedEntityInstanceUid, programUid)"))
    override fun getEnrollmentAccess(trackedEntityInstanceUid: String, programUid: String): Single<EnrollmentAccess> {
        return rxSingle { suspendGetEnrollmentAccess(trackedEntityInstanceUid, programUid) }
    }

    override fun rxGetEnrollmentAccess(trackedEntityInstanceUid: String, programUid: String): Single<EnrollmentAccess> {
        return rxSingle { suspendGetEnrollmentAccess(trackedEntityInstanceUid, programUid) }
    }

    override suspend fun suspendGetEnrollmentAccess(
        trackedEntityInstanceUid: String,
        programUid: String,
    ): EnrollmentAccess {
        val program = programRepository.uid(programUid).getInternal() ?: return EnrollmentAccess.NO_ACCESS

        val dataAccess =
            if (program.access()?.data()?.write() == true) {
                EnrollmentAccess.WRITE_ACCESS
            } else {
                EnrollmentAccess.READ_ACCESS
            }

        return when (program.accessLevel()) {
            AccessLevel.PROTECTED ->
                if (isTeiInCaptureScope(trackedEntityInstanceUid) ||
                    hasProgramOwnership(trackedEntityInstanceUid, programUid) ||
                    hasTempOwnership(trackedEntityInstanceUid, programUid)
                ) {
                    dataAccess
                } else {
                    EnrollmentAccess.PROTECTED_PROGRAM_DENIED
                }

            AccessLevel.CLOSED ->
                if (isTeiInCaptureScope(trackedEntityInstanceUid) ||
                    hasProgramOwnership(trackedEntityInstanceUid, programUid)
                ) {
                    dataAccess
                } else {
                    EnrollmentAccess.CLOSED_PROGRAM_DENIED
                }

            else ->
                dataAccess
        }
    }

    private suspend fun isTeiInCaptureScope(trackedEntityInstanceUid: String): Boolean {
        val tei = trackedEntityInstanceRepository.uid(trackedEntityInstanceUid).getInternal()

        return organisationUnitRepository
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .uid(tei?.organisationUnit())
            .existsInternal()
    }

    private suspend fun hasProgramOwnership(trackedEntityInstanceUid: String, programUid: String): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(ProgramOwnerTableInfo.Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid)
            .appendKeyStringValue(ProgramOwnerTableInfo.Columns.PROGRAM, programUid)
            .build()

        val programOwners = programOwnerStore.selectWhere(whereClause)

        if (programOwners.isEmpty()) {
            return false
        }

        val ownerOrgUnit = programOwners.first().ownerOrgUnit()

        return organisationUnitRepository
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .uid(ownerOrgUnit)
            .existsInternal()
    }

    override fun blockingGetAllowEventCreation(enrollmentUid: String, stagesToHide: List<String>): Boolean {
        return runBlocking { suspendGetAllowEventCreation(enrollmentUid, stagesToHide) }
    }

    @Deprecated(message = "Use rxAllowEventCreation instead", ReplaceWith("rxAllowEventCreation(enrollmentUid, stagesToHide)"))
    override fun allowEventCreation(enrollmentUid: String, stagesToHide: List<String>): Single<Boolean> {
        return rxSingle { suspendGetAllowEventCreation(enrollmentUid, stagesToHide) }
    }

    override fun rxAllowEventCreation(enrollmentUid: String, stagesToHide: List<String>): Single<Boolean> {
        return rxSingle { suspendGetAllowEventCreation(enrollmentUid, stagesToHide) }
    }

    override suspend fun suspendGetAllowEventCreation(enrollmentUid: String, stagesToHide: List<String>): Boolean {
        val currentProgramStagesUids = eventCollectionRepository.byEnrollmentUid().eq(enrollmentUid)
            .byDeleted().isFalse.getInternal()
            .map { event: Event -> event.programStage() }

        val enrollment = enrollmentRepository.uid(enrollmentUid).getInternal()
        val programStages = programStagesCollectionRepository.byProgramUid().eq(
            enrollment?.program(),
        ).byAccessDataWrite().isTrue
            .getInternal()
            .filter { programStage: ProgramStage ->
                !currentProgramStagesUids.contains(programStage.uid()) ||
                    programStage.repeatable()!!
            }

        return programStages.find { !stagesToHide.contains(it.uid()) } != null
    }

    private suspend fun hasTempOwnership(tei: String, program: String): Boolean {
        val nowStr = DateUtils.DATE_FORMAT.format(Date())
        val columns = ProgramTempOwnerTableInfo.Columns
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(columns.TRACKED_ENTITY_INSTANCE, tei)
            .appendKeyStringValue(columns.PROGRAM, program)
            .build()

        val ownerships = programTempOwnerStore.selectWhere(whereClause)

        /* If there is no records about ownership, it will be probably caused by an existing break-the-glass in the
         * server. The app is not forced to ask for ownership and there is no record in the SDK.
         */

        return ownerships.isEmpty() ||
            ownerships.any { DateUtils.DATE_FORMAT.format(it.validUntil()) > nowStr }
    }
}
