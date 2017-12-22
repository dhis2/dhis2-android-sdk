package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

import java.util.List;

public interface CategoryComboLinkStore extends DeletableStore {
    long insert(@NonNull CategoryComboLinkModel element);

    List<CategoryComboLink> queryAll();
}
