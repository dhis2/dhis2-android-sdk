package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public abstract class UidsCallFactory<T> {

    public Collection<Callable<List<T>>> create(GenericCallData data, Set<String> uids) {
        List<Set<String>> partitions = Utils.setPartition(uids, getUidLimit());
        List<Callable<List<T>>> calls = new ArrayList<>(partitions.size());
        for (Set<String> partitionUids: partitions) {
            calls.add(createCall(data, partitionUids));
        }
        return calls;
    }

    protected abstract int getUidLimit();

    protected abstract Call<List<T>> createCall(GenericCallData genericCallData, Set<String> uids);
}
