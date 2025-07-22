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

package org.hisp.dhis.android.core.enrollment;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbGeometryColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.EnrollmentStatusColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.StateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreCoordinatesColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreEventListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreNoteListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreRelationshipListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreStateColumnAdapter;
import org.hisp.dhis.android.core.arch.helpers.CoordinateHelper;
import org.hisp.dhis.android.core.common.BaseDeletableDataObject;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.relationship.Relationship;

import java.util.Date;
import java.util.List;

@AutoValue
@SuppressWarnings("PMD.ExcessiveImports")
public abstract class Enrollment extends BaseDeletableDataObject implements ObjectWithUidInterface {

    @Override
    public abstract String uid();

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
    public abstract String organisationUnit();

    @Nullable
    public abstract String program();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date enrollmentDate();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date incidentDate();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date completedDate();

    @Nullable
    @ColumnName(EnrollmentTableInfo.Columns.FOLLOW_UP)
    public abstract Boolean followUp();

    @Nullable
    @ColumnAdapter(EnrollmentStatusColumnAdapter.class)
    public abstract EnrollmentStatus status();

    @Nullable
    public abstract String trackedEntityInstance();

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
    @ColumnAdapter(IgnoreEventListColumnAdapter.class)
    abstract List<Event> events();

    @Nullable
    @ColumnAdapter(IgnoreNoteListColumnAdapter.class)
    public abstract List<Note> notes();

    @Nullable
    @ColumnAdapter(IgnoreRelationshipListColumnAdapter.class)
    abstract List<Relationship> relationships();

    @Nullable
    @ColumnName(DataColumns.AGGREGATED_SYNC_STATE)
    @ColumnAdapter(StateColumnAdapter.class)
    public abstract State aggregatedSyncState();

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
        return new $$AutoValue_Enrollment.Builder();
    }

    public static Enrollment create(Cursor cursor) {
        return $AutoValue_Enrollment.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder implements BaseDeletableDataObject.Builder<Builder> {
        public abstract Builder uid(String uid);

        public abstract Builder created(Date created);

        public abstract Builder lastUpdated(Date lastUpdated);

        public abstract Builder createdAtClient(Date createdAtClient);

        public abstract Builder lastUpdatedAtClient(Date lastUpdatedAtClient);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder program(String program);

        public abstract Builder enrollmentDate(Date enrollmentDate);

        public abstract Builder incidentDate(Date incidentDate);

        public abstract Builder completedDate(Date completedDate);

        public abstract Builder followUp(Boolean followUp);

        public abstract Builder status(EnrollmentStatus status);

        public abstract Builder trackedEntityInstance(String trackedEntityInstance);

        /**
         * @deprecated since 2.30, replaced by {@link #geometry(Geometry geometry)}
         */
        abstract Builder coordinate(Coordinates coordinate);

        public abstract Builder geometry(Geometry geometry);

        abstract Builder events(List<Event> events);

        public abstract Builder notes(List<Note> notes);

        public abstract Builder relationships(List<Relationship> relationships);

        public abstract Builder aggregatedSyncState(State aggregatedSyncState);

        /**
         * @deprecated Use {@link #aggregatedSyncState(State)} and {@link #syncState(State)} instead.
         */
        @Deprecated
        public Builder state(State state) {
            return aggregatedSyncState(state).syncState(state);
        }

        abstract Enrollment autoBuild();

        // Auxiliary fields to access values
        abstract Coordinates coordinate();
        abstract Geometry geometry();
        public Enrollment build() {
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