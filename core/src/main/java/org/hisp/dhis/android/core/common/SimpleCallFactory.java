package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.calls.Call;

import retrofit2.Response;

public interface SimpleCallFactory<T> {
    Call<Response<T>> create(GenericCallData genericCallData);
}
