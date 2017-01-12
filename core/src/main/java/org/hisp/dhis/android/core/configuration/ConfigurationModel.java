package org.hisp.dhis.android.core.configuration;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;

@AutoValue
public abstract class ConfigurationModel extends BaseModel {

    public static class Columns extends BaseModel.Columns {
        public static final String SERVER_URL = "serverUrl";
    }

    @NonNull
    @ColumnName(Columns.SERVER_URL)
    public abstract String serverUrl();

    public abstract ContentValues toContentValues();

    public static ConfigurationModel create(Cursor cursor) {
        return AutoValue_ConfigurationModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ConfigurationModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder serverUrl(String serverUrl);

        public abstract ConfigurationModel build();
    }
}
