package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;

@AutoValue
public abstract class UserOrganisationUnitLinkModel extends BaseModel {

    public interface Columns extends BaseModel.Columns {
        String USER = "user";
        String ORGANISATION_UNIT = "organisationUnit";
        String ORGANISATION_UNIT_SCOPE = "organisationUnitScope";
    }

    @Nullable
    @ColumnName(Columns.USER)
    public abstract String user();

    @Nullable
    @ColumnName(Columns.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @ColumnName(Columns.ORGANISATION_UNIT_SCOPE)
    public abstract String organisationUnitScope();

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

        public abstract Builder organisationUnitScope(@Nullable String organisationUnitScope);

        public abstract UserOrganisationUnitLinkModel build();
    }
}
