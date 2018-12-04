package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.EndpointCall;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.common.GenericCallData;

import java.util.List;

public abstract class QueryCallFactoryImpl<P, Q extends BaseQuery> implements QueryCallFactory<P, Q> {

    protected final GenericCallData data;
    protected final APICallExecutor apiCallExecutor;

    protected QueryCallFactoryImpl(GenericCallData data, APICallExecutor apiCallExecutor) {
        this.data = data;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public final Call<List<P>> create(Q query) {
        return new EndpointCall<>(fetcher(query), processor(query));
    }

    protected abstract CallFetcher<P> fetcher(Q query);
    protected abstract CallProcessor<P> processor(Q query);
}