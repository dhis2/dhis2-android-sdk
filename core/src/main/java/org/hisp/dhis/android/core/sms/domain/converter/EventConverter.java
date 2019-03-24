package org.hisp.dhis.android.core.sms.domain.converter;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.List;

import io.reactivex.Single;

public class EventConverter extends Converter<EventConverter.EventData> {
    @Override
    public Single<String> format(@NonNull EventData event) {
        // TODO
        return Single.just(event.event.toString() + event.values.toString());
    }

    public static class EventData implements Converter.DataToConvert {
        private final Event event;
        private final List<TrackedEntityDataValue> values;

        public EventData(Event event, List<TrackedEntityDataValue> values) {
            this.event = event;
            this.values = values;
        }

        @Override
        public BaseDataModel getDataModel() {
            return event;
        }
    }
}
