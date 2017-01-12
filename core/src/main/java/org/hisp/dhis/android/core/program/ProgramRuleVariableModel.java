package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.data.database.DbProgramRuleVariableSourceTypeColumnAdapter;

@AutoValue
public abstract class ProgramRuleVariableModel extends BaseIdentifiableObjectModel {

    public interface Columns extends BaseIdentifiableObjectModel.Columns {
        String PROGRAM_STAGE = "programStage";
        String PROGRAM_RULE_VARIABLE_SOURCE_TYPE = "programRuleVariableSourceType";
        String USE_CODE_FOR_OPTION_SET = "useCodeForOptionSet";
        String PROGRAM = "program";
        String DATA_ELEMENT = "dataElement";
        String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
    }

    public static ProgramRuleVariableModel create(Cursor cursor) {
        return AutoValue_ProgramRuleVariableModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramRuleVariableModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.USE_CODE_FOR_OPTION_SET)
    public abstract Boolean useCodeForOptionSet();

    @Nullable
    @ColumnName(Columns.PROGRAM)
    public abstract String program();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE)
    public abstract String programStage();

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY_ATTRIBUTE)
    public abstract String trackedEntityAttribute();

    @Nullable
    @ColumnName(Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
    @ColumnAdapter(DbProgramRuleVariableSourceTypeColumnAdapter.class)
    public abstract ProgramRuleVariableSourceType programRuleVariableSourceType();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder useCodeForOptionSet(@Nullable Boolean useCodeForOptionSet);

        public abstract Builder program(@Nullable String program);

        public abstract Builder programStage(@Nullable String programStage);

        public abstract Builder dataElement(@Nullable String dataElement);

        public abstract Builder trackedEntityAttribute(@Nullable String trackedEntityAttribute);

        public abstract Builder programRuleVariableSourceType(@Nullable ProgramRuleVariableSourceType programRuleVariableSourceType);

        public abstract ProgramRuleVariableModel build();

    }

}
