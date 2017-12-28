package org.hisp.dhis.android.core.resource;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.resource.ResourceModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ResourceModelShould {
    private static final Long ID = 2L;
    private static final String RESOURCE_TYPE = "OrganisationUnit";

    // timestamp
    private static final String DATE = "2017-01-18T13:39:00.000";

    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.RESOURCE_TYPE, Columns.LAST_SYNCED
        });

        matrixCursor.addRow(new Object[]{
                ID, RESOURCE_TYPE, DATE
        });

        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ResourceModel resource = ResourceModel.create(matrixCursor);
        assertThat(resource.id()).isEqualTo(ID);
        assertThat(resource.resourceType()).isEqualTo(RESOURCE_TYPE);
        assertThat(resource.lastSynced()).isEqualTo(timeStamp);
    }

    @Test
    @SmallTest
    public void create_content_values_when_created_from_builder() throws Exception {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        ResourceModel resource = ResourceModel.builder()
                .id(ID)
                .resourceType(RESOURCE_TYPE)
                .lastSynced(timeStamp)
                .build();

        ContentValues contentValues = resource.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.RESOURCE_TYPE)).isEqualTo(RESOURCE_TYPE);
        assertThat(contentValues.getAsString(Columns.LAST_SYNCED)).isEqualTo(DATE);
    }
}
