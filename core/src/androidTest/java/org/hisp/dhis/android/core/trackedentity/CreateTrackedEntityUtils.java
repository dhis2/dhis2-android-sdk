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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;

public class CreateTrackedEntityUtils {

    public static final String TEST_CODE = "test_code";
    public static final String TEST_NAME = "test_name";
    public static final String TEST_DISPLAY_NAME = "test_display_name";
    public static final String TEST_CREATED = "2001-02-07T16:04:40.387";
    public static final String TEST_LAST_UPDATED = "2001-02-07T16:04:40.387";
    public static final String TEST_SHORT_NAME = "test_short_name";
    public static final String TEST_DISPLAY_SHORT_NAME = "test_display_short_name";
    public static final String TEST_DESCRIPTION = "test_description";
    public static final String TEST_DISPLAY_DESCRIPTION = "test_display_description";

    public static ContentValues create(long id, String uid) {
        ContentValues trackedEntityType = new ContentValues();
        trackedEntityType.put(TrackedEntityTypeModel.Columns.ID, id);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.UID, uid);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.CODE, TEST_CODE);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.NAME, TEST_NAME);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.DISPLAY_NAME, TEST_DISPLAY_NAME);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.CREATED, TEST_CREATED);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.LAST_UPDATED, TEST_LAST_UPDATED);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.SHORT_NAME, TEST_SHORT_NAME);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.DISPLAY_SHORT_NAME, TEST_DISPLAY_SHORT_NAME);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.DESCRIPTION, TEST_DESCRIPTION);
        trackedEntityType.put(TrackedEntityTypeModel.Columns.DISPLAY_DESCRIPTION, TEST_DISPLAY_DESCRIPTION);
        return trackedEntityType;
    }
}
