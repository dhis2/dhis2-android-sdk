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

package org.hisp.dhis.android.core.event;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbEventStatusColumnAdapter;

import java.util.Date;

@AutoValue
public abstract class EventModel extends BaseDataModel {

    public static final String TABLE = "Event";

    public static class Columns extends BaseDataModel.Columns {
        public static final String UID = "uid";
        public static final String ENROLLMENT_UID = "enrollment";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String CREATED_AT_CLIENT = "createdAtClient";
        public static final String LAST_UPDATED_AT_CLIENT = "lastUpdatedAtClient";
        public static final String STATUS = "status";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String PROGRAM = "program";
        public static final String PROGRAM_STAGE = "programStage";
        public static final String ORGANISATION_UNIT = "organisationUnit";
        public static final String EVENT_DATE = "eventDate";
        public static final String COMPLETE_DATE = "completedDate";
        public static final String DUE_DATE = "dueDate";
        public static final String ATTRIBUTE_CATEGORY_OPTIONS = "attributeCategoryOptions";
        public static final String ATTRIBUTE_OPTION_COMBO = "attributeOptionCombo";
        public static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
    }

    public static EventModel create(Cursor cursor) {
        return AutoValue_EventModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_EventModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    @ColumnName(Columns.UID)
    public abstract String uid();

    // Nullable properties
    @Nullable
    @ColumnName(Columns.ENROLLMENT_UID)
    public abstract String enrollmentUid();

    @Nullable
    @ColumnName(Columns.CREATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @ColumnName(Columns.LAST_UPDATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    @ColumnName(Columns.CREATED_AT_CLIENT)
    public abstract String createdAtClient();

    @Nullable
    @ColumnName(Columns.LAST_UPDATED_AT_CLIENT)
    public abstract String lastUpdatedAtClient();

    @Nullable
    @ColumnName(Columns.PROGRAM)
    public abstract String program();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE)
    public abstract String programStage();

    @Nullable
    @ColumnName(Columns.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @ColumnName(Columns.EVENT_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date eventDate();

    @Nullable
    @ColumnName(Columns.STATUS)
    @ColumnAdapter(DbEventStatusColumnAdapter.class)
    public abstract EventStatus status();

    @Nullable
    @ColumnName(Columns.LATITUDE)
    public abstract String latitude();

    @Nullable
    @ColumnName(Columns.LONGITUDE)
    public abstract String longitude();

    @Nullable
    @ColumnName(Columns.COMPLETE_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date completedDate();

    @Nullable
    @ColumnName(Columns.DUE_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date dueDate();

    @Nullable
    @ColumnName(Columns.ATTRIBUTE_CATEGORY_OPTIONS)
    public abstract String attributeCategoryOptions();

    @Nullable
    @ColumnName(Columns.ATTRIBUTE_OPTION_COMBO)
    public abstract String attributeOptionCombo();

    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY_INSTANCE)
    public abstract String trackedEntityInstance();

    @AutoValue.Builder
    public static abstract class Builder extends BaseDataModel.Builder<Builder> {
        public abstract Builder uid(@NonNull String uid);

        public abstract Builder enrollmentUid(@Nullable String enrollmentUid);

        public abstract Builder created(@Nullable Date created);

        public abstract Builder lastUpdated(@Nullable Date lastUpdated);

        public abstract Builder createdAtClient(@Nullable String createdAtClient);

        public abstract Builder lastUpdatedAtClient(@Nullable String lastUpdatedAtClient);

        public abstract Builder program(@Nullable String program);

        public abstract Builder programStage(@Nullable String programStage);

        public abstract Builder organisationUnit(@Nullable String organisationUnit);

        public abstract Builder eventDate(@Nullable Date eventDate);

        public abstract Builder status(@Nullable EventStatus status);

        public abstract Builder latitude(@Nullable String latitude);

        public abstract Builder longitude(@Nullable String longitude);

        public abstract Builder completedDate(@Nullable Date completedDate);

        public abstract Builder dueDate(@Nullable Date dueDate);

        public abstract Builder attributeCategoryOptions(@Nullable String attributeCategoryOptions);

        public abstract Builder attributeOptionCombo(@Nullable String attributeOptionCombo);

        public abstract Builder trackedEntityInstance(@Nullable String trackedEntityInstance);

        public abstract EventModel build();
    }
}
