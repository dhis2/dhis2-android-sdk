package org.hisp.dhis.android.core.data.database;

import android.support.annotation.NonNull;

public final class DbUtils {

    private DbUtils() {
        // no instances
    }

    @NonNull
    public static String projectionToSqlString(String[] projection) {
        StringBuilder sqlStringBuilder = new StringBuilder();
        for (int i = 0; i < projection.length; i++) {
            sqlStringBuilder.append(projection[i]);
            if (i < projection.length - 1) {
                sqlStringBuilder.append(", ");
            }
        }
        return sqlStringBuilder.toString();
    }
}
