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
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class UserStoreShould extends AbsStoreTestCase {
    public static final String[] USER_PROJECTION = {
            UserModel.Columns.UID,
            UserModel.Columns.CODE,
            UserModel.Columns.NAME,
            UserModel.Columns.DISPLAY_NAME,
            UserModel.Columns.CREATED,
            UserModel.Columns.LAST_UPDATED,
            UserModel.Columns.BIRTHDAY,
            UserModel.Columns.EDUCATION,
            UserModel.Columns.GENDER,
            UserModel.Columns.JOB_TITLE,
            UserModel.Columns.SURNAME,
            UserModel.Columns.FIRST_NAME,
            UserModel.Columns.INTRODUCTION,
            UserModel.Columns.EMPLOYER,
            UserModel.Columns.INTERESTS,
            UserModel.Columns.LANGUAGES,
            UserModel.Columns.EMAIL,
            UserModel.Columns.PHONE_NUMBER,
            UserModel.Columns.NATIONALITY
    };

    private static final Long ID = 2L;
    private static final String UID = "test_user_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private UserStore store;

    public static ContentValues create(long id, String uid) {
        ContentValues user = new ContentValues();
        user.put(UserModel.Columns.ID, id);
        user.put(UserModel.Columns.UID, uid);
        user.put(UserModel.Columns.CODE, "test_code");
        user.put(UserModel.Columns.NAME, "test_name");
        user.put(UserModel.Columns.DISPLAY_NAME, "test_display_name");
        user.put(UserModel.Columns.CREATED, "test_created");
        user.put(UserModel.Columns.LAST_UPDATED, "test_last_updated");
        user.put(UserModel.Columns.BIRTHDAY, "test_birthday");
        user.put(UserModel.Columns.EDUCATION, "test_education");
        user.put(UserModel.Columns.GENDER, "test_gender");
        user.put(UserModel.Columns.JOB_TITLE, "test_job_title");
        user.put(UserModel.Columns.SURNAME, "test_surname");
        user.put(UserModel.Columns.FIRST_NAME, "test_first_name");
        user.put(UserModel.Columns.INTRODUCTION, "test_introduction");
        user.put(UserModel.Columns.EMPLOYER, "test_employer");
        user.put(UserModel.Columns.INTERESTS, "test_interests");
        user.put(UserModel.Columns.LANGUAGES, "test_languages");
        user.put(UserModel.Columns.EMAIL, "test_email");
        user.put(UserModel.Columns.PHONE_NUMBER, "test_phone_number");
        user.put(UserModel.Columns.NATIONALITY, "test_nationality");
        return user;
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        store = new UserStoreImpl(databaseAdapter());
    }

    @Test
    public void insert_in_data_base_when_insert() {
        Date date = new Date();

        long rowId = store.insert(
                "test_user_uid",
                "test_user_code",
                "test_user_name",
                "test_user_display_name",
                date, date,
                "test_user_birthday",
                "test_user_education",
                "test_user_gender",
                "test_user_job_title",
                "test_user_surname",
                "test_user_first_name",
                "test_user_introduction",
                "test_user_employer",
                "test_user_interests",
                "test_user_languages",
                "test_user_email",
                "test_user_phone_number",
                "test_user_nationality"
        );

        Cursor cursor = database().query(UserModel.TABLE,
                USER_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        "test_user_uid",
                        "test_user_code",
                        "test_user_name",
                        "test_user_display_name",
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        "test_user_birthday",
                        "test_user_education",
                        "test_user_gender",
                        "test_user_job_title",
                        "test_user_surname",
                        "test_user_first_name",
                        "test_user_introduction",
                        "test_user_employer",
                        "test_user_interests",
                        "test_user_languages",
                        "test_user_email",
                        "test_user_phone_number",
                        "test_user_nationality"
                )
                .isExhausted();
    }

    @Test
    public void update_in_data_base_when_update() throws Exception {
        Date date = new Date();
        ContentValues user = new ContentValues();
        user.put(UserModel.Columns.ID, ID);
        user.put(UserModel.Columns.UID, UID);
        user.put(UserModel.Columns.CODE, CODE);
        user.put(UserModel.Columns.NAME, NAME);
        user.put(UserModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        user.put(UserModel.Columns.CREATED, BaseIdentifiableObject.DATE_FORMAT.format(date));
        user.put(UserModel.Columns.LAST_UPDATED, BaseIdentifiableObject.DATE_FORMAT.format(date));

        database().insert(UserModel.TABLE, null, user);
        String[] projection = {
                UserModel.Columns.ID, UserModel.Columns.UID,
                UserModel.Columns.CODE, UserModel.Columns.NAME,
                UserModel.Columns.DISPLAY_NAME,
                UserModel.Columns.CREATED,
                UserModel.Columns.LAST_UPDATED
        };

        Cursor cursor = database().query(UserModel.TABLE, projection, null, null, null, null, null);

        // checking that the user was successfully inserted
        assertThatCursor(cursor).hasRow(ID, UID, CODE, NAME, DISPLAY_NAME,
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date));

        String newName = "test_new_name";
        String newDisplayName = "test_new_display_name";

        store.update(UID, CODE, newName, newDisplayName, date, date,
                "test_user_birthday",
                "test_user_education",
                "test_user_gender",
                "test_user_job_title",
                "test_user_surname",
                "test_user_first_name",
                "test_user_introduction",
                "test_user_employer",
                "test_user_interests",
                "test_user_languages",
                "test_user_email",
                "test_user_phone_number",
                "test_user_nationality",
                UID
        );

        cursor = database().query(UserModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).hasRow(ID, UID, CODE, newName, newDisplayName,
                BaseIdentifiableObject.DATE_FORMAT.format(date),
                BaseIdentifiableObject.DATE_FORMAT.format(date)
        ).isExhausted();
    }

    @Test
    public void delete_in_data_base_when_delete() throws Exception {
        ContentValues user = new ContentValues();
        user.put(UserModel.Columns.ID, ID);
        user.put(UserModel.Columns.UID, UID);
        user.put(UserModel.Columns.CODE, CODE);
        user.put(UserModel.Columns.NAME, NAME);
        user.put(UserModel.Columns.DISPLAY_NAME, DISPLAY_NAME);

        database().insert(UserModel.TABLE, null, user);
        String[] projection = {
                UserModel.Columns.ID, UserModel.Columns.UID,
                UserModel.Columns.CODE, UserModel.Columns.NAME,
                UserModel.Columns.DISPLAY_NAME
        };

        Cursor cursor = database().query(UserModel.TABLE, projection, null, null, null, null, null);

        // checking that the user was successfully inserted
        assertThatCursor(cursor).hasRow(ID, UID, CODE, NAME, DISPLAY_NAME);

        // delete the user
        store.delete(UID);

        cursor = database().query(UserModel.TABLE, projection, null, null, null, null, null);
        // checking that user was successfully deleted
        assertThatCursor(cursor).isExhausted();
    }

    @Test
    public void delete_all_rows_in_data_base_when_delete_without_params() {
        ContentValues user = create(1L, "test_user_id");
        database().insert(UserModel.TABLE, null, user);

        int deleted = store.delete();

        Cursor cursor = database().query(UserModel.TABLE,
                null, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_insert_null_uid() {
        store.insert(
                null,
                "test_user_code",
                "test_user_name",
                "test_user_display_name",
                new Date(), new Date(),
                "test_user_birthday",
                "test_user_education",
                "test_user_gender",
                "test_user_job_title",
                "test_user_surname",
                "test_user_first_name",
                "test_user_introduction",
                "test_user_employer",
                "test_user_interests",
                "test_user_languages",
                "test_user_email",
                "test_user_phone_number",
                "test_user_nationality"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_uid() {
        store.update(null,
                CODE, "newName", "newDisplayName",
                new Date(), new Date(),
                "test_user_birthday",
                "test_user_education",
                "test_user_gender",
                "test_user_job_title",
                "test_user_surname",
                "test_user_first_name",
                "test_user_introduction",
                "test_user_employer",
                "test_user_interests",
                "test_user_languages",
                "test_user_email",
                "test_user_phone_number",
                "test_user_nationality",
                UID
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_update_null_where_uid() {
        store.update(UID,
                CODE, "newName", "newDisplayName",
                new Date(), new Date(),
                "test_user_birthday",
                "test_user_education",
                "test_user_gender",
                "test_user_job_title",
                "test_user_surname",
                "test_user_first_name",
                "test_user_introduction",
                "test_user_employer",
                "test_user_interests",
                "test_user_languages",
                "test_user_email",
                "test_user_phone_number",
                "test_user_nationality",
                null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_delete_null_uid() {
        store.delete(null);
    }
}

