package org.hisp.dhis.android.core.relationship;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class RelationshipStoreIntegrationTests extends AbsStoreTestCase {

    //Relationship attributes:
    private static final String TRACKED_ENTITY_INSTANCE_A = "Tei A uid";
    private static final String TRACKED_ENTITY_INSTANCE_B = "Tei B uid";

    //RelationshipType (foreign key):
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE = "test relationshipType uid";

    //Relationship projection:
    private static final String[] RELATIONSHIP_PROJECTION = {
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A,
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B,
            RelationshipModel.Columns.RELATIONSHIP_TYPE
    };

    private RelationshipStore relationshipStore;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        relationshipStore = new RelationshipStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRelationshipInDatabase() {
        //Insert RelationshipType in RelationshipType table, such that it can be used as foreign key:
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(
                RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE
        );
        database().insert(Tables.RELATIONSHIP_TYPE, null, relationshipType);

        // The test itself:
        long rowId = relationshipStore.insert(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        );

        Cursor cursor = database().query(
                Tables.RELATIONSHIP,
                RELATIONSHIP_PROJECTION,
                null, null, null, null, null, null
        );

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                TRACKED_ENTITY_INSTANCE_A,
                TRACKED_ENTITY_INSTANCE_B,
                RELATIONSHIP_TYPE
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistRelationshipNullableInDatabase() {
        //Insert foreign keys in their respective tables:
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(
                RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE
        );
        database().insert(Tables.RELATIONSHIP_TYPE, null, relationshipType);

        long rowId = relationshipStore.insert(null, null, RELATIONSHIP_TYPE);
        Cursor cursor = database().query(Tables.RELATIONSHIP, RELATIONSHIP_PROJECTION,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(null, null, RELATIONSHIP_TYPE).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        relationshipStore.close();
        assertThat(database().isOpen()).isTrue();
    }
}
