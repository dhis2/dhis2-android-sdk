package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import java.util.List;

public class CategoryHandler extends Handler<Category> {

    private final Handler<CategoryOption> categoryOptionHandler;
    private final Store<CategoryOptionLinkModel> categoryOptionLinkStore;

    public CategoryHandler(
            @NonNull Store<Category> store,
            @NonNull Handler<CategoryOption> categoryOptionHandler,
            @NonNull Store<CategoryOptionLinkModel> categoryOptionLinkStore) {
        super(store);
        this.categoryOptionHandler = categoryOptionHandler;
        this.categoryOptionLinkStore = categoryOptionLinkStore;
    }


    @Override
    public void afterInsert(Category category) {
        handleCategoryOption(category);
    }

    private void handleCategoryOption(@NonNull Category category) {
        List<CategoryOption> categoryOptions = category.categoryOptions();
        if (categoryOptions != null) {

            for (CategoryOption option : categoryOptions) {
                categoryOptionHandler.handle(option);

                CategoryOptionLinkModel link = newCategoryOption(category, option);

                categoryOptionLinkStore.insert(link);
            }
        }
    }

    private CategoryOptionLinkModel newCategoryOption(@NonNull Category category,
            @NonNull CategoryOption option) {

        return CategoryOptionLinkModel.builder().category(
                category.uid())
                .option(option.uid())
                .build();
    }

}
