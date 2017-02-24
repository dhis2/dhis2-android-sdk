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

import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class UserCredentials extends BaseIdentifiableObject {
    private static final String USER_ROLES = "userRoles";
    private static final String USERNAME = "username";
    private static final String DELETED = "deleted";

    public static final Field<UserCredentials, String> uid = Field.create(UID);
    public static final Field<UserCredentials, String> code = Field.create(CODE);
    public static final Field<UserCredentials, String> name = Field.create(NAME);
    public static final Field<UserCredentials, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<UserCredentials, String> created = Field.create(CREATED);
    public static final Field<UserCredentials, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<UserCredentials, String> username = Field.create(USERNAME);
    public static final Field<UserCredentials, Boolean> deleted = Field.create(DELETED);
    public static final NestedField<UserCredentials, UserRole> userRoles = NestedField.create(USER_ROLES);

    @Nullable
    @JsonProperty(USERNAME)
    public abstract String username();

    @Nullable
    @JsonProperty(USER_ROLES)
    public abstract List<UserRole> userRoles();


    @JsonCreator
    public static UserCredentials create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(USERNAME) String username,
            @JsonProperty(USER_ROLES) List<UserRole> userRoles,
            @JsonProperty(DELETED) Boolean deleted) {
        return new AutoValue_UserCredentials(uid, code, name, displayName, created, lastUpdated, deleted, username,
                safeUnmodifiableList(userRoles)
        );
    }
}
