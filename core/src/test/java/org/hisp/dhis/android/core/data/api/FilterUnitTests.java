package org.hisp.dhis.android.core.data.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static junit.framework.Assert.fail;

@RunWith(JUnit4.class)
public class FilterUnitTests {

    @Test
    public void fields_shouldThrowExceptionOnNullArguments() {
        try {
            Filter.builder().fields().build();

            fail("IllegalArgumentException was expected but was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void filter_shouldBeImmutable() {
        Filter filter = Filter.builder()
                .fields(
                        Field.create("one"),
                        Field.create("two"),
                        Field.create("three"))
                .build();

        try {
            filter.fields().add(Field.create("four"));

            fail("UnsupportedOperationException was expected but nothing was thrown");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // swallow exception
        }
    }

    @Test
    public void equals_shouldConformToContract() {
        EqualsVerifier.forClass(Filter.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
