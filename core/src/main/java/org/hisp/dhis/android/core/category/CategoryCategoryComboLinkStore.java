package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public final class CategoryCategoryComboLinkStore {

    private CategoryCategoryComboLinkStore() {}

    public static LinkModelStore<CategoryCategoryComboLinkModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.linkModelStore(databaseAdapter, CategoryCategoryComboLinkModel.TABLE,
                new CategoryCategoryComboLinkModel.Columns(),
                CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO);
    }
}

