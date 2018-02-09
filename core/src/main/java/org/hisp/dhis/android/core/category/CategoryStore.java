package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableObjectStore;

import java.util.List;

public interface CategoryStore extends DeletableObjectStore {

    long insert(@NonNull Category category);

    int delete(@NonNull String uid);

    int update(@NonNull Category category);

    Category queryByUid(String uid);

    List<Category> queryAll();
}
