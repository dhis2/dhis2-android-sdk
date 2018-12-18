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
final class CategoryEndpointCallFactory extends UidsCallFactoryImpl<Category> {

    private static final int MAX_UID_LIST_SIZE = 90;

    private final SyncHandler<Category> handler;
    private final CategoryService service;

    @Inject
    CategoryEndpointCallFactory(GenericCallData data,
                                APICallExecutor apiCallExecutor,
                                SyncHandler<Category> handler,
                                CategoryService service) {
        super(data, apiCallExecutor);
        this.handler = handler;
        this.service = service;
    }

    @Override
    protected CallFetcher<Category> fetcher(Set<String> uids) {

        return new UidsNoResourceCallFetcher<Category>(uids, MAX_UID_LIST_SIZE, apiCallExecutor) {

            @Override
            protected retrofit2.Call<Payload<Category>> getCall(UidsQuery query) {
                return service.getCategory(
                        CategoryFields.allFields,
                        CategoryFields.uid.in(query.uids()),
                        Boolean.FALSE);
            }
        };
    }

    @Override
    protected CallProcessor<Category> processor() {
        return new TransactionalNoResourceSyncCallProcessor<>(
                data.databaseAdapter(), handler
        );
    }
}