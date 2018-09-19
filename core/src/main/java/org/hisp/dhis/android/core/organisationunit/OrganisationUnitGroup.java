/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.organisationunit;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;

import java.util.Date;

@AutoValue
public abstract class OrganisationUnitGroup extends BaseIdentifiableObject {

    public static final String SHORT_NAME = "shortName";
    public static final String DISPLAY_SHORT_NAME = "displayShortName";

    private static final Field<OrganisationUnitGroup, String> uid = Field.create(UID);
    private static final Field<OrganisationUnitGroup, String> code = Field.create(CODE);
    private static final Field<OrganisationUnitGroup, String> name = Field.create(NAME);
    private static final Field<OrganisationUnitGroup, String> displayName = Field.create(DISPLAY_NAME);
    private static final Field<OrganisationUnitGroup, String> created = Field.create(CREATED);
    private static final Field<OrganisationUnitGroup, String> lastUpdated = Field.create(LAST_UPDATED);
    private static final Field<OrganisationUnitGroup, Boolean> deleted = Field.create(DELETED);

    private static final Field<OrganisationUnitGroup, String> shortName = Field.create(SHORT_NAME);
    private static final Field<OrganisationUnitGroup, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);


    static final Fields<OrganisationUnitGroup> allFields
            = Fields.<OrganisationUnitGroup>builder().fields(
            uid, code, name, displayName,
            created, lastUpdated, deleted,
            shortName, displayShortName
    ).build();

    @Nullable
    @JsonProperty(SHORT_NAME)
    public abstract String shortName();

    @Nullable
    @JsonProperty(DISPLAY_SHORT_NAME)
    public abstract String displayShortName();

    @JsonCreator
    public static OrganisationUnitGroup create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,

            @JsonProperty(SHORT_NAME) String shortName,
            @JsonProperty(DISPLAY_SHORT_NAME) String displayShortName,
            @JsonProperty(DELETED) Boolean deleted) {

        return new AutoValue_OrganisationUnitGroup(
                uid, code, name, displayName,
                created, lastUpdated, deleted,
                shortName, displayShortName
        );
    }

}