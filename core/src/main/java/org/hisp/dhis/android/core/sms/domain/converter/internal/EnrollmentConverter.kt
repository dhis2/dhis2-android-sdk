/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.sms.domain.converter.internal

import io.reactivex.Completable
import io.reactivex.Single
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission
import org.hisp.dhis.smscompression.models.SMSAttributeValue
import org.hisp.dhis.smscompression.models.SMSEvent
import org.hisp.dhis.smscompression.models.SMSSubmission

internal class EnrollmentConverter(
    localDbRepository: LocalDbRepository,
    dhisVersionManager: DHISVersionManager,
    private val enrollmentUid: String,
) : Converter<TrackedEntityInstance>(localDbRepository, dhisVersionManager) {

    private var trackedEntityInstance: TrackedEntityInstance? = null

    @Suppress("ReturnCount")
    override fun convert(
        tei: TrackedEntityInstance,
        user: String,
        submissionId: Int,
    ): Single<out SMSSubmission> {
        trackedEntityInstance = tei
        val enrollments = tei.enrollments
        if (enrollments == null || enrollments.size != 1) {
            return Single.error<SMSSubmission>(
                IllegalArgumentException("Given instance should have single enrollment"),
            )
        }

        val attributeValues = tei.trackedEntityAttributeValues()
            ?: return Single.error<SMSSubmission>(
                IllegalArgumentException("Given instance should contain attribute values list"),
            )

        return Single.fromCallable {
            val enrollment = enrollments[0]
            val subm = EnrollmentSMSSubmission()

            subm.setSubmissionID(submissionId)
            subm.setUserID(user)

            subm.setEnrollment(enrollment.uid())
            subm.setEnrollmentDate(enrollment.enrollmentDate())
            subm.setEnrollmentStatus(ConverterUtils.convertEnrollmentStatus(enrollment.status()))
            subm.setIncidentDate(enrollment.incidentDate())
            subm.setOrgUnit(enrollment.organisationUnit())
            subm.setTrackerProgram(enrollment.program())
            subm.setTrackedEntityType(tei.trackedEntityType())
            subm.setTrackedEntityInstance(enrollment.trackedEntityInstance())

            if (GeometryHelper.containsAPoint(enrollment.geometry())) {
                subm.setCoordinates(ConverterUtils.convertGeometryPoint(enrollment.geometry()))
            }

            val values = ArrayList<SMSAttributeValue>()
            for (attr in attributeValues) {
                values.add(createAttributeValue(attr.trackedEntityAttribute(), attr.value()))
            }
            subm.setValues(values)

            enrollment.events()?.let { events ->
                val smsEvents = ArrayList<SMSEvent>()
                for (event in events) {
                    smsEvents.add(createSMSEvent(event))
                }
                subm.setEvents(smsEvents)
            }
            subm
        }
    }

    override fun updateSubmissionState(state: State): Completable {
        return localDbRepository.updateEnrollmentSubmissionState(trackedEntityInstance!!, state)
    }

    override fun readItemFromDb(): Single<TrackedEntityInstance> {
        return localDbRepository.getTeiEnrollmentToSubmit(enrollmentUid)
    }

    private fun createAttributeValue(attribute: String?, value: String?): SMSAttributeValue {
        return SMSAttributeValue(attribute, value)
    }

    private fun createSMSEvent(e: Event): SMSEvent {
        val smsEvent = SMSEvent()

        smsEvent.setAttributeOptionCombo(e.attributeOptionCombo())
        smsEvent.setProgramStage(e.programStage())
        smsEvent.setEvent(e.uid())
        smsEvent.setEventStatus(ConverterUtils.convertEventStatus(e.status()))
        smsEvent.setEventDate(e.eventDate())
        smsEvent.setDueDate(e.dueDate())
        smsEvent.setOrgUnit(e.organisationUnit())
        smsEvent.setValues(ConverterUtils.convertDataValues(e.attributeOptionCombo(), e.trackedEntityDataValues()))

        if (GeometryHelper.containsAPoint(e.geometry())) {
            smsEvent.setCoordinates(ConverterUtils.convertGeometryPoint(e.geometry()))
        }

        return smsEvent
    }
}
