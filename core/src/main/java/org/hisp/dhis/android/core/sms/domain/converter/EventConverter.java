package org.hisp.dhis.android.core.sms.domain.converter;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;

import java.util.Collection;
import java.util.List;

public class EventConverter implements Converter<EventConverter.EventData, EventModel> {
    @Override
    public String format(@NonNull EventData event) {
        return event.event.toString() + event.values.toString();
    }

    @Override
    public Collection<String> getConfirmationRequiredTexts(EventModel dataObject) {
        return null;
    }

    public static class EventData implements Converter.DataToConvert {
        private final EventModel event;
        private final List<TrackedEntityDataValueModel> values;

        public EventData(EventModel event, List<TrackedEntityDataValueModel> values) {
            this.event = event;
            this.values = values;
        }

        @Override
        public BaseDataModel getDataModel() {
            return event;
        }
    }
}
