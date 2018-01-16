package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableObjectStore;

import java.util.List;

public interface CategoryStore extends DeletableObjectStore {

    long insert(@NonNull Category element);

    boolean update(@NonNull Category oldElement, @NonNull Category newElement);

    List<Category> queryAll();
}
