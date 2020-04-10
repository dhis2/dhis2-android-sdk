package org.hisp.dhis.android.core.sms.domain.converter.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.helpers.GeometryHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.smscompression.models.SMSSubmission;
import org.hisp.dhis.smscompression.models.SimpleEventSMSSubmission;

import io.reactivex.Completable;
import io.reactivex.Single;

public class SimpleEventConverter extends Converter<Event> {
    private final String eventUid;

    public SimpleEventConverter(LocalDbRepository localDbRepository,
                                DHISVersionManager dhisVersionManager,
                                String eventUid) {
        super(localDbRepository, dhisVersionManager);
        this.eventUid = eventUid;
    }

    @Override
    public Single<? extends SMSSubmission> convert(@NonNull Event e, String user, int submissionId) {
        return Single.fromCallable(() -> {
            SimpleEventSMSSubmission subm = new SimpleEventSMSSubmission();

            subm.setSubmissionID(submissionId);
            subm.setUserID(user);

            subm.setEvent(e.uid());
            subm.setEventDate(e.eventDate());
            subm.setEventStatus(ConverterUtils.convertEventStatus(e.status()));
            subm.setEventProgram(e.program());
            subm.setDueDate(e.dueDate());
            subm.setAttributeOptionCombo(e.attributeOptionCombo());
            subm.setOrgUnit(e.organisationUnit());
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
        return getLocalDbRepository().getSimpleEventToSubmit(eventUid);
    }
}
