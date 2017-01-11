package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

@AutoValue
public abstract class ProgramRuleModel extends BaseIdentifiableObjectModel {

    public interface Columns extends BaseIdentifiableObjectModel.Columns {
        String PROGRAM_STAGE = "programStage";
        String PROGRAM = "program";
        String PRIORITY = "priority";
        String CONDITION = "condition";
    }

    public static ProgramRuleModel create(Cursor cursor) {
        return AutoValue_ProgramRuleModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramRuleModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.PRIORITY)
    public abstract Integer priority();

    @Nullable
    @ColumnName(Columns.CONDITION)
    public abstract String condition();

    @Nullable
    @ColumnName(Columns.PROGRAM)
    public abstract String program();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE)
    public abstract String programStage();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder priority(@Nullable Integer priority);

        public abstract Builder condition(@Nullable String condition);

        public abstract Builder program(@Nullable String program);

        public abstract Builder programStage(@Nullable String programStage);

        public abstract ProgramRuleModel build();
    }
}
