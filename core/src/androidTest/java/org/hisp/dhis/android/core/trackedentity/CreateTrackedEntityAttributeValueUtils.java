package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel.Columns;

public class CreateTrackedEntityAttributeValueUtils {
    private static final String VALUE = "test_value";

    public static ContentValues create(@NonNull String trackedEntityAttributeUid,
                                       @NonNull String trackedEntityInstanceUid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.VALUE, VALUE);
        contentValues.put(Columns.TRACKED_ENTITY_ATTRIBUTE, trackedEntityAttributeUid);
        contentValues.put(Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid);
        return contentValues;
    }
}
