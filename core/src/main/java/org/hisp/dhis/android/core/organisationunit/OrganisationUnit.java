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

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreDataSetListAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreOrganisationUnitGroupListAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreOrganisationUnitListAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreProgramListAdapter;
import org.hisp.dhis.android.core.data.database.OrganisationUnitWithUidColumnAdapter;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.program.Program;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_OrganisationUnit.Builder.class)
public abstract class OrganisationUnit extends BaseNameableObject implements Model {

    public enum Scope {
        SCOPE_DATA_CAPTURE,
        SCOPE_TEI_SEARCH
    }

    @Nullable
    @JsonProperty()
    @ColumnAdapter(OrganisationUnitWithUidColumnAdapter.class)
    public abstract OrganisationUnit parent();

    @Nullable
    @JsonProperty()
    public abstract String path();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date openingDate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date closedDate();

    @Nullable
    @JsonProperty()
    public abstract Integer level();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreProgramListAdapter.class)
    public abstract List<Program> programs();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreDataSetListAdapter.class)
    public abstract List<DataSet> dataSets();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreOrganisationUnitListAdapter.class)
    public abstract List<OrganisationUnit> ancestors();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreOrganisationUnitGroupListAdapter.class)
    public abstract List<OrganisationUnitGroup> organisationUnitGroups();

    @Nullable
    @JsonIgnore()
    public abstract String displayNamePath();

    @NonNull
    public static OrganisationUnit create(Cursor cursor) {
        return AutoValue_OrganisationUnit.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_OrganisationUnit.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseNameableObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder parent(OrganisationUnit parent);

        public abstract Builder path(String path);

        public abstract Builder openingDate(Date openingDate);

        public abstract Builder closedDate(Date closedDate);

        public abstract Builder level(Integer level);

        public abstract Builder programs(List<Program> programs);

        public abstract Builder dataSets(List<DataSet> dataSets);

        public abstract Builder ancestors(List<OrganisationUnit> ancestors);

        public abstract Builder organisationUnitGroups(List<OrganisationUnitGroup> organisationUnitGroups);

        public abstract Builder displayNamePath(String displayNamePath);

        public abstract OrganisationUnit build();
    }
}
