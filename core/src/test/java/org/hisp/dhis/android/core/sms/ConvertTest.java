
package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.smscompression.SMSSubmissionReader;
import org.hisp.dhis.smscompression.models.AttributeValue;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ConvertTest {

    @Test
    public void backAndForth() throws Exception {

        TestRepositories.TestLocalDbRepository testLocalDb =
                new TestRepositories.TestLocalDbRepository();

        Enrollment enrollment = TestRepositories.getTestEnrollment(TestRepositories.enrollmentUid, TestRepositories.teiUid);
        AtomicReference<String> result = new AtomicReference<>();
        new QrCodeCase(testLocalDb)
                .generateEnrollmentCode(enrollment.uid())
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
        EnrollmentSMSSubmission subm = (EnrollmentSMSSubmission) reader.readSubmission(smsBytes,
                new TestRepositories.TestMetadata());
        assertNotNull(subm);
        assertEquals(subm.getUserID(), TestRepositories.TestLocalDbRepository.userId);
        assertEquals(subm.getEnrollment(), enrollment.uid());
        assertEquals(subm.getTrackedEntityInstance(), enrollment.trackedEntityInstance());
        assertEquals(subm.getTrackedEntityType(), TestRepositories.trackedEntityType);
        assertEquals(subm.getOrgUnit(), enrollment.organisationUnit());
        assertEquals(subm.getTrackerProgram(), enrollment.program());
        for (AttributeValue item : subm.getValues()) {
            assertTrue(containsAttributeValue(TestRepositories.getTestValues(), item));
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

}
