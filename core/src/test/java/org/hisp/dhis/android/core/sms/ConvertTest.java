package org.hisp.dhis.android.core.sms;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.smscompression.SMSSubmissionReader;
import org.hisp.dhis.smscompression.models.AttributeValue;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.hisp.dhis.smscompression.models.Metadata;
import org.hisp.dhis.smscompression.models.SMSSubmissionHeader;
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
        EnrollmentModel enrollment = getTestEnrollment();
        ArrayList<TrackedEntityAttributeValueModel> values = getTestValues();

        TestMetadata metadata = new TestMetadata();
        TestRepositories.TestLocalDbRepository testLocalDb =
                new TestRepositories.TestLocalDbRepository(metadata);

        AtomicReference<String> result = new AtomicReference<>();
        new QrCodeCase(testLocalDb)
                .generateTextCode(enrollment, values)
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
        SMSSubmissionHeader header = reader.readHeader(smsBytes);
        EnrollmentSMSSubmission subm = (EnrollmentSMSSubmission) reader.readSubmission(header, metadata);
        assertNotNull(subm);
        assertEquals(subm.getUserID(), TestRepositories.TestLocalDbRepository.userId);
        assertEquals(subm.getEnrollment(), enrollment.uid());
        assertEquals(subm.getTrackedEntityType(), enrollment.trackedEntityInstance());
        assertEquals(subm.getOrgUnit(), enrollment.organisationUnit());
        assertEquals(subm.getTrackerProgram(), enrollment.program());
        for (AttributeValue item : subm.getValues()) {
            assertTrue(containsAttributeValue(values, item));
        }
    }

    private boolean containsAttributeValue(ArrayList<TrackedEntityAttributeValueModel> values,
                                           AttributeValue item) {
        for (TrackedEntityAttributeValueModel value : values) {
            if (Objects.equals(value.trackedEntityAttribute(), item.getAttribute()) &&
                    Objects.equals(value.value(), item.getValue())) {
                return true;
            }
        }
        return false;
    }

    private static EnrollmentModel getTestEnrollment() {
        return new EnrollmentModel() {
            private Date created = new Date();
            private Date updated = new Date();
            private Date enrollmentDate = new Date();

            @NonNull
            @Override
            public String uid() {
                return "jQK0XnMVFIK";
            }

            @Nullable
            @Override
            public Date created() {
                return created;
            }

            @Nullable
            @Override
            public Date lastUpdated() {
                return updated;
            }

            @Nullable
            @Override
            public String createdAtClient() {
                return null;
            }

            @Nullable
            @Override
            public String lastUpdatedAtClient() {
                return null;
            }

            @Nullable
            @Override
            public String organisationUnit() {
                return "DiszpKrYNg8";
            }

            @Nullable
            @Override
            public String program() {
                return "IpHINAT79UW";
            }

            @Nullable
            @Override
            public Date enrollmentDate() {
                return enrollmentDate;
            }

            @Nullable
            @Override
            public Date incidentDate() {
                return null;
            }

            @Nullable
            @Override
            public Boolean followUp() {
                return null;
            }

            @Nullable
            @Override
            public EnrollmentStatus enrollmentStatus() {
                return null;
            }

            @Nullable
            @Override
            public String trackedEntityInstance() {
                return "MmzaWDDruXW";
            }

            @Nullable
            @Override
            public String latitude() {
                return null;
            }

            @Nullable
            @Override
            public String longitude() {
                return null;
            }

            @Nullable
            @Override
            public State state() {
                return null;
            }

            @Nullable
            @Override
            public Long id() {
                return 341L;
            }

            @Override
            public ContentValues toContentValues() {
                return null;
            }
        };
    }

    private static ArrayList<TrackedEntityAttributeValueModel> getTestValues() {
        ArrayList<TrackedEntityAttributeValueModel> list = new ArrayList<>();
        list.add(getTestValue("w75KJ2mc4zz", "Anne"));
        list.add(getTestValue("zDhUuAYrxNC", "Anski"));
        list.add(getTestValue("cejWyOfXge6", "Female"));
        list.add(getTestValue("mLur0EGaw9A", "OU test"));
        return list;
    }

    private static TrackedEntityAttributeValueModel getTestValue(String attr, String value) {
        return new TrackedEntityAttributeValueModel() {
            private Date created = new Date();
            private Date updated = new Date();

            @Nullable
            @Override
            public String value() {
                return value;
            }

            @Nullable
            @Override
            public Date created() {
                return created;
            }

            @Nullable
            @Override
            public Date lastUpdated() {
                return updated;
            }

            @Nullable
            @Override
            public String trackedEntityAttribute() {
                return attr;
            }

            @Nullable
            @Override
            public String trackedEntityInstance() {
                return "MmzaWDDruXW";
            }

            @Nullable
            @Override
            public Long id() {
                return null;
            }

            @Override
            public ContentValues toContentValues() {
                return null;
            }
        };
    }

    public static class TestMetadata extends Metadata {
        EnrollmentModel enrollment = getTestEnrollment();

        public List<String> getUsers() {
            return Collections.singletonList(TestRepositories.TestLocalDbRepository.userId);
        }

        public List<String> getTrackedEntityTypes() {
            return Collections.singletonList(enrollment.trackedEntityInstance());
        }

        public List<String> getTrackedEntityAttributes() {
            ArrayList<String> attrs = new ArrayList<>();
            for (TrackedEntityAttributeValueModel item : getTestValues()) {
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
