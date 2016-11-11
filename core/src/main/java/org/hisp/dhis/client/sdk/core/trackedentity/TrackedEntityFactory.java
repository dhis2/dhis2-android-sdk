package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import retrofit2.Retrofit;

public final class TrackedEntityFactory {
    private TrackedEntityFactory() {
        // no instances
    }

    public static TrackedEntityInteractor create(@NonNull Retrofit retrofit,
            @NonNull ContentResolver contentResolver) {
        TrackedEntityApi trackedEntityApi = retrofit.create(TrackedEntityApi.class);
        TrackedEntityStore trackedEntityStore = new TrackedEntityStoreImpl(contentResolver);
        return new TrackedEntityInteractorImpl(trackedEntityStore, trackedEntityApi);
    }

    public static TrackedEntityDataValueInteractor create(@NonNull ContentResolver resolver) {
        TrackedEntityDataValueStore trackedEntityDataValueStore =
                new TrackedEntityDataValueStoreImpl(resolver);
        return new TrackedEntityDataValueInteractorImpl(trackedEntityDataValueStore);
    }

    public static TrackedEntityAttributeValueInteractor createTrackedEntityAttributeValueInteractor(@NonNull ContentResolver resolver) {
        TrackedEntityAttributeValueStore trackedEntityAttributeValueStore =
                new TrackedEntityAttributeValueStoreImpl(resolver);
        return new TrackedEntityAttributeValueInteractorImpl(trackedEntityAttributeValueStore);
    }
}
