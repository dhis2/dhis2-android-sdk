package org.hisp.dhis.android.core.database;

import android.support.annotation.NonNull;

import java.util.Locale;

public final class DbTestUtils {
    private DbTestUtils() {
        // no instances
    }

    @NonNull
    public static String[] unambiguousProjection(@NonNull String table, @NonNull String[] projection) {
        String[] unambiguousProjection = new String[projection.length];

        for (int index = 0; index < projection.length; index++) {
            unambiguousProjection[index] = String.format(Locale.US,
                    "%s.%s", table, projection[index]);
        }

        return unambiguousProjection;
    }
}
