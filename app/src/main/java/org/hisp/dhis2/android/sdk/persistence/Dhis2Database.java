package org.hisp.dhis2.android.sdk.persistence;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * @author Simen Skogly Russnes
 * Database definition for DbFlow
 */
@Database(name = Dhis2Database.NAME, version = Dhis2Database.VERSION, foreignKeysSupported = true)
public class Dhis2Database {

    public static final String NAME = "Dhis2";

    public static final int VERSION = 1;

}
