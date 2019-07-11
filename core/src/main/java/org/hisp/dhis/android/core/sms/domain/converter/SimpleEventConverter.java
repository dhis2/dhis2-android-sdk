package org.hisp.dhis.android.core.sms.domain.converter;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.smscompression.SMSConsts;
import org.hisp.dhis.smscompression.models.SMSDataValue;
import org.hisp.dhis.smscompression.models.SMSSubmission;
import org.hisp.dhis.smscompression.models.SimpleEventSMSSubmission;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class SimpleEventConverter extends Converter<Event> {
    private final String eventUid;

    public SimpleEventConverter(LocalDbRepository localDbRepository, String eventUid) {
        super(localDbRepository);
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
            subm.setValues(convertDataValues(e.attributeOptionCombo(), e.trackedEntityDataValues()));
            subm.setOrgUnit(e.organisationUnit());
            subm.setUserID(user);
            subm.setEventStatus(translateStatus(e.status()));
            return subm;
        });
    }

    static SMSConsts.SMSEventStatus translateStatus(EventStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case ACTIVE:
                return SMSConsts.SMSEventStatus.ACTIVE;
            case COMPLETED:
                return SMSConsts.SMSEventStatus.COMPLETED;
            case SCHEDULE:
                return SMSConsts.SMSEventStatus.SCHEDULE;
            case SKIPPED:
                return SMSConsts.SMSEventStatus.SKIPPED;
            case VISITED:
                return SMSConsts.SMSEventStatus.VISITED;
            case OVERDUE:
                return SMSConsts.SMSEventStatus.OVERDUE;
            default:
                return null;
        }
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateEventSubmissionState(eventUid, state);
    }

    @Override
    Single<Event> readItemFromDb() {
        return getLocalDbRepository().getSimpleEventToSubmit(eventUid);
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    private List<SMSDataValue> convertDataValues(String catOptionCombo,
                                                 List<TrackedEntityDataValue> trackedEntityDataValues) {
        ArrayList<SMSDataValue> dataValues = new ArrayList<>();
        if (trackedEntityDataValues == null) {
            return dataValues;
        }
        for (TrackedEntityDataValue tedv : trackedEntityDataValues) {
            dataValues.add(new SMSDataValue(catOptionCombo, tedv.dataElement(), tedv.value()));
        }
        return dataValues;
    }
}
