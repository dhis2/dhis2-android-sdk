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

package org.hisp.dhis.client.sdk.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_User.Builder.class)
public abstract class User extends BaseIdentifiableObject {
    public static final String GENDER_MALE = "gender_male";
    public static final String GENDER_FEMALE = "gender_female";
    public static final String GENDER_OTHER = "gender_other";

    private static final String JSON_PROPERTY_BIRTHDAY = "birthday";
    private static final String JSON_PROPERTY_EDUCATION = "education";
    private static final String JSON_PROPERTY_GENDER = "gender";
    private static final String JSON_PROPERTY_JOB_TITLE = "jobTitle";
    private static final String JSON_PROPERTY_SURNAME = "surname";
    private static final String JSON_PROPERTY_FIRST_NAME = "firstName";
    private static final String JSON_PROPERTY_INTRODUCTION = "introduction";
    private static final String JSON_PROPERTY_EMPLOYER = "employer";
    private static final String JSON_PROPERTY_INTERESTS = "interests";
    private static final String JSON_PROPERTY_LANGUAGES = "languages";
    private static final String JSON_PROPERTY_EMAIL = "email";
    private static final String JSON_PROPERTY_PHONE_NUMBER = "phoneNumber";
    private static final String JSON_PROPERTY_NATIONALITY = "nationality";
    private static final String JSON_PROPERTY_USER_CREDENTIALS = "userCredentials";
    private static final String JSON_PROPERTY_ORGANISATION_UNITS = "organisationUnits";
    private static final String JSON_PROPERTY_TEI_SEARCH_ORGANISATION_UNITS = "teiSearchOrganisationUnits";
    private static final String JSON_PROPERTY_DATA_VIEW_ORGANISATION_UNITS = "dataViewOrganisationUnits";

    @Nullable
    @JsonProperty(JSON_PROPERTY_BIRTHDAY)
    public abstract String birthday();

    @Nullable
    @JsonProperty(JSON_PROPERTY_EDUCATION)
    public abstract String education();

    @Nullable
    @JsonProperty(JSON_PROPERTY_GENDER)
    public abstract String gender();

    @Nullable
    @JsonProperty(JSON_PROPERTY_JOB_TITLE)
    public abstract String jobTitle();

    @Nullable
    @JsonProperty(JSON_PROPERTY_SURNAME)
    public abstract String surname();

    @Nullable
    @JsonProperty(JSON_PROPERTY_FIRST_NAME)
    public abstract String firstName();

    @Nullable
    @JsonProperty(JSON_PROPERTY_INTRODUCTION)
    public abstract String introduction();

    @Nullable
    @JsonProperty(JSON_PROPERTY_EMPLOYER)
    public abstract String employer();

    @Nullable
    @JsonProperty(JSON_PROPERTY_INTERESTS)
    public abstract String interests();

    @Nullable
    @JsonProperty(JSON_PROPERTY_LANGUAGES)
    public abstract String languages();

    @Nullable
    @JsonProperty(JSON_PROPERTY_EMAIL)
    public abstract String email();

    @Nullable
    @JsonProperty(JSON_PROPERTY_PHONE_NUMBER)
    public abstract String phoneNumber();

    @Nullable
    @JsonProperty(JSON_PROPERTY_NATIONALITY)
    public abstract String nationality();

    @JsonProperty(JSON_PROPERTY_USER_CREDENTIALS)
    public abstract UserCredentials userCredentials();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ORGANISATION_UNITS)
    public abstract List<OrganisationUnit> organisationUnits();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TEI_SEARCH_ORGANISATION_UNITS)
    public abstract List<OrganisationUnit> teiSearchOrganisationUnits();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DATA_VIEW_ORGANISATION_UNITS)
    public abstract List<OrganisationUnit> dataViewOrganisationUnits();

    public abstract Builder toBuilder();

    @Override
    public boolean isValid() {
        if (userCredentials() == null) {
            return false;
        }
        return super.isValid();
    }

    public static Builder builder() {
        return new AutoValue_User.Builder();
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_BIRTHDAY)
        public abstract Builder birthday(@Nullable String birthday);

        @JsonProperty(JSON_PROPERTY_EDUCATION)
        public abstract Builder education(@Nullable String education);

        @JsonProperty(JSON_PROPERTY_GENDER)
        public abstract Builder gender(@Nullable String gender);

        @JsonProperty(JSON_PROPERTY_JOB_TITLE)
        public abstract Builder jobTitle(@Nullable String jobTitle);

        @JsonProperty(JSON_PROPERTY_SURNAME)
        public abstract Builder surname(@Nullable String surName);

        @JsonProperty(JSON_PROPERTY_FIRST_NAME)
        public abstract Builder firstName(@Nullable String firstName);

        @JsonProperty(JSON_PROPERTY_INTRODUCTION)
        public abstract Builder introduction(@Nullable String introduction);

        @JsonProperty(JSON_PROPERTY_EMPLOYER)
        public abstract Builder employer(@Nullable String employer);

        @JsonProperty(JSON_PROPERTY_INTERESTS)
        public abstract Builder interests(@Nullable String interests);

        @JsonProperty(JSON_PROPERTY_LANGUAGES)
        public abstract Builder languages(@Nullable String languages);

        @JsonProperty(JSON_PROPERTY_EMAIL)
        public abstract Builder email(@Nullable String email);

        @JsonProperty(JSON_PROPERTY_PHONE_NUMBER)
        public abstract Builder phoneNumber(@Nullable String phoneNumber);

        @JsonProperty(JSON_PROPERTY_NATIONALITY)
        public abstract Builder nationality(@Nullable String nationality);

        @JsonProperty(JSON_PROPERTY_USER_CREDENTIALS)
        public abstract Builder userCredentials(UserCredentials userCredentials);

        @JsonProperty(JSON_PROPERTY_ORGANISATION_UNITS)
        public abstract Builder organisationUnits(@Nullable List<OrganisationUnit> organisationUnits);

        @JsonProperty(JSON_PROPERTY_TEI_SEARCH_ORGANISATION_UNITS)
        public abstract Builder teiSearchOrganisationUnits(@Nullable List<OrganisationUnit> organisationUnits);

        @JsonProperty(JSON_PROPERTY_DATA_VIEW_ORGANISATION_UNITS)
        public abstract Builder dataViewOrganisationUnits(@Nullable List<OrganisationUnit> organisationUnits);

        // internal, not exposed
        abstract List<OrganisationUnit> organisationUnits();

        abstract List<OrganisationUnit> teiSearchOrganisationUnits();

        abstract List<OrganisationUnit> dataViewOrganisationUnits();

        abstract UserCredentials userCredentials();

        abstract User autoBuild();

        public User build() {
            if (organisationUnits() != null) {
                organisationUnits(Collections.unmodifiableList(organisationUnits()));
            }

            if (teiSearchOrganisationUnits() != null) {
                teiSearchOrganisationUnits(Collections.unmodifiableList(teiSearchOrganisationUnits()));
            }

            if (dataViewOrganisationUnits() != null) {
                dataViewOrganisationUnits(Collections.unmodifiableList(dataViewOrganisationUnits()));
            }

            return autoBuild();
        }
    }
}
