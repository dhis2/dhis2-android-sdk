/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.enrollment;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo.Columns;

import androidx.annotation.NonNull;

public class CreateEnrollmentUtils {
    private static final String UID = "test_enrollment";
    private static final String ORGANISATION_UNIT = "test_orgUnit";
    private static final String PROGRAM = "test_program";
    private static final Boolean FOLLOW_UP = true;
    private static final EnrollmentStatus ENROLLMENT_STATUS = EnrollmentStatus.ACTIVE;
    private static final String TRACKED_ENTITY_INSTANCE = "test_trackedEntityInstance";
    private static final FeatureType GEOMETRY_TYPE = FeatureType.POINT;
    private static final String GEOMETRY_COORDINATES = "[10.1337, 59.140]";
    private static final State STATE = State.TO_UPDATE;

    // used for timestamps
    private static final String DATE = "2017-04-05T15:39:00.000";

    public static ContentValues create(@NonNull String uid, @NonNull String programUid,
                                       @NonNull String organisationUnitUid,
                                       @NonNull String trackedEntityInstanceUid) {
        ContentValues enrollment = new ContentValues();
        enrollment.put(Columns.UID, uid);
        enrollment.put(Columns.ORGANISATION_UNIT, organisationUnitUid);
        enrollment.put(Columns.PROGRAM, programUid);
        enrollment.put(Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid);
        enrollment.put(Columns.FOLLOW_UP, FOLLOW_UP);
        enrollment.put(Columns.GEOMETRY_TYPE, GEOMETRY_TYPE.getFeatureType());
        enrollment.put(Columns.GEOMETRY_COORDINATES, GEOMETRY_COORDINATES);
        enrollment.put(Columns.SYNC_STATE, STATE.name());
        enrollment.put(Columns.CREATED, DATE);
        enrollment.put(Columns.LAST_UPDATED, DATE);

        return enrollment;
    }
}
