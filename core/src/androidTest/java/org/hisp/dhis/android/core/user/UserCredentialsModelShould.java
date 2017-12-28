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

package org.hisp.dhis.android.core.user;

import static com.google.common.truth.Truth.assertThat;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class UserCredentialsModelShould {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String USERNAME = "test_username";
    private static final String USER = "test_user";

    private final Date date;
    private final String dateString;

    public UserCredentialsModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                UserCredentialsModel.Columns.ID,
                UserCredentialsModel.Columns.UID,
                UserCredentialsModel.Columns.CODE,
                UserCredentialsModel.Columns.NAME,
                UserCredentialsModel.Columns.DISPLAY_NAME,
                UserCredentialsModel.Columns.CREATED,
                UserCredentialsModel.Columns.LAST_UPDATED,
                UserCredentialsModel.Columns.USERNAME,
                UserCredentialsModel.Columns.USER
        });
        cursor.addRow(new Object[]{ID, UID, CODE, NAME, DISPLAY_NAME, dateString, dateString, USERNAME, USER});
        cursor.moveToFirst();

        UserCredentialsModel model = UserCredentialsModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.uid()).isEqualTo(UID);
        assertThat(model.code()).isEqualTo(CODE);
        assertThat(model.name()).isEqualTo(NAME);
        assertThat(model.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.username()).isEqualTo(USERNAME);
        assertThat(model.user()).isEqualTo(USER);
    }

    @Test
    @SmallTest
    public void create_content_values_when_created_from_builder() {
        UserCredentialsModel model = UserCredentialsModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .username(USERNAME)
                .user(USER)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(UserCredentialsModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.USERNAME)).isEqualTo(USERNAME);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.USER)).isEqualTo(USER);
    }
}
