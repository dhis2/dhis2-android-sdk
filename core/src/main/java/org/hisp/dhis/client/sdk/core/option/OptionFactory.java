package org.hisp.dhis.client.sdk.core.option;

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Retrofit;

public final class OptionFactory {
    private OptionFactory() {
        // no instances
    }

    public static OptionSetInteractor create(@NonNull Retrofit retrofit,
            @NonNull ContentResolver contentResolver, @NonNull ObjectMapper objectMapper,
            @NonNull Context context) {
        OptionSetStore optionSetStore = new OptionSetStoreImpl(contentResolver, objectMapper, context);
        OptionSetApi optionSetApi = retrofit.create(OptionSetApi.class);
        return new OptionSetInteractorImpl(optionSetStore, optionSetApi);
    }
}
