package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.calls.Call;

public interface GenericCallFactory<T> {
    Call<T> create(GenericCallData genericCallData);
}
