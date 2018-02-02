package org.hisp.dhis.android.core.common;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.Category;

import java.util.Date;

public class CategoryMother {

    public static Category generateCategory(@NonNull String uid) {
        Date today = new Date();
        return Category.builder()
                .uid(uid)
                .code("BIRTHS_ATTENDED")
                .created(today)
                .name("Births attended by")
                .shortName("Births attended by")
                .displayName("Births attended by")
                .dataDimensionType("DISAGGREGATION").build();
    }
}
