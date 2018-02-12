package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.IdentifiableStore;

import java.util.List;

public interface CategoryStore extends IdentifiableStore {

    long insert(@NonNull Category category);

    int delete(@NonNull String uid);

    int update(@NonNull Category category);

    Category queryByUid(String uid);

    List<Category> queryAll();
}
