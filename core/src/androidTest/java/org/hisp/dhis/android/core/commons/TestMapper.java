package org.hisp.dhis.android.core.commons;

import android.content.ContentValues;
import android.database.Cursor;

final class TestMapper implements Mapper<TestModel> {

    @Override
    public ContentValues toContentValues(TestModel model) {
        return TestModel.values(model.id(), model.value());
    }

    @Override
    public TestModel toModel(Cursor cursor) {
        return new TestModel(
                cursor.getLong(cursor.getColumnIndex(TestModel.ID)),
                cursor.getString(cursor.getColumnIndex(TestModel.VALUE)));
    }
}