package org.hisp.dhis.android.core.trackedentity.api;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.enrollment.note.Note229Compatible;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class TrackedEntityInstanceUtils {

    private static CodeGenerator codeGenerator = new CodeGeneratorImpl();

    private static String validOrgUnitUid = "DiszpKrYNg8"; // Ngelehun CHC
    private static String validProgramUid = "IpHINAT79UW"; // Child Programme
    private static String validProgramStageUid = "A03MvHHogjR"; // Birth
    private static String validStringDataElementUid = "H6uSAMO5WLD"; // MCH Apgar Comment
    private static String validNumberDataElementUid = "a3kGcGDCuk6"; // MCH Apgar Score
    private static String trackedEntityTypeUid = "nEenWmSyUEp"; // Person
    private static String validTrackedEntityAttributeUid = "w75KJ2mc4zz"; // First name
    private static String coordinates = "[9,9]";
    private static FeatureType featureType = FeatureType.POINT;

    private static String validCategoryOptionUid = "xYerKDKCefk"; // Default
    private static String validCategoryComboOptionUid = "HllvX50cXC0"; // Default


    private static TrackedEntityInstance createTrackedEntityInstance(String trackedEntityInstanceUid,
                                                              String orgUnitUid,
                                                              List<TrackedEntityAttributeValue> attributes,
                                                              List<Relationship229Compatible> relationships,
                                                              List<Enrollment> enrollments) {
        Date refDate = getValidDate();

        return TrackedEntityInstance.create(trackedEntityInstanceUid, refDate, refDate, null,
                null, orgUnitUid, trackedEntityTypeUid, coordinates, featureType, false,
                attributes, relationships, enrollments);
    }

    private static TrackedEntityAttributeValue createTrackedEntityAttributeValue(String attributeUid,
                                                                          String value) {
        return TrackedEntityAttributeValue.create(attributeUid, value, null, null);
    }

    static TrackedEntityInstance createValidTrackedEntityInstance() {
        return createTrackedEntityInstance(
                codeGenerator.generate(),
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.<Enrollment>emptyList());
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithInvalidAttribute() {
        return createTrackedEntityInstance(
                codeGenerator.generate(),
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue("invalid_uid", "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.<Enrollment>emptyList());
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithInvalidOrgunit() {
        return createTrackedEntityInstance(
                codeGenerator.generate(),
                "invalid_ou_uid",
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.<Enrollment>emptyList());
    }

    static TrackedEntityInstance createValidTrackedEntityInstanceAndEnrollment() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.singletonList(createValidEnrollment(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceAndTwoActiveEnrollment() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Arrays.asList(createValidEnrollment(teiUid), createValidEnrollment(teiUid)));
    }

    static TrackedEntityInstance createValidTrackedEntityInstanceWithFutureEnrollment() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.singletonList(createFutureEnrollment(teiUid)));
    }

    static TrackedEntityInstance createValidTrackedEntityInstanceWithEnrollmentAndEvent() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.singletonList(createValidEnrollmentAndEvent(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithEnrollmentAndFutureEvent() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.singletonList(createEnrollmentAndFutureEvent(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithInvalidDataElement() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.singletonList(createEnrollmentAndEventWithInvalidDataElement(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithValidAndInvalidDataValue() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.singletonList(createEnrollmentAndEventWithValidAndInvalidDataValue(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithCompletedEnrollmentAndEvent() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.<Relationship229Compatible>emptyList(),
                Collections.singletonList(createCompletedEnrollmentWithEvent(teiUid)));
    }

    private static Enrollment createValidEnrollment(String teiUid) {
        Date refDate = getValidDate();

        return Enrollment.create(codeGenerator.generate(), refDate, refDate, null, null, validOrgUnitUid,
                validProgramUid, refDate, refDate, false, EnrollmentStatus.ACTIVE, teiUid, null, false,
                Collections.<Event>emptyList(), Collections.<Note229Compatible>emptyList());
    }

    private static Enrollment createFutureEnrollment(String teiUid) {
        Date refDate = getFutureDate();

        return Enrollment.create(codeGenerator.generate(), refDate, refDate, null, null, validOrgUnitUid,
                validProgramUid, refDate, refDate, false, EnrollmentStatus.ACTIVE, teiUid, null, false,
                Collections.<Event>emptyList(), Collections.<Note229Compatible>emptyList());
    }

    private static Enrollment createValidEnrollmentAndEvent(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createValidEvent(teiUid, enrollmentUid);

        return Enrollment.create(enrollmentUid, refDate, refDate, null, null, validOrgUnitUid,
                validProgramUid, refDate, refDate, false, EnrollmentStatus.ACTIVE, teiUid, null, false,
                Collections.singletonList(event), Collections.<Note229Compatible>emptyList());
    }

    private static Enrollment createEnrollmentAndFutureEvent(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createFutureEvent(teiUid, enrollmentUid);

        return Enrollment.create(enrollmentUid, refDate, refDate, null, null, validOrgUnitUid,
                validProgramUid, refDate, refDate, false, EnrollmentStatus.ACTIVE, teiUid, null, false,
                Collections.singletonList(event), Collections.<Note229Compatible>emptyList());
    }

    private static Enrollment createEnrollmentAndEventWithInvalidDataElement(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createEventWithInvalidDataElement(teiUid, enrollmentUid);

        return Enrollment.create(enrollmentUid, refDate, refDate, null, null, validOrgUnitUid,
                validProgramUid, refDate, refDate, false, EnrollmentStatus.ACTIVE, teiUid, null, false,
                Collections.singletonList(event), Collections.<Note229Compatible>emptyList());
    }

    private static Enrollment createEnrollmentAndEventWithValidAndInvalidDataValue(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createEventWithValidAndInvalidDataValue(teiUid, enrollmentUid);

        return Enrollment.create(enrollmentUid, refDate, refDate, null, null, validOrgUnitUid,
                validProgramUid, refDate, refDate, false, EnrollmentStatus.ACTIVE, teiUid, null, false,
                Collections.singletonList(event), Collections.<Note229Compatible>emptyList());
    }

    private static Enrollment createCompletedEnrollmentWithEvent(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createValidCompletedEvent(teiUid, enrollmentUid);

        return Enrollment.create(enrollmentUid, refDate, refDate, null, null, validOrgUnitUid,
                validProgramUid, refDate, refDate, false, EnrollmentStatus.COMPLETED, teiUid, null, false,
                Collections.singletonList(event), Collections.<Note229Compatible>emptyList());
    }

    private static Event createValidEvent(String teiUid, String enrollmentUid) {
        return createEvent(teiUid, enrollmentUid, getValidDate(),
                Collections.singletonList(TrackedEntityDataValue.create(null, null, validNumberDataElementUid,
                        null, "9", false)));
    }

    private static Event createValidCompletedEvent(String teiUid, String enrollmentUid) {
        Date refDate = getValidDate();
        List<TrackedEntityDataValue> values = Collections.singletonList(TrackedEntityDataValue.create(null, null, validNumberDataElementUid,
                null, "9", false));
        return Event.create(codeGenerator.generate(), enrollmentUid, refDate, refDate, null, null, validProgramUid,
                validProgramStageUid, validOrgUnitUid, refDate, EventStatus.COMPLETED, null, null, null, false,
                values, validCategoryOptionUid, validCategoryComboOptionUid, teiUid);
    }

    private static Event createFutureEvent(String teiUid, String enrollmentUid) {
        return createEvent(teiUid, enrollmentUid, getFutureDate(), Collections.<TrackedEntityDataValue>emptyList());
    }

    private static Event createEventWithInvalidDataElement(String teiUid, String enrollmentUid) {
        return createEvent(teiUid, enrollmentUid, getValidDate(),
                Collections.singletonList(TrackedEntityDataValue.create(null, null, "invalidUid", null,
                        "value", false)));
    }

    private static Event createEventWithValidAndInvalidDataValue(String teiUid, String enrollmentUid) {
        return createEvent(teiUid, enrollmentUid, getValidDate(),
                Arrays.asList(
                        TrackedEntityDataValue.create(null, null, validStringDataElementUid, null,
                                "some comment", false),
                        TrackedEntityDataValue.create(null, null, validNumberDataElementUid, null,
                                "string! invalid value", false)));
    }

    private static Event createEvent(String teiUid, String enrollmentUid, Date refDate,
                                     List<TrackedEntityDataValue> values) {
        return Event.create(codeGenerator.generate(), enrollmentUid, refDate, refDate, null, null, validProgramUid,
                validProgramStageUid, validOrgUnitUid, refDate, EventStatus.ACTIVE, null, null, null, false,
                values, validCategoryOptionUid, validCategoryComboOptionUid,
                teiUid);
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

    static void assertTei(ImportSummary importSummary, ImportStatus status) {
        assertSummary(importSummary, status);
    }

    static void assertEnrollments(ImportSummary importSummary, ImportStatus status) {
        for (ImportSummary enrollmentSummary : importSummary.importEnrollment().importSummaries()) {
            assertSummary(enrollmentSummary, status);
        }
    }

    static void assertEvents(ImportSummary importSummary, ImportStatus status) {
        for (ImportSummary enrollmentSummary : importSummary.importEnrollment().importSummaries()) {
            for (ImportSummary eventSummary : enrollmentSummary.importEvent().importSummaries()) {
                assertSummary(eventSummary, status);
            }
        }
    }

    private static void assertSummary(ImportSummary importSummary, ImportStatus status) {
        assertThat(importSummary.importStatus()).isEqualTo(status);
    }
}
