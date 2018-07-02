package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.calls.EndpointCall;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;

import java.util.List;

public abstract class ListCallFactoryImpl<P> implements ListCallFactory<P> {

    @Override
    public final Call<List<P>> create(GenericCallData genericCallData) {
        return new EndpointCall<>(fetcher(genericCallData), processor(genericCallData));
    }

    protected abstract CallFetcher<P> fetcher(GenericCallData data);
    protected abstract CallProcessor<P> processor(GenericCallData data);
}