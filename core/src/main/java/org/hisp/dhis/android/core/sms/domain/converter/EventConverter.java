package org.hisp.dhis.android.core.sms.domain.converter;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.smscompression.models.DataValue;
import org.hisp.dhis.smscompression.models.Metadata;
import org.hisp.dhis.smscompression.models.TrackerEventSMSSubmission;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

public class EventConverter extends Converter<EventConverter.EventData> {
    final private Metadata metadata;

    public EventConverter(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public Single<String> format(@NonNull EventData event) {
        return getSmsSubmissionWriter(metadata).map(smsSubmissionWriter -> {
            Event e = event.event;
            TrackerEventSMSSubmission subm = new TrackerEventSMSSubmission();
            subm.setAttributeOptionCombo(e.attributeOptionCombo());
            subm.setEvent(e.uid());
            subm.setProgramStage(e.programStage());
            subm.setTimestamp(e.lastUpdated());
            subm.setTrackedEntityInstance(e.trackedEntityInstance());
            subm.setValues(convertDataValues(e.trackedEntityDataValues()));
            subm.setOrgUnit(e.organisationUnit());
            subm.setUserID(event.user);
            return base64(smsSubmissionWriter.compress(subm));
        });
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    private List<DataValue> convertDataValues(List<TrackedEntityDataValue> trackedEntityDataValues) {
        ArrayList<DataValue> dataValues = new ArrayList<>();
        if (trackedEntityDataValues == null) {
            return dataValues;
        }
        for (TrackedEntityDataValue tedv : trackedEntityDataValues) {
            dataValues.add(new DataValue(null, tedv.dataElement(), tedv.value()));
        }
        return dataValues;
    }

    public static class EventData implements Converter.DataToConvert {
        private final Event event;
        private final String user;

        public EventData(Event event, String user) {
            this.event = event;
            this.user = user;
        }

        @Override
        public BaseDataModel getDataModel() {
            return event;
        }
    }
}
