package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.EndpointCall;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;

import java.util.List;

public abstract class ListCallFactoryImpl<P> implements ListCallFactory<P> {

    protected final GenericCallData data;
    protected final APICallExecutor apiCallExecutor;

    protected ListCallFactoryImpl(GenericCallData data, APICallExecutor apiCallExecutor) {
        this.data = data;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public final Call<List<P>> create() {
        return new EndpointCall<>(fetcher(), processor());
    }

    protected abstract CallFetcher<P> fetcher();
    protected abstract CallProcessor<P> processor();
}