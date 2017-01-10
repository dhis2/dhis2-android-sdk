package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionContract.Columns;

@AutoValue
public abstract class ProgramStageSectionModel extends BaseIdentifiableObjectModel {

    public static ProgramStageSectionModel create(Cursor cursor) {
        return AutoValue_ProgramStageSectionModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramStageSectionModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE)
    public abstract String programStage();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        public abstract Builder programStage(@Nullable String programStage);

        public abstract ProgramStageSectionModel build();
    }
}
