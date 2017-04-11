package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;

@AutoValue
public abstract class ProgramStageSectionDataElementLinkModel extends BaseModel {
    public static final String TABLE = "programStageSectionDataElementLinkTable";

    public static class Columns extends BaseModel.Columns {
        public static final String PROGRAM_STAGE_SECTION = "programStageSection";
        public static final String DATA_ELEMENT = "dataElement";
    }

    @NonNull
    public static ProgramStageSectionDataElementLinkModel create(Cursor cursor) {
        return AutoValue_ProgramStageSectionDataElementLinkModel.createFromCursor(cursor);
    }

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_ProgramStageSectionDataElementLinkModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE_SECTION)
    public abstract String programStageSection();

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {

        public abstract Builder programStageSection(@Nullable String programStageSection);

        public abstract Builder dataElement(@Nullable String dataElement);

        public abstract ProgramStageSectionDataElementLinkModel build();
    }
}
