package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableDataModel;

import static org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel.Columns.UID;

@AutoValue
public abstract class TrackedEntityInstanceModel extends BaseIdentifiableDataModel {

    public static class Columns extends BaseIdentifiableDataModel.Columns {
        public static final String UID = "uid";
        public static final String ORGANISATION_UNIT = "organisationUnit";
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static TrackedEntityInstanceModel.Builder builder() {
        return new $$AutoValue_TrackedEntityInstanceModel.Builder();
    }

    @NonNull
    public static TrackedEntityInstanceModel create(Cursor cursor) {
        return AutoValue_TrackedEntityInstanceModel.createFromCursor(cursor);
    }

    @ColumnName(UID)
    public abstract String uid();

    @ColumnName(ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableDataModel.Builder<Builder> {
        public abstract Builder uid(@Nullable String uid);

        public abstract Builder organisationUnit(@Nullable String organisationUnit);

        public abstract TrackedEntityInstanceModel build();
    }
}
