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

package org.hisp.dhis.android.core.event.internal;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.helpers.UidGenerator;
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.internal.EventImportSummary;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class EventUtils {

    private static UidGenerator uidGenerator = new UidGeneratorImpl();

    private static String validEnrollmentUid = "Lo3SHzCnMSm"; // Contraceptives Voucher Program

    private static String validOrgUnitUid = "DiszpKrYNg8"; // Ngelehun CHC
    private static String validProgramUid = "kla3mAPgvCH"; // Contraceptives Voucher Program
    private static String validProgramStageUid = "aNLq9ZYoy9W"; // Contraceptives Voucher Program

    private static String validNumberDataElementUid1 = "W7aC8jLASW8"; // Voucher IMCI
    private static String validNumberDataElementUid2 = "b6dOUjAarHD"; // Voucher HTC

    private static String validCategoryComboOptionUid = "amw2rQP6r6M"; // Default

    private static Event createEvent(String enrollmentUid, Date date, String programUid, String programStageUid,
                                     String orgunitUid, List<TrackedEntityDataValue> dataValues,
                                     String attributeOptionComboUid) {

        return Event.builder()
                .uid(uidGenerator.generate()).enrollment(enrollmentUid).created(date).lastUpdated(date)
                .program(programUid).programStage(programStageUid).organisationUnit(orgunitUid)
                .eventDate(date).status(EventStatus.ACTIVE).trackedEntityDataValues(dataValues)
                .attributeOptionCombo(attributeOptionComboUid).build();
    }

    static Event createValidEvent() {
        Date refDate = getValidDate();
        return createEvent(validEnrollmentUid, refDate, validProgramUid, validProgramStageUid, validOrgUnitUid,
                getValidDataValues(), validCategoryComboOptionUid);
    }

    private static List<TrackedEntityDataValue> getValidDataValues() {
        return Lists.newArrayList(
                TrackedEntityDataValue.builder()
                        .dataElement(validNumberDataElementUid2)
                        .value("40")
                        .providedElsewhere(false)
                        .build(),
                TrackedEntityDataValue.builder()
                        .dataElement(validNumberDataElementUid1)
                        .value("20")
                        .providedElsewhere(false)
                        .build()
        );
    }

    private static List<TrackedEntityDataValue> getInvalidDataValues() {
        return Lists.newArrayList(
                TrackedEntityDataValue.builder()
                        .dataElement(validNumberDataElementUid2)
                        .value("string")
                        .providedElsewhere(false)
                        .build(),
                TrackedEntityDataValue.builder()
                        .dataElement(validNumberDataElementUid1)
                        .value("false")
                        .providedElsewhere(false)
                        .build()
        );
    }

    public static Event createEventWithInvalidOrgunit() {
        Date refDate = getValidDate();
        return createEvent(validEnrollmentUid, refDate, validProgramUid, validProgramStageUid, "invalid_ou",
                getValidDataValues(), validCategoryComboOptionUid);
    }

    public static Event createEventWithInvalidAttributeOptionCombo() {
        Date refDate = getValidDate();
        return createEvent(validEnrollmentUid, refDate, validProgramUid, validProgramStageUid, validOrgUnitUid,
                getValidDataValues(), "HllvX50cXC0");
    }

    public static Event createEventWithFutureDate() {
        Date refDate = getFutureDate();
        return createEvent(validEnrollmentUid, refDate, validProgramUid, validProgramStageUid, validOrgUnitUid,
                getValidDataValues(), validCategoryComboOptionUid);
    }

    public static Event createEventWithInvalidProgram() {
        Date refDate = getValidDate();
        return createEvent(validEnrollmentUid, refDate, "invalid_program", "invalid_program_stage", validOrgUnitUid,
                getValidDataValues(), validCategoryComboOptionUid);
    }

    public static Event createEventWithInvalidDataValues() {
        Date refDate = getValidDate();
        return createEvent(validEnrollmentUid, refDate, validProgramUid, validProgramStageUid, validOrgUnitUid,
                getInvalidDataValues(), validCategoryComboOptionUid);
    }

    private static Date getValidDate() {
        Long newTime = (new Date()).getTime() - (130 * 60 * 1000);
        return new Date(newTime);
    }

    private static Date getFutureDate() {
        Long newTime = (new Date()).getTime() + (2 * 24 * 60 * 60 * 1000);
        return new Date(newTime);
    }


    // Assertions

    static void assertEvent(EventImportSummary eventSummary, ImportStatus status) {
        assertThat(eventSummary.status()).isEqualTo(status);
    }

}
