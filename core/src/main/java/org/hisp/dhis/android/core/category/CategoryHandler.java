package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.List;

public class CategoryHandler {
    @VisibleForTesting
    private final CategoryStore categoryStore;
    private final CategoryOptionHandler categoryOptionHandler;
    private final CategoryCategoryOptionLinkStore categoryCategoryOptionLinkStore;

    public CategoryHandler(
            @NonNull CategoryStore categoryStore,
            @NonNull CategoryOptionHandler categoryOptionHandler,
            @NonNull CategoryCategoryOptionLinkStore categoryCategoryOptionLinkStore) {
        this.categoryStore = categoryStore;
        this.categoryOptionHandler = categoryOptionHandler;
        this.categoryCategoryOptionLinkStore = categoryCategoryOptionLinkStore;
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

            for (CategoryOption option : categoryOptions) {
                categoryOptionHandler.handle(category.uid(), option);
            }
        }
    }
}
