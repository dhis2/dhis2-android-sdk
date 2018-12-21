package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactoryImpl;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.fetchers.UidsNoResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.calls.processors.TransactionalNoResourceSyncCallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.UidsQuery;

import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class CategoryComboEndpointCallFactory extends UidsCallFactoryImpl<CategoryCombo> {

    private static final int MAX_UID_LIST_SIZE = 130;

    private final CategoryComboService service;
    private final SyncHandler<CategoryCombo> handler;

    @Inject
    CategoryComboEndpointCallFactory(GenericCallData data,
                                     APICallExecutor apiCallExecutor,
                                     CategoryComboService service,
                                     SyncHandler<CategoryCombo> handler) {
        super(data, apiCallExecutor);
        this.service = service;
        this.handler = handler;
    }

    @Override
    protected CallFetcher<CategoryCombo> fetcher(Set<String> uids) {

        return new UidsNoResourceCallFetcher<CategoryCombo>(uids, MAX_UID_LIST_SIZE, apiCallExecutor) {

            @Override
            protected retrofit2.Call<Payload<CategoryCombo>> getCall(UidsQuery query) {
                return service.getCategoryCombos(
                        CategoryComboFields.allFields,
                        CategoryComboFields.uid.in(query.uids()),
                        Boolean.FALSE);
            }
        };
    }

    @Override
    protected CallProcessor<CategoryCombo> processor() {
        return new TransactionalNoResourceSyncCallProcessor<>(
                data.databaseAdapter(), handler);
    }
}
