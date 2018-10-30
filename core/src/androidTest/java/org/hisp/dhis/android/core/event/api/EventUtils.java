package org.hisp.dhis.android.core.event.api;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;

import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class EventUtils {

    private static CodeGenerator codeGenerator = new CodeGeneratorImpl();

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

        return Event.create(codeGenerator.generate(), enrollmentUid, date, date, null, null, programUid,
                programStageUid, orgunitUid, date, EventStatus.ACTIVE, null, null, null, false, dataValues,
                attributeOptionComboUid, null);
    }

    static Event createValidEvent() {
        Date refDate = getValidDate();
        return createEvent(validEnrollmentUid, refDate, validProgramUid, validProgramStageUid, validOrgUnitUid,
                getValidDataValues(), validCategoryComboOptionUid);
    }

    private static List<TrackedEntityDataValue> getValidDataValues() {
        return Lists.newArrayList(
                TrackedEntityDataValue.create(null, null, validNumberDataElementUid2, null, "40", false),
                TrackedEntityDataValue.create(null, null, validNumberDataElementUid1, null, "20", false)
        );
    }

    private static List<TrackedEntityDataValue> getInvalidDataValues() {
        return Lists.newArrayList(
                TrackedEntityDataValue.create(null, null, validNumberDataElementUid2, null, "string", false),
                TrackedEntityDataValue.create(null, null, validNumberDataElementUid1, null, "false", false)
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

    static void assertEvent(ImportSummary eventSummary, ImportStatus status) {
        assertThat(eventSummary.importStatus()).isEqualTo(status);
    }

}
