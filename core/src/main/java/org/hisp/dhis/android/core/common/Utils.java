package org.hisp.dhis.android.core.common;

import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * A collection of utility abstractions
 */
public final class Utils {

    private Utils() {
        // no instances
    }

    /**
     * A Null-safe safeUnmodifiableList.
     *
     * @param list
     * @return
     */
    @Nullable
    public static <T> List<T> safeUnmodifiableList(@Nullable List<T> list) {
        if (list != null) {
            return Collections.unmodifiableList(list);
        }

        return null;
    }
}
