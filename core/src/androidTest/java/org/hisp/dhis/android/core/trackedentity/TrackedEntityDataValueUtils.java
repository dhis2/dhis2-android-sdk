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

public class TrackedEntityDataValueUtils {

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    private static final long ID = 11L;
    private static final String EVENT = "test_event";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String STORED_BY = "test_storedBy";
    private static final String VALUE = "test_value";
    private static final Boolean PROVIDED_ELSEWHERE = false;

    public static ContentValues create(long id) {

        ContentValues trackedEntityDataValues = new ContentValues();
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.ID, id);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.EVENT, EVENT);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.CREATED, DATE);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.LAST_UPDATED, DATE);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.DATA_ELEMENT, DATA_ELEMENT);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.STORED_BY, STORED_BY);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.VALUE, VALUE);
        trackedEntityDataValues.put(TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE, PROVIDED_ELSEWHERE);
        return trackedEntityDataValues;
    }
}
