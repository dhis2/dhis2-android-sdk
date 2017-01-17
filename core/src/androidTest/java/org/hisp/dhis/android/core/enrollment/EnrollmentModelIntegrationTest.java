package org.hisp.dhis.android.core.enrollment;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.AndroidTestUtils;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class EnrollmentModelIntegrationTest {
    private static final Long ID = 1L;
    private static final String UID = "test_enrollment";
    private static final String ORGANISATION_UNIT = "test_orgUnit";
    private static final String PROGRAM = "test_program";
    private static final Boolean FOLLOW_UP = true;
    private static final EnrollmentStatus ENROLLMENT_STATUS = EnrollmentStatus.ACTIVE;
    private static final String TRACKED_ENTITY_INSTANCE = "test_trackedEntityInstance";
    private static final String LATITUDE = "10.1337";
    private static final String LONGITUDE = "59.140";
    private static final State STATE = State.TO_UPDATE;

    // timestamp
    private static final String DATE = "2016-01-12T10:01:00.000";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.UID, Columns.CREATED, Columns.LAST_UPDATED, Columns.ORGANISATION_UNIT,
                Columns.PROGRAM, Columns.DATE_OF_ENROLLMENT, Columns.DATE_OF_INCIDENT, Columns.FOLLOW_UP,
                Columns.ENROLLMENT_STATUS, Columns.TRACKED_ENTITY_INSTANCE, Columns.LATITUDE, Columns.LONGITUDE,
                Columns.STATE
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, DATE, DATE, ORGANISATION_UNIT, PROGRAM, DATE, DATE, AndroidTestUtils.toInteger(FOLLOW_UP), ENROLLMENT_STATUS,
                TRACKED_ENTITY_INSTANCE, LATITUDE, LONGITUDE, STATE
        });

        matrixCursor.moveToFirst();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        EnrollmentModel enrollmentModel = EnrollmentModel.create(matrixCursor);
        assertThat(enrollmentModel.id()).isEqualTo(ID);
        assertThat(enrollmentModel.uid()).isEqualTo(UID);
        assertThat(enrollmentModel.created()).isEqualTo(date);
        assertThat(enrollmentModel.lastUpdated()).isEqualTo(date);
        assertThat(enrollmentModel.organisationUnit()).isEqualTo(ORGANISATION_UNIT);
        assertThat(enrollmentModel.program()).isEqualTo(PROGRAM);
        assertThat(enrollmentModel.dateOfEnrollment()).isEqualTo(date);
        assertThat(enrollmentModel.dateOfIncident()).isEqualTo(date);
        assertThat(enrollmentModel.followUp()).isEqualTo(FOLLOW_UP);
        assertThat(enrollmentModel.enrollmentStatus()).isEqualTo(ENROLLMENT_STATUS);
        assertThat(enrollmentModel.trackedEntityInstance()).isEqualTo(TRACKED_ENTITY_INSTANCE);
        assertThat(enrollmentModel.latitude()).isEqualTo(LATITUDE);
        assertThat(enrollmentModel.longitude()).isEqualTo(LONGITUDE);

    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        EnrollmentModel enrollmentModel = EnrollmentModel.builder()
                .id(ID)
                .uid(UID)
                .created(date)
                .lastUpdated(date)
                .organisationUnit(ORGANISATION_UNIT)
                .program(PROGRAM)
                .dateOfEnrollment(date)
                .dateOfIncident(date)
                .followUp(FOLLOW_UP)
                .enrollmentStatus(ENROLLMENT_STATUS)
                .trackedEntityInstance(TRACKED_ENTITY_INSTANCE)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .state(STATE)
                .build();

        ContentValues enrollmentContentValues = enrollmentModel.toContentValues();
        assertThat(enrollmentContentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(enrollmentContentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(enrollmentContentValues.getAsString(Columns.CREATED)).isEqualTo(DATE);
        assertThat(enrollmentContentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(enrollmentContentValues.getAsString(Columns.ORGANISATION_UNIT)).isEqualTo(ORGANISATION_UNIT);
        assertThat(enrollmentContentValues.getAsString(Columns.PROGRAM)).isEqualTo(PROGRAM);
        assertThat(enrollmentContentValues.getAsString(Columns.DATE_OF_ENROLLMENT)).isEqualTo(DATE);
        assertThat(enrollmentContentValues.getAsString(Columns.DATE_OF_INCIDENT)).isEqualTo(DATE);
        assertThat(enrollmentContentValues.getAsBoolean(Columns.FOLLOW_UP)).isEqualTo(FOLLOW_UP);
        assertThat(enrollmentContentValues.getAsString(Columns.ENROLLMENT_STATUS)).isEqualTo(ENROLLMENT_STATUS.name());
        assertThat(enrollmentContentValues.getAsString(Columns.TRACKED_ENTITY_INSTANCE)).isEqualTo(TRACKED_ENTITY_INSTANCE);
        assertThat(enrollmentContentValues.getAsString(Columns.LATITUDE)).isEqualTo(LATITUDE);
        assertThat(enrollmentContentValues.getAsString(Columns.LONGITUDE)).isEqualTo(LONGITUDE);
        assertThat(enrollmentContentValues.getAsString(Columns.STATE)).isEqualTo(STATE.name());

    }
}
