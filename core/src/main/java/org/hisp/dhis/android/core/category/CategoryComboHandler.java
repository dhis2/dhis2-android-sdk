package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

import java.util.List;

public class CategoryComboHandler {

    @NonNull
    private final CategoryCategoryComboLinkStore categoryCategoryComboLinkStore;

    @NonNull
    private final CategoryOptionComboHandler optionComboHandler;

    @NonNull
    private final CategoryComboStore store;

    public CategoryComboHandler(
            @NonNull CategoryComboStore store,
            @NonNull CategoryCategoryComboLinkStore categoryCategoryComboLinkStore,
            @NonNull CategoryOptionComboHandler optionComboHandler) {
        this.store = store;
        this.categoryCategoryComboLinkStore = categoryCategoryComboLinkStore;
        this.optionComboHandler = optionComboHandler;
    }

    public void handle(CategoryCombo categoryCombo) {

        if (isDeleted(categoryCombo)) {
            store.delete(categoryCombo.uid());
        } else {
            int rowsAffected = store.update(categoryCombo);
            boolean updated = rowsAffected >= 1;

            if (!updated) {
                store.insert(categoryCombo);

            }
            handleRelations(categoryCombo, updated);
        }
    }

    private void handleRelations(@NonNull CategoryCombo categoryCombo, boolean isUpdated) {

        handleCategoriesLink(categoryCombo, isUpdated);

        handleOptionCombo(categoryCombo);

    }

    private void handleOptionCombo(@NonNull CategoryCombo categoryCombo) {
        List<CategoryOptionCombo> optionsCombo = categoryCombo.categoryOptionCombos();

        if (optionsCombo != null) {
            for (CategoryOptionCombo optionCombo : optionsCombo) {
                optionComboHandler.handle(optionCombo);
            }
        }
    }

    private void handleCategoriesLink(@NonNull CategoryCombo categoryCombo, boolean isUpdated) {
        List<Category> categories = categoryCombo.categories();

        if(isUpdated) {
            //delete old relations
            categoryCategoryComboLinkStore.removeCategoryComboRelations(categoryCombo.uid());
        }

        if (categories != null) {

            for (Category category : categories) {

                CategoryCategoryComboLinkModel link = newCategoryComboLink(categoryCombo, category);

                categoryCategoryComboLinkStore.insert(link);
            }
        }
    }

    private CategoryCategoryComboLinkModel newCategoryComboLink(
            @NonNull CategoryCombo categoryCombo,
            @NonNull Category category) {

        return CategoryCategoryComboLinkModel.builder().category(
                category.uid())
                .categoryCombo(categoryCombo.uid())
                .build();
    }
}
