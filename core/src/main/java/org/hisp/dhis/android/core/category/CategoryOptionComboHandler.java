package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

import java.util.List;
public class CategoryOptionComboHandler {
    @NonNull
    private final CategoryOptionComboStore store;

    @NonNull
    private final CategoryOptionComboCategoryLinkStore categoryComboOptionLinkCategoryStore;

    public CategoryOptionComboHandler(
            @NonNull CategoryOptionComboStore store,
            @NonNull CategoryOptionComboCategoryLinkStore categoryComboOptionLinkCategoryStore) {
        this.store = store;
        this.categoryComboOptionLinkCategoryStore = categoryComboOptionLinkCategoryStore;
    }

    public void handle(@NonNull CategoryOptionCombo entity) {

        if (isDeleted(entity)) {
            store.delete(entity.uid());
        } else {

            int rowsAffected = store.update(entity);
            boolean updated = rowsAffected >= 1;

            if (!updated) {
                store.insert(entity);
            }

           handleOptionComboLinkCategory(entity, updated);
        }
    }

    private void handleOptionComboLinkCategory(@NonNull CategoryOptionCombo optionCombo, boolean isUpdated) {

        List<CategoryOption> categoryOptions = optionCombo.categoryOptions();

        if(isUpdated) {
            //delete old relations
            categoryComboOptionLinkCategoryStore.removeCategoryComboOptionRelationsByCategoryOptionCombo(
                    optionCombo.uid());
        }

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
}
