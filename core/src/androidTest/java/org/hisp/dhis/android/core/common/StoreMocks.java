package org.hisp.dhis.android.core.common;

import android.database.Cursor;

import org.hisp.dhis.android.core.option.OptionSetModel;

import java.util.Date;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

class StoreMocks {
    static OptionSetModel generateOptionSetModel() {
        return OptionSetModel.builder()
                .uid("1234567890")
                .code("code")
                .name("name")
                .displayName("displayName")
                .created(new Date())
                .lastUpdated(new Date())
                .version(1)
                .valueType(ValueType.AGE)
                .build();
    }

    static OptionSetModel generateUpdatedOptionSetModel() {
        return OptionSetModel.builder()
                .uid("1234567890")
                .code("updated_code")
                .name("name")
                .displayName("updated_displayName")
                .created(new Date())
                .lastUpdated(new Date())
                .version(2)
                .valueType(ValueType.AGE)
                .build();
    }

    static OptionSetModel generateOptionSetModelWithoutUid() {
        return OptionSetModel.builder()
                .uid(null)
                .code("code")
                .name("name")
                .displayName("displayName")
                .created(new Date())
                .lastUpdated(new Date())
                .version(1)
                .valueType(ValueType.AGE)
                .build();
    }

    static void optionSetCursorAssert(Cursor cursor, OptionSetModel m) {
        assertThatCursor(cursor).hasRow(
                m.uid(),
                m.code(),
                m.name(),
                m.displayName(),
                m.createdStr(),
                m.lastUpdatedStr(),
                m.version(),
                m.valueType()
        ).isExhausted();
    }
}
