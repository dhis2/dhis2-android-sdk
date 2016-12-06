package org.hisp.dhis.android.core.commons;

import android.content.ContentValues;

import org.hisp.dhis.android.core.commons.database.Model;

final class TestModel implements Model {
    static final String ID = "test_id";
    static final String VALUE = "test_value";

    private final Long id;
    private final String value;

    TestModel(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public boolean isValid() {
        return id != null;
    }

    public String value() {
        return value;
    }

    static ContentValues values(Long id, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestModel.ID, id);
        contentValues.put(TestModel.VALUE, value);
        return contentValues;
    }

    static ContentValues values(String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestModel.VALUE, value);
        return contentValues;
    }
}