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

package org.hisp.dhis.android.core.systeminfo;

import android.content.ContentValues;
import android.database.MatrixCursor;

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.systeminfo.SystemInfoSamples;
import org.hisp.dhis.android.core.systeminfo.SystemInfoTableInfo.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.runner.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class SystemInfoDatabaseMappingShould {

    private final SystemInfo systemInfo = SystemInfoSamples.get1();
    private final String dateString;

    public SystemInfoDatabaseMappingShould() {
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(systemInfo.serverDate());
    }

    @Test
    public void map_cursor_to_object() {
        String[] columnsWithId = CollectionsHelper.appendInNewArray(SystemInfoTableInfo.TABLE_INFO.columns().all(),
                SystemInfoTableInfo.Columns.ID);
        MatrixCursor cursor = new MatrixCursor(columnsWithId);

        cursor.addRow(new Object[]{dateString, systemInfo.dateFormat(),
                systemInfo.version(), systemInfo.contextPath(), systemInfo.systemName(), systemInfo.id()});
        cursor.moveToFirst();

        SystemInfo dbSystemInfo = SystemInfo.create(cursor);
        cursor.close();

        assertThat(dbSystemInfo).isEqualTo(systemInfo);
    }

    @Test
    public void map_object_to_content_values() {
        ContentValues contentValues = systemInfo.toContentValues();
        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(systemInfo.id());
        assertThat(contentValues.getAsString(Columns.SERVER_DATE)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.DATE_FORMAT)).isEqualTo(systemInfo.dateFormat());
        assertThat(contentValues.getAsString(Columns.CONTEXT_PATH)).isEqualTo(systemInfo.contextPath());
        assertThat(contentValues.getAsString(Columns.VERSION)).isEqualTo(systemInfo.version());
        assertThat(contentValues.getAsString(Columns.SYSTEM_NAME)).isEqualTo(systemInfo.systemName());
    }
}
