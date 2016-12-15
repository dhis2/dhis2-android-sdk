package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;

@AutoValue
public abstract class AuthenticatedUserModel extends BaseModel {

    @Nullable
    @ColumnName(AuthenticatedUserContract.Columns.ID)
    public abstract Long id();

    @Nullable
    @ColumnName(AuthenticatedUserContract.Columns.USER)
    public abstract String user();

    @Nullable
    @ColumnName(AuthenticatedUserContract.Columns.CREDENTIALS)
    public abstract String credentials();

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_AuthenticatedUserModel.Builder();
    }

    @NonNull
    public static AuthenticatedUserModel create(Cursor cursor) {
        return AutoValue_AuthenticatedUserModel.createFromCursor(cursor);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder id(@Nullable Long id);

        public abstract Builder user(@Nullable String user);

        public abstract Builder credentials(@Nullable String credentials);

        public abstract AuthenticatedUserModel build();
    }
}
