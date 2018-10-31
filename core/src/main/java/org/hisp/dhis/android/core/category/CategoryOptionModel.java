package org.hisp.dhis.android.core.category;


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.Date;

@AutoValue
public abstract class CategoryOptionModel extends BaseNameableObjectModel {
    public static final String TABLE = "CategoryOption";

    public static class Columns extends BaseNameableObjectModel.Columns {
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        public static final String ACCESS_DATA_WRITE = "accessDataWrite";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), START_DATE, END_DATE);
        }
    }

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date startDate();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date endDate();

    @Nullable
    public abstract Boolean accessDataWrite();

    @NonNull
    public static Builder builder() {
        return new $AutoValue_CategoryOptionModel.Builder();
    }

    @NonNull
    public static CategoryOptionModel create(Cursor cursor) {
        return AutoValue_CategoryOptionModel.createFromCursor(cursor);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {

        public abstract Builder startDate(Date startDate);

        public abstract Builder endDate(Date endDate);

        public abstract Builder accessDataWrite(Boolean accessDataWrite);

        public abstract CategoryOptionModel build();
    }
}
