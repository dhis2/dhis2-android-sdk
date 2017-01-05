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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityStoreIntegrationTests extends AbsStoreTestCase {
    public static final String[] TRACKED_ENTITY_PROJECTION = {
            TrackedEntityContract.Columns.UID,
            TrackedEntityContract.Columns.CODE,
            TrackedEntityContract.Columns.NAME,
            TrackedEntityContract.Columns.DISPLAY_NAME,
            TrackedEntityContract.Columns.CREATED,
            TrackedEntityContract.Columns.LAST_UPDATED,
            TrackedEntityContract.Columns.SHORT_NAME,
            TrackedEntityContract.Columns.DISPLAY_SHORT_NAME,
            TrackedEntityContract.Columns.DESCRIPTION,
            TrackedEntityContract.Columns.DISPLAY_DESCRIPTION,
    };

    private TrackedEntityStore trackedEntityStore;

    public static ContentValues create(long id, String uid) {
        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityContract.Columns.ID, id);
        trackedEntity.put(TrackedEntityContract.Columns.UID, uid);
        trackedEntity.put(TrackedEntityContract.Columns.CODE, "test_code");
        trackedEntity.put(TrackedEntityContract.Columns.NAME, "test_name");
        trackedEntity.put(TrackedEntityContract.Columns.DISPLAY_NAME, "test_display_name");
        trackedEntity.put(TrackedEntityContract.Columns.CREATED, "test_created");
        trackedEntity.put(TrackedEntityContract.Columns.LAST_UPDATED, "test_last_updated");
        trackedEntity.put(TrackedEntityContract.Columns.SHORT_NAME, "test_short_name");
        trackedEntity.put(TrackedEntityContract.Columns.DISPLAY_SHORT_NAME, "test_display_short_name");
        trackedEntity.put(TrackedEntityContract.Columns.DESCRIPTION, "test_description");
        trackedEntity.put(TrackedEntityContract.Columns.DISPLAY_DESCRIPTION, "test_display_description");
        return trackedEntity;
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        trackedEntityStore = new TrackedEntityStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        Date date = new Date();

        long rowId = trackedEntityStore.insert(
                "test_uid",
                "test_code",
                "test_name",
                "test_display_name",
                date,
                date,
                "test_short_name",
                "test_display_short_name",
                "test_description",
                "test_display_description"
        );

        Cursor cursor = database().query(DbOpenHelper.Tables.TRACKED_ENTITY,
                TRACKED_ENTITY_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        "test_uid",
                        "test_code",
                        "test_name",
                        "test_display_name",
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        "test_short_name",
                        "test_display_short_name",
                        "test_description",
                        "test_display_description"
                )
                .isExhausted();
    }

    // ToDo: consider introducing conflict resolution strategy

    @Test
    public void close_shouldNotCloseDatabase() {
        trackedEntityStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
