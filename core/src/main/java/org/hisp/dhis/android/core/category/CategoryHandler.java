package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

import java.util.List;

public class CategoryHandler {

    private final CategoryOptionHandler categoryOptionHandler;
    private final CategoryCategoryOptionLinkStore categoryCategoryOptionLinkStore;
    private final CategoryStore categoryStore;

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
            categoryStore.delete(category.uid());
        } else {

            int numberOfRows = categoryStore.update(category);
            boolean updated = numberOfRows >= 1;

            if (!updated) {
                categoryStore.insert(category);
                handleCategoryOption(category);
            }
        }
    }

    private void handleCategoryOption(@NonNull Category category) {
        List<CategoryOption> categoryOptions = category.categoryOptions();
        if (categoryOptions != null) {

            for (CategoryOption option : categoryOptions) {
                categoryOptionHandler.handle(option);

                CategoryCategoryOptionLinkModel link = newCategoryOption(category, option);

                categoryCategoryOptionLinkStore.insert(link);
            }
        }
    }

    private CategoryCategoryOptionLinkModel newCategoryOption(@NonNull Category category,
            @NonNull CategoryOption option) {

        return CategoryCategoryOptionLinkModel.builder().category(
                category.uid())
                .categoryOption(option.uid())
                .build();
    }
}
