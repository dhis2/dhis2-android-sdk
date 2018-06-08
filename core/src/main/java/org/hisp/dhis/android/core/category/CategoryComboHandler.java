package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class CategoryComboHandler {

    @NonNull
    private final CategoryOptionComboCategoryLinkStore categoryComboOptionLinkCategoryStore;

    @NonNull
    private final LinkModelHandler<Category, CategoryCategoryComboLinkModel> categoryCategoryComboLinkHandler;

    @NonNull
    private final CategoryOptionComboHandler optionComboHandler;

    @NonNull
    private final CategoryComboStore store;

    CategoryComboHandler(
            @NonNull CategoryComboStore store,
            @NonNull CategoryOptionComboCategoryLinkStore
                    categoryComboOptionCategoryLinkStore,
            @NonNull LinkModelHandler<Category, CategoryCategoryComboLinkModel> categoryCategoryComboLinkHandler,
            @NonNull CategoryOptionComboHandler optionComboHandler) {
        this.store = store;
        this.categoryComboOptionLinkCategoryStore = categoryComboOptionCategoryLinkStore;
        this.categoryCategoryComboLinkHandler = categoryCategoryComboLinkHandler;
        this.optionComboHandler = optionComboHandler;
    }

    public void handle(CategoryCombo combo) {

        if (isDeleted(combo)) {
            store.delete(combo);
        } else {

            boolean updated = store.update(combo, combo);

            if (!updated) {
                store.insert(combo);
            }

            handleRelations(combo);
        }
    }

    private void handleRelations(@NonNull CategoryCombo combo) {
        categoryCategoryComboLinkHandler.handleMany(combo.uid(), combo.categories(),
                new CategoryCategoryComboLinkModelBuilder(combo));

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

    public static CategoryComboHandler create(DatabaseAdapter databaseAdapter) {
        return new CategoryComboHandler(
                new CategoryComboStoreImpl(databaseAdapter),
                new CategoryOptionComboCategoryLinkStoreImpl(databaseAdapter),
                new LinkModelHandlerImpl<Category, CategoryCategoryComboLinkModel>(
                        CategoryCategoryComboLinkStore.create(databaseAdapter)
                ),
                CategoryOptionComboHandler.create(databaseAdapter)
        );
    }
}
