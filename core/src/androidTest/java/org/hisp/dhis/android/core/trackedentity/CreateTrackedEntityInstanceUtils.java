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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;

public class CreateTrackedEntityInstanceUtils {
    private static final State STATE = State.TO_POST;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    public static ContentValues create(@NonNull String uid,
                                       @NonNull String organisationUnit,
                                       @NonNull String trackedEntity) {
        ContentValues trackedEntityInstance = new ContentValues();
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.UID, uid);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.CREATED, DATE);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.LAST_UPDATED, DATE);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT, organisationUnit);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.TRACKED_ENTITY, trackedEntity);
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.STATE, STATE.name());
        return trackedEntityInstance;
    }
}
