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

package org.hisp.dhis.android.core.sms.mockrepos.testobjects

import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance
import org.hisp.dhis.android.core.sms.domain.model.internal.SMSDataValueSet
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import java.util.Date

@Suppress("TooManyFunctions")
object MockObjects {
    private val sampleDate = Date(1585041172000L)
    const val user = "AIK2aQOJIbj"
    const val enrollmentUid = "jQK0XnMVFIK"
    const val enrollmentUidWithNullEvents = "aQr0XnMVyIq"
    const val enrollmentUidWithoutEvents = "hQKhXnMVLIm"
    const val enrollmentUidWithoutGeometry = "e5xQ7RriVpK"

    @JvmField val enrollmentDate = sampleDate

    @JvmField val incidentDate = sampleDate

    @JvmField val completedDate = sampleDate

    @JvmField val enrollmentStatus = EnrollmentStatus.ACTIVE
    const val latitude = 1.234F
    const val longitude = -0.123F
    const val teiUid = "MmzaWDDruXW"
    const val teiUid2 = "ggg3R9nRSTI"
    const val trackedEntityType = "nEenWmSyUEp"
    const val program = "IpHINAT79UW"
    const val orgUnit = "DiszpKrYNg8"
    const val attributeOptionCombo = "w5hsiyYZfuR"
    const val categoryOptionCombo = "HllvX50cXC0"
    const val eventUid = "gqmgkrLT3XH"
    const val programStage = "bUzhUa4QWbQ"

    @JvmField val eventDate = sampleDate

    @JvmField val dueDate = sampleDate

    @JvmField val eventStatus = EventStatus.COMPLETED
    const val period = "2019"
    const val relationship = "Tj1ddhpeCFL"
    const val relationshipType = "R74HPJyNLs9"
    const val dataSetUid = "R75HPJyNLs2"
    const val dataSetEmptyListUid = "aUztUa3QPbQ"
    const val isCompleted = true

    private fun getEnrollmentBuilder(): Enrollment.Builder {
        return Enrollment.builder()
            .created(Date())
            .lastUpdated(Date())
            .organisationUnit(orgUnit)
            .program(program)
            .enrollmentDate(enrollmentDate)
            .incidentDate(incidentDate)
            .completedDate(completedDate)
            .status(enrollmentStatus)
            .trackedEntityInstance(teiUid)
            .attributeOptionCombo(attributeOptionCombo)
    }

    private fun getTestTEIEnrollment(enrollment: Enrollment): TrackedEntityInstance {
        return TrackedEntityInstance.builder()
            .enrollments(listOf(enrollment))
            .uid(teiUid)
            .trackedEntityType(trackedEntityType)
            .trackedEntityAttributeValues(getTestAttributeValues())
            .build()
    }

    @JvmStatic
    fun getTEIEnrollment(): TrackedEntityInstance {
        val enrollment = getEnrollmentBuilder()
            .events(listOf(getTrackerEvent()))
            .uid(enrollmentUid)
            .geometry(GeometryHelper.createPointGeometry(longitude.toDouble(), latitude.toDouble()))
            .build()
        return getTestTEIEnrollment(enrollment)
    }

    @JvmStatic
    fun getTEIEnrollmentWithoutEvents(): TrackedEntityInstance {
        val enrollment = getEnrollmentBuilder()
            .events(null)
            .uid(enrollmentUidWithNullEvents)
            .build()
        return getTestTEIEnrollment(enrollment)
    }

    @JvmStatic
    fun getTEIEnrollmentWithEventEmpty(): TrackedEntityInstance {
        val enrollment = getEnrollmentBuilder()
            .events(emptyList())
            .uid(enrollmentUidWithoutEvents)
            .build()
        return getTestTEIEnrollment(enrollment)
    }

    @JvmStatic
    fun getTEIEnrollmentWithoutGeometry(): TrackedEntityInstance {
        val enrollment = getEnrollmentBuilder()
            .events(listOf(getTrackerEvent()))
            .uid(enrollmentUidWithoutGeometry)
            .build()
        return getTestTEIEnrollment(enrollment)
    }

    @JvmStatic
    fun getTestAttributeValues(): List<TrackedEntityAttributeValue> {
        return getValues().map { getTestAttributeValue(it.first, it.second) }
    }

    private fun getTestAttributeValue(attr: String, value: String): TrackedEntityAttributeValue {
        return TrackedEntityAttributeValue.builder()
            .value(value)
            .created(Date())
            .lastUpdated(Date())
            .trackedEntityAttribute(attr)
            .trackedEntityInstance(teiUid)
            .build()
    }

    private fun getBaseEvent(): Event {
        return Event.builder()
            .attributeOptionCombo(attributeOptionCombo)
            .uid(eventUid)
            .lastUpdated(Date())
            .trackedEntityDataValues(getTeiDataValues())
            .organisationUnit(orgUnit)
            .eventDate(eventDate)
            .dueDate(dueDate)
            .status(eventStatus)
            .geometry(GeometryHelper.createPointGeometry(longitude.toDouble(), latitude.toDouble()))
            .build()
    }

    @JvmStatic
    fun getSimpleEvent(): Event {
        return getBaseEvent().toBuilder()
            .program(program)
            .build()
    }

    @JvmStatic
    fun getTrackerEvent(): Event {
        return getBaseEvent().toBuilder()
            .programStage(programStage)
            .enrollment(enrollmentUid)
            .build()
    }

    @JvmStatic
    fun getTeiDataValues(): List<TrackedEntityDataValue> {
        return getValues().map { getTeiDataValue(it.first, it.second) }
    }

    private fun getTeiDataValue(dataElement: String, value: String): TrackedEntityDataValue {
        return TrackedEntityDataValue.builder()
            .value(value)
            .created(Date())
            .lastUpdated(Date())
            .dataElement(dataElement)
            .event(eventUid)
            .build()
    }

    @JvmStatic
    internal fun getSMSDataValueSet(): SMSDataValueSet {
        return SMSDataValueSet(getDataValues(), isCompleted)
    }

    @JvmStatic
    internal fun getSMSDataValueSetEmptyList(): SMSDataValueSet {
        return SMSDataValueSet(emptyList(), isCompleted)
    }

    @JvmStatic
    fun getDataValues(): List<DataValue> {
        return getValues().map { getDataValue(it.first, it.second) }
    }

    private fun getDataValue(dataElement: String, value: String): DataValue {
        return DataValue.builder()
            .attributeOptionCombo(attributeOptionCombo)
            .categoryOptionCombo(categoryOptionCombo)
            .dataElement(dataElement)
            .value(value)
            .organisationUnit(orgUnit)
            .period(period)
            .build()
    }

    private fun getValues(): List<Pair<String, String>> {
        return listOf(
            Pair("UXz7xuGCEhU", "2"),
            Pair("X8zyunlgUfM", "Replacement"),
            Pair("a3kGcGDCuk6", "2019"),
            Pair("bx6fsa0t90x", "true"),
        )
    }

    @JvmStatic
    fun getRelationship(): Relationship {
        val from = RelationshipItem.builder()
            .trackedEntityInstance(
                RelationshipItemTrackedEntityInstance.builder()
                    .trackedEntityInstance(teiUid)
                    .build(),
            ).build()
        val to = RelationshipItem.builder()
            .trackedEntityInstance(
                RelationshipItemTrackedEntityInstance.builder()
                    .trackedEntityInstance(teiUid2)
                    .build(),
            ).build()
        return Relationship.builder()
            .from(from)
            .to(to)
            .relationshipType(relationshipType)
            .uid(relationship)
            .build()
    }
}
