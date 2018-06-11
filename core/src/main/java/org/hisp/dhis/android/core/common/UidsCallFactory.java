package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.calls.Call;

import java.util.List;
import java.util.Set;

public interface UidsCallFactory<T> {
    Call<List<T>> create(GenericCallData genericCallData, Set<String> uids);
}
