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

public class CreateTrackedEntityDataValueUtils {

    private static String EVENT = "test_event";
    private static String DATA_ELEMENT = "test_dataElement";
    private static String STORED_BY = "test_storedBy";
    private static String VALUE = "12";
    private static Boolean PROVIDED_ELSEWHERE = false;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    public static ContentValues create(long id) {

        ContentValues values = new ContentValues();

        values.put(TrackedEntityDataValueModel.Columns.ID, id);
        values.put(TrackedEntityDataValueModel.Columns.EVENT, EVENT);
        values.put(TrackedEntityDataValueModel.Columns.DATA_ELEMENT, DATA_ELEMENT);
        values.put(TrackedEntityDataValueModel.Columns.STORED_BY, STORED_BY);
        values.put(TrackedEntityDataValueModel.Columns.VALUE, VALUE);
        values.put(TrackedEntityDataValueModel.Columns.CREATED, DATE);
        values.put(TrackedEntityDataValueModel.Columns.LAST_UPDATED, DATE);
        values.put(TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE, PROVIDED_ELSEWHERE);

        return values;
    }

    public static ContentValues create(@NonNull Long id, @NonNull String event, @NonNull String dataElement) {
        ContentValues values = new ContentValues();

        values.put(TrackedEntityDataValueModel.Columns.ID, id);
        values.put(TrackedEntityDataValueModel.Columns.EVENT, event);
        values.put(TrackedEntityDataValueModel.Columns.DATA_ELEMENT, dataElement);
        values.put(TrackedEntityDataValueModel.Columns.STORED_BY, STORED_BY);
        values.put(TrackedEntityDataValueModel.Columns.VALUE, VALUE);
        values.put(TrackedEntityDataValueModel.Columns.CREATED, DATE);
        values.put(TrackedEntityDataValueModel.Columns.LAST_UPDATED, DATE);
        values.put(TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE, PROVIDED_ELSEWHERE);

        return values;
    }
}
