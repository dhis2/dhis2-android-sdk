package org.hisp.dhis.android.core.common;


import org.hisp.dhis.android.core.category.CategoryOption;

import java.util.Date;

public final class CategoryOptionMother {

    public static CategoryOption generatedCategoryOption(String uid) {
        return CategoryOption.builder()
                .uid(uid)
                .code("SECHN")
                .created(new Date())
                .name("SECHN")
                .shortName("SECHN")
                .displayName("SECHN")
                .build();
    }
}
