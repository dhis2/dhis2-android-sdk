package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

import java.util.List;

public interface CategoryCategoryOptionLinkStore extends DeletableStore {

    long insert(@NonNull CategoryCategoryOptionLinkModel element);

    int delete(@NonNull CategoryCategoryOptionLinkModel element);

    int update(@NonNull CategoryCategoryOptionLinkModel oldCategoryCategoryOptionLinkModel,
            @NonNull CategoryCategoryOptionLinkModel newCategoryCategoryOptionLinkModel);

    List<CategoryCategoryOptionLinkModel> queryAll();
}
