package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.EndpointCall;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;

import java.util.List;
import java.util.Set;

public abstract class UidsCallFactory<P> {
    public final Call<List<P>> create(GenericCallData genericCallData, Set<String> uids) {
        return new EndpointCall<>(fetcher(genericCallData, uids), processor(genericCallData));
    }

    protected abstract CallFetcher<P> fetcher(GenericCallData data, Set<String> uids);
    protected abstract CallProcessor<P> processor(GenericCallData data);
}