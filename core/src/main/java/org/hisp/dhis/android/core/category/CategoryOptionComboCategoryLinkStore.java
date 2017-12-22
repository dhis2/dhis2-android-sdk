package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

public interface CategoryOptionComboCategoryLinkStore extends DeletableStore {
    long insert(@NonNull CategoryOptionComboCategoryLinkModel element);
}
