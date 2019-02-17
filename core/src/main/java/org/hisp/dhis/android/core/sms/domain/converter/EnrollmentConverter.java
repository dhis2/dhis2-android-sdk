package org.hisp.dhis.android.core.sms.domain.converter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.smscompression.models.AttributeValue;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.ArrayList;
import java.util.Collection;

import io.reactivex.Single;

import static org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter.EnrollmentData;

public class EnrollmentConverter extends Converter<EnrollmentData, EnrollmentModel> {

    private static final String TAG = EnrollmentConverter.class.getSimpleName();
    private Metadata metadata;

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
            subm.setTrackedEntityType(enrollment.enrollmentModel.trackedEntityInstance());
            subm.setEnrollment(enrollment.enrollmentModel.uid());
            subm.setTimestamp(enrollment.enrollmentModel.lastUpdated());
            ArrayList<AttributeValue> values = new ArrayList<>();
            for (TrackedEntityAttributeValueModel attr : enrollment.attributes) {
                values.add(new AttributeValue(attr.trackedEntityAttribute(), attr.value()));
            }
            subm.setValues(values);
            byte[] compressSubm = smsSubmissionWriter.compress(subm);
            String encoded;
            try {
                encoded = Base64.encodeToString(compressSubm, Base64.DEFAULT);
            } catch (Throwable t) {
                encoded = java.util.Base64.getEncoder().encodeToString(compressSubm);
            }
            return encoded;
        });
    }

    @Override
    public Single<? extends Collection<String>> getConfirmationRequiredTexts(EnrollmentModel enrollmentModel) {
        // TODO
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
