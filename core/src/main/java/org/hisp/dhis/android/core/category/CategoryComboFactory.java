package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.Date;

import retrofit2.Retrofit;

public class CategoryComboFactory {
    private final ResponseValidator<CategoryCombo> validator;
    private final DatabaseAdapter databaseAdapter;
    private final CategoryComboService categoryComboService;

    private final CategoryComboStore categoryComboStore;
    private final CategoryCategoryComboLinkStore categoryComboOptionLinkStore;
    private final CategoryOptionComboStore categoryOptionComboStore;
    private final CategoryOptionComboCategoryLinkStoreImpl categoryComboOptionCategoryLinkStore;

    private final ResourceHandler resourceHandler;
    private final CategoryComboHandler categoryComboHandler;
    private final CategoryOptionComboHandler categoryOptionComboHandler;

    public CategoryComboFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        validator = new ResponseValidator<>();
        this.databaseAdapter = databaseAdapter;
        this.categoryComboService = retrofit.create(CategoryComboService.class);
        this.resourceHandler = resourceHandler;
        this.categoryComboStore = new CategoryComboStoreImpl(databaseAdapter);
        this.categoryOptionComboStore = new CategoryOptionComboStoreImpl(databaseAdapter);
        this.categoryComboOptionLinkStore = new CategoryCategoryComboLinkStoreImpl(databaseAdapter);
        this.categoryComboOptionCategoryLinkStore = new CategoryOptionComboCategoryLinkStoreImpl(
                databaseAdapter);
        this.categoryOptionComboHandler = new CategoryOptionComboHandler(categoryOptionComboStore, categoryComboOptionCategoryLinkStore);
        this.categoryComboHandler = new CategoryComboHandler(categoryComboStore,
                categoryComboOptionLinkStore,
                categoryOptionComboHandler);
    }

    public CategoryComboEndpointCall newEndPointCall(CategoryComboQuery categoryComboQuery,
            Date serverDate) throws Exception {
        return new CategoryComboEndpointCall(categoryComboQuery, categoryComboService, validator,
                categoryComboHandler, resourceHandler, databaseAdapter, serverDate);
    }

    public ResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    public DatabaseAdapter getDatabaseAdapter() {
        return databaseAdapter;
    }

    public CategoryComboStore getCategoryComboStore() {
        return categoryComboStore;
    }

    public CategoryCategoryComboLinkStore getCategoryComboOptionLinkStore() {
        return categoryComboOptionLinkStore;
    }

    public CategoryOptionComboCategoryLinkStoreImpl getCategoryComboOptionCategoryLinkStore() {
        return categoryComboOptionCategoryLinkStore;
    }

    public CategoryComboHandler getCategoryComboHandler() {
        return categoryComboHandler;
    }

    public CategoryComboService getCategoryComboService() {
        return categoryComboService;
    }

    public CategoryOptionComboStore getCategoryOptionComboStore() {
        return categoryOptionComboStore;
    }

    public CategoryOptionComboHandler getCategoryOptionComboHandler() {
        return categoryOptionComboHandler;
    }
}
