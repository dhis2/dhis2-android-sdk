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
    public static class Columns extends BaseDataModel.Columns {
        public static final String EVENT_UID = "uid";
        public static final String ENROLLMENT_UID = "enrollment";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String STATUS = "status";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String PROGRAM = "program";
        public static final String PROGRAM_STAGE = "programStage";
        public static final String ORGANISATION_UNIT = "orgUnit";
        public static final String EVENT_DATE = "eventDate";
        public static final String COMPLETE_DATE = "completedDate";
        public static final String DUE_DATE = "dueDate";
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
    @ColumnName(Columns.EVENT_UID)
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
    public abstract Double latitude();

    @Nullable
    @ColumnName(Columns.LONGITUDE)
    public abstract Double longitude();

    @Nullable
    @ColumnName(Columns.COMPLETE_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date completedDate();

    @Nullable
    @ColumnName(Columns.DUE_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date dueDate();

    @AutoValue.Builder
    public static abstract class Builder extends BaseDataModel.Builder<Builder> {
        public abstract Builder uid(@NonNull String uid);

        public abstract Builder enrollmentUid(@Nullable String enrollmentUid);

        public abstract Builder created(@Nullable Date created);

        public abstract Builder lastUpdated(@Nullable Date lastUpdated);

        public abstract Builder program(@Nullable String program);

        public abstract Builder programStage(@Nullable String programStage);

        public abstract Builder organisationUnit(@Nullable String organisationUnit);

        public abstract Builder eventDate(@Nullable Date eventDate);

        public abstract Builder status(@Nullable EventStatus status);

        public abstract Builder latitude(@Nullable Double latitude);

        public abstract Builder longitude(@Nullable Double longitude);

        public abstract Builder completedDate(@Nullable Date completedDate);

        public abstract Builder dueDate(@Nullable Date dueDate);

        public abstract EventModel build();
    }
}
