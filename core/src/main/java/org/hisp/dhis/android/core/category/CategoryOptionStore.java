package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.StoreFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public final class CategoryOptionStore {

    private CategoryOptionStore() {}

    public static IdentifiableObjectStore<CategoryOptionModel> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.identifiableStore(databaseAdapter, CategoryOptionModel.TABLE,
                new CategoryOptionModel.Columns().all());
    }
}