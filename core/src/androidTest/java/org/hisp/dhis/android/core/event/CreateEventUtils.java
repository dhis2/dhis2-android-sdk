/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        event.put(EventTableInfo.Columns.UID, uid);

        if (enrollmentUid == null) {
            event.putNull(EventFields.ENROLLMENT);
        }

        event.put(EventFields.ENROLLMENT, enrollmentUid);
        event.put(EventFields.CREATED, DATE);
        event.put(EventFields.LAST_UPDATED, DATE);
        event.put(EventFields.STATUS, STATUS.name());
        event.put(EventTableInfo.Columns.LATITUDE, LATITUDE);
        event.put(EventTableInfo.Columns.LONGITUDE, LONGITUDE);
        event.put(EventFields.PROGRAM, program);
        event.put(EventFields.PROGRAM_STAGE, programStage);
        event.put(EventTableInfo.Columns.ORGANISATION_UNIT, orgUnit);
        event.put(EventFields.EVENT_DATE, DATE);
        event.put(EventFields.COMPLETE_DATE, DATE);
        event.put(EventFields.DUE_DATE, DATE);
        event.put(BaseDataModel.Columns.STATE, State.TO_POST.name());
        return event;
    }
}