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

package org.hisp.dhis.android.core.user;

import android.database.Cursor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreOrganisationUnitListAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreUserCredentialsAdapter;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_User.Builder.class)
public abstract class User extends BaseIdentifiableObject implements CoreObject {

    @Nullable
    public abstract String birthday();

    @Nullable
    public abstract String education();

    @Nullable
    public abstract String gender();

    @Nullable
    public abstract String jobTitle();

    @Nullable
    public abstract String surname();

    @Nullable
    public abstract String firstName();

    @Nullable
    public abstract String introduction();

    @Nullable
    public abstract String employer();

    @Nullable
    public abstract String interests();

    @Nullable
    public abstract String languages();

    @Nullable
    public abstract String email();

    @Nullable
    public abstract String phoneNumber();

    @Nullable
    public abstract String nationality();

    @Nullable
    @ColumnAdapter(IgnoreUserCredentialsAdapter.class)
    abstract UserCredentials userCredentials();

    @Nullable
    @ColumnAdapter(IgnoreOrganisationUnitListAdapter.class)
    abstract List<OrganisationUnit> organisationUnits();

    @Nullable
    @ColumnAdapter(IgnoreOrganisationUnitListAdapter.class)
    abstract List<OrganisationUnit> teiSearchOrganisationUnits();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $$AutoValue_User.Builder();
    }

    @NonNull
    public static User create(Cursor cursor) {
        return AutoValue_User.createFromCursor(cursor);
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        public abstract Builder id(Long id);

        public abstract Builder birthday(String birthday);

        public abstract Builder education(String education);

        public abstract Builder gender(String gender);

        public abstract Builder jobTitle(String jobTitle);

        public abstract Builder surname(String surname);

        public abstract Builder firstName(String firstName);

        public abstract Builder introduction(String introduction);

        public abstract Builder employer(String employer);

        public abstract Builder interests(String interests);

        public abstract Builder languages(String languages);

        public abstract Builder email(String email);

        public abstract Builder phoneNumber(String phoneNumber);

        public abstract Builder nationality(String nationality);

        public abstract Builder userCredentials(UserCredentials userCredentials);

        public abstract Builder organisationUnits(List<OrganisationUnit> organisationUnits);

        public abstract Builder teiSearchOrganisationUnits(List<OrganisationUnit> teiSearchOrganisationUnits);

        public abstract User build();
    }
}
