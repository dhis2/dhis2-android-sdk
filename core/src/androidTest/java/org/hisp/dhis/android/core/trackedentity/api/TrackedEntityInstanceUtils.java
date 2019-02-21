/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity.api;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.imports.BaseImportSummary;
import org.hisp.dhis.android.core.imports.EnrollmentImportSummary;
import org.hisp.dhis.android.core.imports.EventImportSummary;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.TEIImportSummary;
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

    private static String validCategoryComboOptionUid = "HllvX50cXC0"; // Default


    private static TrackedEntityInstance createTrackedEntityInstance(String trackedEntityInstanceUid,
                                                              String orgUnitUid,
                                                              List<TrackedEntityAttributeValue> attributes,
                                                              List<Relationship229Compatible> relationships,
                                                              List<Enrollment> enrollments) {
        Date refDate = getValidDate();

        return TrackedEntityInstance.builder()
                .uid(trackedEntityInstanceUid)
                .created(refDate)
                .lastUpdated(refDate)
                .organisationUnit(orgUnitUid)
                .trackedEntityType(trackedEntityTypeUid)
                .coordinates(coordinates)
                .featureType(featureType)
                .deleted(false)
                .trackedEntityAttributeValues(attributes)
                .relationships(relationships)
                .enrollments(enrollments)
                .build();
    }

    private static TrackedEntityAttributeValue createTrackedEntityAttributeValue(String attributeUid, String value) {

        return TrackedEntityAttributeValue.builder().value(value).trackedEntityAttribute(attributeUid).build();
    }

    static TrackedEntityInstance createValidTrackedEntityInstance() {
        return createTrackedEntityInstance(
                codeGenerator.generate(),
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.emptyList());
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithInvalidAttribute() {
        return createTrackedEntityInstance(
                codeGenerator.generate(),
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue("invalid_uid", "9")),
                Collections.emptyList(),
                Collections.emptyList());
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithInvalidOrgunit() {
        return createTrackedEntityInstance(
                codeGenerator.generate(),
                "invalid_ou_uid",
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.emptyList());
    }

    static TrackedEntityInstance createValidTrackedEntityInstanceAndEnrollment() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createValidEnrollment(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceAndTwoActiveEnrollment() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Arrays.asList(createValidEnrollment(teiUid), createValidEnrollment(teiUid)));
    }

    static TrackedEntityInstance createValidTrackedEntityInstanceWithFutureEnrollment() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createFutureEnrollment(teiUid)));
    }

    static TrackedEntityInstance createValidTrackedEntityInstanceWithEnrollmentAndEvent() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createValidEnrollmentAndEvent(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithEnrollmentAndFutureEvent() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createEnrollmentAndFutureEvent(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithInvalidDataElement() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createEnrollmentAndEventWithInvalidDataElement(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithValidAndInvalidDataValue() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createEnrollmentAndEventWithValidAndInvalidDataValue(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithCompletedEnrollmentAndEvent() {
        String teiUid = codeGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createCompletedEnrollmentWithEvent(teiUid)));
    }

    private static Enrollment createValidEnrollment(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder().build();
    }

    private static Enrollment createFutureEnrollment(String teiUid) {
        Date refDate = getFutureDate();
        String enrollmentUid = codeGenerator.generate();

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder().build();
    }

    private static Enrollment createValidEnrollmentAndEvent(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createValidEvent(teiUid, enrollmentUid);

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
                .events(Collections.singletonList(event)).build();
    }

    private static Enrollment createEnrollmentAndFutureEvent(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createFutureEvent(teiUid, enrollmentUid);

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
                .events(Collections.singletonList(event)).build();
    }

    private static Enrollment createEnrollmentAndEventWithInvalidDataElement(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createEventWithInvalidDataElement(teiUid, enrollmentUid);

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
                .events(Collections.singletonList(event)).build();
    }

    private static Enrollment createEnrollmentAndEventWithValidAndInvalidDataValue(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createEventWithValidAndInvalidDataValue(teiUid, enrollmentUid);

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
                .events(Collections.singletonList(event)).build();
    }

    private static Enrollment createCompletedEnrollmentWithEvent(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = codeGenerator.generate();
        Event event = createValidCompletedEvent(teiUid, enrollmentUid);

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder()
                .status(EnrollmentStatus.COMPLETED)
                .events(Collections.singletonList(event)).build();
    }

    private static Enrollment getEnrollment(String enrollmentUid, String teiUid, Date refDate) {
        return Enrollment.builder().uid(enrollmentUid).created(refDate).lastUpdated(refDate)
                .organisationUnit(validOrgUnitUid).program(validProgramUid).enrollmentDate(refDate).incidentDate(refDate)
                .followUp(false).status(EnrollmentStatus.ACTIVE).trackedEntityInstance(teiUid).deleted(false)
                .events(Collections.emptyList()).notes(Collections.emptyList()).build();
    }

    private static Event createValidEvent(String teiUid, String enrollmentUid) {
        return createEvent(teiUid, enrollmentUid, getValidDate(),
                Collections.singletonList(TrackedEntityDataValue.builder()
                        .dataElement(validNumberDataElementUid)
                        .value("9")
                        .providedElsewhere(false)
                        .build()));
    }

    private static Event createValidCompletedEvent(String teiUid, String enrollmentUid) {
        Date refDate = getValidDate();
        List<TrackedEntityDataValue> values = Collections.singletonList(TrackedEntityDataValue.builder()
                .dataElement(validNumberDataElementUid)
                .value("9")
                .providedElsewhere(false)
                .build());

        return Event.builder().uid(codeGenerator.generate()).enrollment(enrollmentUid)
                .created(refDate).lastUpdated(refDate).program(validProgramUid).programStage(validProgramStageUid)
                .organisationUnit(validOrgUnitUid).eventDate(refDate).status(EventStatus.COMPLETED).deleted(false)
                .trackedEntityDataValues(values).attributeOptionCombo(validCategoryComboOptionUid)
                .trackedEntityInstance(teiUid).build();
    }

    private static Event createFutureEvent(String teiUid, String enrollmentUid) {
        return createEvent(teiUid, enrollmentUid, getFutureDate(), Collections.emptyList());
    }

    private static Event createEventWithInvalidDataElement(String teiUid, String enrollmentUid) {
        return createEvent(teiUid, enrollmentUid, getValidDate(),
                Collections.singletonList(TrackedEntityDataValue.builder()
                        .dataElement("invalidUid")
                        .value("value")
                        .providedElsewhere(false)
                        .build()));
    }

    private static Event createEventWithValidAndInvalidDataValue(String teiUid, String enrollmentUid) {
        return createEvent(teiUid, enrollmentUid, getValidDate(),
                Arrays.asList(
                        TrackedEntityDataValue.builder()
                                .dataElement(validNumberDataElementUid)
                                .value("some comment")
                                .providedElsewhere(false)
                                .build(),
                        TrackedEntityDataValue.builder()
                                .dataElement(validNumberDataElementUid)
                                .value("string! invalid value")
                                .providedElsewhere(false)
                                .build()));
    }

    private static Event createEvent(String teiUid, String enrollmentUid, Date refDate,
                                     List<TrackedEntityDataValue> values) {

        return Event.builder().uid(codeGenerator.generate()).enrollment(enrollmentUid)
                .created(refDate).lastUpdated(refDate).program(validProgramUid).programStage(validProgramStageUid)
                .organisationUnit(validOrgUnitUid).eventDate(refDate).status(EventStatus.ACTIVE).deleted(false)
                .trackedEntityDataValues(values).attributeOptionCombo(validCategoryComboOptionUid)
                .trackedEntityInstance(teiUid).build();
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

    static void assertTei(TEIImportSummary importSummary, ImportStatus status) {
        assertSummary(importSummary, status);
    }

    static void assertEnrollments(TEIImportSummary importSummary, ImportStatus status) {
        for (EnrollmentImportSummary enrollmentSummary : importSummary.enrollments().importSummaries()) {
            assertSummary(enrollmentSummary, status);
        }
    }

    static void assertEvents(TEIImportSummary importSummary, ImportStatus status) {
        for (EnrollmentImportSummary enrollmentSummary :
                importSummary.enrollments().importSummaries()) {
            for (EventImportSummary eventSummary : enrollmentSummary.events().importSummaries()) {
                assertSummary(eventSummary, status);
            }
        }
    }

    private static void assertSummary(BaseImportSummary importSummary, ImportStatus status) {
        assertThat(importSummary.status()).isEqualTo(status);
    }
}