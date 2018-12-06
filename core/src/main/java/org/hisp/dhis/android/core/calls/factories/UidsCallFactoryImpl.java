package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.EndpointCall;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;

import java.util.List;
import java.util.Set;

public abstract class UidsCallFactoryImpl<P> implements UidsCallFactory<P> {

    protected final GenericCallData data;
    protected final APICallExecutor apiCallExecutor;

    protected UidsCallFactoryImpl(GenericCallData data, APICallExecutor apiCallExecutor) {
        this.data = data;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public final Call<List<P>> create(Set<String> uids) {
        return new EndpointCall<>(fetcher(uids), processor());
    }

    protected abstract CallFetcher<P> fetcher(Set<String> uids);
    protected abstract CallProcessor<P> processor();
}