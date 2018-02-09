package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class CategoryEndpointCall implements Call<Response<Payload<Category>>> {

    private final CategoryQuery categoryQuery;
    private final CategoryService categoryService;
    private final ResponseValidator<Category> responseValidator;
    private final CategoryHandler handler;
    private final ResourceHandler resourceHandler;
    private final DatabaseAdapter databaseAdapter;
    private final Date serverDate;
    private boolean isExecuted;

    public CategoryEndpointCall(CategoryQuery categoryQuery,
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
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<Category>> call() throws Exception {

        validateIsNotTryingToExcuteAgain();

        Response<Payload<Category>> response = categoryService.getCategory(getFields(),
                categoryQuery.paging(),
                categoryQuery.page(), categoryQuery.pageSize(),
                Category.uid.in(categoryQuery.uIds())).execute();

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

    private void validateIsNotTryingToExcuteAgain() {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }
    }

    @NonNull
    private Fields<Category> getFields() {
        return Fields.<Category>builder().fields(Category.uid,
                Category.code, Category.name, Category.displayName,
                Category.created, Category.lastUpdated, Category.deleted,
                Category.shortName, Category.displayName,
                Category.dataDimensionType,
                Category.categoryOptions.with(CategoryOption.uid, CategoryOption.code,
                        CategoryOption.name, CategoryOption.displayName,
                        CategoryOption.created, CategoryOption.lastUpdated, CategoryOption.deleted,
                        CategoryOption.shortName, CategoryOption.displayName))
                .build();

    }
}
