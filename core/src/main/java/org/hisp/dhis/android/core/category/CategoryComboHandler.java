package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.common.OrphanCleanerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

class CategoryComboHandler extends IdentifiableSyncHandlerImpl<CategoryCombo> {

    private final SyncHandler<CategoryOptionCombo> optionComboHandler;
    private final LinkModelHandler<Category, CategoryCategoryComboLinkModel> categoryCategoryComboLinkHandler;
    private final OrphanCleaner<CategoryCombo, CategoryOptionCombo> categoryOptionCleaner;

    CategoryComboHandler(
            @NonNull IdentifiableObjectStore<CategoryCombo> store,
            @NonNull SyncHandler<CategoryOptionCombo> optionComboHandler,
            @NonNull LinkModelHandler<Category, CategoryCategoryComboLinkModel> categoryCategoryComboLinkHandler,
            OrphanCleaner<CategoryCombo, CategoryOptionCombo> categoryOptionCleaner) {
        super(store);
        this.optionComboHandler = optionComboHandler;
        this.categoryCategoryComboLinkHandler = categoryCategoryComboLinkHandler;
        this.categoryOptionCleaner = categoryOptionCleaner;
    }

    @Override
    protected void afterObjectHandled(CategoryCombo combo, HandleAction action) {
        optionComboHandler.handleMany(combo.categoryOptionCombos());
        categoryCategoryComboLinkHandler.handleMany(combo.uid(), combo.categories(),
                new CategoryCategoryComboLinkModelBuilder(combo));

        if (action == HandleAction.Update) {
            categoryOptionCleaner.deleteOrphan(combo, combo.categoryOptionCombos());
        }
    }

    public static IdentifiableSyncHandlerImpl<CategoryCombo> create(DatabaseAdapter databaseAdapter) {
        return new CategoryComboHandler(
                CategoryComboStore.create(databaseAdapter),
                CategoryOptionComboHandler.create(databaseAdapter),
                new LinkModelHandlerImpl<Category, CategoryCategoryComboLinkModel>(
                        CategoryCategoryComboLinkStore.create(databaseAdapter)
                ),
                new OrphanCleanerImpl<CategoryCombo, CategoryOptionCombo>(CategoryOptionComboModel.TABLE,
                        CategoryOptionComboModel.Columns.CATEGORY_COMBO, databaseAdapter)
        );
    }
}