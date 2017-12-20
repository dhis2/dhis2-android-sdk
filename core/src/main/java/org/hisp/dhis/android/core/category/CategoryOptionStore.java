package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

public interface CategoryOptionStore {

    long insert(@NonNull CategoryOption categoryOption);

    boolean delete(@NonNull CategoryOption categoryOption);

    boolean update(@NonNull CategoryOption oldCategoryOption,
            @NonNull CategoryOption newCategoryOption);
}
