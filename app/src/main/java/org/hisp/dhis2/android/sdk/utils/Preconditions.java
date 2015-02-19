package org.hisp.dhis2.android.sdk.utils;

public class Preconditions {
    private Preconditions() {
        // no instances
    }

    /* this is just convenience which allows
    to reduce amount of boilerplate code */
    public static <T> T isNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }

        return obj;
    }
}
