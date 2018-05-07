package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SimpleCallFactory;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class CategoryEndpointCall extends SyncCall<Response<Payload<Category>>> {

    private final CategoryQuery categoryQuery;
    private final CategoryService categoryService;
    private final ResponseValidator<Category> responseValidator;
    private final CategoryHandler handler;
    private final ResourceHandler resourceHandler;
    private final DatabaseAdapter databaseAdapter;
    private final Date serverDate;

    CategoryEndpointCall(CategoryQuery categoryQuery,
            CategoryService categoryService,
            ResponseValidator<Category> responseValidator,
            CategoryHandler handler,
            ResourceHandler resourceHandler,
            DatabaseAdapter databaseAdapter, Date serverDate) {
        this.categoryQuery = categoryQuery;
        this.categoryService = categoryService;
        this.responseValidator = responseValidator;
        this.handler = handler;
        this.resourceHandler = resourceHandler;
        this.databaseAdapter = databaseAdapter;
        this.serverDate = new Date(serverDate.getTime());
    }

    @Override
    public Response<Payload<Category>> call() throws Exception {
        super.setExecuted();

        Response<Payload<Category>> response = categoryService.getCategory(Category.allFields, categoryQuery.paging(),
                categoryQuery.page(), categoryQuery.pageSize()).execute();

        if (responseValidator.isValid(response)) {
            List<Category> categories = response.body().items();

            handle(categories);
        }

        return response;
    }

    private void handle(List<Category> categories) {
        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            for (Category category : categories) {
                handler.handle(category);
            }
            resourceHandler.handleResource(ResourceModel.Type.CATEGORY, serverDate);
            transaction.setSuccessful();
        } finally {
            transaction.end();
        }
    }

    public static final SimpleCallFactory<Payload<Category>> FACTORY
            = new SimpleCallFactory<Payload<Category>>() {

        @Override
        public Call<Response<Payload<Category>>> create(GenericCallData genericCallData) {
            return new CategoryEndpointCall(
                    CategoryQuery.defaultQuery(),
                    genericCallData.retrofit().create(CategoryService.class),
                    new ResponseValidator<Category>(),
                    CategoryHandler.create(genericCallData.databaseAdapter()),
                    ResourceHandler.create(genericCallData.databaseAdapter()),
                    genericCallData.databaseAdapter(),
                    genericCallData.serverDate()
            );
        }
    };
}
