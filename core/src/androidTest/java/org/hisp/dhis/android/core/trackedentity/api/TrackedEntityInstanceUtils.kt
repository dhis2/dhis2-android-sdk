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

package org.hisp.dhis.android.core.trackedentity.api

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.UidGenerator
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.BaseImportSummary
import org.hisp.dhis.android.core.imports.internal.TEIImportSummary
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import java.util.Date

@Suppress("TooManyFunctions")
internal object TrackedEntityInstanceUtils {

    private val uidGenerator: UidGenerator = UidGeneratorImpl()

    private const val validOrgUnitUid = "DiszpKrYNg8" // Ngelehun CHC
    private const val validProgramUid = "IpHINAT79UW" // Child Programme
    private const val validProgramStageUid = "A03MvHHogjR" // Birth
    private const val validNumberDataElementUid = "a3kGcGDCuk6" // MCH Apgar Score
    private const val trackedEntityTypeUid = "nEenWmSyUEp" // Person
    private const val validTrackedEntityAttributeUid = "w75KJ2mc4zz" // First name
    private val featureType = FeatureType.POINT
    private val geometry = Geometry.builder().type(featureType).coordinates("[-11.96, 9.49]").build()

    private const val validCategoryComboOptionUid = "HllvX50cXC0" // Default

    private fun createTrackedEntityInstance(
        trackedEntityInstanceUid: String,
        orgUnitUid: String,
        attributes: List<TrackedEntityAttributeValue>,
        relationships: List<Relationship>,
        enrollments: List<Enrollment>,
    ): TrackedEntityInstance {
        val refDate = getValidDate()

        return TrackedEntityInstance.builder()
            .relationships(relationships)
            .enrollments(enrollments)
            .uid(trackedEntityInstanceUid)
            .created(refDate)
            .lastUpdated(refDate)
            .organisationUnit(orgUnitUid)
            .trackedEntityType(trackedEntityTypeUid)
            .geometry(geometry)
            .deleted(false)
            .trackedEntityAttributeValues(attributes)
            .build()
    }

    private fun createTrackedEntityAttributeValue(
        attributeUid: String,
        value: String,
        teiUid: String,
    ): TrackedEntityAttributeValue {
        return TrackedEntityAttributeValue.builder()
            .value(value)
            .trackedEntityAttribute(attributeUid)
            .trackedEntityInstance(teiUid)
            .build()
    }

