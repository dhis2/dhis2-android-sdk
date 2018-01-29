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

    public void handle(CategoryCombo combo) {

        if (isDeleted(combo)) {
            store.delete(combo);
        } else {

            boolean updated = store.update(combo);

            if (!updated) {
                store.insert(combo);
            }
            handleRelations(combo, updated);
        }
    }

    private void handleRelations(@NonNull CategoryCombo combo, boolean isUpdated) {

        handleCategoriesLink(combo, isUpdated);

        handleOptionCombo(combo);

    }

    private void handleOptionCombo(@NonNull CategoryCombo combo) {
        List<CategoryOptionCombo> optionsCombo = combo.categoryOptionCombos();

        if (optionsCombo != null) {
            for (CategoryOptionCombo optionCombo : optionsCombo) {
                optionComboHandler.handle(optionCombo);
            }
        }
    }

    private void handleCategoriesLink(@NonNull CategoryCombo combo, boolean isUpdated) {
        List<Category> categories = combo.categories();

        if(isUpdated) {
            //delete old relations
            categoryCategoryComboLinkStore.removeCategoryComboRelations(combo.uid());
        }

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
