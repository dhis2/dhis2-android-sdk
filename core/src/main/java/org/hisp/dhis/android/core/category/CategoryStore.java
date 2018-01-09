package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

import java.util.List;

public interface CategoryStore extends DeletableStore {

    long insert(@NonNull Category element);

    boolean delete(@NonNull String uid);

    boolean update(@NonNull Category oldElement, @NonNull Category newElement);

    List<Category> queryAll();

    Boolean exists(String categoryUId);
}
