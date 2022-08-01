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

package org.hisp.dhis.android.core.organisationunit;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbGeometryColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringArrayColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreFeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreObjectWithUidListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreOrganisationUnitGroupListAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreOrganisationUnitListAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreStringColumnAdapter;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.ObjectWithUid;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_OrganisationUnit.Builder.class)
public abstract class OrganisationUnit extends BaseNameableObject implements CoreObject {

    public enum Scope {
        SCOPE_DATA_CAPTURE,
        SCOPE_TEI_SEARCH
    }

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid parent();

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
    @ColumnAdapter(IgnoreStringColumnAdapter.class)
    abstract String coordinates();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreFeatureTypeColumnAdapter.class)
    abstract FeatureType featureType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbGeometryColumnAdapter.class)
    public abstract Geometry geometry();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> programs();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> dataSets();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreOrganisationUnitListAdapter.class)
    abstract List<OrganisationUnit> ancestors();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreOrganisationUnitGroupListAdapter.class)
    public abstract List<OrganisationUnitGroup> organisationUnitGroups();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(StringArrayColumnAdapter.class)
    public abstract List<String> displayNamePath();

    @NonNull
    public static OrganisationUnit create(Cursor cursor) {
        return AutoValue_OrganisationUnit.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $$AutoValue_OrganisationUnit.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseNameableObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder parent(ObjectWithUid parent);

        public abstract Builder path(String path);

        public abstract Builder openingDate(Date openingDate);

        public Builder openingDate(@NonNull String openingDateStr) throws ParseException {
            return openingDate(BaseIdentifiableObject.DATE_FORMAT.parse(openingDateStr));
        }

        public abstract Builder closedDate(Date closedDate);

        public Builder closedDate(@NonNull String closedDateStr) throws ParseException {
            return closedDate(BaseIdentifiableObject.DATE_FORMAT.parse(closedDateStr));
        }

        public abstract Builder level(Integer level);

        abstract Builder coordinates(String coordinates);

        abstract Builder featureType(FeatureType featureType);

        public abstract Builder geometry(Geometry geometry);

        public abstract Builder programs(List<ObjectWithUid> programs);

        public abstract Builder dataSets(List<ObjectWithUid> dataSets);

        abstract Builder ancestors(List<OrganisationUnit> ancestors);

        public abstract Builder organisationUnitGroups(List<OrganisationUnitGroup> organisationUnitGroups);

        public abstract Builder displayNamePath(List<String> displayNamePath);

        abstract OrganisationUnit autoBuild();

        // Auxiliary fields to access values
        abstract FeatureType featureType();
        abstract String coordinates();
        abstract Geometry geometry();

        public OrganisationUnit build() {
            if (geometry() == null && coordinates() != null && featureType() != null) {
                geometry(Geometry.builder()
                        .type(featureType())
                        .coordinates(coordinates())
                        .build());
            }
            return autoBuild();
        }
    }
}
