
package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.sms.mockrepos.MockLocalDbRepository;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockMetadata;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.smscompression.SMSSubmissionReader;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.hisp.dhis.smscompression.models.SMSAttributeValue;
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
        MockLocalDbRepository testLocalDb = new MockLocalDbRepository();

        AtomicReference<String> result = new AtomicReference<>();
        new QrCodeCase(testLocalDb)
                .generateEnrollmentCode(MockObjects.enrollmentUid)
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
        EnrollmentSMSSubmission subm = (EnrollmentSMSSubmission) reader.readSubmission(
                smsBytes, new MockMetadata());
        assertNotNull(subm);

        assertEquals(subm.getUserID(), MockObjects.user);
        assertEquals(subm.getEnrollment(), MockObjects.enrollmentUid);
        assertEquals(subm.getTrackedEntityInstance(), MockObjects.teiUid);
        assertEquals(subm.getTrackedEntityType(), MockObjects.trackedEntityType);
        assertEquals(subm.getOrgUnit(), MockObjects.orgUnit);
        assertEquals(subm.getTrackerProgram(), MockObjects.program);
        for (SMSAttributeValue item : subm.getValues()) {
            assertTrue(containsAttributeValue(MockObjects.getTestValues(), item));
        }
    }

    private boolean containsAttributeValue(ArrayList<TrackedEntityAttributeValue> values,
                                           SMSAttributeValue item) {
        for (TrackedEntityAttributeValue value : values) {
            if (Objects.equals(value.trackedEntityAttribute(), item.getAttribute()) &&
                    Objects.equals(value.value(), item.getValue())) {
                return true;
            }
        }
        return false;
    }

}
