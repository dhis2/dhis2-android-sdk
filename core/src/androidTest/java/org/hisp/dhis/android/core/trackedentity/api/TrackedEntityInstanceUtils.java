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

package org.hisp.dhis.android.core.trackedentity.api;

import org.hisp.dhis.android.core.arch.helpers.UidGenerator;
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.internal.BaseImportSummary;
import org.hisp.dhis.android.core.imports.internal.EnrollmentImportSummary;
import org.hisp.dhis.android.core.imports.internal.EventImportSummary;
import org.hisp.dhis.android.core.imports.internal.TEIImportSummary;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class TrackedEntityInstanceUtils {

    private static UidGenerator uidGenerator = new UidGeneratorImpl();

    private static String validOrgUnitUid = "DiszpKrYNg8"; // Ngelehun CHC
    private static String validProgramUid = "IpHINAT79UW"; // Child Programme
    private static String validProgramStageUid = "A03MvHHogjR"; // Birth
    private static String validStringDataElementUid = "H6uSAMO5WLD"; // MCH Apgar Comment
    private static String validNumberDataElementUid = "a3kGcGDCuk6"; // MCH Apgar Score
    private static String trackedEntityTypeUid = "nEenWmSyUEp"; // Person
    private static String validTrackedEntityAttributeUid = "w75KJ2mc4zz"; // First name
    private static FeatureType featureType = FeatureType.POINT;
    private static Geometry geometry = Geometry.builder().type(featureType).coordinates("[-11.96, 9.49]").build();

    private static String validCategoryComboOptionUid = "HllvX50cXC0"; // Default


    private static TrackedEntityInstance createTrackedEntityInstance(String trackedEntityInstanceUid,
                                                              String orgUnitUid,
                                                              List<TrackedEntityAttributeValue> attributes,
                                                              List<Relationship> relationships,
                                                              List<Enrollment> enrollments) {
        Date refDate = getValidDate();

        return TrackedEntityInstanceInternalAccessor
                .insertEnrollments(
                        TrackedEntityInstanceInternalAccessor
                                .insertRelationships(TrackedEntityInstance.builder(), relationships),
                        enrollments)
                .uid(trackedEntityInstanceUid)
                .created(refDate)
                .lastUpdated(refDate)
                .organisationUnit(orgUnitUid)
                .trackedEntityType(trackedEntityTypeUid)
                .geometry(geometry)
                .deleted(false)
                .trackedEntityAttributeValues(attributes)
                .build();
    }

    private static TrackedEntityAttributeValue createTrackedEntityAttributeValue(String attributeUid, String value) {

        return TrackedEntityAttributeValue.builder().value(value).trackedEntityAttribute(attributeUid).build();
    }

    static TrackedEntityInstance createValidTrackedEntityInstance() {
        return createTrackedEntityInstance(
                uidGenerator.generate(),
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.emptyList());
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithInvalidAttribute() {
        return createTrackedEntityInstance(
                uidGenerator.generate(),
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue("invalid_uid", "9")),
                Collections.emptyList(),
                Collections.emptyList());
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithInvalidOrgunit() {
        return createTrackedEntityInstance(
                uidGenerator.generate(),
                "invalid_ou_uid",
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.emptyList());
    }

    static TrackedEntityInstance createValidTrackedEntityInstanceAndEnrollment() {
        String teiUid = uidGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createValidEnrollment(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceAndTwoActiveEnrollment() {
        String teiUid = uidGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Arrays.asList(createValidEnrollment(teiUid), createValidEnrollment(teiUid)));
    }

    static TrackedEntityInstance createValidTrackedEntityInstanceWithFutureEnrollment() {
        String teiUid = uidGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createFutureEnrollment(teiUid)));
    }

    static TrackedEntityInstance createValidTrackedEntityInstanceWithEnrollmentAndEvent() {
        String teiUid = uidGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createValidEnrollmentAndEvent(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithEnrollmentAndFutureEvent() {
        String teiUid = uidGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createEnrollmentAndFutureEvent(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithInvalidDataElement() {
        String teiUid = uidGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createEnrollmentAndEventWithInvalidDataElement(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithValidAndInvalidDataValue() {
        String teiUid = uidGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createEnrollmentAndEventWithValidAndInvalidDataValue(teiUid)));
    }

    static TrackedEntityInstance createTrackedEntityInstanceWithCompletedEnrollmentAndEvent() {
        String teiUid = uidGenerator.generate();
        return createTrackedEntityInstance(
                teiUid,
                validOrgUnitUid,
                Collections.singletonList(createTrackedEntityAttributeValue(validTrackedEntityAttributeUid, "9")),
                Collections.emptyList(),
                Collections.singletonList(createCompletedEnrollmentWithEvent(teiUid)));
    }

    private static Enrollment createValidEnrollment(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = uidGenerator.generate();

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder().build();
    }

    private static Enrollment createFutureEnrollment(String teiUid) {
        Date refDate = getFutureDate();
        String enrollmentUid = uidGenerator.generate();

        return getEnrollment(enrollmentUid, teiUid, refDate).toBuilder().build();
    }

    private static Enrollment createValidEnrollmentAndEvent(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = uidGenerator.generate();
        Event event = createValidEvent(enrollmentUid);

        return EnrollmentInternalAccessor.insertEvents(getEnrollment(enrollmentUid, teiUid, refDate).toBuilder(),
                Collections.singletonList(event))
                .build();
    }

    private static Enrollment createEnrollmentAndFutureEvent(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = uidGenerator.generate();
        Event event = createFutureEvent(enrollmentUid);

        return EnrollmentInternalAccessor.insertEvents(getEnrollment(enrollmentUid, teiUid, refDate).toBuilder(),
                Collections.singletonList(event))
                .build();
    }

    private static Enrollment createEnrollmentAndEventWithInvalidDataElement(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = uidGenerator.generate();
        Event event = createEventWithInvalidDataElement(enrollmentUid);

        return EnrollmentInternalAccessor.insertEvents(getEnrollment(enrollmentUid, teiUid, refDate).toBuilder(),
                Collections.singletonList(event))
                .build();
    }

    private static Enrollment createEnrollmentAndEventWithValidAndInvalidDataValue(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = uidGenerator.generate();
        Event event = createEventWithValidAndInvalidDataValue(enrollmentUid);

        return EnrollmentInternalAccessor.insertEvents(getEnrollment(enrollmentUid, teiUid, refDate).toBuilder(),
                Collections.singletonList(event))
                .build();
    }

    private static Enrollment createCompletedEnrollmentWithEvent(String teiUid) {
        Date refDate = getValidDate();
        String enrollmentUid = uidGenerator.generate();
        Event event = createValidCompletedEvent(enrollmentUid);

        return EnrollmentInternalAccessor.insertEvents(getEnrollment(enrollmentUid, teiUid, refDate).toBuilder(),
                Collections.singletonList(event))
                .status(EnrollmentStatus.COMPLETED)
                .build();
    }

    private static Enrollment getEnrollment(String enrollmentUid, String teiUid, Date refDate) {

        return EnrollmentInternalAccessor.insertEvents(Enrollment.builder(), Collections.emptyList())
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
                .notes(Collections.emptyList())
                .build();
    }

    private static Event createValidEvent(String enrollmentUid) {
        return createEvent(enrollmentUid, getValidDate(),
                Collections.singletonList(TrackedEntityDataValue.builder()
                        .dataElement(validNumberDataElementUid)
                        .value("9")
                        .providedElsewhere(false)
                        .build()));
    }

    private static Event createValidCompletedEvent(String enrollmentUid) {
        Date refDate = getValidDate();
        List<TrackedEntityDataValue> values = Collections.singletonList(TrackedEntityDataValue.builder()
                .dataElement(validNumberDataElementUid)
                .value("9")
                .providedElsewhere(false)
                .build());

        return Event.builder().uid(uidGenerator.generate()).enrollment(enrollmentUid)
                .created(refDate).lastUpdated(refDate).program(validProgramUid).programStage(validProgramStageUid)
                .organisationUnit(validOrgUnitUid).eventDate(refDate).status(EventStatus.COMPLETED).deleted(false)
                .trackedEntityDataValues(values).attributeOptionCombo(validCategoryComboOptionUid)
                .build();
    }

    private static Event createFutureEvent(String enrollmentUid) {
        return createEvent(enrollmentUid, getFutureDate(), Collections.emptyList());
    }

    private static Event createEventWithInvalidDataElement(String enrollmentUid) {
        return createEvent(enrollmentUid, getValidDate(),
                Collections.singletonList(TrackedEntityDataValue.builder()
                        .dataElement("invalidUid")
                        .value("value")
                        .providedElsewhere(false)
                        .build()));
    }

    private static Event createEventWithValidAndInvalidDataValue(String enrollmentUid) {
        return createEvent(enrollmentUid, getValidDate(),
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

    private static Event createEvent(String enrollmentUid, Date refDate,
                                     List<TrackedEntityDataValue> values) {

        return Event.builder().uid(uidGenerator.generate()).enrollment(enrollmentUid)
                .created(refDate).lastUpdated(refDate).program(validProgramUid).programStage(validProgramStageUid)
                .organisationUnit(validOrgUnitUid).eventDate(refDate).status(EventStatus.ACTIVE).deleted(false)
                .trackedEntityDataValues(values).attributeOptionCombo(validCategoryComboOptionUid)
                .build();
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