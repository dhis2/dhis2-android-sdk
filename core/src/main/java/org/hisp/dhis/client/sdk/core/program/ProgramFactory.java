package org.hisp.dhis.client.sdk.core.program;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Retrofit;

public final class ProgramFactory {
    private ProgramFactory() {
        // no instances
    }

    public static ProgramInteractor create(@NonNull Retrofit retrofit,
                                           @NonNull ContentResolver contentResolver,
                                           @NonNull ObjectMapper objectMapper) {
        ProgramsApi programsApi = retrofit.create(ProgramsApi.class);
        ProgramStore programStore = new ProgramStoreImpl(contentResolver, objectMapper);
        return new ProgramInteractorImpl(programsApi, programStore);
    }
}
