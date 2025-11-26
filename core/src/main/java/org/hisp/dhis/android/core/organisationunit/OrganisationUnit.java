/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.ObjectWithUid;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@AutoValue
public abstract class OrganisationUnit extends BaseNameableObject implements CoreObject {

    public enum Scope {
        SCOPE_DATA_CAPTURE,
        SCOPE_TEI_SEARCH
    }

    @Nullable
    public abstract ObjectWithUid parent();

    @Nullable
    public abstract String path();

    @Nullable
    public abstract Date openingDate();

    @Nullable
    public abstract Date closedDate();

    @Nullable
    public abstract Integer level();

    @Nullable
    public abstract Geometry geometry();

    @Nullable
    public abstract List<ObjectWithUid> programs();

    @Nullable
    public abstract List<ObjectWithUid> dataSets();

    @Nullable
    public abstract List<OrganisationUnitGroup> organisationUnitGroups();

    @Nullable
    public abstract List<String> displayNamePath();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_OrganisationUnit.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseNameableObject.Builder<Builder> {
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

        public abstract Builder geometry(Geometry geometry);

        public abstract Builder programs(List<ObjectWithUid> programs);

        public abstract Builder dataSets(List<ObjectWithUid> dataSets);

        public abstract Builder organisationUnitGroups(List<OrganisationUnitGroup> organisationUnitGroups);

        public abstract Builder displayNamePath(List<String> displayNamePath);

        public abstract OrganisationUnit build();
    }
}
