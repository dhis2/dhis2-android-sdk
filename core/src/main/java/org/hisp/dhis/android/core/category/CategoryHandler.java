package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.GenericHandler;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class CategoryHandler {

    private final GenericHandler<CategoryOption, CategoryOptionModel> categoryOptionHandler;
    private final CategoryCategoryOptionLinkStore categoryCategoryOptionLinkStore;
    private final CategoryStore categoryStore;

    CategoryHandler(
            @NonNull CategoryStore categoryStore,
            @NonNull GenericHandler<CategoryOption, CategoryOptionModel> categoryOptionHandler,
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

            categoryOptionHandler.handleMany(categoryOptions, new CategoryOptionModelBuilder());

            for (CategoryOption option : categoryOptions) {
                CategoryCategoryOptionLinkModel link = newCategoryOption(category, option);

                categoryCategoryOptionLinkStore.insert(link);
            }
        }
    }

    private CategoryCategoryOptionLinkModel newCategoryOption(@NonNull Category category,
            @NonNull CategoryOption option) {

        return CategoryCategoryOptionLinkModel.builder().category(
                category.uid())
                .option(option.uid())
                .build();
    }

    public static CategoryHandler create(DatabaseAdapter databaseAdapter) {
        return new CategoryHandler(
                new CategoryStoreImpl(databaseAdapter),
                CategoryOptionHandler.create(databaseAdapter),
                new CategoryCategoryOptionLinkStoreImpl(databaseAdapter)
        );
    }
}
