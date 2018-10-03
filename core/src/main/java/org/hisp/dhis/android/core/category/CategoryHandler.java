package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandler;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandlerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

class CategoryHandler extends IdentifiableSyncHandlerImpl<Category> {

    private final SyncHandler<CategoryOption> categoryOptionHandler;
    private final OrderedLinkModelHandler<CategoryOption, CategoryCategoryOptionLinkModel>
            categoryCategoryOptionLinkHandler;

    CategoryHandler(
            @NonNull IdentifiableObjectStore<Category> categoryStore,
            @NonNull SyncHandler<CategoryOption> categoryOptionHandler,
            @NonNull OrderedLinkModelHandler<CategoryOption, CategoryCategoryOptionLinkModel>
                    categoryCategoryOptionLinkHandler) {
        super(categoryStore);
        this.categoryOptionHandler = categoryOptionHandler;
        this.categoryCategoryOptionLinkHandler = categoryCategoryOptionLinkHandler;
    }

    @Override
    protected void afterObjectHandled(Category category, HandleAction handleAction) {
        List<CategoryOption> categoryOptions = category.categoryOptions();
        if (categoryOptions != null) {
            categoryOptionHandler.handleMany(categoryOptions);
            categoryCategoryOptionLinkHandler.handleMany(category.uid(), category.categoryOptions(),
                    new CategoryCategoryOptionLinkModelBuilder(category));
        }
    }

    public static CategoryHandler create(DatabaseAdapter databaseAdapter) {
        return new CategoryHandler(
                CategoryStore.create(databaseAdapter),
                CategoryOptionHandler.create(databaseAdapter),
                new OrderedLinkModelHandlerImpl<CategoryOption, CategoryCategoryOptionLinkModel>(
                        CategoryCategoryOptionLinkStore.create(databaseAdapter)
                )
        );
    }
}
