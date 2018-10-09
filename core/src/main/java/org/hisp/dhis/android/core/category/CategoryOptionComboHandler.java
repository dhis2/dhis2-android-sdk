package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

final class CategoryOptionComboHandler extends IdentifiableSyncHandlerImpl<CategoryOptionCombo> {


    private final LinkModelHandler<CategoryOption, CategoryOptionComboCategoryOptionLinkModel>
            categoryOptionComboCategoryOptionLinkHandler;

    private CategoryOptionComboHandler(IdentifiableObjectStore<CategoryOptionCombo> store,
                                       LinkModelHandler<CategoryOption, CategoryOptionComboCategoryOptionLinkModel>
                                               categoryOptionComboCategoryOptionLinkHandler) {
        super(store);
        this.categoryOptionComboCategoryOptionLinkHandler = categoryOptionComboCategoryOptionLinkHandler;
    }

    @Override
    protected void afterObjectHandled(CategoryOptionCombo optionCombo, HandleAction action) {
        categoryOptionComboCategoryOptionLinkHandler.handleMany(optionCombo.uid(),
                optionCombo.categoryOptions(),
                new CategoryOptionComboCategoryOptionLinkModelBuilder(optionCombo));
    }

    public static SyncHandlerWithTransformer<CategoryOptionCombo> create(DatabaseAdapter databaseAdapter) {
        return new CategoryOptionComboHandler(
                CategoryOptionComboStore.create(databaseAdapter),
                new LinkModelHandlerImpl<CategoryOption, CategoryOptionComboCategoryOptionLinkModel>(
                        CategoryOptionComboCategoryOptionLinkStore.create(databaseAdapter)
                )
        );
    }
}