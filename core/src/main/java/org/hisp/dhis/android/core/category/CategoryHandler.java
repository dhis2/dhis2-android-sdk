package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class CategoryHandler extends IdentifiableSyncHandlerImpl<Category> {

    private final SyncHandler<CategoryOption> categoryOptionHandler;
    private final OrderedLinkModelHandler<CategoryOption, CategoryCategoryOptionLinkModel>
            categoryCategoryOptionLinkHandler;

    @Inject
    CategoryHandler(
            @NonNull IdentifiableObjectStore<Category> categoryStore,
            @NonNull SyncHandler<CategoryOption> categoryOptionHandler,
            @NonNull OrderedLinkModelHandler<CategoryOption, CategoryCategoryOptionLinkModel>
                    categoryCategoryOptionLinkHandler) {
        super(categoryStore);
        this.categoryOptionHandler = categoryOptionHandler;
        this.categoryCategoryOptionLinkHandler = categoryCategoryOptionLinkHandler;
    }

    @Override
    protected void afterObjectHandled(Category category, HandleAction handleAction) {
        List<CategoryOption> categoryOptions = category.categoryOptions();
        if (categoryOptions != null) {
            List<CategoryOption> categoryOptionsWithAccess = new ArrayList<>();
            for (CategoryOption categoryOption: categoryOptions) {
                if (categoryOption.access().data().read()) {
                    categoryOptionsWithAccess.add(categoryOption);
                }
            }
            categoryOptionHandler.handleMany(categoryOptionsWithAccess);
            categoryCategoryOptionLinkHandler.handleMany(category.uid(), categoryOptionsWithAccess,
                    new CategoryCategoryOptionLinkModelBuilder(category));
        }
    }
}
