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

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
// @JsonDeserialize(builder = AutoValue_User.Builder.class)
public abstract class User extends BaseIdentifiableObject {
    public static final String GENDER_MALE = "gender_male";
    public static final String GENDER_FEMALE = "gender_female";
    public static final String GENDER_OTHER = "gender_other";

    private static final String BIRTHDAY = "birthday";
    private static final String EDUCATION = "education";
    private static final String GENDER = "gender";
    private static final String JOB_TITLE = "jobTitle";
    private static final String SURNAME = "surname";
    private static final String FIRST_NAME = "firstName";
    private static final String INTRODUCTION = "introduction";
    private static final String EMPLOYER = "employer";
    private static final String INTERESTS = "interests";
    private static final String LANGUAGES = "languages";
    private static final String EMAIL = "email";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String NATIONALITY = "nationality";
    private static final String USER_CREDENTIALS = "userCredentials";
    private static final String ORGANISATION_UNITS = "organisationUnits";
    private static final String TEI_SEARCH_ORGANISATION_UNITS = "teiSearchOrganisationUnits";
    private static final String DATA_VIEW_ORGANISATION_UNITS = "dataViewOrganisationUnits";
    private static final String DELETED = "deleted";

    public static final Field<User, String> uid
            = Field.create(UID);
    public static final Field<User, String> code
            = Field.create(CODE);
    public static final Field<User, String> name
            = Field.create(NAME);
    public static final Field<User, String> displayName
            = Field.create(DISPLAY_NAME);
    public static final Field<User, String> created
            = Field.create(CREATED);
    public static final Field<User, String> lastUpdated
            = Field.create(LAST_UPDATED);
    public static final Field<User, String> birthday
            = Field.create(BIRTHDAY);
    public static final Field<User, String> education
            = Field.create(EDUCATION);
    public static final Field<User, String> gender
            = Field.create(GENDER);
    public static final Field<User, String> jobTitle
            = Field.create(JOB_TITLE);
    public static final Field<User, String> surname
            = Field.create(SURNAME);
    public static final Field<User, String> firstName
            = Field.create(FIRST_NAME);
    public static final Field<User, String> introduction
            = Field.create(INTRODUCTION);
    public static final Field<User, String> employer
            = Field.create(EMPLOYER);
    public static final Field<User, String> interests
            = Field.create(INTERESTS);
    public static final Field<User, String> languages
            = Field.create(LANGUAGES);
    public static final Field<User, String> email
            = Field.create(EMAIL);
    public static final Field<User, String> phoneNumber
            = Field.create(PHONE_NUMBER);
    public static final Field<User, String> nationality
            = Field.create(NATIONALITY);
    public static final Field<User, Boolean> deleted
            = Field.create(DELETED);
    public static final NestedField<User, UserCredentials> userCredentials
            = NestedField.create(USER_CREDENTIALS);
    public static final NestedField<User, OrganisationUnit> organisationUnits
            = NestedField.create(ORGANISATION_UNITS);
    public static final NestedField<User, OrganisationUnit> teiSearchOrganisationUnits
            = NestedField.create(TEI_SEARCH_ORGANISATION_UNITS);
    public static final NestedField<User, OrganisationUnit> dataViewOrganisationUnits
            = NestedField.create(DATA_VIEW_ORGANISATION_UNITS);

    @Nullable
    @JsonProperty(BIRTHDAY)
    public abstract String birthday();

    @Nullable
    @JsonProperty(EDUCATION)
    public abstract String education();

    @Nullable
    @JsonProperty(GENDER)
    public abstract String gender();

    @Nullable
    @JsonProperty(JOB_TITLE)
    public abstract String jobTitle();

    @Nullable
    @JsonProperty(SURNAME)
    public abstract String surname();

    @Nullable
    @JsonProperty(FIRST_NAME)
    public abstract String firstName();

    @Nullable
    @JsonProperty(INTRODUCTION)
    public abstract String introduction();

    @Nullable
    @JsonProperty(EMPLOYER)
    public abstract String employer();

    @Nullable
    @JsonProperty(INTERESTS)
    public abstract String interests();

    @Nullable
    @JsonProperty(LANGUAGES)
    public abstract String languages();

    @Nullable
    @JsonProperty(EMAIL)
    public abstract String email();

    @Nullable
    @JsonProperty(PHONE_NUMBER)
    public abstract String phoneNumber();

    @Nullable
    @JsonProperty(NATIONALITY)
    public abstract String nationality();

    @JsonProperty(USER_CREDENTIALS)
    public abstract UserCredentials userCredentials();

    @Nullable
    @JsonProperty(ORGANISATION_UNITS)
    public abstract List<OrganisationUnit> organisationUnits();

    @Nullable
    @JsonProperty(TEI_SEARCH_ORGANISATION_UNITS)
    public abstract List<OrganisationUnit> teiSearchOrganisationUnits();

    @Nullable
    @JsonProperty(DATA_VIEW_ORGANISATION_UNITS)
    public abstract List<OrganisationUnit> dataViewOrganisationUnits();

    @JsonCreator
    public static User create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(BIRTHDAY) String birthday,
            @JsonProperty(EDUCATION) String education,
            @JsonProperty(GENDER) String gender,
            @JsonProperty(JOB_TITLE) String jobTitle,
            @JsonProperty(SURNAME) String surname,
            @JsonProperty(FIRST_NAME) String firstName,
            @JsonProperty(INTRODUCTION) String introduction,
            @JsonProperty(EMPLOYER) String employer,
            @JsonProperty(INTERESTS) String interests,
            @JsonProperty(LANGUAGES) String languages,
            @JsonProperty(EMAIL) String email,
            @JsonProperty(PHONE_NUMBER) String phoneNumber,
            @JsonProperty(NATIONALITY) String nationality,
            @JsonProperty(USER_CREDENTIALS) UserCredentials userCredentials,
            @JsonProperty(ORGANISATION_UNITS) List<OrganisationUnit> orgUnits,
            @JsonProperty(TEI_SEARCH_ORGANISATION_UNITS) List<OrganisationUnit> searchOrgUnits,
            @JsonProperty(DATA_VIEW_ORGANISATION_UNITS) List<OrganisationUnit> dataViewOrgUnits,
            @JsonProperty(DELETED) Boolean deleted) {
        // ToDo: change from jackson to gson and implement autovalue-gson extension

        return new AutoValue_User(
                uid, code, name, displayName, created, lastUpdated, deleted, birthday, education, gender,
                jobTitle, surname, firstName, introduction, employer, interests, languages, email,
                phoneNumber, nationality, userCredentials,
                safeUnmodifiableList(orgUnits),
                safeUnmodifiableList(searchOrgUnits),
                safeUnmodifiableList(dataViewOrgUnits)
        );
    }

}
