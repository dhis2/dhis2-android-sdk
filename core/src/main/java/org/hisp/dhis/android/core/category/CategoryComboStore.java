package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.IdentifiableStore;

import java.util.List;

public interface CategoryComboStore extends IdentifiableStore {
    long insert(@NonNull CategoryCombo categoryCombo);

    int update(@NonNull CategoryCombo oldCategoryCombo);

    int delete(@NonNull String uid);

    List<CategoryCombo> queryAll();

    CategoryCombo queryByUid(String uid);
}
