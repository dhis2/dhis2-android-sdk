package org.hisp.dhis.android.core.sms.domain.converter;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;

import java.util.Collection;

import static org.hisp.dhis.android.core.sms.domain.converter.EnrollmentConverter.EnrollmentData;

public class EnrollmentConverter implements Converter<EnrollmentData, EnrollmentModel> {
    @Override
    public String format(@NonNull EnrollmentData enrollment) {
        return enrollment.enrollmentModel.toString() + enrollment.attributes.toString();
    }

    @Override
    public Collection<String> getConfirmationRequiredTexts(EnrollmentModel enrollmentModel) {
        return null;
    }

    public static class EnrollmentData implements Converter.DataToConvert {
        private final EnrollmentModel enrollmentModel;
        private final Collection<TrackedEntityAttributeValueModel> attributes;

        public EnrollmentData(EnrollmentModel enrollmentModel,
                              Collection<TrackedEntityAttributeValueModel> attributes) {
            this.enrollmentModel = enrollmentModel;
            this.attributes = attributes;
        }

        @Override
        public BaseDataModel getDataModel() {
            return enrollmentModel;
        }
    }
}
