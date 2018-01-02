package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

import java.util.List;

public interface CategoryOptionComboStore extends DeletableStore {

    long insert(@NonNull CategoryOptionCombo element);

    boolean delete(@NonNull CategoryOptionCombo element);

    boolean update(@NonNull CategoryOptionCombo oldElement,
            @NonNull CategoryOptionCombo newElement);

    List<CategoryOptionCombo> queryAll();
}
