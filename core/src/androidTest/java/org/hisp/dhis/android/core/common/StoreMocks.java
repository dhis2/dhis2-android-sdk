package org.hisp.dhis.android.core.common;

import android.database.Cursor;

import org.hisp.dhis.android.core.option.OptionSet;

import java.util.Date;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

class StoreMocks {
    static OptionSet generateOptionSet() {
        return OptionSet.builder()
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

    static OptionSet generateUpdatedOptionSet() {
        return OptionSet.builder()
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

    static OptionSet generateOptionSetWithoutUid() {
        return OptionSet.builder()
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

    static void optionSetCursorAssert(Cursor cursor, OptionSet o) {
        assertThatCursor(cursor).hasRow(
                o.uid(),
                o.code(),
                o.name(),
                o.displayName(),
                BaseIdentifiableObject.DATE_FORMAT.format(o.created()),
                BaseIdentifiableObject.DATE_FORMAT.format(o.lastUpdated()),
                o.version(),
                o.valueType()
        ).isExhausted();
    }
}
