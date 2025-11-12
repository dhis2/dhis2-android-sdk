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

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseDeletableDataObject;
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
    public abstract Date created();

    @Nullable
    public abstract Date lastUpdated();

    @Nullable
    public abstract Date createdAtClient();

    @Nullable
    public abstract Date lastUpdatedAtClient();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract String programStage();

    @Nullable
    public abstract String organisationUnit();

    @Nullable
    public abstract Date eventDate();

    @Nullable
    public abstract EventStatus status();

    @Nullable
    public abstract Geometry geometry();

    @Nullable
    public abstract Date completedDate();

    @Nullable
    public abstract String completedBy();

    @Nullable
    public abstract Date dueDate();

    @Nullable
    public abstract String attributeOptionCombo();

    @Nullable
    public abstract String assignedUser();

    @Nullable
    public abstract List<Note> notes();

    @Nullable
    public abstract List<TrackedEntityDataValue> trackedEntityDataValues();

    @Nullable
    abstract List<Relationship> relationships();

    @Nullable
    public abstract State aggregatedSyncState();

    @Nullable
    abstract String trackedEntityInstance();

    /**
     * @deprecated Use {@link #aggregatedSyncState()} instead.
     */
    @Deprecated
    @Nullable
    public State state() {
        return aggregatedSyncState();
    }

    public static Builder builder() {
        return new AutoValue_Event.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder implements BaseDeletableDataObject.Builder<Builder> {
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

        public abstract Event build();
    }
}
