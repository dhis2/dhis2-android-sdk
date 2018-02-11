package org.hisp.dhis.android.core.common;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryEndpointCall;
import org.hisp.dhis.android.core.category.CategoryHandler;
import org.hisp.dhis.android.core.category.CategoryQuery;
import org.hisp.dhis.android.core.category.CategoryService;
import org.hisp.dhis.android.core.category.ResponseValidator;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.Date;

import retrofit2.Retrofit;

public class CategoryCallFactory {

    @NonNull
    public static CategoryEndpointCall create(Retrofit retrofit, DatabaseAdapter databaseAdapter) {
        CategoryService categoryService = retrofit.create(CategoryService.class);

        ResponseValidator<Category> categoryResponseValidator = new ResponseValidator<>();

        CategoryHandler categoryHandler = HandlerFactory.createCategoryHandler(databaseAdapter);

        ResourceHandler resourceHandler = HandlerFactory.createResourceHandler(databaseAdapter);

        CategoryQuery categoryQuery = CategoryQuery.defaultQuery();

        return new CategoryEndpointCall(categoryQuery,
                categoryService, categoryResponseValidator, categoryHandler, resourceHandler,
                databaseAdapter, new Date());

    }
}
