package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public final class CategoryOptionComboCategoryOptionLinkStore {

    private CategoryOptionComboCategoryOptionLinkStore() {}

    public static LinkModelStore<CategoryOptionComboCategoryOptionLinkModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.linkModelStore(databaseAdapter, CategoryOptionComboCategoryOptionLinkModel.TABLE,
                new CategoryOptionComboCategoryOptionLinkModel.Columns(),
                CategoryOptionComboCategoryOptionLinkModel.Columns.CATEGORY_OPTION_COMBO);
    }
}