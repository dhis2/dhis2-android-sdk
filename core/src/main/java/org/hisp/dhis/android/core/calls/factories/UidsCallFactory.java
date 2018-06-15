package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.GenericCallData;

import java.util.List;
import java.util.Set;

public interface UidsCallFactory<P> {
    Call<List<P>> create(GenericCallData genericCallData, Set<String> uids);
}