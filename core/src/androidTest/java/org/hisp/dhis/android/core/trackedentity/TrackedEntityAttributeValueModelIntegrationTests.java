package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.State;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeValueModelIntegrationTests {
    //BaseModel:
    private static final long ID = 11L;
    //BaseDataModel:
    private static final State STATE = State.SYNCED;
    //TrackedEntityAttributeValueModdel:
    private static final String ATTRIBUTE = "TestAttribute";
    private static final String VALUE = "TestValue";

    @Test
    public void create_shouldConvertToModel() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                TrackedEntityAttributeValueModel.Columns.ID,
                TrackedEntityAttributeValueModel.Columns.STATE,
                TrackedEntityAttributeValueModel.Columns.ATTRIBUTE,
                TrackedEntityAttributeValueModel.Columns.VALUE
        });

        matrixCursor.addRow(new Object[] {ID, STATE, ATTRIBUTE, VALUE});

        matrixCursor.moveToFirst();

        TrackedEntityAttributeValueModel model = TrackedEntityAttributeValueModel.create(matrixCursor);

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.state()).isEqualTo(STATE);
        assertThat(model.trackedEntityAttribute()).isEqualTo(ATTRIBUTE);
        assertThat(model.value()).isEqualTo(VALUE);

        matrixCursor.close();
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() {
        TrackedEntityAttributeValueModel model = TrackedEntityAttributeValueModel.builder()
                .id(ID)
                .state(STATE)
                .trackedEntityAttribute(ATTRIBUTE)
                .value(VALUE)
                .build();

        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(TrackedEntityAttributeValueModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(TrackedEntityAttributeValueModel.Columns.STATE)).isEqualTo(STATE.name());
        assertThat(contentValues.getAsString(TrackedEntityAttributeValueModel.Columns.ATTRIBUTE)).isEqualTo(ATTRIBUTE);
        assertThat(contentValues.getAsString(TrackedEntityAttributeValueModel.Columns.VALUE)).isEqualTo(VALUE);
    }
}

