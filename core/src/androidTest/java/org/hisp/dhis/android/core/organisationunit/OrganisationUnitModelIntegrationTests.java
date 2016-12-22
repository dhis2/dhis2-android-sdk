package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract.Columns;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class OrganisationUnitModelIntegrationTests {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final String PATH = "test_path";
    private static final String PARENT = "test_parent";
    private static final int LEVEL = 100;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.UID,
                Columns.CODE,
                Columns.NAME,
                Columns.DISPLAY_NAME,
                Columns.CREATED,
                Columns.LAST_UPDATED,
                Columns.SHORT_NAME,
                Columns.DISPLAY_SHORT_NAME,
                Columns.DESCRIPTION,
                Columns.DISPLAY_DESCRIPTION,
                Columns.PATH,
                Columns.OPENING_DATE,
                Columns.CLOSED_DATE,
                Columns.PARENT,
                Columns.LEVEL,
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE, SHORT_NAME, DISPLAY_SHORT_NAME,
                DESCRIPTION, DISPLAY_DESCRIPTION, PATH, DATE, DATE, PARENT, LEVEL
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        OrganisationUnitModel organisationUnitModel = OrganisationUnitModel.create(matrixCursor);

        assertThat(organisationUnitModel.id()).isEqualTo(ID);
        assertThat(organisationUnitModel.uid()).isEqualTo(UID);
        assertThat(organisationUnitModel.code()).isEqualTo(CODE);
        assertThat(organisationUnitModel.name()).isEqualTo(NAME);
        assertThat(organisationUnitModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(organisationUnitModel.created()).isEqualTo(date);
        assertThat(organisationUnitModel.lastUpdated()).isEqualTo(date);
        assertThat(organisationUnitModel.shortName()).isEqualTo(SHORT_NAME);
        assertThat(organisationUnitModel.displayShortName()).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(organisationUnitModel.description()).isEqualTo(DESCRIPTION);
        assertThat(organisationUnitModel.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(organisationUnitModel.path()).isEqualTo(PATH);
        assertThat(organisationUnitModel.openingDate()).isEqualTo(date);
        assertThat(organisationUnitModel.closedDate()).isEqualTo(date);
        assertThat(organisationUnitModel.parent()).isEqualTo(PARENT);
        assertThat(organisationUnitModel.level()).isEqualTo(LEVEL);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        OrganisationUnitModel organisationUnitModel = OrganisationUnitModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .shortName(SHORT_NAME)
                .displayShortName(DISPLAY_SHORT_NAME)
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .path(PATH)
                .openingDate(date)
                .closedDate(date)
                .parent(PARENT)
                .level(LEVEL)
                .build();

        ContentValues contentValues = organisationUnitModel.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.PATH)).isEqualTo(PATH);
        assertThat(contentValues.getAsString(Columns.OPENING_DATE)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.CLOSED_DATE)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.PARENT)).isEqualTo(PARENT);
        assertThat(contentValues.getAsInteger(Columns.LEVEL)).isEqualTo(LEVEL);
    }
}
