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
package org.hisp.dhis.android.core.event

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.helpers.AccessHelper
import org.hisp.dhis.android.core.category.CategoryOptionComboService
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentCollectionRepository
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentServiceImpl
import org.hisp.dhis.android.core.event.internal.EventDateUtils
import org.hisp.dhis.android.core.event.internal.EventServiceImpl
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerStore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class EventServiceShould {

    private val eventUid: String = "eventUid"
    private val attributeOptionComboUid = "attOptionComboUid"

    private val readDataAccess = AccessHelper.createForDataWrite(false)
    private val writeDataAccess = AccessHelper.createForDataWrite(true)

    private val enrollment: Enrollment = mock()
    private val event: Event = mock()
    private val program: Program = mock()
    private val programStage: ProgramStage = mock()

    private val enrollmentRepository: EnrollmentCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val eventRepository: EventCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val programRepository: ProgramCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val programStageRepository: ProgramStageCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val enrollmentService: EnrollmentServiceImpl = mock()
    private val organisationUnitService: OrganisationUnitService = mock()
    private val categoryOptionComboService: CategoryOptionComboService =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val eventDateUtils: EventDateUtils = mock()
    private val programOwnerStore: ProgramOwnerStore = mock()

    private val eventService: EventService = EventServiceImpl(
        enrollmentRepository,
        eventRepository,
        programRepository,
        programStageRepository,
        enrollmentService,
        organisationUnitService,
        categoryOptionComboService,
        eventDateUtils,
        programOwnerStore,
    )

    private val firstJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-01T00:00:00.000")

    @Before
    fun setUp() = runTest {
        whenever(eventRepository.uid(any()).suspendGet()) doReturn event
        whenever(enrollmentRepository.uid(any()).suspendGet()) doReturn enrollment
        whenever(programRepository.uid(any()).suspendGet()) doReturn program
        whenever(programStageRepository.uid(any()).suspendGet()) doReturn programStage

        whenever(event.uid()) doReturn eventUid
        whenever(event.eventDate()) doReturn firstJanuary
    }

    @Test
    fun `Should return true if program and program stage have write access`() {
        whenever(program.access()) doReturn writeDataAccess
        whenever(programStage.access()) doReturn writeDataAccess

        assertTrue(eventService.blockingHasDataWriteAccess(event.uid()))
    }

    @Test
    fun `Should return false if program stage has not write access`() {
        whenever(program.access()) doReturn writeDataAccess
        whenever(programStage.access()) doReturn readDataAccess

        assertFalse(eventService.blockingHasDataWriteAccess(event.uid()))
    }

    @Test
    fun `Should return true if programStage has write access and program has not write access`() {
        whenever(program.access()) doReturn readDataAccess
        whenever(programStage.access()) doReturn writeDataAccess

        assertTrue(eventService.blockingHasDataWriteAccess(event.uid()))
    }

    @Test
    fun `Should return true if has access to categoryCombo or attribute option combo is null`() = runTest {
        whenever(event.attributeOptionCombo()) doReturn attributeOptionComboUid
        whenever(categoryOptionComboService.suspendHasAccess(attributeOptionComboUid, firstJanuary)) doReturn true
        assertTrue(eventService.blockingHasCategoryComboAccess(event))

        whenever(event.attributeOptionCombo()) doReturn null
        whenever(categoryOptionComboService.suspendHasAccess(attributeOptionComboUid, firstJanuary)) doReturn false
        assertTrue(eventService.blockingHasCategoryComboAccess(event))
    }

    @Test
    fun `Should return false if has not access to categoryCombo`() = runTest {
        whenever(event.attributeOptionCombo()) doReturn attributeOptionComboUid
        whenever(categoryOptionComboService.suspendHasAccess(attributeOptionComboUid, firstJanuary)) doReturn false
        assertFalse(eventService.blockingHasCategoryComboAccess(event))
    }

    @Test
    fun `Should return no data write access edition status`() = runTest {
        whenever(event.attributeOptionCombo()) doReturn attributeOptionComboUid
        whenever(categoryOptionComboService.suspendHasAccess(attributeOptionComboUid, firstJanuary)) doReturn false
        val status = eventService.blockingGetEditableStatus(event.uid())

        assertThat(status is EventEditableStatus.NonEditable).isTrue()
        assertThat((status as EventEditableStatus.NonEditable).reason)
            .isEquivalentAccordingToCompareTo(EventNonEditableReason.NO_DATA_WRITE_ACCESS)
    }

    @Test
    fun `Should return editable when owner orgunit is in capture scope after transfer`() = runTest {
        whenever(programStage.access()) doReturn writeDataAccess
        whenever(event.organisationUnit()) doReturn "OU2"
        whenever(event.enrollment()) doReturn "enrollmentUid"
        whenever(event.program()) doReturn "programUid"
        whenever(enrollment.trackedEntityInstance()) doReturn "teiUid"
        whenever(organisationUnitService.suspendIsDateInOrgunitRange(any(), any())) doReturn true
        whenever(organisationUnitService.suspendIsInCaptureScope("OU1")) doReturn true
        whenever(enrollmentService.suspendIsOpen(any())) doReturn true

        val programOwner: ProgramOwner = mock()
        whenever(programOwner.ownerOrgUnit()) doReturn "OU1"
        whenever(programOwnerStore.selectWhere(any())) doReturn listOf(programOwner)

        val status = eventService.blockingGetEditableStatus(event.uid())
        assertThat(status is EventEditableStatus.Editable).isTrue()
    }

    @Test
    fun `Should return not editable when neither event nor owner orgunit is in capture scope`() = runTest {
        whenever(programStage.access()) doReturn writeDataAccess
        whenever(event.organisationUnit()) doReturn "OU2"
        whenever(event.enrollment()) doReturn "enrollmentUid"
        whenever(event.program()) doReturn "programUid"
        whenever(enrollment.trackedEntityInstance()) doReturn "teiUid"
        whenever(organisationUnitService.suspendIsDateInOrgunitRange(any(), any())) doReturn true
        whenever(organisationUnitService.isInSearchScope("OU2")) doReturn false
        whenever(organisationUnitService.isInSearchScope("OU3")) doReturn false
        whenever(organisationUnitService.suspendIsInCaptureScope(any())) doReturn false
        whenever(enrollmentService.suspendIsOpen(any())) doReturn true

        val programOwner: ProgramOwner = mock()
        whenever(programOwner.ownerOrgUnit()) doReturn "OU3"
        whenever(programOwnerStore.selectWhere(any())) doReturn listOf(programOwner)

        val status = eventService.blockingGetEditableStatus(event.uid())
        assertThat(status is EventEditableStatus.NonEditable).isTrue()
        assertThat((status as EventEditableStatus.NonEditable).reason)
            .isEquivalentAccordingToCompareTo(EventNonEditableReason.ORGUNIT_IS_NOT_IN_USER_SCOPE)
    }
}
