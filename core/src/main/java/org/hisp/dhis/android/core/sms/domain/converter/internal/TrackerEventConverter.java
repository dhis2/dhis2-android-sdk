package org.hisp.dhis.android.core.sms.domain.converter.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.helpers.GeometryHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SmsVersionRepository;
import org.hisp.dhis.smscompression.models.SMSSubmission;
import org.hisp.dhis.smscompression.models.TrackerEventSMSSubmission;

import io.reactivex.Completable;
import io.reactivex.Single;

public class TrackerEventConverter extends Converter<Event> {
    private final String eventUid;

    public TrackerEventConverter(LocalDbRepository localDbRepository,
                                 SmsVersionRepository smsVersionRepository,
                                 String eventUid) {
        super(localDbRepository, smsVersionRepository);
        this.eventUid = eventUid;
    }

    @Override
    public Single<? extends SMSSubmission> convert(@NonNull Event e, String user, int submissionId) {
        return Single.fromCallable(() -> {
            TrackerEventSMSSubmission subm = new TrackerEventSMSSubmission();

            subm.setSubmissionID(submissionId);
            subm.setUserID(user);

            subm.setEvent(e.uid());
            subm.setEventDate(e.eventDate());
            subm.setEventStatus(ConverterUtils.convertEventStatus(e.status()));;
            subm.setProgramStage(e.programStage());
            subm.setDueDate(e.dueDate());
            subm.setAttributeOptionCombo(e.attributeOptionCombo());
            subm.setOrgUnit(e.organisationUnit());
            subm.setEnrollment(e.enrollment());
            subm.setValues(ConverterUtils.convertDataValues(e.attributeOptionCombo(), e.trackedEntityDataValues()));

            if (GeometryHelper.containsAPoint(e.geometry())) {
                subm.setCoordinates(ConverterUtils.convertGeometryPoint(e.geometry()));
            }

            return subm;
        });
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateEventSubmissionState(eventUid, state);
    }

    @Override
    Single<Event> readItemFromDb() {
        return getLocalDbRepository().getTrackerEventToSubmit(eventUid);
    }
}
