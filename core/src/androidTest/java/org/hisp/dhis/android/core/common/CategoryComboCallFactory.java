package org.hisp.dhis.android.core.common;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboEndpointCall;
import org.hisp.dhis.android.core.category.CategoryComboHandler;
import org.hisp.dhis.android.core.category.CategoryComboQuery;
import org.hisp.dhis.android.core.category.CategoryComboService;
import org.hisp.dhis.android.core.category.ResponseValidator;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.Date;

import retrofit2.Retrofit;

public class CategoryComboCallFactory {
    @NonNull
    public static CategoryComboEndpointCall create(Retrofit retrofit,
            DatabaseAdapter databaseAdapter) {
        CategoryComboService categoryComboService = retrofit.create(CategoryComboService.class);

        ResponseValidator<CategoryCombo> categoryComboResponseValidator = new ResponseValidator<>();

        CategoryComboHandler categoryComboHandler = HandlerFactory.createCategoryComboHandler(
                databaseAdapter);

        ResourceHandler resourceHandler = HandlerFactory.createResourceHandler(databaseAdapter);

        CategoryComboQuery categoryComboQuery = CategoryComboQuery.defaultQuery();

        return new CategoryComboEndpointCall(
                categoryComboQuery,
                categoryComboService, categoryComboResponseValidator, categoryComboHandler,
                resourceHandler,
                databaseAdapter, new Date());

    }
}
