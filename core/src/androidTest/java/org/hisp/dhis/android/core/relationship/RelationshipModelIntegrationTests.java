package org.hisp.dhis.android.core.relationship;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class RelationshipModelIntegrationTests {

    //table id:
    private static final long ID = 11L;

    // RelationshipModel attributes:
    private static final String TRACKED_ENTITY_INSTANCE_A = "Tei A uid";
    private static final String TRACKED_ENTITY_INSTANCE_B = "Tei B uid";
    private static final String RELATIONSHIP_TYPE = "RelationshipType uid";

    @Test
    public void create_shouldConvertToModel() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                RelationshipModel.Columns.ID,
                RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A,
                RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B,
                RelationshipModel.Columns.RELATIONSHIP_TYPE
        });

        matrixCursor.addRow(new Object[]{
                ID, TRACKED_ENTITY_INSTANCE_A, TRACKED_ENTITY_INSTANCE_B, RELATIONSHIP_TYPE});

        matrixCursor.moveToFirst();

        RelationshipModel relationshipModel = RelationshipModel.create(matrixCursor);

        assertThat(relationshipModel.id()).isEqualTo(ID);
        assertThat(relationshipModel.trackedEntityInstanceA()).isEqualTo(TRACKED_ENTITY_INSTANCE_A);
        assertThat(relationshipModel.trackedEntityInstanceB()).isEqualTo(TRACKED_ENTITY_INSTANCE_B);
        assertThat(relationshipModel.relationshipType()).isEqualTo(RELATIONSHIP_TYPE);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() {
        RelationshipModel relationshipModel = RelationshipModel.builder()
                .id(ID)
                .trackedEntityInstanceA(TRACKED_ENTITY_INSTANCE_A)
                .trackedEntityInstanceB(TRACKED_ENTITY_INSTANCE_B)
                .relationshipType(RELATIONSHIP_TYPE)
                .build();

        ContentValues contentValues = relationshipModel.toContentValues();

        assertThat(contentValues.getAsLong(RelationshipModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A)).isEqualTo(TRACKED_ENTITY_INSTANCE_A);
        assertThat(contentValues.getAsString(RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B)).isEqualTo(TRACKED_ENTITY_INSTANCE_B);
        assertThat(contentValues.getAsString(RelationshipModel.Columns.RELATIONSHIP_TYPE)).isEqualTo(RELATIONSHIP_TYPE);
    }
}
