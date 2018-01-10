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

    public void handle(CategoryCombo combo) {

        if (isDeleted(combo)) {
            store.delete(combo);
        } else {

            boolean updated = store.update(combo, combo);

            if (!updated) {
                store.insert(combo);
                handleRelations(combo);
            }
        }
    }

    private void handleRelations(@NonNull CategoryCombo combo) {

        handleCategoriesLink(combo);

        handleOptionCombo(combo);

    }

    private void handleOptionCombo(@NonNull CategoryCombo combo) {
        List<CategoryOptionCombo> optionsCombo = combo.categoryOptionCombos();

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
                .optionCombo(optionCombo.uid())
                .build();
    }

    private void handleCategoriesLink(@NonNull CategoryCombo combo) {
        List<Category> categories = combo.categories();

        if (categories != null) {
            for (Category category : categories) {

                CategoryCategoryComboLinkModel link = newCategoryComboLink(combo, category);

                categoryCategoryComboLinkStore.insert(link);
            }
        }
    }

    private CategoryCategoryComboLinkModel newCategoryComboLink(@NonNull CategoryCombo combo,
            @NonNull Category category) {

        return CategoryCategoryComboLinkModel.builder().category(
                category.uid())
                .combo(combo.uid())
                .build();
    }
}
