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

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.database.AbsDataStore;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentTable.EnrollmentColumns;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

class EnrollmentStoreImpl extends AbsDataStore<Enrollment> implements EnrollmentStore {

    EnrollmentStoreImpl(ContentResolver contentResolver) {
        super(contentResolver, new EnrollmentMapper());
    }

    @Override
    public List<Enrollment> query(String orgUnitUid, String programUid) {
        isNull(orgUnitUid, "organisationUnit uid must not be null");
        isNull(programUid, "program uid must not be null");

        final String selection = EnrollmentColumns.COLUMN_ORGANISATION_UNIT + " = ? AND " +
                EnrollmentColumns.COLUMN_PROGRAM + " = ?";
        final String[] selectionArgs = new String[]{
                orgUnitUid, programUid
        };

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModels(cursor);
    }

    @Override
    public List<Enrollment> queryByTrackedEntityInstance(String trackedEntityInstanceUid) {
        isNull(trackedEntityInstanceUid, "trackedEntityInstanceUid must not be null!");
        final String selection = EnrollmentColumns.COLUMN_TRACKED_ENTITY_INSTANCE + " = ?";
        final String[] selectionArgs = new String[] {trackedEntityInstanceUid};
        Cursor cursor = contentResolver.query(mapper.getContentUri(), mapper.getProjection(),
                selection, selectionArgs, null);
        return toModels(cursor);
    }

    @Override
    public Enrollment query(String uid) {
        isNull(uid, "Uid must not be null");

        final String selection = EnrollmentColumns.COLUMN_UID + " = ?";
        final String[] selectionArgs = new String[]{
                uid
        };

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModel(cursor);
    }
}
