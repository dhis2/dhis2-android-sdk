package org.hisp.dhis.client.sdk.core.enrollment;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import retrofit2.Retrofit;

public final class EnrollmentFactory {
    private EnrollmentFactory() {
        // no instances
    }

    public static EnrollmentInteractor create(@NonNull Retrofit retrofit,
            @NonNull ContentResolver contentResolver) {
        EnrollmentStore enrollmentStore = new EnrollmentStoreImpl(contentResolver);
        EnrollmentApi enrollmentApi = retrofit.create(EnrollmentApi.class);
        return new EnrollmentInteractorImpl(enrollmentStore, enrollmentApi);
    }
}
