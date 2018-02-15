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

package org.hisp.dhis.android.core.organisationunit;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.program.Program;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_OrganisationUnit.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OrganisationUnit extends BaseNameableObject {
    private static final String PARENT = "parent";
    private static final String PATH = "path";
    private static final String OPENING_DATE = "openingDate";
    private static final String CLOSED_DATE = "closedDate";
    private static final String LEVEL = "level";
    private static final String PROGRAMS = "programs";

    public static final Field<OrganisationUnit, String> uid = Field.create(BaseIdentifiableObject.UID);
    public static final Field<OrganisationUnit, String> code = Field.create(BaseIdentifiableObject.CODE);
    public static final Field<OrganisationUnit, String> name = Field.create(BaseIdentifiableObject.NAME);
    public static final Field<OrganisationUnit, String> displayName = Field.create(BaseIdentifiableObject.DISPLAY_NAME);
    public static final Field<OrganisationUnit, String> created = Field.create(BaseIdentifiableObject.CREATED);
    public static final Field<OrganisationUnit, String> lastUpdated = Field.create(BaseIdentifiableObject.LAST_UPDATED);
    public static final Field<OrganisationUnit, String> shortName = Field.create(SHORT_NAME);
    public static final Field<OrganisationUnit, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);
    public static final Field<OrganisationUnit, String> description = Field.create(DESCRIPTION);
    public static final Field<OrganisationUnit, String> displayDescription = Field.create(DISPLAY_DESCRIPTION);
    public static final Field<OrganisationUnit, String> path = Field.create(PATH);
    public static final Field<OrganisationUnit, String> openingDate = Field.create(OPENING_DATE);
    public static final Field<OrganisationUnit, String> closedDate = Field.create(CLOSED_DATE);
    public static final Field<OrganisationUnit, String> level = Field.create(LEVEL);
    public static final Field<OrganisationUnit, Boolean> deleted = Field.create(DELETED);
    public static final NestedField<OrganisationUnit, OrganisationUnit> parent = NestedField.create(PARENT);
    public static final NestedField<OrganisationUnit, Program> programs = NestedField.create(PROGRAMS);

    @Nullable
    @JsonProperty(PARENT)
    public abstract OrganisationUnit parent();

    @Nullable
    @JsonProperty(PATH)
    public abstract String path();

    @Nullable
    @JsonProperty(OPENING_DATE)
    public abstract Date openingDate();

    @Nullable
    @JsonProperty(CLOSED_DATE)
    public abstract Date closedDate();

    @Nullable
    @JsonProperty(LEVEL)
    public abstract Integer level();

    @Nullable
    @JsonProperty(PROGRAMS)
    public abstract List<Program> programs();

    abstract OrganisationUnit.Builder toBuilder();

    static OrganisationUnit.Builder builder() {
        return new AutoValue_OrganisationUnit.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends
            BaseNameableObject.Builder<OrganisationUnit.Builder> {

        @JsonProperty(PARENT)
        public abstract OrganisationUnit.Builder parent(
                @Nullable OrganisationUnit organisationUnit);

        @JsonProperty(PATH)
        public abstract OrganisationUnit.Builder path(@Nullable String path);

        @JsonProperty(OPENING_DATE)
        public abstract OrganisationUnit.Builder openingDate(@Nullable Date openningDate);

        @JsonProperty(CLOSED_DATE)
        public abstract OrganisationUnit.Builder closedDate(@Nullable Date closedDate);

        @JsonProperty(LEVEL)
        public abstract OrganisationUnit.Builder level(@Nullable Integer level);

        @JsonProperty(PROGRAMS)
        public abstract OrganisationUnit.Builder programs(@Nullable List<Program> programs);

        public abstract OrganisationUnit build();
    }
}
