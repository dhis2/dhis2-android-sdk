package org.hisp.dhis.android.core.common;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.CategoryCombo;

import java.util.Date;

public class CategoryComboMother {

    public static CategoryCombo generateCategoryCombo(@NonNull String uid) {
        Date today = new Date();
        return CategoryCombo.builder()
                .uid(uid)
                .code("BIRTHS")
                .created(today)
                .name("Births")
                .displayName("Births")
                .isDefault(false)
                .build();
    }
}
