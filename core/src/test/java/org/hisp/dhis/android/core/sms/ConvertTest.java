
package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.smscompression.SMSSubmissionReader;
import org.hisp.dhis.smscompression.models.AttributeValue;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.hisp.dhis.smscompression.models.Metadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ConvertTest {

    @Test
    public void backAndForth() throws Exception {
        Enrollment enrollment = getTestEnrollment();
        ArrayList<TrackedEntityAttributeValue> values = getTestValues();
        String trackedEntityType = "nEenWmSyUEp";

        TestMetadata metadata = new TestMetadata();
        TestRepositories.TestLocalDbRepository testLocalDb =
                new TestRepositories.TestLocalDbRepository(metadata);

        AtomicReference<String> result = new AtomicReference<>();
        new QrCodeCase(testLocalDb)
                .generateTextCode(enrollment, trackedEntityType, values)
                .test()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(value -> {
                    result.set(value);
                    return !value.isEmpty();
                });

        assertNotNull(result.get());
        byte[] smsBytes = Base64.getDecoder().decode(result.get());

        SMSSubmissionReader reader = new SMSSubmissionReader();
        EnrollmentSMSSubmission subm = (EnrollmentSMSSubmission) reader.readSubmission(smsBytes, metadata);
        assertNotNull(subm);
        assertEquals(subm.getUserID(), TestRepositories.TestLocalDbRepository.userId);
        assertEquals(subm.getEnrollment(), enrollment.uid());
        assertEquals(subm.getTrackedEntityInstance(), enrollment.trackedEntityInstance());
        assertEquals(subm.getTrackedEntityType(), trackedEntityType);
        assertEquals(subm.getOrgUnit(), enrollment.organisationUnit());
        assertEquals(subm.getTrackerProgram(), enrollment.program());
        for (AttributeValue item : subm.getValues()) {
            assertTrue(containsAttributeValue(values, item));
        }
    }

    private boolean containsAttributeValue(ArrayList<TrackedEntityAttributeValue> values,
                                           AttributeValue item) {
        for (TrackedEntityAttributeValue value : values) {
            if (Objects.equals(value.trackedEntityAttribute(), item.getAttribute()) &&
                    Objects.equals(value.value(), item.getValue())) {
                return true;
            }
        }
        return false;
    }

    private static Enrollment getTestEnrollment() {
        return Enrollment.builder()
                .uid("jQK0XnMVFIK")
                .created(new Date())
                .lastUpdated(new Date())
                .organisationUnit("DiszpKrYNg8")
                .program("IpHINAT79UW")
                .enrollmentDate(new Date())
                .trackedEntityInstance("MmzaWDDruXW")
                .id(341L).build();
    }

    private static ArrayList<TrackedEntityAttributeValue> getTestValues() {
        ArrayList<TrackedEntityAttributeValue> list = new ArrayList<>();
        list.add(getTestValue("w75KJ2mc4zz", "Anne"));
        list.add(getTestValue("zDhUuAYrxNC", "Anski"));
        list.add(getTestValue("cejWyOfXge6", "Female"));
        list.add(getTestValue("mLur0EGaw9A", "OU test"));
        return list;
    }

    private static TrackedEntityAttributeValue getTestValue(String attr, String value) {
        return TrackedEntityAttributeValue.builder()
                .value(value)
                .created(new Date())
                .lastUpdated(new Date())
                .trackedEntityAttribute(attr)
                .trackedEntityInstance("MmzaWDDruXW")
                .build();
    }

    public static class TestMetadata extends Metadata {
        Enrollment enrollment = getTestEnrollment();

        public List<String> getUsers() {
            return Collections.singletonList(TestRepositories.TestLocalDbRepository.userId);
        }

        public List<String> getTrackedEntityTypes() {
            return Collections.singletonList(enrollment.trackedEntityInstance());
        }

        public List<String> getTrackedEntityAttributes() {
            ArrayList<String> attrs = new ArrayList<>();
            for (TrackedEntityAttributeValue item : getTestValues()) {
                attrs.add(item.trackedEntityAttribute());
            }
            return attrs;
        }

        public List<String> getPrograms() {
            return Collections.singletonList(enrollment.program());
        }

        public List<String> getOrganisationUnits() {
            return Collections.singletonList(enrollment.organisationUnit());
        }
    }
}
