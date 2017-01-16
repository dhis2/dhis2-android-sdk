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
    //TrackedEntityAttributeValueModel:
    private static final String VALUE = "TestValue";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "TestAttribute";
    private static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";

    @Test
    public void create_shouldConvertToModel() {

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                TrackedEntityAttributeValueModel.Columns.ID,
                TrackedEntityAttributeValueModel.Columns.STATE,
                TrackedEntityAttributeValueModel.Columns.VALUE,
                TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE,
                TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE
        });

        matrixCursor.addRow(new Object[]{ID, STATE, VALUE, TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE});

        matrixCursor.moveToFirst();

        TrackedEntityAttributeValueModel model = TrackedEntityAttributeValueModel.create(matrixCursor);

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.state()).isEqualTo(STATE);
        assertThat(model.value()).isEqualTo(VALUE);
        assertThat(model.trackedEntityAttribute()).isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(model.trackedEntityInstance()).isEqualTo(TRACKED_ENTITY_INSTANCE);
        matrixCursor.close();
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() {

        TrackedEntityAttributeValueModel model = TrackedEntityAttributeValueModel.builder()
                .id(ID)
                .state(STATE)
                .trackedEntityAttribute(TRACKED_ENTITY_ATTRIBUTE)
                .trackedEntityInstance(TRACKED_ENTITY_INSTANCE)
                .value(VALUE)
                .build();

        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(TrackedEntityAttributeValueModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(TrackedEntityAttributeValueModel.Columns.STATE)).isEqualTo(STATE.name());
        assertThat(contentValues.getAsString(TrackedEntityAttributeValueModel.Columns.VALUE)).isEqualTo(VALUE);
        assertThat(contentValues.getAsString(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE))
                .isEqualTo(TRACKED_ENTITY_ATTRIBUTE);
        assertThat(contentValues.getAsString(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE))
                .isEqualTo(TRACKED_ENTITY_INSTANCE);
    }
}

