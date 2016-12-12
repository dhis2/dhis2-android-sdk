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

package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.commons.BaseIdentifiableObjectModel;

// TODO: Tests
@AutoValue
public abstract class UserModel extends BaseIdentifiableObjectModel {
    public static final String GENDER_MALE = "gender_male";
    public static final String GENDER_FEMALE = "gender_female";
    public static final String GENDER_OTHER = "gender_other";

    @Nullable
    @ColumnName(UserContract.Columns.BIRTHDAY)
    public abstract String birthday();

    @Nullable
    @ColumnName(UserContract.Columns.EDUCATION)
    public abstract String education();

    @Nullable
    @ColumnName(UserContract.Columns.GENDER)
    public abstract String gender();

    @Nullable
    @ColumnName(UserContract.Columns.JOB_TITLE)
    public abstract String jobTitle();

    @Nullable
    @ColumnName(UserContract.Columns.SURNAME)
    public abstract String surname();

    @Nullable
    @ColumnName(UserContract.Columns.FIRST_NAME)
    public abstract String firstName();

    @Nullable
    @ColumnName(UserContract.Columns.INTRODUCTION)
    public abstract String introduction();

    @Nullable
    @ColumnName(UserContract.Columns.EMPLOYER)
    public abstract String employer();

    @Nullable
    @ColumnName(UserContract.Columns.INTERESTS)
    public abstract String interests();

    @Nullable
    @ColumnName(UserContract.Columns.LANGUAGES)
    public abstract String languages();

    @Nullable
    @ColumnName(UserContract.Columns.EMAIL)
    public abstract String email();

    @Nullable
    @ColumnName(UserContract.Columns.PHONE_NUMBER)
    public abstract String phoneNumber();

    @Nullable
    @ColumnName(UserContract.Columns.NATIONALITY)
    public abstract String nationality();

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_UserModel.Builder();
    }

    @NonNull
    public static UserModel create(Cursor cursor) {
        return AutoValue_UserModel.createFromCursor(cursor);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {
        public abstract Builder birthday(@Nullable String birthday);

        public abstract Builder education(@Nullable String education);

        public abstract Builder gender(@Nullable String gender);

        public abstract Builder jobTitle(@Nullable String jobTitle);

        public abstract Builder surname(@Nullable String surName);

        public abstract Builder firstName(@Nullable String firstName);

        public abstract Builder introduction(@Nullable String introduction);

        public abstract Builder employer(@Nullable String employer);

        public abstract Builder interests(@Nullable String interests);

        public abstract Builder languages(@Nullable String languages);

        public abstract Builder email(@Nullable String email);

        public abstract Builder phoneNumber(@Nullable String phoneNumber);

        public abstract Builder nationality(@Nullable String nationality);

        public abstract UserModel build();
    }
}
