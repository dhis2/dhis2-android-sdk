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

package org.hisp.dhis.android.core.event;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbGeometryColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.EventStatusColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.StateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreCoordinatesColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreNoteListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreRelationshipListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreStateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreStringColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreTrackedEntityDataValueListColumnAdapter;
import org.hisp.dhis.android.core.arch.helpers.CoordinateHelper;
import org.hisp.dhis.android.core.common.BaseDeletableDataObject;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.Date;
import java.util.List;

@AutoValue
@SuppressWarnings({"PMD.GodClass", "PMD.ExcessivePublicCount", "PMD.ExcessiveImports"})
public abstract class Event extends BaseDeletableDataObject implements ObjectWithUidInterface {

    @Override
    public abstract String uid();

    @Nullable
    public abstract String enrollment();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date createdAtClient();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdatedAtClient();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract String programStage();

    @Nullable
    public abstract String organisationUnit();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date eventDate();

    @Nullable
    @ColumnAdapter(EventStatusColumnAdapter.class)
    public abstract EventStatus status();

    /**
     * @deprecated since 2.30, replaced by {@link #geometry()}
     */
    @Nullable
    @Deprecated
    @ColumnAdapter(IgnoreCoordinatesColumnAdapter.class)
    abstract Coordinates coordinate();

    @Nullable
    @ColumnAdapter(DbGeometryColumnAdapter.class)
    public abstract Geometry geometry();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date completedDate();

    @Nullable
    public abstract String completedBy();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date dueDate();

    @Nullable
    public abstract String attributeOptionCombo();

    @Nullable
    public abstract String assignedUser();

    @Nullable
    @ColumnAdapter(IgnoreNoteListColumnAdapter.class)
    public abstract List<Note> notes();

    @Nullable
    @ColumnAdapter(IgnoreTrackedEntityDataValueListColumnAdapter.class)
    public abstract List<TrackedEntityDataValue> trackedEntityDataValues();

    @Nullable
    @ColumnAdapter(IgnoreRelationshipListColumnAdapter.class)
    abstract List<Relationship> relationships();

    @Nullable
    @ColumnName(DataColumns.AGGREGATED_SYNC_STATE)
    @ColumnAdapter(StateColumnAdapter.class)
    public abstract State aggregatedSyncState();

    @Nullable
    @ColumnAdapter(IgnoreStringColumnAdapter.class)
    abstract String trackedEntityInstance();

    /**
     * @deprecated Use {@link #aggregatedSyncState()} instead.
     */
    @Deprecated
    @Nullable
    @ColumnAdapter(IgnoreStateColumnAdapter.class)
    public State state() {
        return aggregatedSyncState();
    }

    public static Builder builder() {
        return new $$AutoValue_Event.Builder();
    }

    public static Event create(Cursor cursor) {
        return $AutoValue_Event.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseDeletableDataObject.Builder<Builder> {
        public abstract Builder uid(String uid);

        public abstract Builder enrollment(String enrollment);

        public abstract Builder created(Date created);

        public abstract Builder lastUpdated(Date lastUpdated);

        public abstract Builder createdAtClient(Date createdAtClient);

        public abstract Builder lastUpdatedAtClient(Date lastUpdatedAtClient);

        public abstract Builder program(String program);

        public abstract Builder programStage(String programStage);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder eventDate(Date eventDate);

        public abstract Builder status(EventStatus status);

        /**
         * @deprecated since 2.29, replaced by {@link #geometry(Geometry geometry)}
         */
        abstract Builder coordinate(Coordinates coordinate);

        public abstract Builder geometry(Geometry geometry);

        public abstract Builder completedDate(Date completedDate);

        public abstract Builder completedBy(String completedBy);

        public abstract Builder dueDate(Date dueDate);

        public abstract Builder attributeOptionCombo(String attributeOptionCombo);

        public abstract Builder assignedUser(String assignedUser);

        public abstract Builder notes(List<Note> notes);

        public abstract Builder trackedEntityDataValues(List<TrackedEntityDataValue> trackedEntityDataValues);

        public abstract Builder relationships(List<Relationship> relationships);

        public abstract Builder aggregatedSyncState(State aggregatedSyncState);

        abstract Builder trackedEntityInstance(String trackedEntityInstance);

        /**
         * @deprecated Use {@link #aggregatedSyncState(State)} and {@link #syncState(State)} instead.
         */
        @Deprecated
        public Builder state(State state) {
            return aggregatedSyncState(state).syncState(state);
        }

        abstract Event autoBuild();

        // Auxiliary fields to access values
        abstract Coordinates coordinate();
        abstract Geometry geometry();
        public Event build() {
            if (geometry() == null) {
                if (coordinate() != null) {
                    geometry(CoordinateHelper.getGeometryFromCoordinates(coordinate()));
                }
            } else {
                coordinate(CoordinateHelper.getCoordinatesFromGeometry(geometry()));
            }
            return autoBuild();
        }
    }
}