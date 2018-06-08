package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public final class CategoryCategoryOptionLinkStore {

    private CategoryCategoryOptionLinkStore() {}

    public static LinkModelStore<CategoryCategoryOptionLinkModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.linkModelStore(databaseAdapter, CategoryCategoryOptionLinkModel.TABLE,
                new CategoryCategoryOptionLinkModel.Columns(),
                CategoryCategoryOptionLinkModel.Columns.CATEGORY);
    }
}
