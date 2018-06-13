package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.calls.Call;

import java.util.List;
import java.util.Set;

public abstract class UidsCallFactory<T> {

    protected abstract int getUidLimit();

    public abstract Call<List<T>> create(GenericCallData genericCallData, Set<String> uids);
}
