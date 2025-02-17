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

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface;
import org.hisp.dhis.android.core.common.ObjectWithSyncStateInterface;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.NewTrackerImporterEvent;
import org.hisp.dhis.android.core.note.NewTrackerImporterNote;
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationship;
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityAttributeValue;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class NewTrackerImporterEnrollment implements ObjectWithUidInterface,
        ObjectWithSyncStateInterface, ObjectWithDeleteInterface {

    @Override
    public abstract String uid();

    @Nullable
    @Override
    public abstract Boolean deleted();

    @Nullable
    @Override
    public abstract State syncState();

    @Nullable
    public abstract Date createdAt();

    @Nullable
    public abstract Date updatedAt();

    @Nullable
    public abstract Date createdAtClient();

    @Nullable
    public abstract Date updatedAtClient();

    @Nullable
    public abstract String organisationUnit();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract Date enrolledAt();

    @Nullable
    public abstract Date occurredAt();

    @Nullable
    public abstract Date completedAt();

    @Nullable
    public abstract Boolean followUp();

    @Nullable
    public abstract EnrollmentStatus status();

    @Nullable
    public abstract String trackedEntity();

    @Nullable
    public abstract Geometry geometry();

    @Nullable
    public abstract State aggregatedSyncState();

    @Nullable
    public abstract List<NewTrackerImporterTrackedEntityAttributeValue> attributes();

    @Nullable
    public abstract List<NewTrackerImporterEvent> events();

    @Nullable
    public abstract List<NewTrackerImporterNote> notes();

    @Nullable
    abstract List<NewTrackerImporterRelationship> relationships();

    public static Builder builder() {
        return new AutoValue_NewTrackerImporterEnrollment.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder deleted(Boolean deleted);

        public abstract Builder syncState(State syncState);

        public abstract Builder createdAt(Date createdAt);

        public abstract Builder updatedAt(Date lastUpdatedAt);

        public abstract Builder createdAtClient(Date createdAtClient);

        public abstract Builder updatedAtClient(Date updatedAtClient);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder program(String program);

        public abstract Builder enrolledAt(Date enrolledAt);

        public abstract Builder occurredAt(Date occurredAt);

        public abstract Builder completedAt(Date completedAt);

        public abstract Builder followUp(Boolean followUp);

        public abstract Builder status(EnrollmentStatus status);

        public abstract Builder trackedEntity(String trackedEntityInstance);

        public abstract Builder geometry(Geometry geometry);

        public abstract Builder aggregatedSyncState(State aggregatedSyncState);

        public abstract Builder attributes(
                List<NewTrackerImporterTrackedEntityAttributeValue> trackedEntityAttributeValues);

        public abstract Builder events(List<NewTrackerImporterEvent> events);

        public abstract Builder notes(List<NewTrackerImporterNote> notes);

        public abstract Builder relationships(List<NewTrackerImporterRelationship> relationships);

        public abstract NewTrackerImporterEnrollment build();
    }
}
