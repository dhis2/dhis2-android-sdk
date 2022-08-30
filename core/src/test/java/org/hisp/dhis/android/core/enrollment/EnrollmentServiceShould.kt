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
package org.hisp.dhis.android.core.enrollment

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.helpers.AccessHelper
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.DataAccess
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentServiceImpl
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramTempOwner
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class EnrollmentServiceShould {

    private val enrollmentUid: String = "enrollmentUid"
    private val trackedEntityInstanceUid: String = "trackedEntityInstanceUid"
    private val programUid: String = "programUid"
    private val organisationUnitId: String = "organisationUnitId"

    private val enrollment: Enrollment = mock()
    private val trackedEntityInstance: TrackedEntityInstance = mock()
    private val program: Program = mock()
    private val programTempOwner: ProgramTempOwner = mock()

    private val enrollmentRepository: EnrollmentCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val trackedEntityInstanceRepository: TrackedEntityInstanceCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val programRepository: ProgramCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val organisationUnitRepository: OrganisationUnitCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val eventCollectionRepository: EventCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val programStageCollectionRepository: ProgramStageCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val programTempOwnerStore: ObjectWithoutUidStore<ProgramTempOwner> = mock()

    private lateinit var enrollmentService: EnrollmentService

    @Before
    fun setUp() {
        whenever(enrollmentRepository.uid(enrollmentUid).blockingGet()) doReturn enrollment
        whenever(
            trackedEntityInstanceRepository
                .uid(trackedEntityInstanceUid).blockingGet()
        ) doReturn trackedEntityInstance
        whenever(programRepository.uid(programUid).blockingGet()) doReturn program

        whenever(enrollment.uid()) doReturn enrollmentUid
        whenever(trackedEntityInstance.organisationUnit()) doReturn organisationUnitId

        enrollmentService = EnrollmentServiceImpl(
            enrollmentRepository,
            trackedEntityInstanceRepository,
            programRepository,
            organisationUnitRepository,
            eventCollectionRepository,
            programStageCollectionRepository,
            programTempOwnerStore
        )
    }

    @Test
    fun `IsOpen should return true if enrollment is not found`() {
        whenever(enrollmentRepository.uid(enrollmentUid).blockingGet()) doReturn null
        assertTrue(enrollmentService.blockingIsOpen(enrollmentUid))
    }

    @Test
    fun `IsOpen should return false if enrollment is not active`() {
        whenever(enrollment.status()) doReturn EnrollmentStatus.COMPLETED
        assertFalse(enrollmentService.blockingIsOpen(enrollmentUid))

        whenever(enrollment.status()) doReturn null
        assertFalse(enrollmentService.blockingIsOpen(enrollmentUid))
    }

    @Test
    fun `IsOpen should return true if enrollment is active`() {
        whenever(enrollment.status()) doReturn EnrollmentStatus.ACTIVE
        assertTrue(enrollmentService.blockingIsOpen(enrollmentUid))
    }

    @Test
    fun `GetEnrollmentAccess should return no access if program not found`() {
        whenever(programRepository.uid("other uid").blockingGet()) doReturn null

        val access = enrollmentService.blockingGetEnrollmentAccess(trackedEntityInstanceUid, "other uid")
        assert(access == EnrollmentAccess.NO_ACCESS)
    }

    @Test
    fun `GetEnrollmentAccess should return data access if program is open`() {
        whenever(program.accessLevel()) doReturn AccessLevel.OPEN

        whenever(program.access()) doReturn AccessHelper.createForDataWrite(false)
        val accessRead = enrollmentService.blockingGetEnrollmentAccess(trackedEntityInstanceUid, programUid)
        assert(accessRead == EnrollmentAccess.READ_ACCESS)

        whenever(program.access()) doReturn AccessHelper.createForDataWrite(true)
        val accessWrite = enrollmentService.blockingGetEnrollmentAccess(trackedEntityInstanceUid, programUid)
        assert(accessWrite == EnrollmentAccess.WRITE_ACCESS)
    }

    @Test
    fun `GetEnrollmentAccess should return data access if protected program in capture scope`() {
        whenever(program.accessLevel()) doReturn AccessLevel.PROTECTED
        whenever(program.access()) doReturn AccessHelper.createForDataWrite(true)
        whenever(
            organisationUnitRepository
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .uid(organisationUnitId)
                .blockingExists()
        ) doReturn true

        val access = enrollmentService.blockingGetEnrollmentAccess(trackedEntityInstanceUid, programUid)
        assert(access == EnrollmentAccess.WRITE_ACCESS)
    }

    @Test
    fun `GetEnrollmentAccess should return access denied if protected program not in capture scope`() {
        whenever(program.accessLevel()) doReturn AccessLevel.PROTECTED
        whenever(program.access()) doReturn AccessHelper.createForDataWrite(true)
        whenever(
            organisationUnitRepository
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .uid(organisationUnitId)
                .blockingExists()
        ) doReturn false
        whenever(programTempOwnerStore.selectWhere(any())) doReturn listOf(programTempOwner)
        whenever(programTempOwner.validUntil()) doReturn DateUtils.DATE_FORMAT.parse("1999-01-01T00:00:00.000")

        val access = enrollmentService.blockingGetEnrollmentAccess(trackedEntityInstanceUid, programUid)
        assert(access == EnrollmentAccess.PROTECTED_PROGRAM_DENIED)
    }

    @Test
    fun `GetEnrollmentAccess should return data access if protected program has broken glass`() {
        whenever(program.accessLevel()) doReturn AccessLevel.PROTECTED
        whenever(program.access()) doReturn AccessHelper.createForDataWrite(true)
        whenever(
            organisationUnitRepository
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .uid(organisationUnitId)
                .blockingExists()
        ) doReturn false
        whenever(programTempOwnerStore.selectWhere(any())) doReturn listOf(programTempOwner)
        whenever(programTempOwner.validUntil()) doReturn DateUtils.DATE_FORMAT.parse("2999-01-01T00:00:00.000")

        val access = enrollmentService.blockingGetEnrollmentAccess(trackedEntityInstanceUid, programUid)
        assert(access == EnrollmentAccess.WRITE_ACCESS)
    }

    @Test
    fun `Enrollment has any events that allows events creation`() {

        whenever(enrollmentRepository.uid(enrollmentUid).blockingGet()) doReturn enrollment
        whenever(enrollment.program()) doReturn programUid

        whenever(
            programStageCollectionRepository.byProgramUid().eq(programUid)
        ) doReturn programStageCollectionRepository
        whenever(
            programStageCollectionRepository.byAccessDataWrite().isTrue
        ) doReturn programStageCollectionRepository
        whenever(
            programStageCollectionRepository.get()
        ) doReturn Single.just(getProgramStages())

        whenever(
            eventCollectionRepository.byEnrollmentUid().eq(enrollmentUid)
        ) doReturn eventCollectionRepository
        whenever(
            eventCollectionRepository.byDeleted().isFalse
        ) doReturn eventCollectionRepository
        whenever(eventCollectionRepository.get()) doReturn Single.just(getEventList())

        assertTrue(enrollmentService.blockingGetAllowEventCreation(enrollmentUid, listOf("1")))
    }

    @Test
    fun `Enrollment has not any events that allows events creation`() {

        whenever(enrollmentRepository.uid(enrollmentUid).blockingGet()) doReturn enrollment
        whenever(enrollment.program()) doReturn programUid

        whenever(
            programStageCollectionRepository.byProgramUid().eq(programUid)
        ) doReturn programStageCollectionRepository
        whenever(
            programStageCollectionRepository.byAccessDataWrite().isTrue
        ) doReturn programStageCollectionRepository
        whenever(
            programStageCollectionRepository.get()
        ) doReturn Single.just(getProgramStages())

        whenever(
            eventCollectionRepository.byEnrollmentUid().eq(enrollmentUid)
        ) doReturn eventCollectionRepository
        whenever(
            eventCollectionRepository.byDeleted().isFalse
        ) doReturn eventCollectionRepository
        whenever(eventCollectionRepository.get()) doReturn Single.just(getEventList())

        assertFalse(enrollmentService.blockingGetAllowEventCreation(enrollmentUid, listOf("1", "2")))
    }

    private fun getEventList() = listOf(
        Event.builder()
            .uid("eventUid1")
            .programStage("1")
            .build(),
        Event.builder()
            .uid("eventUid2")
            .programStage("2")
            .build()
    )

    private fun getProgramStages() = listOf(
        ProgramStage.builder()
            .access(Access.create(true, true, DataAccess.create(true, true)))
            .uid("1")
            .repeatable(true)
            .build(),
        ProgramStage.builder()
            .access(Access.create(true, true, DataAccess.create(true, true)))
            .uid("2")
            .repeatable(true)
            .build()
    )
}
