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

package org.hisp.dhis.android.core.configuration;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.configuration.ConfigurationModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.HttpUrl;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ConfigurationModelShould {
    private static final long ID = 11L;
    private static final String SERVER_URL = "https://testurl.org/";

    @Test
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.SERVER_URL
        });
        cursor.addRow(new Object[]{
                ID, SERVER_URL
        });
        cursor.moveToFirst();

        ConfigurationModel model = ConfigurationModel.create(cursor);

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.serverUrl()).isEqualTo(HttpUrl.parse(SERVER_URL));
    }

    @Test
    public void create_content_values_when_created_from_builder() {
        ConfigurationModel configurationModel = ConfigurationModel.builder()
                .id(ID).serverUrl(HttpUrl.parse(SERVER_URL)).build();

        ContentValues contentValues = configurationModel.toContentValues();
        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.SERVER_URL)).isEqualTo(SERVER_URL);
    }
}
