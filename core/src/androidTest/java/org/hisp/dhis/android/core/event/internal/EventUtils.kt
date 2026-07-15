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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.UidGenerator
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.EventImportSummary
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import java.util.Date

internal object EventUtils {

    private val uidGenerator: UidGenerator = UidGeneratorImpl()

    private const val validEnrollmentUid = "Lo3SHzCnMSm" // Contraceptives Voucher Program

    private const val validOrgUnitUid = "DiszpKrYNg8" // Ngelehun CHC
    private const val validProgramUid = "kla3mAPgvCH" // Contraceptives Voucher Program
    private const val validProgramStageUid = "aNLq9ZYoy9W" // Contraceptives Voucher Program

    private const val validNumberDataElementUid1 = "W7aC8jLASW8" // Voucher IMCI
    private const val validNumberDataElementUid2 = "b6dOUjAarHD" // Voucher HTC

    private const val validCategoryComboOptionUid = "amw2rQP6r6M" // Default

    private fun createEvent(
        enrollmentUid: String,
        date: Date,
        programUid: String,
        programStageUid: String,
        orgunitUid: String,
        dataValues: List<TrackedEntityDataValue>,
        attributeOptionComboUid: String,
    ): Event {
        return Event.builder()
            .uid(uidGenerator.generate()).enrollment(enrollmentUid).created(date).lastUpdated(date)
            .program(programUid).programStage(programStageUid).organisationUnit(orgunitUid)
            .eventDate(date).status(EventStatus.ACTIVE).trackedEntityDataValues(dataValues)
            .attributeOptionCombo(attributeOptionComboUid).build()
    }

    fun createValidEvent(): Event {
        val refDate = getValidDate()
        return createEvent(
            validEnrollmentUid,
            refDate,
            validProgramUid,
            validProgramStageUid,
            validOrgUnitUid,
            getValidDataValues(),
            validCategoryComboOptionUid,
        )
    }

    private fun getValidDataValues(): List<TrackedEntityDataValue> {
        return listOf(
            TrackedEntityDataValue.builder()
                .dataElement(validNumberDataElementUid2)
                .value("40")
                .providedElsewhere(false)
                .build(),
            TrackedEntityDataValue.builder()
                .dataElement(validNumberDataElementUid1)
                .value("20")
                .providedElsewhere(false)
                .build(),
        )
    }

    private fun getInvalidDataValues(): List<TrackedEntityDataValue> {
        return listOf(
            TrackedEntityDataValue.builder()
                .dataElement(validNumberDataElementUid2)
                .value("string")
                .providedElsewhere(false)
                .build(),
            TrackedEntityDataValue.builder()
                .dataElement(validNumberDataElementUid1)
                .value("false")
                .providedElsewhere(false)
                .build(),
        )
    }

    fun createEventWithInvalidOrgunit(): Event {
        val refDate = getValidDate()
        return createEvent(
            validEnrollmentUid,
            refDate,
            validProgramUid,
            validProgramStageUid,
            "invalid_ou",
            getValidDataValues(),
            validCategoryComboOptionUid,
        )
    }

    fun createEventWithInvalidAttributeOptionCombo(): Event {
        val refDate = getValidDate()
        return createEvent(
            validEnrollmentUid,
            refDate,
            validProgramUid,
            validProgramStageUid,
            validOrgUnitUid,
            getValidDataValues(),
            "HllvX50cXC0",
        )
    }

    fun createEventWithFutureDate(): Event {
        val refDate = getFutureDate()
        return createEvent(
            validEnrollmentUid,
            refDate,
            validProgramUid,
            validProgramStageUid,
            validOrgUnitUid,
            getValidDataValues(),
            validCategoryComboOptionUid,
        )
    }

    fun createEventWithInvalidProgram(): Event {
        val refDate = getValidDate()
        return createEvent(
            validEnrollmentUid,
            refDate,
            "invalid_program",
            "invalid_program_stage",
            validOrgUnitUid,
            getValidDataValues(),
            validCategoryComboOptionUid,
        )
    }

    fun createEventWithInvalidDataValues(): Event {
        val refDate = getValidDate()
        return createEvent(
            validEnrollmentUid,
            refDate,
            validProgramUid,
            validProgramStageUid,
            validOrgUnitUid,
            getInvalidDataValues(),
            validCategoryComboOptionUid,
        )
    }

    private fun getValidDate(): Date {
        val newTime = Date().time - (130 * 60 * 1000)
        return Date(newTime)
    }

    private fun getFutureDate(): Date {
        val newTime = Date().time + (2 * 24 * 60 * 60 * 1000)
        return Date(newTime)
    }

    // Assertions

    fun assertEvent(eventSummary: EventImportSummary, status: ImportStatus) {
        assertThat(eventSummary.status).isEqualTo(status)
    }
}
