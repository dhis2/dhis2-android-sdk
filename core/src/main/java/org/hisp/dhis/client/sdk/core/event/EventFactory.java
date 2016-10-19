package org.hisp.dhis.client.sdk.core.event;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import retrofit2.Retrofit;

public final class EventFactory {
    private EventFactory() {
        // no instances
    }

    public static EventInteractor create(@NonNull Retrofit retrofit,
            @NonNull ContentResolver contentResolver) {
        EventStore eventStore = new EventStoreImpl(contentResolver);
        EventApi eventApi = retrofit.create(EventApi.class);
        return new EventInteractorImpl(eventStore, eventApi);
    }
}
