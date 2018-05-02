package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import retrofit2.Response;
import retrofit2.Retrofit;

public interface BlockCallFactory<T> {
    Call<Response<T>> create(DatabaseAdapter databaseAdapter, Retrofit retrofit);
}
