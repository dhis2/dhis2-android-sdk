package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.calls.factories.ListCallFactory;
import org.hisp.dhis.android.core.calls.factories.ListCallFactoryImpl;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.fetchers.PayloadNoResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.calls.processors.TransactionalNoResourceSyncCallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;

import retrofit2.Retrofit;

public final class CategoryComboEndpointCall {

    private CategoryComboEndpointCall() {
    }

    public static ListCallFactory<CategoryCombo> factory(Retrofit retrofit) {

        final CategoryComboService service = retrofit.create(CategoryComboService.class);

        return new ListCallFactoryImpl<CategoryCombo>() {

            @Override
            protected CallFetcher<CategoryCombo> fetcher(GenericCallData data) {
                return new PayloadNoResourceCallFetcher<CategoryCombo>() {

                    @Override
                    protected retrofit2.Call<Payload<CategoryCombo>> getCall() {
                        return service.getCategoryCombos(CategoryComboFields.allFields, Boolean.FALSE);
                    }
                };
            }

            @Override
            protected CallProcessor<CategoryCombo> processor(GenericCallData data) {
                return new TransactionalNoResourceSyncCallProcessor<>(
                        data.databaseAdapter(),
                        CategoryComboHandler.create(data.databaseAdapter()));
            }
        };
    }
}
