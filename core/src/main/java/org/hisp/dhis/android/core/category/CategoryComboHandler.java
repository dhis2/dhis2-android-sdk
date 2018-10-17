package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandler;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandlerImpl;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.common.OrphanCleanerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

class CategoryComboHandler extends IdentifiableSyncHandlerImpl<CategoryCombo> {

    private final SyncHandlerWithTransformer<CategoryOptionCombo> optionComboHandler;
    private final OrderedLinkModelHandler<Category, CategoryCategoryComboLinkModel> categoryCategoryComboLinkHandler;
    private final OrphanCleaner<CategoryCombo, CategoryOptionCombo> categoryOptionCleaner;

    CategoryComboHandler(
            @NonNull IdentifiableObjectStore<CategoryCombo> store,
            @NonNull SyncHandlerWithTransformer<CategoryOptionCombo> optionComboHandler,
            @NonNull OrderedLinkModelHandler<Category, CategoryCategoryComboLinkModel> categoryCategoryComboLinkHandler,
            OrphanCleaner<CategoryCombo, CategoryOptionCombo> categoryOptionCleaner) {
        super(store);
        this.optionComboHandler = optionComboHandler;
        this.categoryCategoryComboLinkHandler = categoryCategoryComboLinkHandler;
        this.categoryOptionCleaner = categoryOptionCleaner;
    }

    @Override
    protected void afterObjectHandled(final CategoryCombo combo, HandleAction action) {
        optionComboHandler.handleMany(combo.categoryOptionCombos(),
                new ModelBuilder<CategoryOptionCombo, CategoryOptionCombo>() {
            @Override
            public CategoryOptionCombo buildModel(CategoryOptionCombo optionCombo) {
                return optionCombo.toBuilder().categoryCombo(combo).build();
            }
        });

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
                new OrderedLinkModelHandlerImpl<Category, CategoryCategoryComboLinkModel>(
                        CategoryCategoryComboLinkStore.create(databaseAdapter)
                ),
                new OrphanCleanerImpl<CategoryCombo, CategoryOptionCombo>(CategoryOptionComboModel.TABLE,
                        CategoryOptionComboModel.Columns.CATEGORY_COMBO, databaseAdapter)
        );
    }
}