package org.hisp.dhis.client.sdk.core.commons;

import android.support.annotation.NonNull;

public interface ServerUrlPreferences {
    boolean save(@NonNull String serverUrl);

    boolean clear();

    String get();
}
