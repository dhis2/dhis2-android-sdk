package org.hisp.dhis.android.core.data.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter;

import org.hisp.dhis.android.core.common.FormType;

public class DbFormTypeColumnAdapter implements ColumnTypeAdapter<FormType> {

    @Override
    public FormType fromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        String sourceValue = cursor.getString(columnIndex);

        FormType formType = null;
        if (sourceValue != null) {
            try {
                formType = FormType.valueOf(sourceValue);
            } catch (Exception exception) {
                throw new RuntimeException("Unknown form type", exception);
            }
        }

        return formType;
    }

    @Override
    public void toContentValues(ContentValues contentValues, String columnName, FormType formType) {
        if (formType != null) {
            contentValues.put(columnName, formType.name());
        }
    }
}
