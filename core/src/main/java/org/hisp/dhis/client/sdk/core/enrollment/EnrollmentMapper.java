/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.core.enrollment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.AbsMapper;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentTable.EnrollmentColumns;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.State;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.enrollment.EnrollmentStatus;

import java.text.ParseException;

import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getLong;
import static org.hisp.dhis.client.sdk.core.commons.database.DbUtils.getString;

class EnrollmentMapper extends AbsMapper<Enrollment> {

    EnrollmentMapper() {
        // explicit constructor
    }

    @Override
    public Uri getContentUri() {
        return EnrollmentTable.CONTENT_URI;
    }

    @Override
    public Uri getContentItemUri(long id) {
        return ContentUris.withAppendedId(EnrollmentTable.CONTENT_URI, id);
    }

    @Override
    public String[] getProjection() {
        return EnrollmentTable.PROJECTION;
    }

    @Override
    public ContentValues toContentValues(Enrollment enrollment) {
        if (!enrollment.isValid()) {
            throw new IllegalArgumentException("Enrollment is not valid");
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(EnrollmentColumns.COLUMN_ID, enrollment.id());
        contentValues.put(EnrollmentColumns.COLUMN_UID, enrollment.uid());
        contentValues.put(EnrollmentColumns.COLUMN_CREATED, BaseIdentifiableObject.DATE_FORMAT.format(enrollment.created()));
        contentValues.put(EnrollmentColumns.COLUMN_LAST_UPDATED, enrollment.lastUpdated() != null ? BaseIdentifiableObject.DATE_FORMAT.format(enrollment.lastUpdated()) : null);
        contentValues.put(EnrollmentColumns.COLUMN_TRACKED_ENTITY_INSTANCE, enrollment.trackedEntityInstance());
        contentValues.put(EnrollmentColumns.COLUMN_ENROLLMENT_DATE, enrollment.dateOfEnrollment() != null ? BaseIdentifiableObject.DATE_FORMAT.format(enrollment.dateOfEnrollment()) : null);
        contentValues.put(EnrollmentColumns.COLUMN_INCIDENT_DATE, enrollment.dateOfIncident() != null ? BaseIdentifiableObject.DATE_FORMAT.format(enrollment.dateOfIncident()) : null);
        contentValues.put(EnrollmentColumns.COLUMN_ENROLLMENT_STATUS, enrollment.enrollmentStatus().toString());
        contentValues.put(EnrollmentColumns.COLUMN_ORGANISATION_UNIT, enrollment.organisationUnit());
        contentValues.put(EnrollmentColumns.COLUMN_PROGRAM, enrollment.program());
        contentValues.put(EnrollmentColumns.COLUMN_FOLLOWUP, enrollment.followUp());
        contentValues.put(EnrollmentColumns.COLUMN_STATE, enrollment.state().toString());

        return contentValues;
    }

    @Override
    public Enrollment toModel(Cursor cursor) {
        Enrollment enrollment = null;

        try {
            enrollment = Enrollment.builder()
                    .id(getLong(cursor, EnrollmentColumns.COLUMN_ID))
                    .uid(getString(cursor, EnrollmentColumns.COLUMN_UID))
                    .enrollmentStatus(EnrollmentStatus.valueOf(getString(cursor, EnrollmentColumns.COLUMN_ENROLLMENT_STATUS)))
                    .organisationUnit(getString(cursor, EnrollmentColumns.COLUMN_ORGANISATION_UNIT))
                    .program(getString(cursor, EnrollmentColumns.COLUMN_PROGRAM))
                    .trackedEntityInstance(getString(cursor, EnrollmentColumns.COLUMN_TRACKED_ENTITY_INSTANCE))
                    .state(State.valueOf(getString(cursor, EnrollmentColumns.COLUMN_STATE)))
                    .created(BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, EnrollmentColumns.COLUMN_CREATED)))
                    .lastUpdated(getString(cursor, EnrollmentColumns.COLUMN_LAST_UPDATED) != null ? BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, EnrollmentColumns.COLUMN_LAST_UPDATED)) : null)
                    .followUp(Boolean.getBoolean(getString(cursor, EnrollmentColumns.COLUMN_FOLLOWUP)))
                    .dateOfIncident(getString(cursor, EnrollmentColumns.COLUMN_INCIDENT_DATE) != null ? BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, EnrollmentColumns.COLUMN_INCIDENT_DATE)) : null)
                    .dateOfEnrollment(getString(cursor, EnrollmentColumns.COLUMN_ENROLLMENT_DATE) != null ? BaseIdentifiableObject.DATE_FORMAT
                            .parse(getString(cursor, EnrollmentColumns.COLUMN_ENROLLMENT_DATE)) : null).build();

        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return enrollment;
    }
}
