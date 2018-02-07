package org.hisp.dhis.android.core.category;


import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import android.support.annotation.NonNull;

public class CategoryOptionHandler {

    @NonNull
    private final CategoryOptionStore store;

    public CategoryOptionHandler(@NonNull CategoryOptionStore store) {
        this.store = store;
    }

    public void handle(@NonNull CategoryOption categoryOption) {

        if (isDeleted(categoryOption)) {
            store.delete(categoryOption.uid());
        } else {

            int numberOfRows = store.update(categoryOption);
            boolean updated = numberOfRows >= 1;

            if (!updated) {
                store.insert(categoryOption);
            }
        }
    }
}
