package org.hisp.dhis2.android.sdk.utils;

import org.joda.time.LocalDate;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
public class Utils {

    public static final String getCurrentDate() {
        LocalDate localDate = new LocalDate();
        return localDate.toString();
    }
}
