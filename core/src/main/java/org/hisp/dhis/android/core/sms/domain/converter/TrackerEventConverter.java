package org.hisp.dhis.android.core.sms.domain.converter;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.smscompression.models.DataValue;
import org.hisp.dhis.smscompression.models.SMSSubmission;
import org.hisp.dhis.smscompression.models.TrackerEventSMSSubmission;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class TrackerEventConverter extends Converter<Event> {
    private final String eventUid;
    private final String teiUid;

    public TrackerEventConverter(LocalDbRepository localDbRepository, String eventUid, String teiUid) {
        super(localDbRepository);
        this.eventUid = eventUid;
        this.teiUid = teiUid;
    }

    @Override
    public Single<? extends SMSSubmission> convert(@NonNull Event e, String user) {
        return Single.fromCallable(() -> {
            TrackerEventSMSSubmission subm = new TrackerEventSMSSubmission();
            subm.setAttributeOptionCombo(e.attributeOptionCombo());
            subm.setEvent(e.uid());
            subm.setProgramStage(e.programStage());
            subm.setTimestamp(e.lastUpdated());
            subm.setTrackedEntityInstance(e.trackedEntityInstance());
            subm.setValues(convertDataValues(e.attributeOptionCombo(), e.trackedEntityDataValues()));
            subm.setOrgUnit(e.organisationUnit());
            subm.setUserID(user);
            return subm;
        });
    }

    @Override
    public Completable updateSubmissionState(State state) {
        return getLocalDbRepository().updateEventSubmissionState(eventUid, state);
    }

    @Override
    Single<Event> readItemFromDb() {
        return getLocalDbRepository().getTrackerEventToSubmit(eventUid, teiUid);
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    private List<DataValue> convertDataValues(String catOptionCombo,
                                              List<TrackedEntityDataValue> trackedEntityDataValues) {
        ArrayList<DataValue> dataValues = new ArrayList<>();
        if (trackedEntityDataValues == null) {
            return dataValues;
        }
        for (TrackedEntityDataValue tedv : trackedEntityDataValues) {
            dataValues.add(new DataValue(catOptionCombo, tedv.dataElement(), tedv.value()));
        }
        return dataValues;
    }
}
