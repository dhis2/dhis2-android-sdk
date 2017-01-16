package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;

public class CreateTrackedEntityDataValueUtils {

    private static String EVENT = "test_event";
    private static String DATA_ELEMENT = "test_dataElement";
    private static String STORED_BY = "test_storedBy";
    private static String VALUE = "test_value";
    private static Boolean PROVIDED_ELSEWHERE = false;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    public static ContentValues create(long id) {

        ContentValues values = new ContentValues();

        values.put(TrackedEntityDataValueModel.Columns.ID, id);
        values.put(TrackedEntityDataValueModel.Columns.EVENT, EVENT);
        values.put(TrackedEntityDataValueModel.Columns.DATA_ELEMENT, DATA_ELEMENT);
        values.put(TrackedEntityDataValueModel.Columns.STORED_BY, STORED_BY);
        values.put(TrackedEntityDataValueModel.Columns.VALUE, VALUE);
        values.put(TrackedEntityDataValueModel.Columns.CREATED, DATE);
        values.put(TrackedEntityDataValueModel.Columns.LAST_UPDATED, DATE);
        values.put(TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE, PROVIDED_ELSEWHERE);

        return values;
    }
}
