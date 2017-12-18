package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

public class CategoryOptionComboHandler extends Handler<CategoryOptionCombo> {

    public CategoryOptionComboHandler(
            @NonNull Store<CategoryOptionCombo> store) {
        super(store);
    }

    @Override
    public void afterInsert(CategoryOptionCombo entity) {
        //This method has not body because doesn't
        //have an implementation for afterInsert from the Handler
        // parent
    }
}
