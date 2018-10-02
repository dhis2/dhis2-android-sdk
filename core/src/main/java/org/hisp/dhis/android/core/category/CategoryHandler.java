package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

public class CategoryHandler extends IdentifiableSyncHandlerImpl<Category> {

    private final SyncHandler<CategoryOption> categoryOptionHandler;
    private final LinkModelHandler<CategoryOption, CategoryCategoryOptionLinkModel>
            categoryCategoryOptionLinkHandler;

    CategoryHandler(
            @NonNull IdentifiableObjectStore<Category> categoryStore,
            @NonNull SyncHandler<CategoryOption> categoryOptionHandler,
            @NonNull LinkModelHandler<CategoryOption, CategoryCategoryOptionLinkModel>
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
                new LinkModelHandlerImpl<CategoryOption, CategoryCategoryOptionLinkModel>(
                        CategoryCategoryOptionLinkStore.create(databaseAdapter)
                )
        );
    }
}