    fun createValidTrackedEntityInstance(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            emptyList(),
        )
    }

    fun createTrackedEntityInstanceWithInvalidAttribute(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue("invalid_uid", "9", teiUid)),
            emptyList(),
            emptyList(),
        )
    }

    fun createTrackedEntityInstanceWithInvalidOrgunit(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            "invalid_ou_uid",
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            emptyList(),
        )
    }

    fun createValidTrackedEntityInstanceAndEnrollment(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            listOf(createValidEnrollment(teiUid)),
        )
    }

    fun createTrackedEntityInstanceAndTwoActiveEnrollment(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            listOf(createValidEnrollment(teiUid), createValidEnrollment(teiUid)),
        )
    }

    fun createValidTrackedEntityInstanceWithFutureEnrollment(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            listOf(createFutureEnrollment(teiUid)),
        )
    }

    fun createValidTrackedEntityInstanceWithEnrollmentAndEvent(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            listOf(createValidEnrollmentAndEvent(teiUid)),
        )
    }

    fun createTrackedEntityInstanceWithEnrollmentAndFutureEvent(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            listOf(createEnrollmentAndFutureEvent(teiUid)),
        )
    }

    fun createTrackedEntityInstanceWithInvalidDataElement(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            listOf(createEnrollmentAndEventWithInvalidDataElement(teiUid)),
        )
    }

    fun createTrackedEntityInstanceWithValidAndInvalidDataValue(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            listOf(createEnrollmentAndEventWithValidAndInvalidDataValue(teiUid)),
        )
    }

    fun createTrackedEntityInstanceWithCompletedEnrollmentAndEvent(): TrackedEntityInstance {
        val teiUid = uidGenerator.generate()
        return createTrackedEntityInstance(
            teiUid,
            validOrgUnitUid,
            listOf(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9", teiUid)),
            emptyList(),
            listOf(createCompletedEnrollmentWithEvent(teiUid)),
        )
    }

    private fun createValidEnrollment(teiUid: String): Enrollment {
        val refDate = getValidDate()
        val enrollmentUid = uidGenerator.generate()

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder().build()
    }

    private fun createFutureEnrollment(teiUid: String): Enrollment {
        val refDate = getFutureDate()
        val enrollmentUid = uidGenerator.generate()

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder().build()
    }

    private fun createValidEnrollmentAndEvent(teiUid: String): Enrollment {
        val refDate = getValidDate()
        val enrollmentUid = uidGenerator.generate()
        val event = createValidEvent(enrollmentUid)

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
            .events(listOf(event))
            .build()
    }

    private fun createEnrollmentAndFutureEvent(teiUid: String): Enrollment {
        val refDate = getValidDate()
        val enrollmentUid = uidGenerator.generate()
        val event = createFutureEvent(enrollmentUid)

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
            .events(listOf(event))
            .build()
    }

    private fun createEnrollmentAndEventWithInvalidDataElement(teiUid: String): Enrollment {
        val refDate = getValidDate()
        val enrollmentUid = uidGenerator.generate()
        val event = createEventWithInvalidDataElement(enrollmentUid)

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
            .events(listOf(event))
            .build()
    }

    private fun createEnrollmentAndEventWithValidAndInvalidDataValue(teiUid: String): Enrollment {
        val refDate = getValidDate()
        val enrollmentUid = uidGenerator.generate()
        val event = createEventWithValidAndInvalidDataValue(enrollmentUid)

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
            .events(listOf(event))
            .build()
    }

    private fun createCompletedEnrollmentWithEvent(teiUid: String): Enrollment {
        val refDate = getValidDate()
        val enrollmentUid = uidGenerator.generate()
        val event = createValidCompletedEvent(enrollmentUid)

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
            .events(listOf(event))
            .status(EnrollmentStatus.COMPLETED)
            .build()
    }

    private fun getEnrollment(enrollmentUid: String, teiUid: String, refDate: Date): Enrollment {
        return Enrollment.builder()
            .events(emptyList())
            .uid(enrollmentUid)
            .created(refDate)
            .lastUpdated(refDate)
            .organisationUnit(validOrgUnitUid)
            .program(validProgramUid)
            .enrollmentDate(refDate)
            .incidentDate(refDate)
            .completedDate(refDate)
            .followUp(false)
            .status(EnrollmentStatus.ACTIVE)
            .trackedEntityInstance(teiUid)
            .deleted(false)
            .notes(emptyList())
            .attributeOptionCombo("bRowv6yZOF2")
            .build()
    }

    private fun createValidEvent(enrollmentUid: String): Event {
        return createEvent(
            enrollmentUid,
            getValidDate(),
            listOf(
                TrackedEntityDataValue.builder()
                    .dataElement(validNumberDataElementUid)
                    .value("9")
                    .providedElsewhere(false)
                    .build(),
            ),
        )
    }

    private fun createValidCompletedEvent(enrollmentUid: String): Event {
        val refDate = getValidDate()
        val values = listOf(
            TrackedEntityDataValue.builder()
                .dataElement(validNumberDataElementUid)
                .value("9")
                .providedElsewhere(false)
                .build(),
        )

        return Event.builder().uid(uidGenerator.generate()).enrollment(enrollmentUid)
            .created(refDate).lastUpdated(refDate).program(validProgramUid).programStage(validProgramStageUid)
            .organisationUnit(validOrgUnitUid).eventDate(refDate).status(EventStatus.COMPLETED).deleted(false)
            .trackedEntityDataValues(values).attributeOptionCombo(validCategoryComboOptionUid)
            .build()
    }

    private fun createFutureEvent(enrollmentUid: String): Event {
        return createEvent(enrollmentUid, getFutureDate(), emptyList())
    }

    private fun createEventWithInvalidDataElement(enrollmentUid: String): Event {
        return createEvent(
            enrollmentUid,
            getValidDate(),
            listOf(
                TrackedEntityDataValue.builder()
                    .dataElement("invalidUid")
                    .value("value")
                    .providedElsewhere(false)
                    .build(),
            ),
        )
    }

    private fun createEventWithValidAndInvalidDataValue(enrollmentUid: String): Event {
        return createEvent(
            enrollmentUid,
            getValidDate(),
            listOf(
                TrackedEntityDataValue.builder()
                    .dataElement(validNumberDataElementUid)
                    .value("some comment")
                    .providedElsewhere(false)
                    .build(),
                TrackedEntityDataValue.builder()
                    .dataElement(validNumberDataElementUid)
                    .value("string! invalid value")
                    .providedElsewhere(false)
                    .build(),
            ),
        )
    }

    private fun createEvent(
        enrollmentUid: String,
        refDate: Date,
        values: List<TrackedEntityDataValue>,
    ): Event {
        return Event.builder().uid(uidGenerator.generate()).enrollment(enrollmentUid)
            .created(refDate).lastUpdated(refDate).program(validProgramUid).programStage(validProgramStageUid)
            .organisationUnit(validOrgUnitUid).eventDate(refDate).status(EventStatus.ACTIVE).deleted(false)
            .trackedEntityDataValues(values).attributeOptionCombo(validCategoryComboOptionUid)
            .build()
    }

    private fun getValidDate(): Date {
        val newTime = Date().time - (130 * 60 * 1000)
        return Date(newTime)
    }

    private fun getFutureDate(): Date {
        val newTime = Date().time + (2L * 24 * 60 * 60 * 1000)
        return Date(newTime)
    }

    // Assertions

    fun assertTei(importSummary: TEIImportSummary, status: ImportStatus) {
        assertSummary(importSummary, status)
    }

    fun assertEnrollments(importSummary: TEIImportSummary, status: ImportStatus) {
        for (enrollmentSummary in importSummary.enrollments!!.importSummaries!!) {
            assertSummary(enrollmentSummary, status)
        }
    }

    fun assertEvents(importSummary: TEIImportSummary, status: ImportStatus) {
        for (enrollmentSummary in importSummary.enrollments!!.importSummaries!!) {
            for (eventSummary in enrollmentSummary.events!!.importSummaries!!) {
                assertSummary(eventSummary, status)
            }
        }
    }

    private fun assertSummary(importSummary: BaseImportSummary, status: ImportStatus) {
        assertThat(importSummary.status).isEqualTo(status)
    }
}
