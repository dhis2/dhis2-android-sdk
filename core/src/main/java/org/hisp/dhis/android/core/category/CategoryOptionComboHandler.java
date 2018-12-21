package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.LinkModelHandler;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class CategoryOptionComboHandler extends IdentifiableSyncHandlerImpl<CategoryOptionCombo> {


    private final LinkModelHandler<CategoryOption, CategoryOptionComboCategoryOptionLinkModel>
            categoryOptionComboCategoryOptionLinkHandler;

    @Inject
    CategoryOptionComboHandler(CategoryOptionComboStore store,
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
}