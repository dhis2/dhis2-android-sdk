package org.hisp.dhis.android.core.data.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter;

import org.hisp.dhis.android.core.program.ProgramRuleVariableSourceType;

public class DbProgramRuleVariableSourceTypeColumnAdapter implements ColumnTypeAdapter<ProgramRuleVariableSourceType> {

    @Override
    public ProgramRuleVariableSourceType fromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        String sourceValue = cursor.getString(columnIndex);

        ProgramRuleVariableSourceType programRuleVariableSourceType = null;
        if (sourceValue != null) {
            try {
                programRuleVariableSourceType = ProgramRuleVariableSourceType.valueOf(sourceValue);
            } catch (Exception exception) {
                throw new RuntimeException("Unknown program rule variable source type");
            }
        }
        return programRuleVariableSourceType;
    }

    @Override
    public void toContentValues(ContentValues contentValues, String columnName, ProgramRuleVariableSourceType programRuleVariableSourceType) {
        if (programRuleVariableSourceType != null) {
            contentValues.put(columnName, programRuleVariableSourceType.name());
        }
    }
}
