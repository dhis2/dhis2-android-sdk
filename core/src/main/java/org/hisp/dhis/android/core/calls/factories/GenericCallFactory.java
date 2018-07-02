package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.GenericCallData;

public interface GenericCallFactory<P> {
    Call<P> create(GenericCallData genericCallData);
}