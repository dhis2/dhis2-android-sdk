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

package org.hisp.dhis.android.core.event;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbGeometryColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.NewTrackerImporterUserInfoColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.EventStatusColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.StateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreNewTackerImporterTrackedEntityDataValueListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreNewTrackerImporterNoteListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreRelationshipListColumnAdapter;
import org.hisp.dhis.android.core.common.BaseDeletableDataObject;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.internal.EventFields;
import org.hisp.dhis.android.core.note.NewTrackerImporterNote;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterUserInfo;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_NewTrackerImporterEvent.Builder.class)
@SuppressWarnings({"PMD.GodClass", "PMD.ExcessivePublicCount"})
public abstract class NewTrackerImporterEvent extends BaseDeletableDataObject implements ObjectWithUidInterface {

    @Override
    @JsonProperty(EventFields.UID)
    public abstract String uid();

    @Nullable
    @JsonProperty()
    public abstract String enrollment();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date createdAt();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date updatedAt();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date createdAtClient();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date updatedAtClient();

    @Nullable
    @JsonProperty()
    public abstract String program();

    @Nullable
    @JsonProperty()
    public abstract String programStage();

    @Nullable
    @JsonProperty(EventFields.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date occurredAt();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(EventStatusColumnAdapter.class)
    public abstract EventStatus status();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbGeometryColumnAdapter.class)
    public abstract Geometry geometry();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date completedAt();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date scheduledAt();

    @Nullable
    @JsonProperty()
    public abstract String attributeOptionCombo();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(NewTrackerImporterUserInfoColumnAdapter.class)
    public abstract NewTrackerImporterUserInfo assignedUser();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreNewTrackerImporterNoteListColumnAdapter.class)
    public abstract List<NewTrackerImporterNote> notes();

    @Nullable
    @JsonProperty(EventFields.TRACKED_ENTITY_DATA_VALUES)
    @ColumnAdapter(IgnoreNewTackerImporterTrackedEntityDataValueListColumnAdapter.class)
    public abstract List<NewTrackerImporterTrackedEntityDataValue> trackedEntityDataValues();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreRelationshipListColumnAdapter.class)
    abstract List<Relationship> relationships();

    @Nullable
    @ColumnName(DataColumns.AGGREGATED_SYNC_STATE)
    @ColumnAdapter(StateColumnAdapter.class)
    public abstract State aggregatedSyncState();

    public static Builder builder() {
        return new $$AutoValue_NewTrackerImporterEvent.Builder();
    }

    public static NewTrackerImporterEvent create(Cursor cursor) {
        return $AutoValue_NewTrackerImporterEvent.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseDeletableDataObject.Builder<Builder> {
        public abstract Builder id(Long id);

        @JsonProperty(EventFields.UID)
        public abstract Builder uid(String uid);

        public abstract Builder enrollment(String enrollment);

        public abstract Builder createdAt(Date createdAt);

        public abstract Builder updatedAt(Date updatedAt);

        public abstract Builder createdAtClient(Date createdAtClient);

        public abstract Builder updatedAtClient(Date updatedAtClient);

        public abstract Builder program(String program);

        public abstract Builder programStage(String programStage);

        @JsonProperty(EventFields.ORGANISATION_UNIT)
        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder occurredAt(Date occurredAt);

        public abstract Builder status(EventStatus status);

        public abstract Builder geometry(Geometry geometry);

        public abstract Builder completedAt(Date completedAt);

        public abstract Builder scheduledAt(Date scheduledAt);

        public abstract Builder attributeOptionCombo(String attributeOptionCombo);

        public abstract Builder assignedUser(NewTrackerImporterUserInfo assignedUser);

        public abstract Builder notes(List<NewTrackerImporterNote> notes);

        @JsonProperty(EventFields.TRACKED_ENTITY_DATA_VALUES)
        public abstract Builder trackedEntityDataValues(
                List<NewTrackerImporterTrackedEntityDataValue> trackedEntityDataValues);

        public abstract Builder relationships(List<Relationship> relationships);

        public abstract Builder aggregatedSyncState(State aggregatedSyncState);

        public abstract Geometry geometry();

        public abstract NewTrackerImporterEvent build();
    }
}