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
package org.hisp.dhis.android.core.event.internal

import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.category.CategoryOptionComboService
import org.hisp.dhis.android.core.enrollment.EnrollmentCollectionRepository
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentServiceImpl
import org.hisp.dhis.android.core.event.*
import org.hisp.dhis.android.core.event.EventService
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerStore
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.hisp.dhis.android.persistence.trackedentity.ProgramOwnerTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList", "TooManyFunctions")
internal class EventServiceImpl(
    private val enrollmentRepository: EnrollmentCollectionRepository,
    private val eventRepository: EventCollectionRepository,
    private val programRepository: ProgramCollectionRepository,
    private val programStageRepository: ProgramStageCollectionRepository,
    private val enrollmentService: EnrollmentServiceImpl,
    private val organisationUnitService: OrganisationUnitService,
    private val categoryOptionComboService: CategoryOptionComboService,
    private val eventDateUtils: EventDateUtils,
    private val programOwnerStore: ProgramOwnerStore,
) : EventService {

    override fun blockingHasDataWriteAccess(eventUid: String): Boolean {
        return runBlocking { suspendHasDataWriteAccess(eventUid) }
    }

    @Deprecated(message = "Use rxHasDataWriteAccess instead", ReplaceWith("rxHasDataWriteAccess(eventUid)"))
    override fun hasDataWriteAccess(eventUid: String): Single<Boolean> {
        return rxSingle { suspendHasDataWriteAccess(eventUid) }
    }

    override fun rxHasDataWriteAccess(eventUid: String): Single<Boolean> {
        return rxSingle { suspendHasDataWriteAccess(eventUid) }
    }

    override suspend fun suspendHasDataWriteAccess(eventUid: String): Boolean {
        val event = eventRepository.uid(eventUid).suspendGet() ?: return false

        return programStageRepository.uid(event.programStage()).suspendGet()?.access()?.data()?.write() ?: false
    }

    override fun blockingIsInOrgunitRange(event: Event): Boolean {
        return runBlocking { suspendIsInOrgunitRange(event) }
    }

    @Deprecated(message = "Use rxIsInOrgunitRange instead", ReplaceWith("rxIsInOrgunitRange(event)"))
    override fun isInOrgunitRange(event: Event): Single<Boolean> {
        return rxSingle { suspendIsInOrgunitRange(event) }
    }

    override fun rxIsInOrgunitRange(event: Event): Single<Boolean> {
        return rxSingle { suspendIsInOrgunitRange(event) }
    }

    override suspend fun suspendIsInOrgunitRange(event: Event): Boolean {
        return event.eventDate()?.let { eventDate ->
            event.organisationUnit()?.let { orgunitUid ->
                organisationUnitService.suspendIsDateInOrgunitRange(orgunitUid, eventDate)
            }
        } ?: true
    }

    override fun blockingHasCategoryComboAccess(event: Event): Boolean {
        return runBlocking { suspendHasCategoryComboAccess(event) }
    }

    @Deprecated(message = "Use rxHasCategoryComboAccess instead", ReplaceWith("rxHasCategoryComboAccess(event)"))
    override fun hasCategoryComboAccess(event: Event): Single<Boolean> {
        return rxSingle { suspendHasCategoryComboAccess(event) }
    }

    override fun rxHasCategoryComboAccess(event: Event): Single<Boolean> {
        return rxSingle { suspendHasCategoryComboAccess(event) }
    }

    override suspend fun suspendHasCategoryComboAccess(event: Event): Boolean {
        return event.attributeOptionCombo()?.let {
            categoryOptionComboService.suspendHasAccess(it, event.eventDate())
        } ?: true
    }

    override fun blockingIsEditable(eventUid: String): Boolean {
        return runBlocking { suspendIsEditable(eventUid) }
    }

    @Deprecated(message = "Use rxIsEditable instead", ReplaceWith("rxIsEditable(eventUid)"))
    override fun isEditable(eventUid: String): Single<Boolean> {
        return rxSingle { suspendIsEditable(eventUid) }
    }

    override fun rxIsEditable(eventUid: String): Single<Boolean> {
        return rxSingle { suspendIsEditable(eventUid) }
    }

    override suspend fun suspendIsEditable(eventUid: String): Boolean {
        return suspendGetEditableStatus(eventUid) is EventEditableStatus.Editable
    }

    @Suppress("ComplexMethod")
    override fun blockingGetEditableStatus(eventUid: String): EventEditableStatus {
        return runBlocking { suspendGetEditableStatus(eventUid) }
    }

    @Deprecated(message = "Use rxGetEditableStatus instead", ReplaceWith("rxGetEditableStatus(eventUid)"))
    override fun getEditableStatus(eventUid: String): Single<EventEditableStatus> {
        return rxSingle { suspendGetEditableStatus(eventUid) }
    }

    override fun rxGetEditableStatus(eventUid: String): Single<EventEditableStatus> {
        return rxSingle { suspendGetEditableStatus(eventUid) }
    }

    override suspend fun suspendGetEditableStatus(eventUid: String): EventEditableStatus {
        val event = eventRepository.uid(eventUid).suspendGet()!!
        val program = programRepository.uid(event.program()).suspendGet()
        val programStage = programStageRepository.uid(event.programStage()).suspendGet()

        return when {
            event.status() == EventStatus.COMPLETED && programStage?.blockEntryForm() == true ->
                EventEditableStatus.NonEditable(EventNonEditableReason.BLOCKED_BY_COMPLETION)

            eventDateUtils.isEventExpired(
                event = event,
                completeExpiryDays = program?.completeEventsExpiryDays() ?: 0,
                programPeriodType = program?.expiryPeriodType(),
                expiryDays = program?.expiryDays() ?: 0,
            ) ->
                EventEditableStatus.NonEditable(EventNonEditableReason.EXPIRED)

            !suspendHasDataWriteAccess(eventUid) ->
                EventEditableStatus.NonEditable(EventNonEditableReason.NO_DATA_WRITE_ACCESS)

            !suspendIsInOrgunitRange(event) ->
                EventEditableStatus.NonEditable(EventNonEditableReason.EVENT_DATE_IS_NOT_IN_ORGUNIT_RANGE)

            !suspendHasCategoryComboAccess(event) ->
                EventEditableStatus.NonEditable(EventNonEditableReason.NO_CATEGORY_COMBO_ACCESS)

            event.enrollment()?.let { !enrollmentService.suspendIsOpen(it) } ?: false ->
                EventEditableStatus.NonEditable(EventNonEditableReason.ENROLLMENT_IS_NOT_OPEN)

            !isOwnedByUser(event, program?.accessLevel()) ->
                EventEditableStatus.NonEditable(EventNonEditableReason.ORGUNIT_IS_NOT_IN_USER_SCOPE)

            else ->
                EventEditableStatus.Editable()
        }
    }

    override fun blockingCanAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Boolean {
        return runBlocking { suspendCanAddEventToEnrollment(enrollmentUid, programStageUid) }
    }

    @Deprecated(
        message = "Use rxCanAddEventToEnrollment instead",
        ReplaceWith("rxCanAddEventToEnrollment(enrollmentUid, programStageUid)"),
    )
    override fun canAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Single<Boolean> {
        return rxSingle { suspendCanAddEventToEnrollment(enrollmentUid, programStageUid) }
    }

    override fun rxCanAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Single<Boolean> {
        return rxSingle { suspendCanAddEventToEnrollment(enrollmentUid, programStageUid) }
    }

    override suspend fun suspendCanAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Boolean {
        val enrollment = enrollmentRepository.uid(enrollmentUid).suspendGet()
        val programStage = programStageRepository.uid(programStageUid).suspendGet()

        if (enrollment == null || programStage == null) {
            return false
        }

        val isActiveEnrollment = enrollment.status() == EnrollmentStatus.ACTIVE

        val acceptMoreEvents =
            if (programStage.repeatable() == true) {
                true
            } else {
                getEventCount(enrollmentUid, programStageUid) == 0
            }

        return isActiveEnrollment && acceptMoreEvents
    }

    @Suppress("ReturnCount")
    private suspend fun isOwnedByUser(event: Event, accessLevel: AccessLevel?): Boolean {
        val ownerOrgUnit = getOwnerOrgUnit(event) ?: return false

        if (organisationUnitService.suspendIsInCaptureScope(ownerOrgUnit)) return true

        val allowsSearchScope = (accessLevel ?: AccessLevel.OPEN) in listOf(AccessLevel.OPEN, AccessLevel.AUDITED)
        return allowsSearchScope && organisationUnitService.isInSearchScope(ownerOrgUnit)
    }

    private suspend fun getOwnerOrgUnit(event: Event): String? {
        val program = event.program()
        val tei = event.enrollment()
            ?.let { enrollmentRepository.uid(it).suspendGet() }
            ?.trackedEntityInstance()

        if (program == null || tei == null) return event.organisationUnit()

        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(ProgramOwnerTableInfo.Columns.PROGRAM, program)
            .appendKeyStringValue(ProgramOwnerTableInfo.Columns.TRACKED_ENTITY_INSTANCE, tei)
            .build()

        return programOwnerStore.selectWhere(whereClause)
            .firstOrNull()?.ownerOrgUnit()
            ?: event.organisationUnit()
    }

    private suspend fun getEventCount(enrollmentUid: String, programStageUid: String): Int {
        return eventRepository
            .byEnrollmentUid().eq(enrollmentUid)
            .byProgramStageUid().eq(programStageUid)
            .byDeleted().isFalse
            .suspendCount()
    }
}
