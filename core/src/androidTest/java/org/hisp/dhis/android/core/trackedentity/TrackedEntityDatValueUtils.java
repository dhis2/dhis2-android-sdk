package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;

public class TrackedEntityDatValueUtils {

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    private static final long ID = 11L;
    private static final String EVENT = "test_event";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String STORED_BY = "test_storedBy";
    private static final String VALUE = "test_value";
    private static final Boolean PROVIDED_ELSEWHERE = false;

    public static ContentValues create(long id) {

        ContentValues trackedEntityDataValues = new ContentValues();
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.ID, id);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.EVENT, EVENT);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.CREATED, DATE);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.LAST_UPDATED, DATE);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.DATA_ELEMENT, DATA_ELEMENT);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.STORED_BY, STORED_BY);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.VALUE, VALUE);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE, PROVIDED_ELSEWHERE);
        return trackedEntityDataValues;
    }
}
