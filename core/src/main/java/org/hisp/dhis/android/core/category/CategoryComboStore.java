package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

public interface CategoryComboStore {
    long insert(@NonNull CategoryCombo categoryCombo);

    boolean delete(@NonNull CategoryCombo categoryCombo);

    boolean update(@NonNull CategoryCombo oldCategoryCombo, @NonNull CategoryCombo newCategoryCombo);
}
