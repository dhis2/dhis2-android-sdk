package org.hisp.dhis.android.core.commons.database;

import android.net.Uri;

public final class DbContract {
    public static final String AUTHORITY = "org.hisp.dhis.android.core";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
}
