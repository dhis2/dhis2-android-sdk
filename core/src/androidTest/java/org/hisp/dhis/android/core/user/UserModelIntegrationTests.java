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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserModelIntegrationTests {
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

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                UserModel.Columns.ID,
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
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE, BIRTHDAY, EDUCATION, GENDER,
                JOB_TITLE, SURNAME, FIRST_NAME, INTRODUCTION, EMPLOYER, INTERESTS, LANGUAGES, EMAIL,
                PHONE_NUMBER, NATIONALITY
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
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
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

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

        assertThat(contentValues.getAsLong(UserModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(UserModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(UserModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(UserModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(UserModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(UserModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(UserModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(UserModel.Columns.BIRTHDAY)).isEqualTo(BIRTHDAY);
        assertThat(contentValues.getAsString(UserModel.Columns.EDUCATION)).isEqualTo(EDUCATION);
        assertThat(contentValues.getAsString(UserModel.Columns.GENDER)).isEqualTo(GENDER);
        assertThat(contentValues.getAsString(UserModel.Columns.JOB_TITLE)).isEqualTo(JOB_TITLE);
        assertThat(contentValues.getAsString(UserModel.Columns.SURNAME)).isEqualTo(SURNAME);
        assertThat(contentValues.getAsString(UserModel.Columns.FIRST_NAME)).isEqualTo(FIRST_NAME);
        assertThat(contentValues.getAsString(UserModel.Columns.INTRODUCTION)).isEqualTo(INTRODUCTION);
        assertThat(contentValues.getAsString(UserModel.Columns.EMPLOYER)).isEqualTo(EMPLOYER);
        assertThat(contentValues.getAsString(UserModel.Columns.INTERESTS)).isEqualTo(INTERESTS);
        assertThat(contentValues.getAsString(UserModel.Columns.LANGUAGES)).isEqualTo(LANGUAGES);
        assertThat(contentValues.getAsString(UserModel.Columns.EMAIL)).isEqualTo(EMAIL);
        assertThat(contentValues.getAsString(UserModel.Columns.PHONE_NUMBER)).isEqualTo(PHONE_NUMBER);
        assertThat(contentValues.getAsString(UserModel.Columns.NATIONALITY)).isEqualTo(NATIONALITY);
    }
}
