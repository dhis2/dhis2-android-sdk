package org.hisp.dhis.android.core.common;

import android.support.annotation.NonNull;

public interface IdentifiableStore extends DeletableStore {

    int delete(@NonNull String uid);

    Boolean exists(String uid);

}
