package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

import java.util.List;

public class CategoryHandler {
    private final CategoryOptionHandler categoryOptionHandler;
    private final CategoryStore categoryStore;

    public CategoryHandler(
            @NonNull CategoryStore categoryStore,
            @NonNull CategoryOptionHandler categoryOptionHandler) {
        this.categoryStore = categoryStore;
        this.categoryOptionHandler = categoryOptionHandler;
    }


    public void handle(Category category) {

        if (isDeleted(category)) {
            categoryStore.delete(category.uid());
        } else {

            int numberOfRows = categoryStore.update(category);
            boolean updated = numberOfRows >= 1;

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



