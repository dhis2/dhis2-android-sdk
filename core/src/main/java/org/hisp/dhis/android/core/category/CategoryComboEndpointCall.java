package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactoryImpl;
import org.hisp.dhis.android.core.calls.fetchers.CallFetcher;
import org.hisp.dhis.android.core.calls.fetchers.UidsNoResourceCallFetcher;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.calls.processors.TransactionalNoResourceSyncCallProcessor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.UidsQuery;

import java.util.Set;

public final class CategoryComboEndpointCall {

    private CategoryComboEndpointCall() {
    }

    public static final UidsCallFactory<CategoryCombo> FACTORY = new UidsCallFactoryImpl<CategoryCombo>() {
        private static final int MAX_UID_LIST_SIZE = 100;

        @Override
        protected CallFetcher<CategoryCombo> fetcher(GenericCallData data, Set<String> uids) {
            final CategoryComboService service = data.retrofit().create(CategoryComboService.class);

            return new UidsNoResourceCallFetcher<CategoryCombo>(uids, MAX_UID_LIST_SIZE) {

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
        protected CallProcessor<CategoryCombo> processor(GenericCallData data) {
            return new TransactionalNoResourceSyncCallProcessor<>(
                    data.databaseAdapter(),
                    CategoryComboHandler.create(data.databaseAdapter())
            );
        }
    };
}
