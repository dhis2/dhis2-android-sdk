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
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.user.UserModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserModelModelShould {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String BIRTHDAY = "test_birthday";
    private static final String EDUCATION = "test_education";
    private static final String GENDER = "test_gender";
    private static final String JOB_TITLE = "test_job_title";
    private static final String SURNAME = "test_surname";
    private static final String FIRST_NAME = "test_first_name";
    private static final String INTRODUCTION = "test_introduction";
    private static final String EMPLOYER = "test_employer";
    private static final String INTERESTS = "test_interests";
    private static final String LANGUAGES = "test_languages";
    private static final String EMAIL = "test_email";
    private static final String PHONE_NUMBER = "test_phone_number";
    private static final String NATIONALITY = "test_nationality";

    // timestamp
    private final Date date;
    private final String dateString;

    public UserModelModelShould() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @Test
    @SmallTest
    public void create_model_when_created_from_database_cursor() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.UID,
                Columns.CODE,
                Columns.NAME,
                Columns.DISPLAY_NAME,
                Columns.CREATED,
                Columns.LAST_UPDATED,
                Columns.BIRTHDAY,
                Columns.EDUCATION,
                Columns.GENDER,
                Columns.JOB_TITLE,
                Columns.SURNAME,
                Columns.FIRST_NAME,
                Columns.INTRODUCTION,
                Columns.EMPLOYER,
                Columns.INTERESTS,
                Columns.LANGUAGES,
                Columns.EMAIL,
                Columns.PHONE_NUMBER,
                Columns.NATIONALITY
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, dateString, dateString, BIRTHDAY, EDUCATION, GENDER,
                JOB_TITLE, SURNAME, FIRST_NAME, INTRODUCTION, EMPLOYER, INTERESTS, LANGUAGES, EMAIL,
                PHONE_NUMBER, NATIONALITY
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        UserModel userModel = UserModel.create(matrixCursor);

        assertThat(userModel.id()).isEqualTo(ID);
        assertThat(userModel.uid()).isEqualTo(UID);
        assertThat(userModel.code()).isEqualTo(CODE);
        assertThat(userModel.name()).isEqualTo(NAME);
        assertThat(userModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(userModel.created()).isEqualTo(date);
        assertThat(userModel.lastUpdated()).isEqualTo(date);
        assertThat(userModel.birthday()).isEqualTo(BIRTHDAY);
        assertThat(userModel.education()).isEqualTo(EDUCATION);
        assertThat(userModel.gender()).isEqualTo(GENDER);
        assertThat(userModel.jobTitle()).isEqualTo(JOB_TITLE);
        assertThat(userModel.surname()).isEqualTo(SURNAME);
        assertThat(userModel.firstName()).isEqualTo(FIRST_NAME);
        assertThat(userModel.introduction()).isEqualTo(INTRODUCTION);
        assertThat(userModel.employer()).isEqualTo(EMPLOYER);
        assertThat(userModel.interests()).isEqualTo(INTERESTS);
        assertThat(userModel.languages()).isEqualTo(LANGUAGES);
        assertThat(userModel.email()).isEqualTo(EMAIL);
        assertThat(userModel.phoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(userModel.nationality()).isEqualTo(NATIONALITY);
    }

    @Test
    @SmallTest
    public void create_content_values_when_created_from_builder() {

        UserModel userModel = UserModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .birthday(BIRTHDAY)
                .education(EDUCATION)
                .gender(GENDER)
                .jobTitle(JOB_TITLE)
                .surname(SURNAME)
                .firstName(FIRST_NAME)
                .introduction(INTRODUCTION)
                .employer(EMPLOYER)
                .interests(INTERESTS)
                .languages(LANGUAGES)
                .email(EMAIL)
                .phoneNumber(PHONE_NUMBER)
                .nationality(NATIONALITY)
                .build();

        ContentValues contentValues = userModel.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.BIRTHDAY)).isEqualTo(BIRTHDAY);
        assertThat(contentValues.getAsString(Columns.EDUCATION)).isEqualTo(EDUCATION);
        assertThat(contentValues.getAsString(Columns.GENDER)).isEqualTo(GENDER);
        assertThat(contentValues.getAsString(Columns.JOB_TITLE)).isEqualTo(JOB_TITLE);
        assertThat(contentValues.getAsString(Columns.SURNAME)).isEqualTo(SURNAME);
        assertThat(contentValues.getAsString(Columns.FIRST_NAME)).isEqualTo(FIRST_NAME);
        assertThat(contentValues.getAsString(Columns.INTRODUCTION)).isEqualTo(INTRODUCTION);
        assertThat(contentValues.getAsString(Columns.EMPLOYER)).isEqualTo(EMPLOYER);
        assertThat(contentValues.getAsString(Columns.INTERESTS)).isEqualTo(INTERESTS);
        assertThat(contentValues.getAsString(Columns.LANGUAGES)).isEqualTo(LANGUAGES);
        assertThat(contentValues.getAsString(Columns.EMAIL)).isEqualTo(EMAIL);
        assertThat(contentValues.getAsString(Columns.PHONE_NUMBER)).isEqualTo(PHONE_NUMBER);
        assertThat(contentValues.getAsString(Columns.NATIONALITY)).isEqualTo(NATIONALITY);
    }
}
