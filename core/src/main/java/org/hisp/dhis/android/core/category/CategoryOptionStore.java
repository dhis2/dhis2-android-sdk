package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableObjectStore;

import java.util.List;

public interface CategoryOptionStore extends DeletableObjectStore {

    long insert(@NonNull CategoryOption categoryOption);

    boolean update(@NonNull CategoryOption oldCategoryOption,
            @NonNull CategoryOption newCategoryOption);

    List<CategoryOption> queryAll();
}
