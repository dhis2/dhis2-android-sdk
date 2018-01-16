package org.hisp.dhis.android.core.common;

import android.support.annotation.NonNull;

public interface DeletableObjectStore extends DeletableStore{

    int delete(@NonNull String uid);

    Boolean exists(String uid);

}
