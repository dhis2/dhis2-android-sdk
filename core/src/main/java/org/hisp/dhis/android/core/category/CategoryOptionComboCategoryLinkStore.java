package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

import java.util.List;

public interface CategoryOptionComboCategoryLinkStore extends DeletableStore {
    long insert(@NonNull CategoryOptionComboCategoryLinkModel element);

    int removeCategoryComboOptionRelationsByCategoryOptionCombo(@NonNull String uid);

    List<String> queryByOptionComboUId(String uId);
}
