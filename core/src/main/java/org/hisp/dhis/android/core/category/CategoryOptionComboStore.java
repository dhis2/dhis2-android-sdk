package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

public interface CategoryOptionComboStore {

    long insert(@NonNull CategoryOptionCombo element);

    boolean delete(@NonNull CategoryOptionCombo element);

    boolean update(@NonNull CategoryOptionCombo oldElement,
            @NonNull CategoryOptionCombo newElement);
}
