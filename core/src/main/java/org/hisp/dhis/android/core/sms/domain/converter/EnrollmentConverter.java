package org.hisp.dhis.android.core.sms.domain.converter;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.smscompression.models.AttributeValue;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.hisp.dhis.smscompression.models.SMSSubmission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class EnrollmentConverter extends Converter<TrackedEntityInstance> {

    private final String enrollmentUid;
    private final String teiUid;

    public EnrollmentConverter(LocalDbRepository localDbRepository, String enrollmentUid, String teiUid) {
        super(localDbRepository);
        this.enrollmentUid = enrollmentUid;
        this.teiUid = teiUid;
    }

    @Override
    public Single<? extends SMSSubmission> convert(@NonNull TrackedEntityInstance tei, String user) {
        List<Enrollment> enrollments = tei.enrollments();
        if (enrollments == null || enrollments.size() != 1) {
            return Single.error(
                    new IllegalArgumentException("Given instance should have single enrollment")
            );
        }

        List<TrackedEntityAttributeValue> attributeValues = tei.trackedEntityAttributeValues();
        if (attributeValues == null) {
            return Single.error(
                    new IllegalArgumentException("Given instance should contain attribute values list")
            );
        }

        return Single.fromCallable(() -> {
            Enrollment enrollment = enrollments.get(0);
            EnrollmentSMSSubmission subm = new EnrollmentSMSSubmission();
            subm.setUserID(user);
            subm.setOrgUnit(enrollment.organisationUnit());
            subm.setTrackerProgram(enrollment.program());
            subm.setTrackedEntityType(tei.trackedEntityType());
            subm.setTrackedEntityInstance(enrollment.trackedEntityInstance());
            subm.setEnrollment(enrollment.uid());
            subm.setTimestamp(new Date());
            ArrayList<AttributeValue> values = new ArrayList<>();
            for (TrackedEntityAttributeValue attr : attributeValues) {
                values.add(createAttributeValue(attr.trackedEntityAttribute(), attr.value()));
            }
            subm.setValues(values);
            return subm;
        });
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateEnrollmentSubmissionState(enrollmentUid, state);
    }

    @Override
    public Single<TrackedEntityInstance> readItemFromDb() {
        return getLocalDbRepository().getTeiEnrollmentToSubmit(enrollmentUid, teiUid);
    }

    private AttributeValue createAttributeValue(String attribute, String value) {
        return new AttributeValue(attribute, value);
    }
}
