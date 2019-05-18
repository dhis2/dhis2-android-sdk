package org.hisp.dhis.android.core.sms.domain.converter;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.smscompression.models.DataValue;
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
    public Single<? extends SMSSubmission> convert(@NonNull Event e, String user) {
        return Single.fromCallable(() -> {
            SimpleEventSMSSubmission subm = new SimpleEventSMSSubmission();
            subm.setEventProgram(e.program());
            subm.setAttributeOptionCombo(e.attributeOptionCombo());
            subm.setEvent(e.uid());
            subm.setTimestamp(e.lastUpdated());
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
        return getLocalDbRepository().getSimpleEventToSubmit(eventUid);
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
