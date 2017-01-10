package org.hisp.dhis.android.core.data.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter;

import org.hisp.dhis.android.core.program.ProgramType;

//TODO: Write unit-test's for this adapter:
public class DbProgramTypeColumnAdapter implements ColumnTypeAdapter<ProgramType> {

    @Override
    public ProgramType fromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        String source = cursor.getString(columnIndex);

        ProgramType programType = null;
        if (source != null) {
            try {
                programType = ProgramType.valueOf(source);
            } catch (IllegalArgumentException exception) {
                throw new RuntimeException("Unknown program type", exception);
            }
        }
        return programType;
    }

    @Override
    public void toContentValues(ContentValues values, String columnName, ProgramType value) {
        if (value != null) {
            values.put(columnName, value.name());
        }
    }
}
