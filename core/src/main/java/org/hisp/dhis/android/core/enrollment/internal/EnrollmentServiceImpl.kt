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
package org.hisp.dhis.android.core.enrollment.internal

import dagger.Reusable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
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
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramTempOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramTempOwnerTableInfo

@Reusable
internal class EnrollmentServiceImpl @Inject constructor(
    private val enrollmentRepository: EnrollmentCollectionRepository,
    private val trackedEntityInstanceRepository: TrackedEntityInstanceCollectionRepository,
    private val programRepository: ProgramCollectionRepository,
    private val organisationUnitRepository: OrganisationUnitCollectionRepository,
    private val eventCollectionRepository: EventCollectionRepository,
    private val programStagesCollectionRepository: ProgramStageCollectionRepository,
    private val programTempOwnerStore: ObjectWithoutUidStore<ProgramTempOwner>
) : EnrollmentService {

    override fun blockingIsOpen(enrollmentUid: String): Boolean {
        val enrollment = enrollmentRepository.uid(enrollmentUid).blockingGet() ?: return true

        return enrollment.status()?.equals(EnrollmentStatus.ACTIVE) ?: false
    }

    override fun isOpen(enrollmentUid: String): Single<Boolean> {
        return Single.fromCallable { blockingIsOpen(enrollmentUid) }
    }

    override fun blockingGetEnrollmentAccess(trackedEntityInstanceUid: String, programUid: String): EnrollmentAccess {
        val program = programRepository.uid(programUid).blockingGet() ?: return EnrollmentAccess.NO_ACCESS

        val dataAccess =
            if (program.access()?.data()?.write() == true) EnrollmentAccess.WRITE_ACCESS
            else EnrollmentAccess.READ_ACCESS

        return when (program.accessLevel()) {
            AccessLevel.PROTECTED ->
                if (hasTempOwnership(trackedEntityInstanceUid, programUid)) dataAccess
                else EnrollmentAccess.PROTECTED_PROGRAM_DENIED
            AccessLevel.CLOSED ->
                if (isTeiInCaptureScope(trackedEntityInstanceUid)) dataAccess
                else EnrollmentAccess.CLOSED_PROGRAM_DENIED
            else ->
                dataAccess
        }
    }

    override fun getEnrollmentAccess(trackedEntityInstanceUid: String, programUid: String): Single<EnrollmentAccess> {
        return Single.fromCallable { blockingGetEnrollmentAccess(trackedEntityInstanceUid, programUid) }
    }

    private fun isTeiInCaptureScope(trackedEntityInstanceUid: String): Boolean {
        val tei = trackedEntityInstanceRepository.uid(trackedEntityInstanceUid).blockingGet()

        return organisationUnitRepository
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .uid(tei.organisationUnit())
            .blockingExists()
    }

    override fun blockingGetAllowEventCreation(enrollmentUid: String, stagesToHide: List<String>): Boolean {
        val programStages = eventCollectionRepository.byEnrollmentUid().eq(enrollmentUid)
            .byDeleted().isFalse.get()
            .toFlowable().flatMapIterable { events: List<Event>? -> events }
            .map { event: Event -> event.programStage() }
            .toList()
            .flatMap { currentProgramStagesUids: List<String?> ->
                val repository = programStagesCollectionRepository.byProgramUid().eq(
                    enrollmentRepository.uid(enrollmentUid).blockingGet().program()
                ).byAccessDataWrite().isTrue

                repository.get().toFlowable()
                    .flatMapIterable { stages: List<ProgramStage>? -> stages }
                    .filter { programStage: ProgramStage ->
                        !currentProgramStagesUids.contains(programStage.uid()) ||
                            programStage.repeatable()!!
                    }
                    .toList()
            }.blockingGet()

        return programStages.find { !stagesToHide.contains(it.uid()) } != null
    }

    override fun allowEventCreation(enrollmentUid: String, stagesToHide: List<String>): Single<Boolean> {
        return Single.fromCallable { blockingGetAllowEventCreation(enrollmentUid, stagesToHide) }
    }

    private fun hasTempOwnership(tei: String, program: String): Boolean {
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
