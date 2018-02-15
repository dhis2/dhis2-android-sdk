package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.IdentifiableStore;

import java.util.List;

public interface CategoryOptionComboStore extends IdentifiableStore {

    long insert(@NonNull CategoryOptionCombo element);

    int delete(@NonNull String uid);

    int update(@NonNull CategoryOptionCombo categoryOptionCombo);

    List<CategoryOptionCombo> queryAll();

    List<CategoryOptionCombo> queryByCategoryComboUId(String uid);

    CategoryOptionCombo queryByUId(String uid);
}
