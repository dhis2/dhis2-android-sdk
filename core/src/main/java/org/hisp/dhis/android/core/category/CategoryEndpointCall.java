package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.calls.factories.ListCallFactory;
import org.hisp.dhis.android.core.calls.factories.ListCallFactoryImpl;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.fetchers.PayloadNoResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.calls.processors.TransactionalNoResourceSyncCallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;

public class CategoryEndpointCall {

    private CategoryEndpointCall() {
    }

    public static ListCallFactory<Category> factory(final CategoryService service) {
        return new ListCallFactoryImpl<Category>() {

            @Override
            protected CallFetcher<Category> fetcher(GenericCallData data) {
                return new PayloadNoResourceCallFetcher<Category>() {

                    @Override
                    protected retrofit2.Call<Payload<Category>> getCall() {
                        return service.getCategory(CategoryFields.allFields, Boolean.FALSE);
                    }
                };
            }

            @Override
            protected CallProcessor<Category> processor(GenericCallData data) {
                return new TransactionalNoResourceSyncCallProcessor<>(
                        data.databaseAdapter(),
                        CategoryHandler.create(data.databaseAdapter()));
            }
        };
    }
}
