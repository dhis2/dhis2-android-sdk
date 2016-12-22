package org.hisp.dhis.android.core.data.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter;

import org.hisp.dhis.android.core.common.ValueType;


public class DbValueTypeColumnAdapter implements ColumnTypeAdapter<ValueType> {

    @Override
    public ValueType fromCursor(Cursor cursor, String columnName) {

        int columnIndex = cursor.getColumnIndex(columnName);
        String sourceValue = cursor.getString(columnIndex);

        ValueType valueType = null;
        if (sourceValue != null) {
            try {
                valueType = ValueType.valueOf(sourceValue);
            } catch (Exception exception) {
                throw new RuntimeException("Unknown value type", exception);
            }
        }


        return valueType;
    }

    @Override
    public void toContentValues(ContentValues contentValues, String columnName, ValueType valueType) {
        if (valueType != null) {
            contentValues.put(columnName, valueType.name());
        }
    }
}
