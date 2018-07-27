package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.calls.Call;

public interface NoArgumentsCallFactory<T> {
    Call<T> create();
}
