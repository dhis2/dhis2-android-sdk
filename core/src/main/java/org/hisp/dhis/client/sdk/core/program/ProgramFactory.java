package org.hisp.dhis.client.sdk.core.program;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.MetadataApi;

import retrofit2.Retrofit;

public final class ProgramFactory {
    private ProgramFactory() {
        // no instances
    }

    public static ProgramInteractor create(@NonNull Retrofit retrofit,
            @NonNull ContentResolver contentResolver, @NonNull ObjectMapper objectMapper) {
        ProgramsApi programsApi = retrofit.create(ProgramsApi.class);
        MetadataApi metadataApi = retrofit.create(MetadataApi.class);
        ProgramStore programStore = new ProgramStoreImpl(contentResolver, objectMapper);
        return new ProgramInteractorImpl(programsApi, programStore, metadataApi);
    }
}
