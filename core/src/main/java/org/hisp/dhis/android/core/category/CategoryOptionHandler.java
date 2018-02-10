package org.hisp.dhis.android.core.category;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

public class CategoryOptionHandler {

    @NonNull
    private final CategoryOptionStore store;

    @NonNull
    private final CategoryCategoryOptionLinkStore categoryCategoryOptionLinkStore;

    public CategoryOptionHandler(@NonNull CategoryOptionStore store,
            @NonNull CategoryCategoryOptionLinkStore categoryCategoryOptionLinkStore) {
        this.store = store;
        this.categoryCategoryOptionLinkStore = categoryCategoryOptionLinkStore;
    }

    public void handle(@NonNull String categoryUId, @NonNull CategoryOption categoryOption) {

        if (isDeleted(categoryOption)) {
            store.delete(categoryOption.uid());
        } else {

            int numberOfRows = store.update(categoryOption);
            boolean updated = numberOfRows >= 1;

            if (!updated) {
                store.insert(categoryOption);

                CategoryCategoryOptionLinkModel link = newCategoryOption(categoryUId,
                        categoryOption);

                categoryCategoryOptionLinkStore.insert(link);
            }
        }
    }

    private CategoryCategoryOptionLinkModel newCategoryOption(@NonNull String categoryUId,
            @NonNull CategoryOption option) {

        return CategoryCategoryOptionLinkModel.builder()
                .category(categoryUId)
                .categoryOption(option.uid())
                .build();
    }
}
