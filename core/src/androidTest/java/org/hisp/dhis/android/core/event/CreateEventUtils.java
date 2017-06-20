/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.event;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;

public class CreateEventUtils {

    private static final String ENROLLMENT_UID = "test_enrollment";
    private static final EventStatus STATUS = EventStatus.ACTIVE;
    private static final String LATITUDE = "10.832152";
    private static final String LONGITUDE = "59.345231";

    // timestamp
    private static final String DATE = "2017-01-12T11:31:00.000";

    public static ContentValues create(@NonNull String uid,
                                       @NonNull String program,
                                       @NonNull String programStage,
                                       @NonNull String orgUnit,
                                       @Nullable String enrollmentUid) {
        ContentValues event = new ContentValues();
        event.put(EventModel.Columns.UID, uid);

        if (enrollmentUid == null) {
            event.putNull(EventModel.Columns.ENROLLMENT_UID);
        }

        event.put(EventModel.Columns.ENROLLMENT_UID, enrollmentUid);
        event.put(EventModel.Columns.CREATED, DATE);
        event.put(EventModel.Columns.LAST_UPDATED, DATE);
        event.put(EventModel.Columns.STATUS, STATUS.name());
        event.put(EventModel.Columns.LATITUDE, LATITUDE);
        event.put(EventModel.Columns.LONGITUDE, LONGITUDE);
        event.put(EventModel.Columns.PROGRAM, program);
        event.put(EventModel.Columns.PROGRAM_STAGE, programStage);
        event.put(EventModel.Columns.ORGANISATION_UNIT, orgUnit);
        event.put(EventModel.Columns.EVENT_DATE, DATE);
        event.put(EventModel.Columns.COMPLETE_DATE, DATE);
        event.put(EventModel.Columns.DUE_DATE, DATE);
        event.put(EventModel.Columns.STATE, State.TO_POST.name());
        return event;
    }
}
