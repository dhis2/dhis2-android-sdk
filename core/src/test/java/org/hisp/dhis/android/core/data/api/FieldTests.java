package org.hisp.dhis.android.core.data.api;

import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class FieldTests {

    @Test(expected = NullPointerException.class)
    public void fieldConstructor_shouldThrowExceptionOnNullName() {
        Field.create(null);
    }

    @Test(expected = NullPointerException.class)
    public void nestedFieldConstructor_shouldThrowExceptionOnNullName() {
        NestedField.create(null);
    }

    @Test
    public void fieldName_shouldReturnCorrectValue() {
        Field field = Field.create("test_field_name");
        assertThat(field.name()).isEqualTo("test_field_name");
    }

    @Test
    public void fieldEquals_shouldConformToContract() {
        EqualsVerifier.forClass(Field.create("").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void nestedFieldEquals_shouldConformToContract() {
        EqualsVerifier.forClass(NestedField.create("").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void nestedFieldWith_shouldReturnNewFieldInstance() {
        Field<String, String> fieldOne = Field.create("fieldOne");
        Field<String, String> fieldTwo = Field.create("fieldTwo");

        NestedField<String, String> nestedField = NestedField.create("test_nested_field");
        NestedField<String, ?> nestedFieldWithChildren = nestedField.with(fieldOne, fieldTwo);

        assertThat(nestedField.children()).isEmpty();
        assertThat(nestedField).isNotEqualTo(nestedFieldWithChildren);
        assertThat(nestedFieldWithChildren.children().contains(fieldOne) &&
                nestedFieldWithChildren.children().contains(fieldTwo)).isTrue();
    }

    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("unchecked")
    public void nestedFieldChildren_shouldBeImmutable() {
        Field<String, String> fieldOne = Field.create("test_field_one");

        NestedField<String, String> nestedField = NestedField.create("test_nested_field");
        NestedField<String, ?> nestedFieldWithChildren = nestedField.with(fieldOne);

        nestedFieldWithChildren.children().remove(0);
    }

    @Test
    public void nestedFieldWith_shouldNotThrowOnNullArguments() {
        NestedField<String, ?> nestedField = NestedField.create("test_nested_field");
        assertThat(nestedField.with().children()).isEmpty();
    }
}
