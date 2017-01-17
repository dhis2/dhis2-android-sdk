package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;

public class CreateTrackedEntityUtils {

    public static final String TEST_CODE = "test_code";
    public static final String TEST_NAME = "test_name";
    public static final String TEST_DISPLAY_NAME = "test_display_name";
    public static final String TEST_CREATED = "test_created";
    public static final String TEST_LAST_UPDATED = "test_last_updated";
    public static final String TEST_SHORT_NAME = "test_short_name";
    public static final String TEST_DISPLAY_SHORT_NAME = "test_display_short_name";
    public static final String TEST_DESCRIPTION = "test_description";
    public static final String TEST_DISPLAY_DESCRIPTION = "test_display_description";

    public static ContentValues create(long id, String uid) {
        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityModel.Columns.ID, id);
        trackedEntity.put(TrackedEntityModel.Columns.UID, uid);
        trackedEntity.put(TrackedEntityModel.Columns.CODE, TEST_CODE);
        trackedEntity.put(TrackedEntityModel.Columns.NAME, TEST_NAME);
        trackedEntity.put(TrackedEntityModel.Columns.DISPLAY_NAME, TEST_DISPLAY_NAME);
        trackedEntity.put(TrackedEntityModel.Columns.CREATED, TEST_CREATED);
        trackedEntity.put(TrackedEntityModel.Columns.LAST_UPDATED, TEST_LAST_UPDATED);
        trackedEntity.put(TrackedEntityModel.Columns.SHORT_NAME, TEST_SHORT_NAME);
        trackedEntity.put(TrackedEntityModel.Columns.DISPLAY_SHORT_NAME, TEST_DISPLAY_SHORT_NAME);
        trackedEntity.put(TrackedEntityModel.Columns.DESCRIPTION, TEST_DESCRIPTION);
        trackedEntity.put(TrackedEntityModel.Columns.DISPLAY_DESCRIPTION, TEST_DISPLAY_DESCRIPTION);
        return trackedEntity;
    }
}
