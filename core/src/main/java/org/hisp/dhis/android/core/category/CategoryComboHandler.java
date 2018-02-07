package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

import java.util.List;

public class CategoryComboHandler {

    @NonNull
    private final CategoryOptionComboCategoryLinkStore categoryComboOptionLinkCategoryStore;

    @NonNull
    private final CategoryCategoryComboLinkStore categoryCategoryComboLinkStore;

    @NonNull
    private final CategoryOptionComboHandler optionComboHandler;

    @NonNull
    private final CategoryComboStore store;

    public CategoryComboHandler(
            @NonNull CategoryComboStore store,
            @NonNull CategoryOptionComboCategoryLinkStore
                    categoryComboOptionCategoryLinkStore,
            @NonNull CategoryCategoryComboLinkStore categoryCategoryComboLinkStore,
            @NonNull CategoryOptionComboHandler optionComboHandler) {
        this.store = store;
        this.categoryComboOptionLinkCategoryStore = categoryComboOptionCategoryLinkStore;
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
                handleRelations(categoryCombo);
            }
        }
    }

    private void handleRelations(@NonNull CategoryCombo categoryCombo) {

        handleCategoriesLink(categoryCombo);

        handleOptionCombo(categoryCombo);

    }

    private void handleOptionCombo(@NonNull CategoryCombo categoryCombo) {
        List<CategoryOptionCombo> optionsCombo = categoryCombo.categoryOptionCombos();

        if (optionsCombo != null) {
            for (CategoryOptionCombo optionCombo : optionsCombo) {
                optionComboHandler.handle(optionCombo);

                handleOptionComboLinkCategory(optionCombo);
            }
        }
    }

    private void handleOptionComboLinkCategory(@NonNull CategoryOptionCombo optionCombo) {

        List<CategoryOption> categoryOptions = optionCombo.categoryOptions();

        if (categoryOptions != null) {
            for (CategoryOption categoryOption : categoryOptions) {

                CategoryOptionComboCategoryLinkModel link = newCategoryOptionLinkCategory(
                        optionCombo,
                        categoryOption);

                categoryComboOptionLinkCategoryStore.insert(link);
            }
        }
    }

    @NonNull
    private CategoryOptionComboCategoryLinkModel newCategoryOptionLinkCategory(
            @NonNull CategoryOptionCombo optionCombo,
            @NonNull CategoryOption categoryOption) {
        return CategoryOptionComboCategoryLinkModel.
                builder()
                .category(categoryOption.uid())
                .categoryOptionCombo(optionCombo.uid())
                .build();
    }

    private void handleCategoriesLink(@NonNull CategoryCombo categoryCombo) {
        List<Category> categories = categoryCombo.categories();

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
