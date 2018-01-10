package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

public interface CategoryStore extends DeletableStore {

    long insert(@NonNull Category element);

    boolean delete(@NonNull Category element);

    boolean update(@NonNull Category oldElement, @NonNull Category newElement);
}
