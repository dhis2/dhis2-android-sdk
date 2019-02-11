package org.hisp.dhis.android.core.sms.domain.converter;

import android.support.annotation.NonNull;
import android.util.Base64;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.smscompression.SMSSubmissionWriter;
import org.hisp.dhis.smscompression.models.AttributeValue;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;

import java.util.ArrayList;
import java.util.Collection;

import static org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter.EnrollmentData;

public class EnrollmentConverter implements Converter<EnrollmentData, EnrollmentModel> {
    @Override
    public String format(@NonNull EnrollmentData enrollment) throws Exception {
        EnrollmentSMSSubmission subm = new EnrollmentSMSSubmission();
        subm.setUserID(enrollment.user);
        subm.setOrgUnit(enrollment.enrollmentModel.organisationUnit());
        subm.setTrackerProgram(enrollment.enrollmentModel.program());
        subm.setTrackedEntityType(type);
        subm.setEnrollment(enrollment.enrollmentModel.uid());
        subm.setTimestamp(enrollment.enrollmentModel.lastUpdated());
        ArrayList<AttributeValue> values = new ArrayList<>();
        for (TrackedEntityAttributeValueModel attr : enrollment.attributes) {
            values.add(new AttributeValue(attr.trackedEntityAttribute(), attr.value()));
        }
        subm.setValues(values);

        SMSSubmissionWriter writer = new SMSSubmissionWriter(metadata);
        byte[] compressSubm = writer.compress(subm);
        return Base64.encodeToString(compressSubm, Base64.DEFAULT);
    }

    @Override
    public Collection<String> getConfirmationRequiredTexts(EnrollmentModel enrollmentModel) {
        return null;
    }

    public static class EnrollmentData implements Converter.DataToConvert {
        private final EnrollmentModel enrollmentModel;
        private final Collection<TrackedEntityAttributeValueModel> attributes;
        private final String user;

        public EnrollmentData(EnrollmentModel enrollmentModel,
                              Collection<TrackedEntityAttributeValueModel> attributes,
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
