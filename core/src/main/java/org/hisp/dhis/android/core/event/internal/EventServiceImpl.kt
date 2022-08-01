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
package org.hisp.dhis.android.core.event.internal

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.category.CategoryOptionComboService
import org.hisp.dhis.android.core.enrollment.EnrollmentCollectionRepository
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentServiceImpl
import org.hisp.dhis.android.core.event.*
import org.hisp.dhis.android.core.event.EventService
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository

@Reusable
@Suppress("LongParameterList", "TooManyFunctions")
internal class EventServiceImpl @Inject constructor(
    private val enrollmentRepository: EnrollmentCollectionRepository,
    private val eventRepository: EventCollectionRepository,
    private val programRepository: ProgramCollectionRepository,
    private val programStageRepository: ProgramStageCollectionRepository,
    private val enrollmentService: EnrollmentServiceImpl,
    private val organisationUnitService: OrganisationUnitService,
    private val categoryOptionComboService: CategoryOptionComboService,
    private val eventDateUtils: EventDateUtils
) : EventService {

    override fun blockingHasDataWriteAccess(eventUid: String): Boolean {
        val event = eventRepository.uid(eventUid).blockingGet() ?: return false

        return programStageRepository.uid(event.programStage()).blockingGet()?.access()?.data()?.write() ?: false
    }

    override fun hasDataWriteAccess(eventUid: String): Single<Boolean> {
        return Single.just(blockingHasDataWriteAccess(eventUid))
    }

    override fun blockingIsInOrgunitRange(event: Event): Boolean {
        return event.eventDate()?.let { eventDate ->
            event.organisationUnit()?.let { orgunitUid ->
                organisationUnitService.blockingIsDateInOrgunitRange(orgunitUid, eventDate)
            }
        } ?: true
    }

    override fun isInOrgunitRange(event: Event): Single<Boolean> {
        return Single.just(blockingIsInOrgunitRange(event))
    }

    override fun blockingHasCategoryComboAccess(event: Event): Boolean {
        return event.attributeOptionCombo()?.let {
            categoryOptionComboService.blockingHasAccess(it, event.eventDate())
        } ?: true
    }

    override fun hasCategoryComboAccess(event: Event): Single<Boolean> {
        return Single.just(blockingHasCategoryComboAccess(event))
    }

    override fun blockingIsEditable(eventUid: String): Boolean {
        return blockingGetEditableStatus(eventUid) is EventEditableStatus.Editable
    }

    override fun isEditable(eventUid: String): Single<Boolean> {
        return Single.just(blockingIsEditable(eventUid))
    }

    @Suppress("ComplexMethod")
    override fun blockingGetEditableStatus(eventUid: String): EventEditableStatus {
        val event = eventRepository.uid(eventUid).blockingGet()
        val program = programRepository.uid(event.program()).blockingGet()
        val programStage = programStageRepository.uid(event.programStage()).blockingGet()

        return when {
            event.status() == EventStatus.COMPLETED && programStage.blockEntryForm() == true ->
                EventEditableStatus.NonEditable(EventNonEditableReason.BLOCKED_BY_COMPLETION)

            eventDateUtils.isEventExpired(
                event = event,
                completeExpiryDays = program.completeEventsExpiryDays() ?: 0,
                programPeriodType = programStage.periodType() ?: program.expiryPeriodType(),
                expiryDays = program.expiryDays() ?: 0
            ) ->
                EventEditableStatus.NonEditable(EventNonEditableReason.EXPIRED)

            !blockingHasDataWriteAccess(eventUid) ->
                EventEditableStatus.NonEditable(EventNonEditableReason.NO_DATA_WRITE_ACCESS)

            !blockingIsInOrgunitRange(event) ->
                EventEditableStatus.NonEditable(EventNonEditableReason.EVENT_DATE_IS_NOT_IN_ORGUNIT_RANGE)

            !blockingHasCategoryComboAccess(event) ->
                EventEditableStatus.NonEditable(EventNonEditableReason.NO_CATEGORY_COMBO_ACCESS)

            event.enrollment()?.let { !enrollmentService.blockingIsOpen(it) } ?: false ->
                EventEditableStatus.NonEditable(EventNonEditableReason.ENROLLMENT_IS_NOT_OPEN)

            event.organisationUnit()?.let { !organisationUnitService.blockingIsInCaptureScope(it) } ?: false ->
                EventEditableStatus.NonEditable(EventNonEditableReason.ORGUNIT_IS_NOT_IN_CAPTURE_SCOPE)

            else ->
                EventEditableStatus.Editable()
        }
    }

    override fun getEditableStatus(eventUid: String): Single<EventEditableStatus> {
        return Single.just(blockingGetEditableStatus(eventUid))
    }

    override fun blockingCanAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Boolean {
        val enrollment = enrollmentRepository.uid(enrollmentUid).blockingGet()
        val programStage = programStageRepository.uid(programStageUid).blockingGet()

        if (enrollment == null || programStage == null) {
            return false
        }

        val isActiveEnrollment = enrollment.status() == EnrollmentStatus.ACTIVE

        val acceptMoreEvents =
            if (programStage.repeatable() == true) true
            else getEventCount(enrollmentUid, programStageUid) == 0

        return isActiveEnrollment && acceptMoreEvents
    }

    override fun canAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Single<Boolean> {
        return Single.just(blockingCanAddEventToEnrollment(enrollmentUid, programStageUid))
    }

    private fun getEventCount(enrollmentUid: String, programStageUid: String): Int {
        return eventRepository
            .byEnrollmentUid().eq(enrollmentUid)
            .byProgramStageUid().eq(programStageUid)
            .byDeleted().isFalse
            .blockingCount()
    }
}
