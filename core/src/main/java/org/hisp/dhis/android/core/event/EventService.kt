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
package org.hisp.dhis.android.core.event

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.category.CategoryOptionComboService
import org.hisp.dhis.android.core.enrollment.EnrollmentCollectionRepository
import org.hisp.dhis.android.core.enrollment.EnrollmentService
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.internal.EventDateUtils
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository

@Reusable
@Suppress("LongParameterList", "TooManyFunctions")
class EventService @Inject constructor(
    private val enrollmentRepository: EnrollmentCollectionRepository,
    private val eventRepository: EventCollectionRepository,
    private val programRepository: ProgramCollectionRepository,
    private val programStageRepository: ProgramStageCollectionRepository,
    private val enrollmentService: EnrollmentService,
    private val organisationUnitService: OrganisationUnitService,
    private val categoryOptionComboService: CategoryOptionComboService,
    private val eventDateUtils: EventDateUtils
) {

    /**
     * Blocking version of [hasDataWriteAccess].
     *
     * @see hasDataWriteAccess
     */
    fun blockingHasDataWriteAccess(eventUid: String): Boolean {
        val event = eventRepository.uid(eventUid).blockingGet() ?: return false

        return programStageRepository.uid(event.programStage()).blockingGet()?.access()?.data()?.write() ?: false
    }

    /**
     * Check if user has data write access to a particular event.
     *
     * It returns true if the user has data write access to both the program and the program stage.
     * If the event does not exist, returns null
     */
    fun hasDataWriteAccess(eventUid: String): Single<Boolean> {
        return Single.just(blockingHasDataWriteAccess(eventUid))
    }

    /**
     * Blocking version of [isInOrgunitRange].
     *
     * @see isInOrgunitRange
     */
    fun blockingIsInOrgunitRange(event: Event): Boolean {
        return event.eventDate()?.let { eventDate ->
            event.organisationUnit()?.let { orgunitUid ->
                organisationUnitService.blockingIsDateInOrgunitRange(orgunitUid, eventDate)
            }
        } ?: true
    }

    /**
     * Check if the event has the event date within the opening period of the assigned organisation unit.
     */
    fun isInOrgunitRange(event: Event): Single<Boolean> {
        return Single.just(blockingIsInOrgunitRange(event))
    }

    /**
     * Blocking version of [hasCategoryComboAccess].
     *
     * @see hasCategoryComboAccess
     */
    fun blockingHasCategoryComboAccess(event: Event): Boolean {
        return event.attributeOptionCombo()?.let {
            categoryOptionComboService.blockingHasAccess(it, event.eventDate())
        } ?: true
    }

    /**
     * Check if user has access to the categoryCombo linked to the event and also if the categoryCombo is active
     * in the event date.
     */
    fun hasCategoryComboAccess(event: Event): Single<Boolean> {
        return Single.just(blockingHasCategoryComboAccess(event))
    }

    /**
     * Blocking version of [isEditable].
     *
     * @see isEditable
     */
    fun blockingIsEditable(eventUid: String): Boolean {
        return blockingGetEditableStatus(eventUid) is EventEditableStatus.Editable
    }

    /**
     * Check if the event can be edited or not. If you want to know the reason why the event is not editable, check
     * the method [getEditableStatus] for a richer description of the status.
     */
    fun isEditable(eventUid: String): Single<Boolean> {
        return Single.just(blockingIsEditable(eventUid))
    }

    /**
     * Blocking version of [getEditableStatus].
     *
     * @see getEditableStatus
     */
    @Suppress("ComplexMethod")
    fun blockingGetEditableStatus(eventUid: String): EventEditableStatus {
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

    /**
     * Returns the editable status of an event. In case the event is not editable, the result also includes the
     * reason why it is not editable.
     */
    fun getEditableStatus(eventUid: String): Single<EventEditableStatus> {
        return Single.just(blockingGetEditableStatus(eventUid))
    }

    /**
     * Blocking version of [canAddEventToEnrollment].
     *
     * @see canAddEventToEnrollment
     */
    fun blockingCanAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Boolean {
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

    /**
     * Evaluates if an enrollments accepts more events for a particular programStage.
     *
     * It takes into account the enrollment status and if the program stage is repeatable or not.
     */
    fun canAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Single<Boolean> {
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
