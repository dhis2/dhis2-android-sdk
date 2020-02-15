package org.hisp.dhis.android.core.sms.domain.converter.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SmsVersionRepository;
import org.hisp.dhis.smscompression.models.SMSSubmission;
import org.hisp.dhis.smscompression.models.SimpleEventSMSSubmission;

import io.reactivex.Completable;
import io.reactivex.Single;

public class SimpleEventConverter extends Converter<Event> {
    private final String eventUid;

    public SimpleEventConverter(LocalDbRepository localDbRepository,
                                SmsVersionRepository smsVersionRepository,
                                String eventUid) {
        super(localDbRepository, smsVersionRepository);
        this.eventUid = eventUid;
    }

    @Override
    public Single<? extends SMSSubmission> convert(@NonNull Event e, String user, int submissionId) {
        return Single.fromCallable(() -> {
            SimpleEventSMSSubmission subm = new SimpleEventSMSSubmission();
            subm.setSubmissionID(submissionId);
            subm.setEventProgram(e.program());
            subm.setAttributeOptionCombo(e.attributeOptionCombo());
            subm.setEvent(e.uid());
            subm.setTimestamp(e.lastUpdated());
            subm.setValues(ConverterUtils.convertDataValues(e.attributeOptionCombo(), e.trackedEntityDataValues()));
            subm.setOrgUnit(e.organisationUnit());
            subm.setUserID(user);
            subm.setEventStatus(ConverterUtils.convertEventStatus(e.status()));
            return subm;
        });
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateEventSubmissionState(eventUid, state);
    }

    @Override
    Single<Event> readItemFromDb() {
        return getLocalDbRepository().getSimpleEventToSubmit(eventUid);
    }
}
