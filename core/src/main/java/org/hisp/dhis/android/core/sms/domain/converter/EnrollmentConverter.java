package org.hisp.dhis.android.core.sms.domain.converter;

import android.annotation.SuppressLint;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.smscompression.models.AttributeValue;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import io.reactivex.Single;

import static org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter.EnrollmentData;

public class EnrollmentConverter extends Converter<EnrollmentData> {
    final private Metadata metadata;

    public EnrollmentConverter(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public Single<String> format(@NonNull EnrollmentData data) {
        return getSmsSubmissionWriter(metadata).map(smsSubmissionWriter -> {
            EnrollmentSMSSubmission subm = new EnrollmentSMSSubmission();
            subm.setUserID(data.user);
            subm.setOrgUnit(data.enrollment.organisationUnit());
            subm.setTrackerProgram(data.enrollment.program());
            subm.setTrackedEntityType(data.trackedEntityType);
            subm.setTrackedEntityInstance(data.enrollment.trackedEntityInstance());
            subm.setEnrollment(data.enrollment.uid());
            subm.setTimestamp(new Date());
            ArrayList<AttributeValue> values = new ArrayList<>();
            for (TrackedEntityAttributeValue attr : data.attributes) {
                values.add(createAttributeValue(attr.trackedEntityAttribute(), attr.value()));
            }
            subm.setValues(values);
            return base64(smsSubmissionWriter.compress(subm));
        });
    }

    @SuppressLint("NewApi")
    private String base64(byte[] bytes) {
        String encoded;
        try {
            encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Throwable t) {
            encoded = null;
            // not android, so will try with pure java
        }
        if (encoded == null) {
            encoded = java.util.Base64.getEncoder().encodeToString(bytes);
        }
        return encoded;
    }

    private AttributeValue createAttributeValue(String attribute, String value) {
        return new AttributeValue(attribute, value);
    }

    public static class EnrollmentData implements Converter.DataToConvert {
        private final Enrollment enrollment;
        private final Collection<TrackedEntityAttributeValue> attributes;
        private final String user;
        private final String trackedEntityType;

        public EnrollmentData(Enrollment enrollment,
                              String trackedEntityType,
                              Collection<TrackedEntityAttributeValue> attributes,
                              String user) {
            this.enrollment = enrollment;
            this.trackedEntityType = trackedEntityType;
            this.attributes = attributes;
            this.user = user;
        }

        @Override
        public BaseDataModel getDataModel() {
            return enrollment;
        }
    }
}
