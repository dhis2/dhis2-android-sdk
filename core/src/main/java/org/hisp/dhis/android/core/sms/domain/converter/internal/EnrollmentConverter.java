package org.hisp.dhis.android.core.sms.domain.converter.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SmsVersionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;
import org.hisp.dhis.smscompression.models.EnrollmentSMSSubmission;
import org.hisp.dhis.smscompression.models.SMSAttributeValue;
import org.hisp.dhis.smscompression.models.SMSEvent;
import org.hisp.dhis.smscompression.models.SMSSubmission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class EnrollmentConverter extends Converter<TrackedEntityInstance> {

    private final String enrollmentUid;

    public EnrollmentConverter(LocalDbRepository localDbRepository,
                               SmsVersionRepository smsVersionRepository,
                               String enrollmentUid) {
        super(localDbRepository, smsVersionRepository);
        this.enrollmentUid = enrollmentUid;
    }

    @Override
    public Single<? extends SMSSubmission> convert(@NonNull TrackedEntityInstance tei, String user, int submissionId) {
        List<Enrollment> enrollments = TrackedEntityInstanceInternalAccessor.accessEnrollments(tei);
        if (enrollments == null || enrollments.size() != 1) {
            return Single.error(
                    new IllegalArgumentException("Given instance should have single enrollment")
            );
        }

        List<Event> events = EnrollmentInternalAccessor.accessEvents(enrollments.get(0));

        List<TrackedEntityAttributeValue> attributeValues = tei.trackedEntityAttributeValues();
        if (attributeValues == null) {
            return Single.error(
                    new IllegalArgumentException("Given instance should contain attribute values list")
            );
        }

        return Single.fromCallable(() -> {
            Enrollment enrollment = enrollments.get(0);
            EnrollmentSMSSubmission subm = new EnrollmentSMSSubmission();
            subm.setSubmissionID(submissionId);
            subm.setUserID(user);
            subm.setOrgUnit(enrollment.organisationUnit());
            subm.setTrackerProgram(enrollment.program());
            subm.setTrackedEntityType(tei.trackedEntityType());
            subm.setTrackedEntityInstance(enrollment.trackedEntityInstance());
            subm.setEnrollment(enrollment.uid());
            subm.setTimestamp(new Date());
            ArrayList<SMSAttributeValue> values = new ArrayList<>();
            for (TrackedEntityAttributeValue attr : attributeValues) {
                values.add(createAttributeValue(attr.trackedEntityAttribute(), attr.value()));
            }
            subm.setValues(values);

            if (events != null) {
                ArrayList<SMSEvent> smsEvents = new ArrayList<>();
                for (Event event : events) {
                    smsEvents.add(createSMSEvent(event));
                }
                subm.setEvents(smsEvents);
            }
            return subm;
        });
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateEnrollmentSubmissionState(enrollmentUid, state);
    }

    @Override
    public Single<TrackedEntityInstance> readItemFromDb() {
        return getLocalDbRepository().getTeiEnrollmentToSubmit(enrollmentUid);
    }

    private SMSAttributeValue createAttributeValue(String attribute, String value) {
        return new SMSAttributeValue(attribute, value);
    }

    private SMSEvent createSMSEvent(Event e) {
        SMSEvent smsEvent = new SMSEvent();
        smsEvent.setAttributeOptionCombo(e.attributeOptionCombo());
        smsEvent.setEvent(e.uid());
        smsEvent.setEventStatus(ConverterUtils.convertEventStatus(e.status()));
        smsEvent.setProgramStage(e.programStage());
        smsEvent.setTimestamp(e.lastUpdated());
        smsEvent.setValues(ConverterUtils.convertDataValues(e.attributeOptionCombo(), e.trackedEntityDataValues()));
        return smsEvent;
    }
}
