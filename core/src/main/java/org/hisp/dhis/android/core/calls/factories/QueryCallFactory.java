package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.EndpointCall;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.common.GenericCallData;

import java.util.List;

public abstract class QueryCallFactory<P, Q extends BaseQuery> {
    public final Call<List<P>> create(GenericCallData data, Q query) {
        return new EndpointCall<>(fetcher(data, query), processor(data, query));
    }

    protected abstract CallFetcher<P> fetcher(GenericCallData data, Q query);
    protected abstract CallProcessor<P> processor(GenericCallData data, Q query);
}