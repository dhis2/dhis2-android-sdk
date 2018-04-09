package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public final class CategoryOptionHandler extends IdentifiableHandlerImpl<CategoryOption, CategoryOptionModel> {

    private CategoryOptionHandler(IdentifiableObjectStore<CategoryOptionModel> categoryOptionStore) {
        super(categoryOptionStore);
    }

    public static GenericHandler<CategoryOption, CategoryOptionModel> create(DatabaseAdapter databaseAdapter) {
        return new IdentifiableHandlerImpl<>(CategoryOptionStore.create(databaseAdapter));
    }
}