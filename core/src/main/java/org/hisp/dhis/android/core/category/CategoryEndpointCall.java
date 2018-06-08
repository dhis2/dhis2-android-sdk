package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericCallFactory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.List;

public class CategoryEndpointCall extends SyncCall<List<Category>> {

    private final GenericCallData data;
    private final CategoryQuery query;
    private final CategoryService service;
    private final CategoryHandler handler;

    CategoryEndpointCall(
            GenericCallData data,
            CategoryQuery query,
            CategoryService service,
            CategoryHandler handler) {
        this.data = data;
        this.query = query;
        this.service = service;
        this.handler = handler;
    }

    @Override
    public List<Category> call() throws Exception {
        super.setExecuted();

        retrofit2.Call<Payload<Category>> call = service.getCategory(Category.allFields, query.paging(),
                query.page(), query.pageSize());
        List<Category> categories = new APICallExecutor().executePayloadCall(call);
        handle(categories);
        return categories;
    }

    private void handle(List<Category> categories) {
        Transaction transaction = data.databaseAdapter().beginNewTransaction();

        try {
            for (Category category : categories) {
                handler.handle(category);
            }
            data.handleResource(ResourceModel.Type.CATEGORY);
            transaction.setSuccessful();
        } finally {
            transaction.end();
        }
    }

    public static final GenericCallFactory<List<Category>> FACTORY
            = new GenericCallFactory<List<Category>>() {

        @Override
        public Call<List<Category>> create(GenericCallData genericCallData) {
            return new CategoryEndpointCall(
                    genericCallData,
                    CategoryQuery.defaultQuery(),
                    genericCallData.retrofit().create(CategoryService.class),
                    CategoryHandler.create(genericCallData.databaseAdapter())
            );
        }
    };
}
