package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.ResponseValidator;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;

import java.util.List;

import retrofit2.Response;

public class CategoryCallEndpoint implements Call<Response<Payload<Category>>> {
    private final CategoryQuery categoryQuery;
    private final CategoryService categoryService;
    private final ResponseValidator<Category> responseValidator;
    private boolean isExecuted;
    private final DatabaseAdapter databaseAdapter;


    public CategoryCallEndpoint(@NonNull CategoryQuery categoryQuery,
            @NonNull CategoryService categoryService,
            @NonNull ResponseValidator<Category> responseValidator,
            @NonNull DatabaseAdapter databaseAdapter) {

        this.categoryQuery = categoryQuery;
        this.categoryService = categoryService;
        this.responseValidator = responseValidator;
        this.databaseAdapter = databaseAdapter;
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
                categoryQuery.page(), categoryQuery.pageSize()).execute();

        if (responseValidator.isValid(response)) {
            List<Category> categories = response.body().items();

            save(categories);


        }

        return response;
    }

    private void save(List<Category> categories) {
        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            for (Category category : categories) {

            }
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
                Category.categoryOptions.with(CategoryOption.uid)).build();

    }
}
