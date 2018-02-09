package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.common.DeletableStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.resource.ResourceHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;

public class CategoryFactory {
    private final DatabaseAdapter databaseAdapter;
    private final CategoryService categoryService;
    private final ResourceHandler resourceHandler;
    private final CategoryHandler categoryHandler;
    private final CategoryOptionHandler categoryOptionHandler;
    private final CategoryStore categoryStore;
    private final CategoryOptionStore categoryOptionStore;
    private final CategoryCategoryOptionLinkStore categoryOptionLinkStore;
    private final ResponseValidator<Category> validator;
    private final List<DeletableStore> deletableStoreList;

    public CategoryFactory(
            Retrofit retrofit, DatabaseAdapter databaseAdapter, ResourceHandler resourceHandler) {
        validator = new ResponseValidator<>();
        this.databaseAdapter = databaseAdapter;
        this.categoryService = retrofit.create(CategoryService.class);
        this.resourceHandler = resourceHandler;
        this.categoryStore = new CategoryStoreImpl(databaseAdapter);
        this.categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter);
        this.categoryOptionLinkStore= new CategoryCategoryOptionLinkStoreImpl(databaseAdapter);
        this.categoryOptionHandler = new CategoryOptionHandler(categoryOptionStore,
                categoryOptionLinkStore);
        this.categoryHandler = new CategoryHandler(categoryStore, categoryOptionHandler);
        this.deletableStoreList = new ArrayList<>();
        this.deletableStoreList.add(categoryStore);
        this.deletableStoreList.add(categoryOptionStore);
        this.deletableStoreList.add(categoryOptionLinkStore);
    }

    public CategoryEndpointCall newEndPointCall(CategoryQuery categoryQuery, Date serverDate) throws Exception {
        return new CategoryEndpointCall(categoryQuery, categoryService, validator,
                categoryHandler, resourceHandler, databaseAdapter, serverDate);
    }

    public ResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    public CategoryHandler getCategoryHandler() {
        return categoryHandler;
    }

    public CategoryOptionHandler getCategoryOptionHandler() {
        return categoryOptionHandler;
    }

    public CategoryStore getCategoryStore() {
        return categoryStore;
    }

    public CategoryOptionStore getCategoryOptionStore() {
        return categoryOptionStore;
    }

    public CategoryCategoryOptionLinkStore getCategoryOptionLinkStore() {
        return categoryOptionLinkStore;
    }

    public List<DeletableStore> getDeletableStores() {
        return deletableStoreList;
    }
}
