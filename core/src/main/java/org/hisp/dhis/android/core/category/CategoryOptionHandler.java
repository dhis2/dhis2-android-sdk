package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

public class CategoryOptionHandler extends Handler<CategoryOption> {

    public CategoryOptionHandler(
            @NonNull Store<CategoryOption> store) {
        super(store);
    }

    @Override
    public void afterInsert(CategoryOption entity) {
        //This method has not body because doesn't
        //have an implementation for afterInsert from the Handler
        // parent
    }
}
