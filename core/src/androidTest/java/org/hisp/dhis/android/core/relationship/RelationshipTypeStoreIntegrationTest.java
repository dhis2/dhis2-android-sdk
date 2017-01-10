package org.hisp.dhis.android.core.relationship;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class RelationshipTypeStoreIntegrationTest extends AbsStoreTestCase {

    //BaseIdentifiable attributes:
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    //RelationshipType attributes:
    private static final String A_IS_TO_B = " a cat";
    private static final String B_IS_TO_A = " a cat owner ";

    private static final String[] RELATIONSHIP_TYPE_PROJECTION = {
            RelationshipTypeContract.Columns.UID,
            RelationshipTypeContract.Columns.CODE,
            RelationshipTypeContract.Columns.NAME,
            RelationshipTypeContract.Columns.DISPLAY_NAME,
            RelationshipTypeContract.Columns.CREATED,
            RelationshipTypeContract.Columns.LAST_UPDATED,
            RelationshipTypeContract.Columns.A_IS_TO_B,
            RelationshipTypeContract.Columns.B_IS_TO_A
    };

    private RelationshipTypeStore relationshipTypeStore;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.relationshipTypeStore = new RelationshipTypeStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRelationshipTypeInDatabase() throws ParseException {

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = relationshipTypeStore.insert(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                date,
                date,
                A_IS_TO_B,
                B_IS_TO_A
        );

        Cursor cursor = database().query(
                Tables.RELATIONSHIP_TYPE,
                RELATIONSHIP_TYPE_PROJECTION,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                CODE,
                NAME,
                DISPLAY_NAME,
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                A_IS_TO_B,
                B_IS_TO_A
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistRelationshipTypeNullableInDatabase() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = relationshipTypeStore.insert(
                UID,
                null,
                NAME,
                null, null, null,
                A_IS_TO_B,
                B_IS_TO_A
        );

        Cursor cursor = database().query(
                Tables.RELATIONSHIP_TYPE,
                RELATIONSHIP_TYPE_PROJECTION,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(
                UID,
                null,
                NAME,
                null, null, null,
                A_IS_TO_B,
                B_IS_TO_A
        ).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        relationshipTypeStore.close();
        assertThat(database().isOpen()).isTrue();
    }
}
