package org.hisp.dhis.android.rules.models;

import org.hisp.dhis.android.rules.models.ValueType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ValueTypeTests {

    @Test
    public void typesShouldCorrespondToEnums() {
        // booleans
        assertThat(ValueType.BOOLEAN.isBoolean()).isTrue();
        assertThat(ValueType.TRUE_ONLY.isBoolean()).isTrue();

        // dates
        assertThat(ValueType.DATE.isDate()).isTrue();
        assertThat(ValueType.DATETIME.isDate()).isTrue();

        // numeric values
        assertThat(ValueType.NUMBER.isNumeric()).isTrue();
        assertThat(ValueType.PERCENTAGE.isNumeric()).isTrue();
        assertThat(ValueType.UNIT_INTERVAL.isNumeric()).isTrue();
        assertThat(ValueType.INTEGER.isNumeric()).isTrue();
        assertThat(ValueType.INTEGER_POSITIVE.isNumeric()).isTrue();
        assertThat(ValueType.INTEGER_NEGATIVE.isNumeric()).isTrue();
        assertThat(ValueType.INTEGER_ZERO_OR_POSITIVE.isNumeric()).isTrue();

        // text values
        assertThat(ValueType.TIME.isText()).isTrue();
        assertThat(ValueType.TEXT.isText()).isTrue();
        assertThat(ValueType.EMAIL.isText()).isTrue();
        assertThat(ValueType.LETTER.isText()).isTrue();
        assertThat(ValueType.USERNAME.isText()).isTrue();
        assertThat(ValueType.LONG_TEXT.isText()).isTrue();
        assertThat(ValueType.COORDINATE.isText()).isTrue();
        assertThat(ValueType.PHONE_NUMBER.isText()).isTrue();

        // files
        assertThat(ValueType.FILE_RESOURCE.isFile()).isTrue();
    }
}
