package org.hisp.dhis.android.core;

/**
 * A collection of convenience functions/abstractions to be used by the tests.
 */
public class AndroidTestUtils {

    /* A helper method to convert an integer to Boolean, where 1 is true and 0 is false*/
    public static Boolean toBoolean(Integer i) {
        if (i == 0) {
            return false;
        } else {
            return true;
        }
    }

    /* A helper method to convert a Boolean to an Integer, where true is 1 and false is 0 */
    public static Integer toInteger(Boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }
}
