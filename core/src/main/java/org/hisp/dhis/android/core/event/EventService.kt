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

import io.reactivex.Single

@Suppress("TooManyFunctions")
interface EventService {

    /**
     * Blocking version of [hasDataWriteAccess].
     *
     * @see hasDataWriteAccess
     */
    fun blockingHasDataWriteAccess(eventUid: String): Boolean

    /**
     * Check if user has data write access to a particular event.
     *
     * It returns true if the user has data write access to both the program and the program stage.
     * If the event does not exist, returns null
     */
    fun hasDataWriteAccess(eventUid: String): Single<Boolean>

    /**
     * Blocking version of [isInOrgunitRange].
     *
     * @see isInOrgunitRange
     */
    fun blockingIsInOrgunitRange(event: Event): Boolean

    /**
     * Check if the event has the event date within the opening period of the assigned organisation unit.
     */
    fun isInOrgunitRange(event: Event): Single<Boolean>

    /**
     * Blocking version of [hasCategoryComboAccess].
     *
     * @see hasCategoryComboAccess
     */
    fun blockingHasCategoryComboAccess(event: Event): Boolean

    /**
     * Check if user has access to the categoryCombo linked to the event and also if the categoryCombo is active
     * in the event date.
     */
    fun hasCategoryComboAccess(event: Event): Single<Boolean>

    /**
     * Blocking version of [isEditable].
     *
     * @see isEditable
     */
    fun blockingIsEditable(eventUid: String): Boolean

    /**
     * Check if the event can be edited or not. If you want to know the reason why the event is not editable, check
     * the method [getEditableStatus] for a richer description of the status.
     */
    fun isEditable(eventUid: String): Single<Boolean>

    /**
     * Blocking version of [getEditableStatus].
     *
     * @see getEditableStatus
     */
    fun blockingGetEditableStatus(eventUid: String): EventEditableStatus

    /**
     * Returns the editable status of an event. In case the event is not editable, the result also includes the
     * reason why it is not editable.
     */
    fun getEditableStatus(eventUid: String): Single<EventEditableStatus>

    /**
     * Blocking version of [canAddEventToEnrollment].
     *
     * @see canAddEventToEnrollment
     */
    fun blockingCanAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Boolean

    /**
     * Evaluates if an enrollments accepts more events for a particular programStage.
     *
     * It takes into account the enrollment status and if the program stage is repeatable or not.
     */
    fun canAddEventToEnrollment(enrollmentUid: String, programStageUid: String): Single<Boolean>
}
