package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;

public class CategoryComboFactory {
    private final ResponseValidator<CategoryCombo> validator;
    private final DatabaseAdapter databaseAdapter;
    private final CategoryComboService categoryComboService;

    private final CategoryOptionComboStore categoryOptionComboStore;
    private final CategoryOptionComboCategoryLinkStoreImpl categoryComboOptionCategoryLinkStore;

    private final ResourceHandler resourceHandler;
    private final CategoryComboHandler categoryComboHandler;
    private final List<DeletableStore> deletableStores;

    public CategoryComboFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        validator = new ResponseValidator<>();
        this.databaseAdapter = databaseAdapter;
        this.categoryComboService = retrofit.create(CategoryComboService.class);
        this.resourceHandler = resourceHandler;
        CategoryComboStore categoryComboStore = new CategoryComboStoreImpl(databaseAdapter);
        this.categoryOptionComboStore = new CategoryOptionComboStoreImpl(databaseAdapter);
        CategoryCategoryComboLinkStore categoryComboOptionLinkStore =
                new CategoryCategoryComboLinkStoreImpl(databaseAdapter);
        this.categoryComboOptionCategoryLinkStore = new CategoryOptionComboCategoryLinkStoreImpl(
                databaseAdapter);
        CategoryOptionComboHandler categoryOptionComboHandler =
                new CategoryOptionComboHandler(categoryOptionComboStore,
                categoryComboOptionCategoryLinkStore);
        this.categoryComboHandler = new CategoryComboHandler(categoryComboStore,
                categoryComboOptionLinkStore,
                categoryOptionComboHandler);

        deletableStores = new ArrayList<>();
        deletableStores.add(categoryComboOptionCategoryLinkStore);
        deletableStores.add(categoryComboStore);
        deletableStores.add(categoryComboOptionLinkStore);
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

    public CategoryOptionComboCategoryLinkStoreImpl getCategoryComboOptionCategoryLinkStore() {
        return categoryComboOptionCategoryLinkStore;
    }

    public CategoryComboHandler getCategoryComboHandler() {
        return categoryComboHandler;
    }

    public CategoryOptionComboStore getCategoryOptionComboStore() {
        return categoryOptionComboStore;
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStores;
    }
}
