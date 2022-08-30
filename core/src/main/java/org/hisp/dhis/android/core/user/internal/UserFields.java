/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.user.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.NestedField;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;

public final class UserFields {
    public static final String BIRTHDAY = "birthday";
    public static final String EDUCATION = "education";
    public static final String GENDER = "gender";
    public static final String JOB_TITLE = "jobTitle";
    public static final String SURNAME = "surname";
    public static final String FIRST_NAME = "firstName";
    public static final String INTRODUCTION = "introduction";
    public static final String EMPLOYER = "employer";
    public static final String INTERESTS = "interests";
    public static final String LANGUAGES = "languages";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String NATIONALITY = "nationality";
    public static final String USER_CREDENTIALS = "userCredentials";
    private static final String ORGANISATION_UNITS = "organisationUnits";
    private static final String TEI_SEARCH_ORGANISATION_UNITS = "teiSearchOrganisationUnits";

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
            teiSearchOrganisationUnits.with(OrganisationUnitFields.fieldsInUserCall)
    ).build();

    private UserFields() {}
}
