package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.calls.Call;

import java.util.Set;

import retrofit2.Response;

public interface UidsCallFactory<T> {
    Call<Response<Payload<T>>> create(GenericCallData genericCallData, Set<String> uids);
}
