package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

public class CategoryOptionHandler extends Handler<CategoryOption> {

    public CategoryOptionHandler(
            @NonNull Store<CategoryOption> store) {
        super(store);
    }

    @Override
    public void afterInsert(CategoryOption entity) {
    }
}
