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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitFields;

public final class UserFields {
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
    // private static final String DATA_VIEW_ORGANISATION_UNITS = "dataViewOrganisationUnits";

    private static final Field<User, String> uid = Field.create(BaseIdentifiableObject.UID);
    private static final Field<User, String> code = Field.create(BaseIdentifiableObject.CODE);
    private static final Field<User, String> name = Field.create(BaseIdentifiableObject.NAME);
    private static final Field<User, String> displayName = Field.create(BaseIdentifiableObject.DISPLAY_NAME);
    private static final Field<User, String> created = Field.create(BaseIdentifiableObject.CREATED);
    private static final Field<User, String> lastUpdated = Field.create(BaseIdentifiableObject.LAST_UPDATED);
    private static final Field<User, String> birthday = Field.create(BIRTHDAY);
    private static final Field<User, String> education = Field.create(EDUCATION);
    private static final Field<User, String> gender = Field.create(GENDER);
    private static final Field<User, String> jobTitle = Field.create(JOB_TITLE);
    private static final Field<User, String> surname = Field.create(SURNAME);
    private static final Field<User, String> firstName = Field.create(FIRST_NAME);
    private static final Field<User, String> introduction = Field.create(INTRODUCTION);
    private static final Field<User, String> employer = Field.create(EMPLOYER);
    private static final Field<User, String> interests = Field.create(INTERESTS);
    private static final Field<User, String> languages = Field.create(LANGUAGES);
    private static final Field<User, String> email = Field.create(EMAIL);
    private static final Field<User, String> phoneNumber = Field.create(PHONE_NUMBER);
    private static final Field<User, String> nationality = Field.create(NATIONALITY);
    private static final Field<User, Boolean> deleted = Field.create(BaseIdentifiableObject.DELETED);
    
    private static final NestedField<User, UserCredentials> userCredentials
            = NestedField.create(USER_CREDENTIALS);
    private static final NestedField<User, OrganisationUnit> organisationUnits
            = NestedField.create(ORGANISATION_UNITS);
    private static final NestedField<User, OrganisationUnit> teiSearchOrganisationUnits
            = NestedField.create(TEI_SEARCH_ORGANISATION_UNITS);

    static final Fields<User> allFieldsWithoutOrgUnit = Fields.<User>builder().fields(
            uid, code, name, displayName, created, lastUpdated, birthday, education, gender, jobTitle,
            surname, firstName, introduction, employer, interests, languages, email, phoneNumber, nationality,
            deleted,
            userCredentials.with(UserCredentialsFields.allFields)
    ).build();

    static final Fields<User> allFieldsWithOrgUnit = Fields.<User>builder().fields(
            uid, code, name, displayName, created, lastUpdated, birthday, education, gender, jobTitle,
            surname, firstName, introduction, employer, interests, languages, email, phoneNumber, nationality,
            deleted,
            userCredentials.with(UserCredentialsFields.allFields),
            organisationUnits.with(OrganisationUnitFields.fieldsInUserCall),
            teiSearchOrganisationUnits.with(OrganisationUnitFields.teiSearchFieldsInUserCall)
    ).build();

    private UserFields() {}
}
