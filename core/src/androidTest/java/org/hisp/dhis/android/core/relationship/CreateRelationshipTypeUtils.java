package org.hisp.dhis.android.core.relationship;

import android.content.ContentValues;

public class CreateRelationshipTypeUtils {
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    //RelationshipTypeModel attributes:
    private static final String A_IS_TO_B = "cat of";
    private static final String B_IS_TO_A = "owner of";

    /**
     * A method to createTrackedEntityAttribute ContentValues from a RelationshipType.
     * To be used by other tests that have RelationshipType as foreign key.
     *
     * @param id
     * @param uid
     * @return
     */
    public static ContentValues create(long id, String uid) {

        ContentValues relationshipType = new ContentValues();

        relationshipType.put(RelationshipTypeModel.Columns.ID, id);
        relationshipType.put(RelationshipTypeModel.Columns.UID, uid);
        relationshipType.put(RelationshipTypeModel.Columns.CODE, CODE);
        relationshipType.put(RelationshipTypeModel.Columns.NAME, NAME);
        relationshipType.put(RelationshipTypeModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        relationshipType.put(RelationshipTypeModel.Columns.CREATED, DATE);
        relationshipType.put(RelationshipTypeModel.Columns.LAST_UPDATED, DATE);
        relationshipType.put(RelationshipTypeModel.Columns.A_IS_TO_B, A_IS_TO_B);
        relationshipType.put(RelationshipTypeModel.Columns.B_IS_TO_A, B_IS_TO_A);

        return relationshipType;
    }
}
