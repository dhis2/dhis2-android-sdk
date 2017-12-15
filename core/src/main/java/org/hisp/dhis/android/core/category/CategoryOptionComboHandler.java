package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

public class CategoryOptionComboHandler extends Handler<CategoryOptionCombo> {

    public CategoryOptionComboHandler(
            @NonNull Store<CategoryOptionCombo> store) {
        super(store);
    }

    @Override
    public void handle(CategoryOptionCombo optionCombo) {
        super.handle(optionCombo);
    }

    @Override
    public void afterInsert(CategoryOptionCombo entity) {}


}
