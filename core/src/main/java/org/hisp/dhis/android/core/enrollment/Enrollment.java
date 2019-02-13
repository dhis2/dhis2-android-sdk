/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.enrollment;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.data.database.CoordinatesColumnAdapter;
import org.hisp.dhis.android.core.data.database.DataDeleteColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbEnrollmentStatusColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreEventListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreNoteListColumnAdapter;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.event.Event;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_Enrollment.Builder.class)
public abstract class Enrollment extends BaseDataModel implements ObjectWithDeleteInterface, ObjectWithUidInterface {

    @Override
    @Nullable
    @JsonProperty(EnrollmentFields.UID)
    public abstract String uid();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    @JsonIgnore()
    public abstract String createdAtClient();

    @Nullable
    @JsonIgnore()
    public abstract String lastUpdatedAtClient();

    @Nullable
    @JsonProperty(EnrollmentFields.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty()
    public abstract String program();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date enrollmentDate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date incidentDate();

    @Nullable
    @JsonProperty(EnrollmentFields.FOLLOW_UP)
    @ColumnName(EnrollmentFields.FOLLOW_UP)
    public abstract Boolean followUp();

    @Nullable
    @ColumnAdapter(DbEnrollmentStatusColumnAdapter.class)
    public abstract EnrollmentStatus status();

    @Nullable
    @JsonIgnore()
    public abstract String trackedEntityInstance();

    @Nullable
    @ColumnAdapter(CoordinatesColumnAdapter.class)
    public abstract Coordinates coordinate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DataDeleteColumnAdapter.class)
    public abstract Boolean deleted();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreEventListColumnAdapter.class)
    public abstract List<Event> events();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreNoteListColumnAdapter.class)
    public abstract List<Note> notes();

    public static Builder builder() {
        return new $$AutoValue_Enrollment.Builder();
    }

    public static Enrollment create(Cursor cursor) {
        return $AutoValue_Enrollment.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseDataModel.Builder<Builder> {
        public abstract Builder id(Long id);

        @JsonProperty(EnrollmentFields.UID)
        public abstract Builder uid(String uid);

        public abstract Builder created(Date created);

        public abstract Builder lastUpdated(Date lastUpdated);

        public abstract Builder createdAtClient(String createdAtClient);

        public abstract Builder lastUpdatedAtClient(String lastUpdatedAtClient);

        @JsonProperty(EnrollmentFields.ORGANISATION_UNIT)
        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder program(String program);

        public abstract Builder enrollmentDate(Date enrollmentDate);

        public abstract Builder incidentDate(Date incidentDate);

        @JsonProperty(EnrollmentFields.FOLLOW_UP)
        public abstract Builder followUp(Boolean followUp);

        public abstract Builder status(EnrollmentStatus status);

        public abstract Builder trackedEntityInstance(String trackedEntityInstance);

        public abstract Builder coordinate(Coordinates coordinate);

        public abstract Builder deleted(Boolean deleted);

        public abstract Builder events(List<Event> events);

        public abstract Builder notes(List<Note> notes);

        public abstract Enrollment build();
    }
}