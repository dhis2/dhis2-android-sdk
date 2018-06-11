package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class CategoryHandler {

    private final GenericHandler<CategoryOption, CategoryOptionModel> categoryOptionHandler;
    private final LinkModelHandler<CategoryOption, CategoryCategoryOptionLinkModel>
            categoryCategoryOptionLinkHandler;
    private final CategoryStore categoryStore;

    CategoryHandler(
            @NonNull CategoryStore categoryStore,
            @NonNull GenericHandler<CategoryOption, CategoryOptionModel> categoryOptionHandler,
            @NonNull LinkModelHandler<CategoryOption, CategoryCategoryOptionLinkModel>
                    categoryCategoryOptionLinkHandler) {
        this.categoryStore = categoryStore;
        this.categoryOptionHandler = categoryOptionHandler;
        this.categoryCategoryOptionLinkHandler = categoryCategoryOptionLinkHandler;
    }

    public void handle(Category category) {

        if (isDeleted(category)) {
            categoryStore.delete(category);
        } else {

            boolean updated = categoryStore.update(category);

            if (!updated) {
                categoryStore.insert(category);
            }

            handleCategoryOption(category);
        }
    }

    private void handleCategoryOption(@NonNull Category category) {
        List<CategoryOption> categoryOptions = category.categoryOptions();
        if (categoryOptions != null) {

            categoryOptionHandler.handleMany(categoryOptions, new CategoryOptionModelBuilder());
            categoryCategoryOptionLinkHandler.handleMany(category.uid(), category.categoryOptions(),
                    new CategoryCategoryOptionLinkModelBuilder(category));
        }
    }

    public static CategoryHandler create(DatabaseAdapter databaseAdapter) {
        return new CategoryHandler(
                new CategoryStoreImpl(databaseAdapter),
                CategoryOptionHandler.create(databaseAdapter),
                new LinkModelHandlerImpl<CategoryOption, CategoryCategoryOptionLinkModel>(
                        CategoryCategoryOptionLinkStore.create(databaseAdapter)
                )
        );
    }
}
