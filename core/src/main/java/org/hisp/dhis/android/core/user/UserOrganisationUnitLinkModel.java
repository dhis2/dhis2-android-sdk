package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.commons.BaseModel;

@AutoValue
public abstract class UserOrganisationUnitLinkModel extends BaseModel {

    @Nullable
    @ColumnName(UserOrganisationUnitLinkContract.Columns.USER)
    public abstract String user();

    @Nullable
    @ColumnName(UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static UserOrganisationUnitLinkModel create(Cursor cursor) {
        return AutoValue_UserOrganisationUnitLinkModel.createFromCursor(cursor);
    }

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_UserOrganisationUnitLinkModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder user(@Nullable String user);

        public abstract Builder organisationUnit(@Nullable String organisationUnit);

        public abstract UserOrganisationUnitLinkModel build();
    }
}
