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

package org.hisp.dhis.android.core.systeminfo;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class SystemInfoModelShould {
    private static final long ID = 1L;
    private static final String DATE_FORMAT = "testDateFormat";
    private static final String VERSION = "test.version-SNAPSHOT";
    private static final String CONTEXT_PATH = "https://test.context.com/path";

    private final Date date;
    private final String dateString;

    public SystemInfoModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.SERVER_DATE,
                Columns.DATE_FORMAT,
                Columns.VERSION,
                Columns.CONTEXT_PATH
        });
        cursor.addRow(new Object[]{ID, dateString, DATE_FORMAT, VERSION, CONTEXT_PATH});
        cursor.moveToFirst();

        SystemInfoModel systemInfoModel = SystemInfoModel.create(cursor);
        cursor.close();

        assertThat(systemInfoModel.id()).isEqualTo(ID);
        assertThat(systemInfoModel.serverDate()).isEqualTo(date);
        assertThat(systemInfoModel.dateFormat()).isEqualTo(DATE_FORMAT);
        assertThat(systemInfoModel.version()).isEqualTo(VERSION);
        assertThat(systemInfoModel.contextPath()).isEqualTo(CONTEXT_PATH);
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        SystemInfoModel model = SystemInfoModel.builder()
                .id(ID)
                .serverDate(date)
                .dateFormat(DATE_FORMAT)
                .version(VERSION)
                .contextPath(CONTEXT_PATH)
                .build();

        ContentValues contentValues = model.toContentValues();
        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.SERVER_DATE)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.DATE_FORMAT)).isEqualTo(DATE_FORMAT);
        assertThat(contentValues.getAsString(Columns.CONTEXT_PATH)).isEqualTo(CONTEXT_PATH);
        assertThat(contentValues.getAsString(Columns.VERSION)).isEqualTo(VERSION);
    }
}
