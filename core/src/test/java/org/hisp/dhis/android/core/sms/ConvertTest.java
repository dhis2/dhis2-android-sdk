package org.hisp.dhis.android.core.sms;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Date;

@RunWith(JUnit4.class)
public class ConvertTest {

    @Test
    public void backAndForth() {
        EnrollmentModel enrollment = getTestEnrollment();
        ArrayList<TrackedEntityAttributeValueModel> values = getTestValues();

        new QrCodeCase(new TestRepositories.TestLocalDbRepository())
                .generateTextCode(enrollment, values)
                .test()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(value -> {

                    return true;
                });
    }

    private EnrollmentModel getTestEnrollment() {
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

    private ArrayList<TrackedEntityAttributeValueModel> getTestValues() {
        ArrayList<TrackedEntityAttributeValueModel> list = new ArrayList<>();
        list.add(getTestValue("w75KJ2mc4zz", "Anne"));
        list.add(getTestValue("zDhUuAYrxNC", "Anski"));
        list.add(getTestValue("cejWyOfXge6", "Female"));
        list.add(getTestValue("mLur0EGaw9A", "OU test"));
        return list;
    }

    private TrackedEntityAttributeValueModel getTestValue(String attr, String value) {
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
}
