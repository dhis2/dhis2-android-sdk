package org.hisp.dhis.android.core.systeminfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;

import java.util.Date;

@AutoValue
public abstract class SystemInfoModel extends BaseModel {

    public interface Columns extends BaseModel.Columns {
        String SERVER_DATE = "serverDate";
        String DATE_FORMAT = "dateFormat";
    }

    public static SystemInfoModel create(Cursor cursor) {
        return AutoValue_SystemInfoModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_SystemInfoModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.SERVER_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date serverDate();

    @Nullable
    @ColumnName(Columns.DATE_FORMAT)
    public abstract String dateFormat();

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {

        public abstract Builder serverDate(@Nullable Date serverDate);

        public abstract Builder dateFormat(@Nullable String dateFormat);

        public abstract SystemInfoModel build();
    }
}
