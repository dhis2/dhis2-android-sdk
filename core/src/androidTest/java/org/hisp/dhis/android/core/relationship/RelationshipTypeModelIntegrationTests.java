package org.hisp.dhis.android.core.relationship;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class RelationshipTypeModelIntegrationTests {
    //BaseIdentifiableModel attributes:
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    //RelationshipModel attributes:
    private static final String A_IS_TO_B = "cat of";
    private static final String B_IS_TO_A = "owner of";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                RelationshipTypeContract.Columns.ID,
                RelationshipTypeContract.Columns.UID,
                RelationshipTypeContract.Columns.CODE,
                RelationshipTypeContract.Columns.NAME,
                RelationshipTypeContract.Columns.DISPLAY_NAME,
                RelationshipTypeContract.Columns.CREATED,
                RelationshipTypeContract.Columns.LAST_UPDATED,
                RelationshipTypeContract.Columns.A_IS_TO_B,
                RelationshipTypeContract.Columns.B_IS_TO_A,
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE, A_IS_TO_B, B_IS_TO_A});

        matrixCursor.moveToFirst();
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        RelationshipTypeModel relationshipTypeModel = RelationshipTypeModel.create(matrixCursor);

        assertThat(relationshipTypeModel.id()).isEqualTo(ID);
        assertThat(relationshipTypeModel.uid()).isEqualTo(UID);
        assertThat(relationshipTypeModel.code()).isEqualTo(CODE);
        assertThat(relationshipTypeModel.name()).isEqualTo(NAME);
        assertThat(relationshipTypeModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(relationshipTypeModel.created()).isEqualTo(timeStamp);
        assertThat(relationshipTypeModel.lastUpdated()).isEqualTo(timeStamp);
        assertThat(relationshipTypeModel.aIsToB()).isEqualTo(A_IS_TO_B);
        assertThat(relationshipTypeModel.bIsToA()).isEqualTo(B_IS_TO_A);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        RelationshipTypeModel relationshipTypeModel = RelationshipTypeModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .aIsToB(A_IS_TO_B)
                .bIsToA(B_IS_TO_A)
                .build();

        ContentValues contentValues = relationshipTypeModel.toContentValues();
        assertThat(contentValues.getAsLong(RelationshipTypeContract.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(RelationshipTypeContract.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(RelationshipTypeContract.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(RelationshipTypeContract.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(RelationshipTypeContract.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(RelationshipTypeContract.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(RelationshipTypeContract.Columns.A_IS_TO_B)).isEqualTo(A_IS_TO_B);
        assertThat(contentValues.getAsString(RelationshipTypeContract.Columns.B_IS_TO_A)).isEqualTo(B_IS_TO_A);
    }
}

