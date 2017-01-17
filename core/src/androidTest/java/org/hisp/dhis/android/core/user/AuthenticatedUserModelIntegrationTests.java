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

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class AuthenticatedUserModelIntegrationTests {
    private static final long ID = 2L;
    private static final String USER = "test_user";
    private static final String CREDENTIALS = "test_credentials";

    @Test
    public void create_shouldConvertToModel() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                AuthenticatedUserModel.Columns.ID, AuthenticatedUserModel.Columns.USER, AuthenticatedUserModel.Columns.CREDENTIALS
        });

        matrixCursor.addRow(new Object[]{
                ID, USER, CREDENTIALS
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        AuthenticatedUserModel authenticatedUserModel =
                AuthenticatedUserModel.create(matrixCursor);

        assertThat(authenticatedUserModel.id()).isEqualTo(ID);
        assertThat(authenticatedUserModel.user()).isEqualTo(USER);
        assertThat(authenticatedUserModel.credentials()).isEqualTo(CREDENTIALS);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() {
        AuthenticatedUserModel authenticatedUserModel = AuthenticatedUserModel.builder()
                .id(ID).user(USER).credentials(CREDENTIALS).build();

        ContentValues contentValues = authenticatedUserModel.toContentValues();
        assertThat(contentValues.getAsLong(AuthenticatedUserModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(AuthenticatedUserModel.Columns.USER)).isEqualTo(USER);
        assertThat(contentValues.getAsString(AuthenticatedUserModel.Columns.CREDENTIALS)).isEqualTo(CREDENTIALS);
    }
}
