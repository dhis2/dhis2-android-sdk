/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
