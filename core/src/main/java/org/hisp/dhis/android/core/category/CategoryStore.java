package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

public interface CategoryStore extends DeletableStore {

    long insert(@NonNull Category category);

    boolean delete(@NonNull Category category);

    boolean update(@NonNull Category category);
}
