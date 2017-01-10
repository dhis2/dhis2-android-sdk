package org.hisp.dhis.android.core.common;

import java.util.Collections;
import java.util.List;

/**
 * A collection of utility abstractions
 */
public class Utils {
    /**
     * A Null-safe safeUnmodifiableList.
     *
     * @param list
     * @return
     */
    public static <T> List<T> safeUnmodifiableList(List<T> list) {
        if (list != null) {
            list = Collections.unmodifiableList(list);
        }
        return list;
    }
}
