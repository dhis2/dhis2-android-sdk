package org.hisp.dhis.android.core.option;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.ValueType;

public class CreateOptionSetUtils {

    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final Integer VERSION = 51;

    // timestamp
    private static final String DATE = "2016-12-20T16:26:00.007";

    public static ContentValues create(long id, String uid) {
        ContentValues optionSet = new ContentValues();
        optionSet.put(OptionSetModel.Columns.ID, id);
        optionSet.put(OptionSetModel.Columns.UID, uid);
        optionSet.put(OptionSetModel.Columns.CODE, CODE);
        optionSet.put(OptionSetModel.Columns.NAME, NAME);
        optionSet.put(OptionSetModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        optionSet.put(OptionSetModel.Columns.VERSION, VERSION);
        optionSet.put(OptionSetModel.Columns.VALUE_TYPE, VALUE_TYPE.name());
        return optionSet;
    }
}
