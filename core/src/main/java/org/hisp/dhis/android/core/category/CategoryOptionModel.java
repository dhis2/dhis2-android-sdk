package org.hisp.dhis.android.core.category;


import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.Date;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class CategoryOptionModel extends BaseNameableObjectModel {
    public static final String TABLE = "CategoryOption";

    public static class Columns extends BaseNameableObjectModel.Columns {
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), START_DATE, END_DATE);
        }
    }

    @Nullable
    @ColumnName(Columns.START_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date startDate();

    @Nullable
    @ColumnName(Columns.END_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date endDate();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        super.bindToStatement(sqLiteStatement);
        sqLiteBind(sqLiteStatement, 11, startDate());
        sqLiteBind(sqLiteStatement, 12, endDate());
    }

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_CategoryOptionModel.Builder();
    }

    @NonNull
    public static CategoryOptionModel create(Cursor cursor) {
        return AutoValue_CategoryOptionModel.createFromCursor(cursor);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {

        public abstract Builder startDate(Date startDate);

        public abstract Builder endDate(Date endDate);

        public abstract CategoryOptionModel build();
    }
}
