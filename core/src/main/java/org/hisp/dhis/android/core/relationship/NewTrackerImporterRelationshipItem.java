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

package org.hisp.dhis.android.core.relationship;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.RelationshipConstraintTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreNewRelationshipItemEnrollmentAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreNewRelationshipItemEventAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreNewRelationshipItemTrackedEntityAdapter;
import org.hisp.dhis.android.core.common.CoreObject;

@AutoValue
@JsonDeserialize(builder = AutoValue_NewTrackerImporterRelationshipItem.Builder.class)
public abstract class NewTrackerImporterRelationshipItem implements CoreObject {

    @Nullable
    @JsonIgnore()
    public abstract String relationship();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(RelationshipConstraintTypeColumnAdapter.class)
    public abstract RelationshipConstraintType relationshipItemType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreNewRelationshipItemTrackedEntityAdapter.class)
    public abstract NewTrackerImporterRelationshipItemTrackedEntity trackedEntity();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreNewRelationshipItemEnrollmentAdapter.class)
    public abstract NewTrackerImporterRelationshipItemEnrollment enrollment();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreNewRelationshipItemEventAdapter.class)
    public abstract NewTrackerImporterRelationshipItemEvent event();

    public static Builder builder() {
        return new $$AutoValue_NewTrackerImporterRelationshipItem.Builder();
    }

    public static NewTrackerImporterRelationshipItem create(Cursor cursor) {
        return $AutoValue_NewTrackerImporterRelationshipItem.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {

        public abstract Builder id(Long id);

        public abstract Builder relationship(String relationship);

        public abstract Builder relationshipItemType(RelationshipConstraintType relationshipItemType);

        public abstract Builder trackedEntity(NewTrackerImporterRelationshipItemTrackedEntity trackedEntity);

        public abstract Builder enrollment(NewTrackerImporterRelationshipItemEnrollment enrollment);

        public abstract Builder event(NewTrackerImporterRelationshipItemEvent event);

        public abstract NewTrackerImporterRelationshipItem build();
    }
}