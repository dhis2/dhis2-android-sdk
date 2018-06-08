package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class CategoryComboHandler {

    @NonNull
    private final LinkModelHandler<CategoryOption, CategoryOptionComboCategoryOptionLinkModel>
            categoryOptionComboCategoryOptionLinkHandler;

    @NonNull
    private final LinkModelHandler<Category, CategoryCategoryComboLinkModel> categoryCategoryComboLinkHandler;

    @NonNull
    private final CategoryOptionComboHandler optionComboHandler;

    @NonNull
    private final CategoryComboStore store;

    CategoryComboHandler(
            @NonNull CategoryComboStore store,
            @NonNull LinkModelHandler<CategoryOption, CategoryOptionComboCategoryOptionLinkModel>
                    categoryOptionComboCategoryOptionLinkHandler,
            @NonNull LinkModelHandler<Category, CategoryCategoryComboLinkModel> categoryCategoryComboLinkHandler,
            @NonNull CategoryOptionComboHandler optionComboHandler) {
        this.store = store;
        this.categoryOptionComboCategoryOptionLinkHandler = categoryOptionComboCategoryOptionLinkHandler;
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
                categoryOptionComboCategoryOptionLinkHandler.handleMany(optionCombo.uid(),
                        optionCombo.categoryOptions(),
                        new CategoryOptionComboCategoryOptionLinkModelBuilder(optionCombo));
            }
        }
    }

    public static CategoryComboHandler create(DatabaseAdapter databaseAdapter) {
        return new CategoryComboHandler(
                new CategoryComboStoreImpl(databaseAdapter),
                new LinkModelHandlerImpl<CategoryOption, CategoryOptionComboCategoryOptionLinkModel>(
                        CategoryOptionComboCategoryOptionLinkStore.create(databaseAdapter)
                ),
                new LinkModelHandlerImpl<Category, CategoryCategoryComboLinkModel>(
                        CategoryCategoryComboLinkStore.create(databaseAdapter)
                ),
                CategoryOptionComboHandler.create(databaseAdapter)
        );
    }
}
