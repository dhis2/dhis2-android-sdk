package org.hisp.dhis.android.core.sms.domain.converter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.smscompression.models.AttributeValue;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.ArrayList;
import java.util.Collection;

import io.reactivex.Single;

import static org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter.EnrollmentData;

public class EnrollmentConverter extends Converter<EnrollmentData, Enrollment> {
    final private Metadata metadata;

    public EnrollmentConverter(Metadata metadata) {
        this.metadata = metadata;
    }

    @SuppressLint("NewApi")
    @Override
    public Single<String> format(@NonNull EnrollmentData enrollment) {
        return getSmsSubmissionWriter(metadata).map(smsSubmissionWriter -> {
            EnrollmentSMSSubmission subm = new EnrollmentSMSSubmission();
            subm.setUserID(enrollment.user);
            subm.setOrgUnit(enrollment.enrollmentModel.organisationUnit());
            subm.setTrackerProgram(enrollment.enrollmentModel.program());
            //TODO no type in model
            subm.setTrackedEntityType(enrollment.enrollmentModel.trackedEntityInstance());
            subm.setTrackedEntityInstance(enrollment.enrollmentModel.trackedEntityInstance());
            subm.setEnrollment(enrollment.enrollmentModel.uid());
            subm.setTimestamp(enrollment.enrollmentModel.lastUpdated());
            ArrayList<AttributeValue> values = new ArrayList<>();
            for (TrackedEntityAttributeValue attr : enrollment.attributes) {
                values.add(createAttributeValue(attr.trackedEntityAttribute(), attr.value()));
            }
            subm.setValues(values);
            byte[] compressSubm = smsSubmissionWriter.compress(subm);
            String encoded;
            try {
                encoded = Base64.encodeToString(compressSubm, Base64.DEFAULT);
            } catch (Throwable t) {
                encoded = null;
                // will try with standard java
            }
            if (encoded == null) {
                encoded = java.util.Base64.getEncoder().encodeToString(compressSubm);
            }
            return encoded;
        });
    }

    private AttributeValue createAttributeValue(String attribute, String value) {
        return new AttributeValue(attribute, value);
    }

    @Override
    public Single<? extends Collection<String>> getConfirmationRequiredTexts(Enrollment enrollmentModel) {
        // TODO
        return null;
    }

    public static class EnrollmentData implements Converter.DataToConvert {
        private final Enrollment enrollmentModel;
        private final Collection<TrackedEntityAttributeValue> attributes;
        private final String user;

        public EnrollmentData(Enrollment enrollmentModel,
                              Collection<TrackedEntityAttributeValue> attributes,
                              String user) {
            this.enrollmentModel = enrollmentModel;
            this.attributes = attributes;
            this.user = user;
        }

        @Override
        public BaseDataModel getDataModel() {
            return enrollmentModel;
        }
    }
}
